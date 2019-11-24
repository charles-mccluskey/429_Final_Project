package mutantdetection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/*This custom class loader compiles a .java file to a .class file and loads the class into memory.*/
public class CompilingClassLoader extends ClassLoader {
    private String fileDirectory;

    public CompilingClassLoader(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBin = loadClassFromFile(name);
        return defineClass(name, classBin, 0, classBin.length);
    }

    private byte[] loadClassFromFile(String name) throws ClassNotFoundException {
        String[] splitName = name.split("\\.");
        String fileName = splitName[splitName.length - 1];
        compileJavaClass(fileDirectory +File.separator+fileName+".java");
        File f = new File(fileDirectory +File.separator+fileName+".class");
        if (!f.exists()) throw new ClassNotFoundException();
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            throw new ClassNotFoundException("Couldn't read mutant .class file");
        }
    }

    /*Takes in a .java filename and compiles it to a .class file*/
    private void compileJavaClass(String fileName) {
        ProcessBuilder pb = new ProcessBuilder("javac", fileName);
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
