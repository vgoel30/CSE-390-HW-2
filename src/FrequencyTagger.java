import java.io.File;
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

public class FrequencyTagger {

	public static void main(String[] args) throws IOException {
		//the word and tag pair obtained after parsing the JSON file
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

		System.out.println("Total words: " + totalWords);

		String[] wordsInTest = new String[totalWords];

		for(int i = 0; i < totalWords; i++){
			String word = testCouples[i].split("/")[0];
			wordsInTest[i] = word;
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

		//keep track of the tags that we have a;ready assigned. Useful for lower-case and upper-case tag assigning
		HashMap<String, String> wordsAndPredictedTags = new HashMap<String, String>();

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
				wordsAndPredictedTags.put(currentWord, tag.replace("\"", ""));
				//toWrite += tag + ":	" + currentWord + "\n";
			}
			else{
				String couple = tagAndWordProbability.get(tagAndWordProbability.lastKey());
				String tag = couple.split("[+]")[0];
				String word = couple.split("[+]")[1];
				//toWrite += couple + "\n";
				//put the word and it's predicted tag into the map
				wordsAndPredictedTags.put(word, tag.replace("\"", ""));
				//assign tag to previous tag
				previousTag = tag;
			}

		}
		StringWriter sw = new StringWriter();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = writerFactory.createWriter(sw);

		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		JSONMethods.generateWordTagJsonArray(wordsAndPredictedTags, arrayBuilder);
		JsonArray predictedTags = arrayBuilder.build();

		System.out.println(predictedTags.size());

		// THEN PUT IT ALL TOGETHER IN A JsonObject
		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add("Predicted Tags", predictedTags)
				.build();



		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		OutputStream os = new FileOutputStream("predicted-tags.json");
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter("predicted-tags.json");
		pw.write(prettyPrinted);
		pw.close();
	}
}