import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;


public class HMMTagger {

	public static void main(String[] args) throws IOException {
		//the tree map has all the tags as a value linked to an integer key (the index for Viterbi)
		TreeMap<Integer,String> keyTagMap = new TreeMap<Integer,String>();

		//TreeMap<String,Integer> tagKeyMap = new TreeMap<String, Integer>();

		//the word and tag pair obtained after parsing the JSON file
		TreeMap<String,String> wordAndTagMap = new TreeMap<String,String>();

		JsonArray tagsJsonArray  = JSONMethods.loadJSONFile("tags.json").getJsonArray("Tags");
		int totalTags = tagsJsonArray.size();
		//building the map from the JSON file
		for(int j = 0; j < totalTags; j++){
			String tag = (String) tagsJsonArray.getJsonObject(j).keySet().toArray()[0];
			JsonNumber value = (JsonNumber) tagsJsonArray.getJsonObject(j).get(tag);
			keyTagMap.put(value.intValue(),tag);
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

		//the array of all the word and their corresponding tags
		JsonArray wordAndTagArray = JSONMethods.loadJSONFile("word-tag.json").getJsonArray("Word And Tag");
		int size = wordAndTagArray.size();

		for(int j = 0; j < size; j++){
			String word = (String) wordAndTagArray.getJsonObject(j).keySet().toArray()[0];
			String tag = ((JsonString) wordAndTagArray.getJsonObject(j).get(word)).toString();
			wordAndTagMap.put(word.toLowerCase(), tag);
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


		HashMap<String,String> predictedTagsMap = new HashMap<String, String>();

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

			//System.out.println(keyTagMap);

			//System.out.println(S);
			//call the viterbi algorithm on each sentence
			predictedTagsMap.putAll(Viterbi(S, A, B, keyTagMap, wordAndTagMap));
		}

		StringWriter sw = new StringWriter();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = writerFactory.createWriter(sw);

		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		JSONMethods.generateWordTagJsonArray(predictedTagsMap, arrayBuilder);
		JsonArray predictedTags = arrayBuilder.build();


		// THEN PUT IT ALL TOGETHER IN A JsonObject
		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add("Predicted Tags", predictedTags)
				.build();



		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		OutputStream os = new FileOutputStream("predicted-tags-hmm.json");
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter("predicted-tags-hmm.json");
		pw.write(prettyPrinted);
		pw.close();

	}

	/**
	 * 
	 * @param S is the input sentence, stored as an array. S[i] is the i(th) word in the sentence.
	 * @param A contains transition probabilities
	 * @param B contains emission probabilities
	 * @param keyTagMap has all the tags in the training set
	 * @throws FileNotFoundException 
	 */
	public static HashMap<String, String> Viterbi(ArrayList<String> S, HashMap<String,Double> A, HashMap<String,Double> B, Map<Integer,String> keyTagMap, Map<String,String> wordAndTagMap) throws FileNotFoundException{
		int n = S.size();
		int T = keyTagMap.size();

		ArrayList<String> predictedTagsList = new ArrayList<String>();
		ArrayList<Double> predictedTagsProbability = new ArrayList<Double>();
		HashMap<String, String> predictedTagsMap = new HashMap<String, String>();

		HashMap<String,Double> mapToReturn = new HashMap<String, Double>();


		String predictedTag = "";

		int[] bestTagsIndex = new int[n];

		double[][] bestPaths = new double[n][T];
		int[][] backPointers = new int[n][T];

		//initializing
		for(int i = 0; i < T; i++){
			String currentTag = keyTagMap.get(i+1);
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
		predictedTagsList.add("<s>");


		//forward pass
		for(int i = 1; i < n; i++){
			//the current word in the sentence
			String word = S.get(i);

			for(int j = 0; j < T; j++){
				//will sort the values as we the along and link them to the best tag sequence automatically.
				//double is the probability value and integer is the tag's index
				TreeMap<Double,Integer> maxValueUnknownMap = new TreeMap<Double, Integer>();
				//the map with the known tag-word pairs
				TreeMap<Double,Integer> maxValueKnownMap = new TreeMap<Double, Integer>();
				String tagJ = keyTagMap.get(j+1);

				//go over all the tag sets 
				for(int k = 0; k < T; k++){
					double transition = A.get(keyTagMap.get(k+1) + "+" + tagJ);
					double emission = 0;

					boolean pairExists = false;

					//if the emission exists, get the emission probability value
					if(B.get(tagJ + "+" + word) != null){
						emission = B.get(tagJ + "+" + word);
						pairExists = true;
					}
					//if not present, get the UNK probability 
					else{
						emission = 0.004;//B.get(tagJ + "+UNK");
					}

					//get the log sum to prevent underflow
					double probability = Math.log(bestPaths[i-1][k]) + Math.log(transition) + Math.log(emission);
					//get the actual probability value
					probability = Math.exp(probability);

					if(pairExists){
						maxValueKnownMap.put(probability, k+1);
					}
					else{
						maxValueUnknownMap.put(probability, k+1);
						//System.out.println(maxValueUnknownMap);
					}
				}
				if(maxValueUnknownMap.size() > 0){
					//the highest probability
					bestPaths[i][j] = maxValueUnknownMap.lastKey();
					predictedTagsProbability.add(maxValueUnknownMap.lastKey());
					//the tag index which is giving the highest probability
					backPointers[i][j] = maxValueUnknownMap.get(bestPaths[i][j]);
				}
				else{
					//double highestProbability = maxValueKnownMap.lastKey();
					//the highest probability
					bestPaths[i][j] = maxValueKnownMap.lastKey();
					predictedTagsProbability.add(maxValueKnownMap.lastKey());
					//the tag index which is giving the highest probability
					backPointers[i][j] = maxValueKnownMap.get(bestPaths[i][j]);
				}
			}
			predictedTag = TaggerMethods.handleNewWord(word, wordAndTagMap, predictedTagsMap).replace("\"", "");
			predictedTagsList.add(predictedTag);
			predictedTagsMap.putIfAbsent(word, predictedTag);
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

		for(int k = n-2; k >=0; k--){
			int firstIndex = k+1;
			int secondIndex = bestTagsIndex[k+1]-1;
			bestTagsIndex[k] = backPointers[firstIndex][secondIndex];
		}

		for(int i = 0; i < n; i++){
			//bestTags[i] = predictedTagsList.get(i);
			//System.out.println(predictedTagsProbability.get(i));
			mapToReturn.put(S.get(i) + "/" + predictedTagsList.get(i),  predictedTagsProbability.get(i));
		}
		System.out.println(mapToReturn+"\n");
		return predictedTagsMap;
	}

	public static void printArray(String[] array){
		int len = array.length;

		for(int i = 0; i < len; i++){
			System.out.print(array[i] + "	");
		}
		System.out.println();
	}

}