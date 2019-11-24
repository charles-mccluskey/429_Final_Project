package Manual_Control;

import mutantdetection.CompilingClassLoader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

class Tester {

	//the number of mutants that have been killed, should only be updated with incMutantsKilled()
	private static int mutantsKilled = 0;

	private static synchronized void incMutantsKilled() {
		mutantsKilled++;
	}

	static void testSUT(String sutDirectory, String mutantDirectory) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, IOException {
	
		//to test here: compile a mutant and run a test on the mutated method.
		//Get mutant files and their names
		File mutantFolder = new File(System.getProperty("user.dir")+"/src/Mutants");
		String[] mutantClassFiles = mutantFolder.list();
		if (mutantClassFiles == null) return;
		String[] mutantClassNames = new String[mutantClassFiles.length];

		//retrieve the name of the SUT
		File sutFolder = new File(System.getProperty("user.dir")+"/src/SUT");
		String[] x = sutFolder.list();
		String sutName = x[0].substring(0,x[0].lastIndexOf('.'));

		for(int i=0;i<mutantClassFiles.length;i++) {
			mutantClassNames[i] = mutantClassFiles[i].substring(0,mutantClassFiles[i].lastIndexOf('.'));
		}

		//retrieve a string array list representing the order of methods in our simulation file
		//retrieve the sim file inputs and put into 2d array list
		List<String> simMethodNames = new ArrayList<>();
		List<List<String>> simMethodArguments = new ArrayList<>();
        try (BufferedReader simReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/Manual_Control/Simulation.txt"))) {
            String method = simReader.readLine();
            int counter = 0;
            while (method != null) {
                String[] parsedLine = method.split(",");
                simMethodNames.add(parsedLine[0]);
                simMethodArguments.add(new ArrayList<>());
                for (int i = 1; i < parsedLine.length; i++) {
                    simMethodArguments.get(counter).add(parsedLine[i].trim());
                }
                method = simReader.readLine();
                counter++;
            }
        }

        //now to run the SUT and note the results
        CompilingClassLoader sccl = new CompilingClassLoader(sutDirectory);
        List<Object> sutReturnValues = runSimulation("SUT."+sutName, simMethodNames, simMethodArguments, sccl);

        /*Run test simulation on vectors*/

        StringBuffer resultsFileBuffer = new StringBuffer();//buffer for writing to the results.txt file later
		CompilingClassLoader mccl = new CompilingClassLoader(mutantDirectory);//classloader for the mutant files

        //Create a separate thread for each mutant and run the test
        List<Thread> testRunners = new ArrayList<>();//container for joining threads later
        for (String mutantClassName : mutantClassNames) {
		    Thread runner = new Thread(() -> {
                try {
                    List<Object> mutantReturnValues = runSimulation("Mutants."+mutantClassName,
                            simMethodNames, simMethodArguments, mccl);
                    //check if mutant is killed by this test
                    if(mutantReturnValues.equals(sutReturnValues)) {
                        resultsFileBuffer.append(mutantClassName).append(" survived the test").append('\n');
                    } else {
                        incMutantsKilled();
                    }
                } catch (Exception e) {e.printStackTrace();}
            });
		    testRunners.add(runner);
            runner.start();
        }
		//wait for all mutants to finish running the simulation
		testRunners.forEach(runner -> {
            try {
                runner.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //write final score at end of file
        resultsFileBuffer.append("Score: ").append(mutantsKilled / mutantClassFiles.length);

        //output results
        File resultsFile = new File(System.getProperty("user.dir") + "/src/Manual_Control/results.txt");
        try (OutputStream resultsOut = Files.newOutputStream(resultsFile.toPath())) {
            resultsOut.write(resultsFileBuffer.toString().getBytes());
        }
	}

    private static List<Object> runSimulation(String simClassName, List<String> simMethodNames,
                                              List<List<String>> simMethodArguments, ClassLoader simClassLoader) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class<?> sut = simClassLoader.loadClass(simClassName);
        List<Method> sutMethods = retrieveMethods(sut,simMethodNames);

        List<Object> returnValues = new ArrayList<>();//holds the return values for each method invocation
        //invoke each method
        for(int i = 0; i < sutMethods.size(); i++) {
            //get method parameters
            Class<?>[] parameterTypes = sutMethods.get(i).getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            //convert each String argument to its proper type
            for(int j = 0; j < simMethodArguments.get(i).size(); j++) {
                args[j] = casting(parameterTypes[j], simMethodArguments.get(i).get(j));
            }
            //get the return value
            returnValues.add(sutMethods.get(i).invoke(sut.getConstructor().newInstance(), args));
        }
        return returnValues;
    }

    //example input: double, 2.0
	@SuppressWarnings("unchecked")
	private static Object casting(Class clas, String val) throws IllegalArgumentException {
		if(clas.isAssignableFrom(double.class)) {
		    return Double.parseDouble(val);
		}else if(clas.isAssignableFrom(int.class)) {
			return Integer.parseInt(val);
		}else if(clas.isAssignableFrom(float.class)) {
			return Float.parseFloat(val);
		}else if(clas.isAssignableFrom(long.class)) {
			return Long.parseLong(val);
		}else if(clas.isAssignableFrom(short.class)) {
			return Short.parseShort(val);
		}else {
			throw new IllegalArgumentException("Unsupported inputs.");
		}
	}
	
	private static List<Method> retrieveMethods(Class<?> clas, List<String> simMethods) {
		//retrieve the class's methods into an unsorted array list. For some reason, these come out randomized...
		Method[] allMethods = clas.getDeclaredMethods();
		ArrayList<Method> unsortedMethods = new ArrayList<Method>();
		for (Method method : allMethods) {
			System.out.println(method.getName());
			unsortedMethods.add(method);
		}
		//now sort them into a different arraylist
		ArrayList<Method> methods = new ArrayList<Method>();
		for (String simMethod : simMethods) {
			for (int j = 0; j < unsortedMethods.size(); j++) {
				if (unsortedMethods.get(j).getName().contentEquals(simMethod)) {
					methods.add(unsortedMethods.get(j));
				}
			}
		}
		//Method objects are now sorted according to the simulation file
		return methods;
	}
	
}
