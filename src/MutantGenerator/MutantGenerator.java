package MutantGenerator;

import java.io.*;

public class MutantGenerator {
	
	static char[] operations = {'+','-','*','/'};
	static int[] mutantCounter= {0,0,0,0};
	
	public MutantGenerator() {
		
	}

	public static void GenerateMutantList(String input) {
		BufferedReader reader;
		//String input = System.getProperty("user.dir")+"/src/MutantGenerator/WorkingInput.txt";
		Boolean mutants=false;
		try{
			reader = new BufferedReader(new FileReader(input));//prep the input file
			String line = reader.readLine();
			String outputFile = System.getProperty("user.dir")+"/src/MutantGenerator/MutantList.txt";
		    FileWriter fileWriter = new FileWriter(outputFile);//prep the output file
		    PrintWriter printWriter = new PrintWriter(fileWriter);
			int counter=1;//intialize the line counter
			StringBuilder lineBuilder = new StringBuilder();
			while(line != null){//go through the lines
				lineBuilder.append("["+counter+"]"+" "+line.trim());
				for(int i=0;i<line.length();i++) {//scan the chars of the line for operations
					for(int o=0;o<operations.length;o++) {//cycle through the operations for each character
						if(line.charAt(i)==operations[o]) {//if the char is an operator
							if(line.charAt(i+1)!=operations[o]) {//if the operator isn't doubled up like ++ or --
								//Then we need to generate mutations
								mutants=true;
								StringBuilder mutationBuilder = new StringBuilder(line); //start with basic string
								for(int m=0;m<operations.length;m++) {//cycle through our ops
									if(line.charAt(i)!=operations[m]) {//if we have a different operation
										//generate a mutant line
										mutationBuilder.setCharAt(i, operations[m]);
										//append the output line with our mutant
										lineBuilder.append(" "+mutationBuilder.toString().trim());
										//augment mutation counter
										mutantCounter[m]++;
									}
								}
							}else {
								i++;
							}
						}
					}
				}
				//reached end of a line, so print built string to mutation list.
				if(mutants) {
					printWriter.println(lineBuilder.toString());
					mutants=false;
				}
				line = reader.readLine();//next line
				counter++;
				lineBuilder.delete(0, lineBuilder.length());//clear the line builder
			}

			for(int i=0;i<operations.length;i++) {
				printWriter.println("Number of "+operations[i]+" mutants: "+mutantCounter[i]);
			}
			printWriter.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}