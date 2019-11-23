package Manual_Control;
import SUT.*;
import MutantGenerator.*;
import java.io.*;
import Mutants.*;

public class Controller {

	public static void main(String[] args) {
		
		String directory = System.getProperty("user.dir")+"/src/SUT/";
		MutantGenerator generator = new MutantGenerator();
		generator.GenerateMutants(directory);
		
		//At this point, a list of mutants have been generated and places in the Mutants package.
		//It is now time to run tests on the mutants as well as the original SUT
		
		/*
		File folder = new File(System.getProperty("user.dir")+"/src/SUT");
		String[] listOfFiles = folder.list();
		File[] otherList = folder.listFiles();
		System.out.println(otherList[1].getName());
		for(int i=0;i<listOfFiles.length;i++) {
			System.out.println(listOfFiles[i].contains(".java"));
			
		}*/
		
	}

}
