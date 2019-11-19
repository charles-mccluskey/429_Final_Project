package MutantGenerator;

import java.io.*;

public class MutantGenerator {
	
	static char[] operations = {'+','-','*','/'};
	static int[] mutantCounter= {0,0,0,0};
	
	public MutantGenerator() {
		
	}

	public static void GenerateMutants(String input) {
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
							if(line.charAt(i+1)!=operations[o] && !(line.charAt(i)=='/'&&line.charAt(i+1)=='*') && !(line.charAt(i)=='*'&&line.charAt(i+1)=='/')) {
								//if the operator isn't doubled up like ++, --, //, and is not an open or close comment
								//Then we need to generate mutations
								mutants=true;
								StringBuilder mutationBuilder = new StringBuilder(line); //start with basic string
								for(int m=0;m<operations.length;m++) {//cycle through our ops
									if(line.charAt(i)!=operations[m]) {//if we have a different operation
										//generate a mutant line
										mutationBuilder.setCharAt(i, operations[m]);
										//append the output line with our mutant
										lineBuilder.append(mutationBuilder.toString());
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

			/*for(int i=0;i<operations.length;i++) {
				printWriter.println("Number of "+operations[i]+" mutants: "+mutantCounter[i]);
			}*/
			reader.close();
			printWriter.close();
			for(int i=0;i<mutantCounter.length;i++) {
				mutantCounter[i]=0;
			}
			MutateCode(input, outputFile);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static void MutateCode(String input, String outputFile) {
		int numMutantFiles=1;
		BufferedReader mutantReader;
		try {
			//retrieve the class name
			String className = input.split("/")[input.split("/").length-1];
			className = className.substring(0,className.lastIndexOf("."));
			
			mutantReader = new BufferedReader(new FileReader(outputFile));
			String mutantLine = mutantReader.readLine();
			while(mutantLine != null) {//cycle through the mutant list

				//retrieve the line to mutate
				String[] mutLine = mutantLine.split("]");//split the line to get "[XX"
				StringBuilder x = new StringBuilder(mutLine[0]);//turn it into stringBuilder
				x.deleteCharAt(0);//delete [ to get XX
				int line = Integer.parseInt(x.toString());//retrieve the line number
				
				String[] mutants = mutantLine.split(";");//use ; to split the mutants. NEED TO RE-ADD THEM
				int numMutants = mutants.length;
				
				for(int i=1;i<numMutants;i++) {//start offset since index 0 is original line
					BufferedReader codeReader = new BufferedReader(new FileReader(input));
					String codeLine="";
					//Now to set up the new mutated file
					String output = System.getProperty("user.dir")+"/src/Mutants/Mutant"+numMutantFiles+".java";
					FileWriter fileWriter = new FileWriter(output);
					PrintWriter printWriter = new PrintWriter(fileWriter);
			    
					//replace package line
					codeLine=codeReader.readLine();
					printWriter.println("package Mutants;");
					
					//write lines up to and NOT including the mutant
					for(int j=1;j<line-1;j++) {
						codeLine=codeReader.readLine();
						if(codeLine.contains(className)) {
							String newClass = "Mutant"+numMutantFiles;
							System.out.println(codeLine);
							System.out.println(className);
							codeLine=codeLine.replaceFirst(className, newClass);
						}
						printWriter.println(codeLine);
					}
					
					//read mutant line to skip it
					codeReader.readLine();
					
					//inject Mutant
					printWriter.println(mutants[i]+";"+"//MUTANT LINE");
					
					//write the rest of the file
					while(codeLine!=null) {
						codeLine=codeReader.readLine();
						if(codeLine==null) {
							break;
						}
						printWriter.println(codeLine);
					}//mutated file should be complete.

					numMutantFiles++;//up the mutant counter for naming the files
					printWriter.close();//and CLOSE THE DAMN READERS SO THEY RESET FOR THE NEXT GO AROUND
					codeReader.close();
				}
				//Get the next set of mutants
				mutantLine = mutantReader.readLine();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}