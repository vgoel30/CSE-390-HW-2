import java.util.HashMap;


public class Estimations {

	//the MLE transition probability
	public static double transitionMLE(String firstTag, String secondTag, HashMap<String, Double> tagsFrequency, HashMap<String, Double> tagCouple){
		if(tagsFrequency.get(firstTag) != null && tagCouple.get(firstTag + "+" + secondTag) != null)
			return 	tagCouple.get(firstTag + "+" + secondTag)/tagsFrequency.get(firstTag);
		else
			return 0;
	}

	public static double transitionMLE(String key, HashMap<String, Double> tagsFrequency, HashMap<String, Double> tagCouple ){
		String firstTag = key.split("[+]")[0];
		String secondTag = key.split("[+]")[1];
		return transitionMLE(firstTag, secondTag, tagsFrequency, tagCouple);
	}

	//the MLE emissions probability
	public static double emissionMLE(String tag, String word, HashMap<String, Double> tagsFrequency, HashMap<String, Double> tagWordCouple){
		if(tagsFrequency.get(tag) != null && tagWordCouple.get(tag + "+" + word) != null)
			return 	tagWordCouple.get(tag + "+" + word)/tagsFrequency.get(tag);
		else
			return 0;
	}

	public static double emissionMLE(String key, HashMap<String, Double> tagsFrequency, HashMap<String, Double> tagWordCouple ){
		String tag = key.split("[+]")[0];
		String word = key.split("[+]")[1];
		return emissionMLE(tag, word, tagsFrequency, tagWordCouple);
	}

	//the laplace transition probability
	public static double transitionLaplace(String firstTag, String secondTag, HashMap<String, Double> tagsFrequency, HashMap<String, Double> tagCouple){
		if(tagsFrequency.get(firstTag) != null && tagCouple.get(firstTag + "+" + secondTag) != null)
			return 	(tagCouple.get(firstTag + "+" + secondTag) + 1)/(tagsFrequency.get(firstTag) + tagsFrequency.size()+1);
		else{
			if(tagsFrequency.get(firstTag) != null)
				return 1/(tagsFrequency.get(firstTag) + tagCouple.size()+1);
			else
				return 1/(tagCouple.size()+1);
		}

	}

	public static double transitionLaplace(String key, HashMap<String, Double> tagsFrequency, HashMap<String, Double> tagCouple ){
		String firstTag = key.split("[+]")[0];
		String secondTag = key.split("[+]")[1];
		return transitionLaplace(firstTag, secondTag, tagsFrequency, tagCouple);
	}

	//the laplace emissions probability
	public static double emissionLaplace(String tag, String word, HashMap<String, Double> tagsFrequency, HashMap<String, Double> wordTagCouple){
		if(tagsFrequency.get(tag) != null && wordTagCouple.get(tag + "+" + word) != null)
			return 	(wordTagCouple.get(tag + "+" + word) + 1)/(tagsFrequency.get(tag) + wordTagCouple.size()+1); 
		else{
			if(tagsFrequency.get(tag) != null)
				return 1/(tagsFrequency.get(tag) + wordTagCouple.size()+2);
			else
				return 1/(wordTagCouple.size()+2);
		}
	}

	public static double emissionLaplace(String key, HashMap<String, Double> tagsFrequency, HashMap<String, Double> tagWordCouple ){
		String tag = key.split("[+]")[0];
		String word = key.split("[+]")[1];
		return emissionLaplace(tag, word, tagsFrequency, tagWordCouple);
	}

	//the laplace (smoothed) probability of a tag
	public static double tagUnigramLaplace(String tag,HashMap<String, Double> tagsFrequency, int totalSize){
		if(tagsFrequency.get(tag) != null)
			return (tagsFrequency.get(tag) + 1)/totalSize;
		else
			return 1/totalSize;
	}

}
