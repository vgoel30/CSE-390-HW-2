import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;


public class play {

	public static void main(String[] args) throws FileNotFoundException {
//		String toTest = "69-scale";
//		System.out.println(toTest.matches("[\\d]+[\\W][\\d]+"));
//		//System.out.println(toTest.contains("*"));
//		
//		String testing = "P/P";
//		System.out.println(testing.replace("/", ""));
		
		File train = new File("test.txt");
		Scanner input = new Scanner(train);
		String wholeFile = "";
		while(input.hasNextLine()){
			wholeFile += input.nextLine() + "\n";
		}
		input.close();
		//System.out.println(wholeFile.replaceAll("\n", "\n<s>/<s> "));
		
		PrintWriter writer = new PrintWriter(train);
		writer.write(wholeFile.replaceAll("\n", "\n<s>/<s> "));
	}

}
