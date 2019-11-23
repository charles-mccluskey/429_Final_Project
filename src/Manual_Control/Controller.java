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
		
		String directory = System.getProperty("user.dir")+"/src/SUT/";
		MutantGenerator generator = new MutantGenerator();
		generator.GenerateMutants(directory);
		
		//At this point, a list of mutants has been generated and placed in the Mutants package.
		//It is now time to run tests on the mutants as well as the original SUT
		
		//to test here: compile a mutant and run a test on the mutated method.
		try {
			String file = System.getProperty("user.dir")+"/src/Mutants/Mutant1";
			CompilingClassLoader ccl = new CompilingClassLoader();
			Class mutant = ccl.loadClass("Mutant1");
			Method test = mutant.getMethod("AreaRectangle", double.class, double.class);
			System.out.println(test.invoke(mutant.newInstance(), 4.0, 5.0));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/ catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
