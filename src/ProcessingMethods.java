import java.util.HashMap;


public class ProcessingMethods {
	
	//helper method to place a word in the hashmap
		public static void putStringInHashMap(String toPut,HashMap<String, Double> map){
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

}
