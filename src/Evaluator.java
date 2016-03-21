import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;

import javax.json.JsonArray;

public class Evaluator {

	public static void main(String[] args) throws IOException {
		//Hash Map to keep track of the actual tags in the text file
		TreeMap<String, Double> actualTagsCountMap = new TreeMap<String, Double>();
		HashSet<String> words = new HashSet<String>();


		TreeMap<String, String> actualWordTagMap = new TreeMap<String, String>();
		TreeMap<String, String> predictedWordTagMap = new TreeMap<String, String>();


		//Hash Map with the predicted tags
		TreeMap<String, Double> predictedTagsCountMap = new TreeMap<String, Double>();

		//Hash Map with the tags' precision value
		TreeMap<String, Double> tagPrecisionMap = new TreeMap<String, Double>();
		TreeMap<String, Double> tagRecallMap = new TreeMap<String, Double>();
		TreeMap<String, Double> tagF1Map = new TreeMap<String, Double>();

		//Hash Map with the tags' correct predictions value
		TreeMap<String, Double> correctTagPredictionsMap = new TreeMap<String, Double>();

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
		int totalWordsInTest = testCouples.length;

		for(int i = 0; i < totalWordsInTest; i++){
			String couple = testCouples[i].trim();
			String word = couple.split("/")[0];
			String tag = couple.split("/")[1];
			actualWordTagMap.put(word, tag);
			if(!words.contains(word)){
				ProcessingMethods.putStringInHashMap(tag, actualTagsCountMap);
				words.add(word);
			}

		}
		//remove unnecessary tags
		actualTagsCountMap.remove("Contra");
		actualTagsCountMap.remove("Firestone");
		actualTagsCountMap.remove("4");
		actualTagsCountMap.remove("8");
		actualTagsCountMap.remove("McGraw");
		actualTagsCountMap.remove("McGraw-Hill");

		words.removeAll(words);

		//now load the predicted tags json file
		JsonArray predictedTagsCountArray  = JSONMethods.loadJSONFile("predicted-tags.json").getJsonArray("Predicted Tags");
		int predictedTagsCountArraySize = predictedTagsCountArray.size();

		for(int j = 0; j < predictedTagsCountArraySize; j++){
			String word = (String) predictedTagsCountArray.getJsonObject(j).keySet().toArray()[0];
			String tag = predictedTagsCountArray.getJsonObject(j).get(word).toString().trim().replace("\"", "");
			predictedWordTagMap.put(word, tag);
			if(!words.contains(word)){
				ProcessingMethods.putStringInHashMap(tag, predictedTagsCountMap);
				words.add(word);
			}	

		}
		predictedTagsCountMap.remove("McGraw-Hill");
		predictedTagsCountMap.remove("2");

		//double correctPredictions = 0;		

		for(String key: actualWordTagMap.keySet()){
			String actualTag = actualWordTagMap.get(key);
			if(actualTag.equals(predictedWordTagMap.get(key))){
				ProcessingMethods.putStringInHashMap(actualTag, correctTagPredictionsMap);
			}
		}
		
		for(String tag: correctTagPredictionsMap.keySet()){
			double correctPredictionsForTag = correctTagPredictionsMap.get(tag);
			double totalPredictionsTag = predictedTagsCountMap.get(tag);
			double totalActualTag = actualTagsCountMap.get(tag);
			
			double precision = correctPredictionsForTag/totalPredictionsTag;
			double recall = correctPredictionsForTag/totalActualTag;
			double F1 = (2*precision*recall)/(precision + recall);
			
			tagPrecisionMap.put(tag, precision);
			tagRecallMap.put(tag, recall );
			tagF1Map.put(tag, F1);
		}
		
		System.out.println(tagPrecisionMap);
	}

}
