import java.util.Comparator;
import java.util.Map;

//implementing the comparator class to order the hashmap
public class ValueComparator implements Comparator<String> {
	Map<String,Double> base;

	public ValueComparator(Map<String,Double> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.

	public int compare(String a, String b) {
		// returning 0 would merge keys
		Double first = (Double)base.get(a);
		Double second = (Double)base.get(b);
		
		if(first.compareTo(second) == 0)
			return 1;
		else
			return -first.compareTo(second);		
	}
}