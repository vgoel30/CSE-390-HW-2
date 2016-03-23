import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.TreeMap;


public class play {

	public static void main(String[] args) throws FileNotFoundException {
		//		String toTest = "69-scale";
		//		System.out.println(toTest.matches("[\\d]+[\\W][\\d]+"));
		//		//System.out.println(toTest.contains("*"));
		//		
		//		String testing = "P/P";
		//		System.out.println(testing.replace("/", ""));

		File train = new File("train.txt");
		Scanner input = new Scanner(train);
		String wholeFile = "";
		while(input.hasNextLine()){
			String text = input.nextLine();
			if(text.contains("\n"))
				System.out.println("HAWW");
			wholeFile += text + "\n";
		}
		input.close();
		//System.out.println(wholeFile.replaceAll("\n", "\n<s>/<s> "));
		//System.out.println(wholeFile);
		
		TreeMap<Double, String> lala = new TreeMap<Double, String>();
		lala.put(5.0, "a");
		lala.put(6.0, "b");
		System.out.println(lala.lastEntry());

	}

}
