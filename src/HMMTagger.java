import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.json.JsonArray;
import javax.json.JsonNumber;


public class HMMTagger {

	public static void main(String[] args) throws IOException {
		//the hash map has all the tags and a corresponding index for the viterbi algorithm's matrices
		TreeMap<Integer,String> tagsMap = new TreeMap<Integer,String>();
		JsonArray tagsJsonArray  = JSONMethods.loadJSONFile("tags.json").getJsonArray("Tags");
		int totalTags = tagsJsonArray.size();
		//building the map from the JSON file
		for(int j = 0; j < totalTags; j++){
			String tag = (String) tagsJsonArray.getJsonObject(j).keySet().toArray()[0];
			JsonNumber value = (JsonNumber) tagsJsonArray.getJsonObject(j).get(tag);
			tagsMap.put(value.intValue(),tag);
		}
		
		System.out.println(tagsMap);

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
				String tag = couples[a].split("/")[1];

				S.add(word);
			}

			//System.out.println(S);
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
			backPointers[0][i] = 0;
		}
		System.out.println(bestPaths[0][4]);
	}

}
