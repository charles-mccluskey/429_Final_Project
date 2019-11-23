package Manual_Control;
import SUT.*;
import MutantGenerator.*;
import java.io.*;
import Mutants.*;
import java.lang.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Controller{

	public static void main(String[] args) throws InterruptedException {
		
		String sutDirectory = System.getProperty("user.dir")+"/src/SUT/";
		String mutantDirectory = System.getProperty("user.dir")+"/src/Mutants";
		File mutantFolder = new File(mutantDirectory);
		
		//Before starting, make sure the mutant directory is EMPTY
		if(mutantFolder.list().length!=0) {
			for(File mutant: mutantFolder.listFiles()) {
				mutant.delete();
			}
		}
		
		//Now generate a fresh set of mutants based on the current SUT
		MutantGenerator generator = new MutantGenerator();
		generator.GenerateMutants(sutDirectory);
		
		//At this point, a list of mutants has been generated and placed in the Mutants package.
		//It is now time to run tests on the mutants as well as the original SUT
		
		Tester.testSUT(sutDirectory,mutantDirectory);

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
