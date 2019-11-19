package Manual_Control;
import SUT.Testable;
import MutantGenerator.MutantGenerator;

public class Controller {

	public static void main(String[] args) {
		
		String input = System.getProperty("user.dir")+"/src/SUT/Testable.java";
		MutantGenerator generator = new MutantGenerator();
		generator.GenerateMutants(input);
	}

}
