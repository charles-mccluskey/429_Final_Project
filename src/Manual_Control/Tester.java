package Manual_Control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import mutantdetection.CompilingClassLoader;

public class Tester {
	
	public static void testSUT(String sutDirectory, String mutantDirectory) {
	
		//to test here: compile a mutant and run a test on the mutated method.
		try {
			//Get mutant files and their names
			File mutantFolder = new File(System.getProperty("user.dir")+"/src/Mutants");
			String[] files = mutantFolder.list();
			String[] mutants = new String[files.length];//NUMBER OF MUTANTS
			
			//retrieve the name of the SUT
			File sutFolder = new File(System.getProperty("user.dir")+"/src/SUT");
			String[] x = sutFolder.list();
			String sutName = x[0].substring(0,x[0].lastIndexOf("."));
			
			for(int i=0;i<files.length;i++) {
				mutants[i] = files[i].substring(0,files[i].lastIndexOf("."));
			}
			
			//prepare the output file to write down our testing results
			String outputFile = System.getProperty("user.dir")+"/src/Manual_Control/results.txt";
		    FileWriter fileWriter = new FileWriter(outputFile);//prep the output file
		    PrintWriter printWriter = new PrintWriter(fileWriter);
		    
		    //retrieve a string array list representing the order of methods in our simulation file
			//retrieve the sim file inputs and put into 2d array list
		    ArrayList<String> simMethods = new ArrayList<String>();
		    ArrayList<ArrayList<Object>> inputs = new ArrayList<ArrayList<Object>>();
			BufferedReader simReader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/Manual_Control/Simulation.txt"));
			String method = simReader.readLine();
			int counter=0;
			while(method!=null) {
				String[] line = method.split(",");
				simMethods.add(line[0]);
				inputs.add(new ArrayList<Object>());
				for(int i=1;i<line.length;i++) {
					inputs.get(counter).add(line[i].trim());
				}
				method=simReader.readLine();
				counter++;
			}
			simReader.close();
			
			//now to run the SUT and note the results
			ArrayList<String> results = new ArrayList<String>();
			int mutantsKilled=0;
			CompilingClassLoader sccl = new CompilingClassLoader(sutDirectory);
			Class<?> sut = sccl.loadClass("SUT."+sutName);
			ArrayList<Method> sutMethods = retrieveMethods(sut,simMethods);
			//iterate through methods
			for(int i=0;i<sutMethods.size();i++) {
				//get method parameter types
				Class<?>[] params = sutMethods.get(i).getParameterTypes();
				Object[] args = new Object[params.length];
				for(int j=0;j<inputs.get(i).size();j++) {
					args[j] = params[j].getConstructor(String.class).newInstance(inputs.get(i).get(j))//(inputs.get(i).get(j));
				}
				//we have the arguments, now we invoke the method
				System.out.println(sutMethods.get(i).invoke(sut.newInstance(), args));
			}
			
			
			//compile and run a single mutant file
			CompilingClassLoader mccl = new CompilingClassLoader(mutantDirectory);
			Class<?> mutant = mccl.loadClass("Mutants."+mutants[0]);//mutant compiled, we can now work with it
			//Method test = mutant.getMethod("AreaRectangle", double.class, double.class);
			//System.out.println(test.invoke(mutant.newInstance(), 4.0, 5.0));

			//retrieve mutant's methods
			ArrayList<Method> methods = retrieveMethods(mutant, simMethods);
			//Now to run methods and compare them against the SUT
			for(int i=0;i<methods.size();i++) {
			//	methods.get(i).invoke(mutant.newInstance(), );
			}
			
			
			
			printWriter.close();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static ArrayList<Method> retrieveMethods(Class<?> clas, ArrayList<String> simMethods) {
		//retrieve the class's methods into an unsorted array list. For some reason, these come out randomized...
		Method[] allMethods = clas.getMethods();
		ArrayList<Method> unsortedMethods = new ArrayList<Method>();
		for(int i=0;i<allMethods.length;i++) {
			if(allMethods[i].getName().contentEquals("wait")) {
				break;
			}
			unsortedMethods.add(allMethods[i]);
		}
		//now sort them into a different arraylist
		ArrayList<Method> methods = new ArrayList<Method>();
		for(int i=0;i<simMethods.size();i++) {
			for(int j=0;j<unsortedMethods.size();j++) {
				if(unsortedMethods.get(j).getName().contentEquals(simMethods.get(i))) {
					methods.add(unsortedMethods.get(j));
				}
			}
		}
		//Method objects are now sorted according to the simulation file
		return methods;
	}
	
}
