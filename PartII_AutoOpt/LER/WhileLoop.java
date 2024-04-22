// While loop Abstraction
import java.util.Set;
import java.util.HashSet;

public class WhileLoop implements LER.Loop {

	private String subscript;

	public WhileLoop(String subscript) {
		this.subscript = subscript;
	}

	public void print() {
		System.out.printf("WHILE LOOP (%s)\n", subscript);
	}

	public Set<String> getRelLoops() {
        Set<String> relLoops = new HashSet<>();
		return relLoops;
    }
}
