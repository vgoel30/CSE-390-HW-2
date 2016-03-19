import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;


public class JSONMethods {
	
	//loads the contents of a JSON file
	public static JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        //System.out.println(json);
        return json;
    }
	
	//make a json array for word and tag 
	public static void generateWordTagJsonArray(HashMap<String, String> map, JsonArrayBuilder arrayBuilder){
		for(String key: map.keySet()){
			JsonObject toAdd = makeWordTagObject(map, key);
			arrayBuilder.add(toAdd);
		}
	}
	
	public static JsonObject makeWordTagObject(HashMap<String, String> map, String key){
		JsonObject jso = Json.createObjectBuilder().add(key,map.get(key)).build();
		return jso;
	}

	//make a json array for mle transitions
	public static void generateTransitionMLEJsonArray(HashMap<String, Double> map, JsonArrayBuilder arrayBuilder, HashMap<String, Double> tagFrequency, HashMap<String, Double> tagCouples){
		for(String key : map.keySet()){
			JsonObject toAdd = makeTransitionMLEObject(map,key, tagFrequency, tagCouples);
			arrayBuilder.add(toAdd);
		}
	}

	public static JsonObject makeTransitionMLEObject(HashMap<String, Double> map, String key, HashMap<String, Double> tagFrequency, HashMap<String, Double> tagCouples){
		JsonObject jso = Json.createObjectBuilder().add(key, Estimations.transitionMLE(key, tagFrequency, tagCouples)).build();
		return jso;
	}

	//make a json array for laplace transitions
	public static void generateTransitionLaplaceJsonArray(HashMap<String, Double> map, JsonArrayBuilder arrayBuilder, HashMap<String, Double> tagFrequency, HashMap<String, Double> tagCouples){
		for(String key : map.keySet()){
			JsonObject toAdd = makeTransitionLaplaceObject(map,key, tagFrequency, tagCouples);
			arrayBuilder.add(toAdd);
		}
	}

	public static JsonObject makeTransitionLaplaceObject(HashMap<String, Double> map, String key, HashMap<String, Double> tagFrequency, HashMap<String, Double> tagCouples){
		JsonObject jso = Json.createObjectBuilder().add(key, Estimations.transitionLaplace(key, tagFrequency, tagCouples)).build();
		return jso;
	}

	//make a json array for mle emissions
	public static void generateEmissionMLEJsonArray(HashMap<String, Double> map, JsonArrayBuilder arrayBuilder, HashMap<String, Double> tagFrequency, HashMap<String, Double> tagCouples){
		for(String key : map.keySet()){
			JsonObject toAdd = makeEmissionMLEObject(map,key, tagFrequency, map);
			arrayBuilder.add(toAdd);
		}
	}

	public static JsonObject makeEmissionMLEObject(HashMap<String, Double> map, String key, HashMap<String, Double> tagFrequency, HashMap<String, Double> tagWordCouple){
		JsonObject jso = Json.createObjectBuilder().add(key, Estimations.emissionMLE(key, tagFrequency, tagWordCouple)).build();
		return jso;
	}

	//make a json array for laplace emissions
	public static void generateEmissionLaplaceJsonArray(HashMap<String, Double> map, JsonArrayBuilder arrayBuilder, HashMap<String, Double> tagFrequency){
		for(String key : map.keySet()){
			JsonObject toAdd = makeEmissionMLEObject(map,key, tagFrequency, map);
			arrayBuilder.add(toAdd);
		}
	}

	public static JsonObject makeEmissionLaplaceObject(HashMap<String, Double> map, String key, HashMap<String, Double> tagFrequency, HashMap<String, Double> tagWordCouple){
		JsonObject jso = Json.createObjectBuilder().add(key, Estimations.emissionLaplace(key, tagFrequency, tagWordCouple)).build();
		return jso;
	}
	
	//make a json array for the unigram laplace probability of a tag
	public static void generateUnigramLaplaceArray(HashMap<String, Double> map,JsonArrayBuilder arrayBuilder,int totalSize){
		for(String tag: map.keySet()){
			JsonObject toAdd = makeUnigramLaplaceObject(tag,map,totalSize);
			arrayBuilder.add(toAdd);
		}
	}
	
	public static JsonObject makeUnigramLaplaceObject(String tag, HashMap<String, Double> map, int totalSize){
		JsonObject jso = Json.createObjectBuilder().add(tag, Estimations.tagUnigramLaplace(tag, map,totalSize)).build();
		return jso;
	}

}
