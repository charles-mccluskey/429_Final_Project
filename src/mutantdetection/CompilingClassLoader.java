package mutantdetection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/*This custom class loader compiles a .java file to a .class file and loads the class into memory.*/
public class CompilingClassLoader extends ClassLoader {
    private String directory;

    /**
     * @param packageClassDirectory the directory containing the class and it's packages.
     *                       e.g. for "my\path\to\package1\package2\MyClass.java", classDirectory would be "my\path\to"
     */
    public CompilingClassLoader(String packageClassDirectory) {
        this.directory = packageClassDirectory;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBin = loadClassFromFile(name);
        return defineClass(name, classBin, 0, classBin.length);
    }

    private byte[] loadClassFromFile(String name) throws ClassNotFoundException {
        compileJavaClass(directory +File.separator+name.replace('.', File.separatorChar));
        File f = new File(directory +File.separator+name.replace('.', File.separatorChar)+".class");
        if (!f.exists()) throw new ClassNotFoundException();
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            throw new ClassNotFoundException("Couldn't read mutant .class file");
        }
    }

    /*Takes in a .java filename and compiles it to a .class file*/
    private void compileJavaClass(String fileName) {
        ProcessBuilder pb = new ProcessBuilder("javac", fileName+".java");
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
