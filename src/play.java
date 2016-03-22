
public class play {

	public static void main(String[] args) {
		String toTest = "69-scale";
		System.out.println(toTest.matches("[\\d]+[\\W][\\d]+"));
		//System.out.println(toTest.contains("*"));
		
		String testing = "P/P";
		System.out.println(testing.replace("/", ""));
	}

}
