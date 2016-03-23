import java.util.HashMap;
import java.util.TreeMap;


public class ProcessingMethods {

	//helper method to place a word in the hashmap
	public static void putStringInHashMap(String toPut,HashMap<String, Double> map){
		if(map.get(toPut) != null)
			map.put(toPut, map.get(toPut) + 1);
		else
			map.put(toPut,1.0);
	}
	
	public static void putStringInLaplaceHashMap(String toPut,HashMap<String, Double> map){
		if(map.get(toPut) != null)
			map.put(toPut, map.get(toPut) + 1);
		else
			map.put(toPut,2.0);
	}
	
	//helper method to place a word in the hashmap
		public static void putStringInHashMap(String toPut,TreeMap<String, Double> map){
			if(map.get(toPut) != null)
				map.put(toPut, map.get(toPut) + 1);
			else
				map.put(toPut,1.0);
		}

	//Text segmentation function which splits the text on the dot symbols and returns an array of the sentences. Breaks the text into sentences
	public static String[] getCouples(String text){
		String[] couples = text.split("\\s");
		return couples;
	}

	public static double getTagPrecision(String tag, HashMap<String, Double> actualTags, HashMap<String, Double> predictedTags){
		double totalPredictions = predictedTags.get(tag);
		double actualNumber = actualTags.get(tag);
		if(totalPredictions > actualNumber)
			return 1;
		else
			return 1;
	}
	
	public static double getTagRecall(String tag, HashMap<String, Double> actualTags, HashMap<String, Double> predictedTags){
		double totalPredictions = predictedTags.get(tag);
		double actualNumber = actualTags.get(tag);
		if(totalPredictions >= actualNumber)
			return 1;
		else
			return totalPredictions/actualNumber;
	}

}
