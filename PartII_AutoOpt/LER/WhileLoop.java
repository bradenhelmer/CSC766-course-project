// While loop Abstraction

public class WhileLoop implements LER.Loop {

	private String subscript;

	public WhileLoop(String subscript) {
		this.subscript = subscript;
	}

	public void print() {
		System.out.printf("WHILE LOOP (%s)\n", subscript);
	}
}
