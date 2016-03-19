import java.util.TreeMap;


public class TaggerMethods {

	//	//list of states 
	//	public static HashSet<String> states = new HashSet<String>();
	//	static{
	//		states.add("Alabama");
	//		states.add("Alaska");
	//		states.add("Arizona");
	//		states.add("California");
	//		states.add("Colorado");
	//		states.add("Connecticut");
	//		states.add("Delaware");
	//		states.add("Florida");
	//		states.add("Georgia");
	//		states.add("Guam");
	//		states.add("Hawaii");
	//		states.add("Idaho");
	//		states.add("Illinois");
	//		states.add("Indiana");
	//		states.add("Iowa");
	//		states.add("Kansas");
	//		states.add("Kentucky");
	//		states.add("Louisiana");
	//		states.add("Maine");
	//		states.add("Maryland");
	//		states.add("Massachusetts");
	//		states.add("Michigan");
	//		states.add("Minnesota");
	//		states.add("Mississippi");
	//		states.add("Missouri");
	//		states.add("Montana");
	//		states.add("Nebraska");
	//		states.add("Nevada");
	//		states.add("Ohio");
	//		states.add("Oklahoma");
	//		states.add("Ontario");
	//		states.add("Oregon");
	//		states.add("Pennsylvania");
	//		states.add("Tennessee");
	//		states.add("Texas");
	//		states.add("Utah");
	//		states.add("Vermont");
	//		states.add("Virginia");
	//		states.add("Washington");
	//		states.add("Wisconsin");
	//		states.add("Wyoming");
	//	}
	//
	//	public static HashSet<String> numberUnits = new HashSet<String>();
	//	static{
	//		numberUnits.add("million");
	//		numberUnits.add("billion");
	//		numberUnits.add("hundred");
	//	}

	public static String handleNewWord(String word, String previousTag, TreeMap<String, String> wordsAndTags, TreeMap<String, String> wordsAndPredictedTags){
		if(wordsAndPredictedTags.containsKey(word))
			return wordsAndPredictedTags.get(word);

		if(wordsAndTags.containsKey(word.toLowerCase()))
			return wordsAndTags.get(word.toLowerCase());

		int length = word.length();

		if(word.charAt(0) >= 65 && word.charAt(0) <= 90 ){
			if(word.charAt(length-1) == 115)
				return "NNPS";
			else
				return "NNP";
		}

		if(word.contains("*"))
			return "-NONE-";

		//for number related
		if(word.matches(".*[\\d]+.*")){

			if(word.matches("[\\d]+[\\W][\\d]+")||word.matches("[\\d]+"))
				return "CD";
			else
				return "JJ";
		}

		if(word.substring(length-2,length).toLowerCase().equals("ed")){
			if(previousTag.equals("VBD")||previousTag.equals("VBZ")||previousTag.equals("VBN")||previousTag.equals("VB")||previousTag.equals("VBG")||previousTag.equals("VBP")){
				return "JJ";
			}
			else
				return "VBN";
		}

		if(length >= 2){
			if(word.substring(length-2,length).toLowerCase().equals("ly"))
				return "RB";
			else if(word.substring(length-2,length).toLowerCase().equals("th"))
				return "JJ";
		}

		if(length >= 3){
			if(word.substring(length-3,length).toLowerCase().equals("ing")){
				return "VBG";
			}}


		if(word.charAt(length-1) == 115)
			return "NNS";

		if(previousTag.equals("VBD")||previousTag.equals("VBZ")||previousTag.equals("VBN")||previousTag.equals("VB")||previousTag.equals("VBG")||previousTag.equals("VBP")){
			return "RB";
		}

		if(word.equalsIgnoreCase("familiar"))
			return "JJ";


		if(previousTag.equals("TO")||word.equalsIgnoreCase("favor")){
			return "VB";
		}

		if(word.contains("continue")||word.contains("oppose")||word.contains("plant")||word.contains("stop")||word.contains("involve")
				||word.contains("dispute")||word.contains("debate")||word.contains("courage")||word.contains("cause")
				||word.contains("prove")||word.equalsIgnoreCase("saw")||word.contains("leap")||word.contains("swap")
				||word.contains("los")||word.contains("cry")||word.contains("stem")||
				word.contains("wish")||word.contains("sen")||word.contains("mail")||word.contains("hire")
				||word.contains("spark")||word.contains("notice")||word.contains("submit")||word.contains("punish")
				||word.substring(length-2, length).equals("te")||word.substring(length-2, length).equals("ze"))
			return "VB";

		return "NN";
	}

}
