package Manual_Control;

import MutantGenerator.MutantGenerator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;


public class Controller{

	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, ClassNotFoundException, InstantiationException, IOException, IllegalAccessException {
		
		String sutDirectory = System.getProperty("user.dir")+"/src/SUT";
		String mutantDirectory = System.getProperty("user.dir")+"/src/Mutants";
		File mutantFolder = new File(mutantDirectory);
		//create the directory if it doesn't exist already
		if (!mutantFolder.exists()) {
		    Files.createDirectory(mutantFolder.toPath());
		} else {
            //Before starting, make sure the mutant directory is EMPTY
            String[] mutantFileNames = mutantFolder.list();
            if(mutantFileNames != null && mutantFileNames.length != 0) {
                File[] mutantFiles = mutantFolder.listFiles();
                if (mutantFiles != null) {
                    for(File mutant: mutantFiles) {
                        Files.delete(mutant.toPath());
                    }
                }
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
