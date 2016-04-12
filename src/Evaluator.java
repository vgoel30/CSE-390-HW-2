import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import javax.json.JsonArray;

public class Evaluator {

	public static void main(String[] args) throws IOException {
		//Hash Map to keep track of the actual tags in the text file
		TreeMap<String, Double> actualTagsCountMap = new TreeMap<String, Double>();
		HashSet<String> words = new HashSet<String>();

		//the actual and predicted word/tag pair for the test file
		TreeMap<String, String> actualWordTagMap = new TreeMap<String, String>();
		TreeMap<String, String> predictedWordTagMap = new TreeMap<String, String>();

		//Hash Map with the predicted tags and their respective counts
		TreeMap<String, Double> predictedTagsCountMap = new TreeMap<String, Double>();

		//Hash Map with the tags' precision value
		TreeMap<String, Double> tagPrecisionMap = new TreeMap<String, Double>();
		TreeMap<String, Double> tagRecallMap = new TreeMap<String, Double>();
		TreeMap<String, Double> tagF1Map = new TreeMap<String, Double>();

		//Hash Map with the tags' correct predictions value
		TreeMap<String, Double> correctTagPredictionsMap = new TreeMap<String, Double>();

		HashSet<String> confusionSet = new HashSet<String>();
		//the confusion matrix as a map
		TreeMap<String,Double> confusionMatrix = new TreeMap<String, Double>();

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

		char choice = 'K';
		Scanner s = new Scanner(System.in);


		System.out.println("Which method's estimates do you want (input A or B)?\nA)Frequency\nB)HMM");
		choice = s.next().charAt(0);	
		s.close();

		JsonArray predictedTagsCountArray;
		int predictedTagsCountArraySize = 0;

		//now load the predicted tags json file
		if(choice == 'A'){
			predictedTagsCountArray  = JSONMethods.loadJSONFile("predicted-tags.json").getJsonArray("Predicted Tags");
			predictedTagsCountArraySize = predictedTagsCountArray.size();
		}
		else{
			predictedTagsCountArray  = JSONMethods.loadJSONFile("predicted-tags-hmm.json").getJsonArray("Predicted Tags");
			predictedTagsCountArraySize = predictedTagsCountArray.size();
		}

		for(int j = 0; j < predictedTagsCountArraySize; j++){
			String word = (String) predictedTagsCountArray.getJsonObject(j).keySet().toArray()[0];
			String tag = predictedTagsCountArray.getJsonObject(j).get(word).toString().trim().replace("\"", "");
			predictedWordTagMap.put(word, tag);
			if(!words.contains(word)){
				ProcessingMethods.putStringInHashMap(tag, predictedTagsCountMap);
				words.add(word);
			}	

		}
		//remove unnecessary tags
		predictedTagsCountMap.remove("McGraw-Hill");
		predictedTagsCountMap.remove("2");

		//getting the top 10 tags for the confusion matrix
		ValueComparator bvc = new ValueComparator(actualTagsCountMap);
		TreeMap<String, Double> mostFrequentTagsMap = new TreeMap<String, Double>(bvc);
		mostFrequentTagsMap.putAll(actualTagsCountMap);

		//the set of keys in the sorted bigrams name hashmap
		Set<String> keySet = mostFrequentTagsMap.keySet();
		//the iterator for the set of keys
		Iterator<String> iterator = keySet.iterator();

		String[] mostFrequentTags = new String[10];

		int i = 0;
		while(iterator.hasNext() && i < 10){
			String key = (String) iterator.next(); //get the key
			mostFrequentTags[i] = key;
			i++;
		}

		//Initializing the confusion matrix
		for(int a = 0; a < 10; a++){
			for(int b = 0; b < 10; b++){
				String confusionMatrixTag = mostFrequentTags[a] + "/" + mostFrequentTags[b];
				confusionSet.add(confusionMatrixTag);
				confusionMatrix.put(confusionMatrixTag, 0.0);
			}
		}

		double correctPredictions = 0;

		//comparing each tag in the actual word/tag map with the predicted to check inaccuracies
		for(String key: actualWordTagMap.keySet()){
			String actualTag = actualWordTagMap.get(key);
			String predictedTag = predictedWordTagMap.get(key);

			String confusionMatrixTag = actualTag + "/" + predictedTag;
			//if the tags are the same, the prediction was correct (hurrah!), put into the map of correct predictions
			if(actualTag.equals(predictedTag)){
				correctPredictions++;
				ProcessingMethods.putStringInHashMap(actualTag, correctTagPredictionsMap);
				//if the tags are from the most frequent tag set, update the confusion matrix
				if(confusionSet.contains(actualTag + "/" + predictedTag))
					ProcessingMethods.putStringInHashMap(confusionMatrixTag, confusionMatrix);
			}
			//  fill the entry of the confusion matrix
			else if(confusionSet.contains(actualTag + "/" + predictedTag)){
				ProcessingMethods.putStringInHashMap(confusionMatrixTag, confusionMatrix);
			}
		}
		double accuracyPercentage = (correctPredictions/actualWordTagMap.size())*100;
		System.out.println("ACCURACY: " + accuracyPercentage + "%\n" );

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



		//printing out the confusion matrix
		Set<String> classNames = new HashSet<String>();
		for(String key : confusionMatrix.keySet()) {
			String[] classes = key.split("/");
			if(classes != null && classes.length > 0) {
				classNames.addAll(Arrays.asList(classes));
			}
		}
		List<String> sortedClassNames = new ArrayList<String>();
		sortedClassNames.addAll(classNames);
		Collections.sort(sortedClassNames);

		System.out.print("p/a");
		for(String predictedClassName : sortedClassNames) {
			System.out.print("\t" + predictedClassName);
		}
		System.out.println();

		for(String actualClassName : sortedClassNames) {
			System.out.print(actualClassName);
			for(String predictedClassName : sortedClassNames) {
				Double value = confusionMatrix.get(actualClassName + "/" + predictedClassName);
				System.out.print("\t");
				if(value != null) {
					System.out.print(value.intValue());
				}
			}
			System.out.println();
		}

	}

}
