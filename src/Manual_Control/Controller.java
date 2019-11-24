package Manual_Control;

import MutantGenerator.MutantGenerator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class Controller{

	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, ClassNotFoundException, InstantiationException, IOException, IllegalAccessException {
		
		String sutDirectory = System.getProperty("user.dir")+"/src/SUT";
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
	}

}
