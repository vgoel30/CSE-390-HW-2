import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.json.JsonArray;
import javax.json.JsonNumber;


public class HMMTagger {

	public static void main(String[] args) throws IOException {
		//the tree map has all the tags as a value linked to an integer key (the index for Viterbi)
		TreeMap<Integer,String> tagsMap = new TreeMap<Integer,String>();
		JsonArray tagsJsonArray  = JSONMethods.loadJSONFile("tags.json").getJsonArray("Tags");
		int totalTags = tagsJsonArray.size();
		//building the map from the JSON file
		for(int j = 0; j < totalTags; j++){
			String tag = (String) tagsJsonArray.getJsonObject(j).keySet().toArray()[0];
			JsonNumber value = (JsonNumber) tagsJsonArray.getJsonObject(j).get(tag);
			tagsMap.put(value.intValue(),tag);
		}

		//the hash map with all the transition probabilities
		HashMap<String,Double> A = new HashMap<String, Double>();
		JsonArray transitionJsonArray = JSONMethods.loadJSONFile("laplace-transitions.json").getJsonArray("Laplace Transitions");
		totalTags = transitionJsonArray.size();
		//building the transition probabilities map from the JSON file
		for(int j = 0; j < totalTags; j++){
			String tag = (String) transitionJsonArray.getJsonObject(j).keySet().toArray()[0];
			JsonNumber value = (JsonNumber) transitionJsonArray.getJsonObject(j).get(tag);
			A.put(tag,value.doubleValue());
		}

		//the hash map with all the emission probabilities
		HashMap<String,Double> B = new HashMap<String, Double>();
		JsonArray emissionJsonArray = JSONMethods.loadJSONFile("laplace-emissions.json").getJsonArray("Laplace Emissions");
		totalTags = emissionJsonArray.size();
		//building the emission probabilities map from the JSON file
		for(int j = 0; j < totalTags; j++){
			String tag = (String) emissionJsonArray.getJsonObject(j).keySet().toArray()[0];
			JsonNumber value = (JsonNumber) emissionJsonArray.getJsonObject(j).get(tag);
			B.put(tag,value.doubleValue());
		}


		//PARSING TEST FILE
		File test = new File("test.txt");

		//all the sentences in the test file
		ArrayList<String> sentences = new ArrayList<String>();

		Scanner input = new Scanner(test);

		while(input.hasNextLine()){
			sentences.add(input.nextLine());
		}
		input.close();
		int totalSentences = sentences.size();


		//go over all the sentences
		for(int i = 0; i < totalSentences; i++){
			String[] couples = sentences.get(i).split("\\s+");

			//all the words in the sentence
			ArrayList<String> S = new ArrayList<String>();

			for(int a = 0; a < couples.length; a++){
				String word = couples[a].split("/")[0];
				//String tag = couples[a].split("/")[1];

				S.add(word);
			}

			//System.out.println(S);
			//call the viterbi algorithm on each sentence
			Viterbi(S, A, B, tagsMap);

		}

	}

	/**
	 * 
	 * @param S is the input sentence, stored as an array. S[i] is the i(th) word in the sentence.
	 * @param A contains transition probabilities
	 * @param B contains emission probabilities
	 * @param tagsMap has all the tags in the training set
	 */
	public static void Viterbi(ArrayList<String> S, HashMap<String,Double> A, HashMap<String,Double> B, Map<Integer,String> tagsMap){
		int n = S.size();
		int T = tagsMap.size();


		int[] bestTagsIndex = new int[n];

		double[][] bestPaths = new double[n][T];
		int[][] backPointers = new int[n][T];

		//initializing
		for(int i = 0; i < T; i++){
			String currentTag = tagsMap.get(i+1);
			String firstWord = S.get(1);
			double emissionProbability = 0;

			if(B.get(currentTag + "+" + firstWord) != null){
				emissionProbability = B.get(currentTag + "+" + firstWord);
			}
			//if not present, get the UNK probability 
			else{
				emissionProbability = B.get(currentTag + "+UNK");
			}
			bestPaths[0][i] = A.get("<s>+"+currentTag)*emissionProbability;
			backPointers[0][i] = 1; //index of the start tag
		}

		//forward pass
		for(int i = 1; i < n; i++){
			//the current word in the sentence
			String word = S.get(i);

			for(int j = 0; j < T; j++){
				//will sort the values as we the along and link them to the best tag sequence automatically.
				//double is the probability value and integer is the tag's index
				TreeMap<Double,Integer> maxValueMap = new TreeMap<Double, Integer>();
				String tagJ = tagsMap.get(j+1);

				//go over all the tag sets 
				for(int k = 0; k < T; k++){
					double transition = A.get(tagsMap.get(k+1) + "+" + tagJ);
					double emission = 0;

					//if the emission exists, get the emission probability value
					if(B.get(tagJ + "+" + word) != null){
						emission = B.get(tagJ + "+" + word);
					}
					//if not present, get the UNK probability 
					else{
						emission = B.get(tagJ + "+UNK");
					}

					//get the log sum to prevent underflow
					double probability = Math.log(bestPaths[i-1][k]) + Math.log(transition) + Math.log(emission);
					//get the actual probability value
					probability = Math.exp(probability);

					//put the probability as the key (faster sorting) and the tag's index as the value
					maxValueMap.put(probability, k+1);
				}
				//the highest probability
				bestPaths[i][j] = maxValueMap.lastKey();
				//the tag index which is giving the highest probability
				backPointers[i][j] = maxValueMap.get(maxValueMap.lastKey());
			}

		}
		//backtrace
		double highestValue = 0;
		int bestIndex = 0;
		for(int k = 0; k < T; k++){
			if(bestPaths[n-1][k] > highestValue){
				highestValue = bestPaths[n-1][k];
				bestIndex = k;
			}
		}
		bestTagsIndex[n-1] = bestIndex + 1;

		//System.out.println(bestTagsIndex);
		//printArray(bestTagsIndex);
		//System.out.println(bestTagsIndex[n-1]);

		for(int k = n-2; k >=0; k--){
			int firstIndex = k+1;
			int secondIndex = bestTagsIndex[k+1]-1;
			bestTagsIndex[k] = backPointers[firstIndex][secondIndex];
		}
		String[] bestTags = new String[n];
		for(int i = 0; i < n; i++){
			bestTags[i] = tagsMap.get(bestTagsIndex[i]);
		}
		printArray(bestTags);
	}

	public static void printArray(String[] array){
		int len = array.length;

		for(int i = 0; i < len; i++){
			System.out.print(array[i] + "	");
		}
		System.out.println();
	}

}
