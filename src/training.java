import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
public class training {

	public static void main(String[] args) throws FileNotFoundException{
		//Hash Map with all the words and their respective frequency
		HashMap<String, Double> wordsFrequency = new HashMap<String, Double>();

		//Hash Map with all the tags and their respective frequency
		HashMap<String, Double> tagsFrequency = new HashMap<String, Double>();

		//Hash Map with all the neighboring tag couples and their respective frequency
		HashMap<String, Double> tagCouple = new HashMap<String, Double>();

		//Hash Map with all the word/tag couples and their respective frequency
		HashMap<String, Double> tagWordCouple = new HashMap<String, Double>();

		//Hash Map with all the word/tag couples and their respective frequency
		HashMap<String, String> wordAndTag = new HashMap<String, String>();

		//Hash Map with all the word/tag couples and their respective frequency
		HashMap<String, Integer> tagsInTraining = new HashMap<String, Integer>();

		File train = new File("train.txt");

		Scanner input = new Scanner(train);
		String wholeFile = "";

		while(input.hasNext()){
			wholeFile += input.next() + "\n";
		}
		input.close();
		
		
		//get the word/tag couple
		String[] couples = ProcessingMethods.getCouples(wholeFile);

		int length = couples.length;
		
		System.out.println(couples[0]);

		//going over the text file and generating the required hashmaps
		for(int i = 0; i < length; i++){
			String couple = couples[i].trim();
			String word = couple.split("/")[0];
			String tag = couple.split("/")[1];

			wordAndTag.put(word, tag);
			
			//generating the hash-map for all the tags in test with an index for the HMM
			if(!tag.equals("2")&&!tag.equals("McGraw-Hill")&&!tag.equals("winter")){
				tagsInTraining.putIfAbsent(tag, tagsInTraining.size() + 1);
			}

			if(i >= 1){
				String previousCouple = couples[i-1].trim();
				String previousTag = previousCouple.split("/")[1];

				ProcessingMethods.putStringInHashMap(previousTag + "+" + tag,tagCouple);
				ProcessingMethods.putStringInHashMap(tag + "+" + word, tagWordCouple);
			}
			ProcessingMethods.putStringInHashMap(word, wordsFrequency);
			ProcessingMethods.putStringInHashMap(tag, tagsFrequency);
		
		}
		
		//System.out.println(wordAndTag.get("<s>"));
		
		tagsFrequency.remove("2");
		tagsFrequency.remove("McGraw-Hill");
		tagsFrequency.remove("winter");
		


		StringWriter sw = new StringWriter();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = writerFactory.createWriter(sw);

		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		JSONMethods.generateTransitionMLEJsonArray(tagCouple, arrayBuilder, tagsFrequency, tagCouple);
		JsonArray transitionMLE = arrayBuilder.build();

		// THEN PUT IT ALL TOGETHER IN A JsonObject
		JsonObject dataManagerJSO = Json.createObjectBuilder()
				.add("MLE Transitions", transitionMLE)
				.build();

		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		OutputStream os = new FileOutputStream("mle-transitions.json");
		JsonWriter jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		String prettyPrinted = sw.toString();
		PrintWriter pw = new PrintWriter("mle-transitions.json");
		pw.write(prettyPrinted);
		pw.close();

		//BUILDING THE LAPLACE TRANSITIONS 
		sw = new StringWriter();
		writerFactory = Json.createWriterFactory(properties);
		jsonWriter = writerFactory.createWriter(sw);

		arrayBuilder = Json.createArrayBuilder();
		JSONMethods.generateTransitionLaplaceJsonArray(tagCouple, arrayBuilder, tagsFrequency, tagCouple);
		JsonArray transitionLaplace = arrayBuilder.build();
		dataManagerJSO = Json.createObjectBuilder()
				.add("Laplace Transitions", transitionLaplace)
				.build();
		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		os = new FileOutputStream("laplace-transitions.json");
		jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		prettyPrinted = sw.toString();
		pw = new PrintWriter("laplace-transitions.json");
		pw.write(prettyPrinted);
		pw.close();

		//BUILDING THE WORD AND TAG JSON FILE
		sw = new StringWriter();
		writerFactory = Json.createWriterFactory(properties);
		jsonWriter = writerFactory.createWriter(sw);
		arrayBuilder = Json.createArrayBuilder();

		JSONMethods.generateWordTagJsonArray(wordAndTag, arrayBuilder);
		JsonArray wordAndTagJsonArray = arrayBuilder.build();
		dataManagerJSO = Json.createObjectBuilder()
				.add("Word And Tag", wordAndTagJsonArray)
				.build();
		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		os = new FileOutputStream("word-tag.json");
		jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		prettyPrinted = sw.toString();
		pw = new PrintWriter("word-tag.json");
		pw.write(prettyPrinted);
		pw.close();




		//BUILDING THE MLE EMISSIONS
		sw = new StringWriter();
		writerFactory = Json.createWriterFactory(properties);
		jsonWriter = writerFactory.createWriter(sw);

		arrayBuilder = Json.createArrayBuilder();
		JSONMethods.generateEmissionMLEJsonArray(tagWordCouple, arrayBuilder, tagsFrequency, tagWordCouple);
		JsonArray emissionMLE = arrayBuilder.build();
		dataManagerJSO = Json.createObjectBuilder()
				.add("MLE Emissions", emissionMLE)
				.build();
		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		os = new FileOutputStream("mle-emissions.json");
		jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		prettyPrinted = sw.toString();
		pw = new PrintWriter("mle-emissions.json");
		pw.write(prettyPrinted);
		pw.close();

		//BUILDING THE LAPLACE EMISSIONS
		sw = new StringWriter();
		writerFactory = Json.createWriterFactory(properties);
		jsonWriter = writerFactory.createWriter(sw);

		arrayBuilder = Json.createArrayBuilder();
		JSONMethods.generateEmissionMLEJsonArray(tagWordCouple, arrayBuilder, tagsFrequency, tagWordCouple);
		JsonArray emissionLaplace = arrayBuilder.build();
		dataManagerJSO = Json.createObjectBuilder()
				.add("Laplace Emissions", emissionLaplace)
				.build();
		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		os = new FileOutputStream("laplace-emissions.json");
		jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		prettyPrinted = sw.toString();
		pw = new PrintWriter("laplace-emissions.json");
		pw.write(prettyPrinted);
		pw.close();

		//BUILDING THE UNIGRAM TAG LAPLACE
		sw = new StringWriter();
		writerFactory = Json.createWriterFactory(properties);
		jsonWriter = writerFactory.createWriter(sw);

		arrayBuilder = Json.createArrayBuilder();
		JSONMethods.generateUnigramLaplaceArray( tagsFrequency, arrayBuilder, length);
		JsonArray unigramTagLaplace = arrayBuilder.build();
		dataManagerJSO = Json.createObjectBuilder()
				.add("Tag Laplace Emissions", unigramTagLaplace)
				.build();
		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		os = new FileOutputStream("laplace-tag-unigrams.json");
		jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		prettyPrinted = sw.toString();
		pw = new PrintWriter("laplace-tag-unigrams.json");
		pw.write(prettyPrinted);
		pw.close();
		
		//BUILDING THE TAG FREQUENCY JSON ARRAY
		sw = new StringWriter();
		writerFactory = Json.createWriterFactory(properties);
		jsonWriter = writerFactory.createWriter(sw);

		arrayBuilder = Json.createArrayBuilder();
		JSONMethods.generateTagsArray(tagsInTraining, arrayBuilder);
		JsonArray tagsArray = arrayBuilder.build();
		dataManagerJSO = Json.createObjectBuilder()
				.add("Tags", tagsArray)
				.build();
		// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
		jsonWriter.writeObject(dataManagerJSO);
		jsonWriter.close();
		// INIT THE WRITER
		os = new FileOutputStream("tags.json");
		jsonFileWriter = Json.createWriter(os);
		jsonFileWriter.writeObject(dataManagerJSO);
		prettyPrinted = sw.toString();
		pw = new PrintWriter("tags.json");
		pw.write(prettyPrinted);
		pw.close();
	}
}