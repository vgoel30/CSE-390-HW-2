import java.io.IOException;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonNumber;


public class HMMTagger {

	public static void main(String[] args) throws IOException {
		//all the tags in training
		HashMap<String,Integer> tagsMap = new HashMap<String,Integer>();
		
		JsonArray tagsJsonArray  = JSONMethods.loadJSONFile("tags.json").getJsonArray("Tags");
		int totalTags = tagsJsonArray.size();
		
		for(int j = 0; j < totalTags; j++){
			String tag = (String) tagsJsonArray.getJsonObject(j).keySet().toArray()[0];
			JsonNumber value = (JsonNumber) tagsJsonArray.getJsonObject(j).get(tag);
			tagsMap.put(tag,value.intValue());
		}
		//the hash map has all the tags and a corresponding index for the viterbi algorithm's matrices
		
		
		
	}

}
