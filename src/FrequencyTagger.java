import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;

public class FrequencyTagger {


	public static void main(String[] args) throws IOException {
		TreeMap<String,String> wordAndTagMap = new TreeMap<String,String>();

		//PARSING TEST FILE
		File test = new File("test.txt");

		Scanner input = new Scanner(test);
		String wholeTestFile = "";

		while(input.hasNext()){
			wholeTestFile += input.next() + "\n";
		}

		input.close();

		//get the word/tag couple
		String[] testCouples = ProcessingMethods.getCouples(wholeTestFile);
		int totalWords = testCouples.length;

		String[] wordsInTest = new String[totalWords];

		for(int i = 0; i < totalWords; i++){
			wordsInTest[i] = testCouples[i].split("/")[0];
		}

		//the array of all the tag-word emission array with the laplace probability 
		JsonArray tagWordArray  = JSONMethods.loadJSONFile("laplace-emissions.json").getJsonArray("Laplace Emissions");
		int tagWordArraySize = tagWordArray.size();

		//the array of all the word and their corresponding tags
		JsonArray wordAndTagArray = JSONMethods.loadJSONFile("word-tag.json").getJsonArray("Word And Tag");
		int size = wordAndTagArray.size();

		for(int j = 0; j < size; j++){
			String word = (String) wordAndTagArray.getJsonObject(j).keySet().toArray()[0];
			String tag = ((JsonString) wordAndTagArray.getJsonObject(j).get(word)).toString();
			wordAndTagMap.put(word.toLowerCase(), tag);
		}


		File predicted_tags = new File("predicted_tags.txt");
		PrintWriter myScanner = new PrintWriter(predicted_tags);
		String toWrite = "";

		//keep track of the tags that we have a;ready assigned. Useful for lower-case and upper-case tag assigning
		TreeMap<String, String> wordsAndPredictedTags = new TreeMap<String, String>();

		String previousTag = "";
		for(int i = 0; i < totalWords; i++){
			String currentWord = wordsInTest[i];

			//list of tags associated with the words
			ArrayList<String> tagsWithCurrentWord = new ArrayList<String>();

			TreeMap<Double,String> tagAndWordProbability = new TreeMap<Double,String>();


			for(int a = 0; a < tagWordArraySize; a++){

				String couple = (String) tagWordArray.getJsonObject(a).keySet().toArray()[0];
				String word = couple.split("[+]")[1];

				if(word.equals(currentWord)){
					//add the tag to the list
					String tag = couple.split("[+]")[0];
					tagsWithCurrentWord.add(tag);
					JsonNumber probability = (JsonNumber) tagWordArray.getJsonObject(a).get(couple);
					tagAndWordProbability.put(probability.doubleValue(), couple);
				}
			}
			//System.out.println(tagAndWordProbability);
			if(tagAndWordProbability.isEmpty()){
				String tag = TaggerMethods.handleNewWord(currentWord,previousTag,wordAndTagMap,wordsAndPredictedTags);
				wordsAndPredictedTags.put(currentWord, tag);
				toWrite += tag + ":	" + currentWord + "\n";
			}
			else{
				String couple = tagAndWordProbability.get(tagAndWordProbability.lastKey());
				String tag = couple.split("[+]")[0];
				String word = couple.split("[+]")[1];
				toWrite += couple + "\n";
				//put the word and it's predicted tag into the map
				wordsAndPredictedTags.put(word.toLowerCase(), tag);
				//assign tag to previous tag
				previousTag = tag;
			}

		}
		myScanner.print(toWrite);
		myScanner.close();


	}


}





