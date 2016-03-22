import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.json.JsonArray;
import javax.json.JsonNumber;


public class HMMTagger {

	public static void main(String[] args) throws IOException {
		//the hash map has all the tags and a corresponding index for the viterbi algorithm's matrices
		HashMap<String,Integer> tagsMap = new HashMap<String,Integer>();
		JsonArray tagsJsonArray  = JSONMethods.loadJSONFile("tags.json").getJsonArray("Tags");
		int totalTags = tagsJsonArray.size();
		//building the map from the JSON file
		for(int j = 0; j < totalTags; j++){
			String tag = (String) tagsJsonArray.getJsonObject(j).keySet().toArray()[0];
			JsonNumber value = (JsonNumber) tagsJsonArray.getJsonObject(j).get(tag);
			tagsMap.put(tag,value.intValue());
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
				String tag = couples[a].split("/")[1];

				S.add(word);
			}

			System.out.println(S);
			//run Viterbi algorithm here
		}

	}

}
