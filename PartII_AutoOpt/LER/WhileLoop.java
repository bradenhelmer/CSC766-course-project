// While loop Abstraction
import java.util.Set;
import java.util.HashSet;

public class WhileLoop implements LER.Loop {

	private String subscript;
	private String conditionExpression;
	private String raw;

	public WhileLoop(String subscript) {
		this.subscript = subscript;
		this.conditionExpression = "";
	}

	@Override
	public String toString() {
		return String.format("%s$%s$%s", GloryUtil.getLoopSymbol(GloryParser.OTHER), subscript,
				conditionExpression);
	}

	public void setCondE(String CE) {
		conditionExpression = CE;
	}

	public void print() {
		System.out.printf("WHILE LOOP (%s)\n", subscript);
	}


	public Set<String> getRelLoops() {
        Set<String> relLoops = new HashSet<>();
		return relLoops;
    }

	@Override
	public void printCStmt() {
		System.out.printf("while (%s) {\n", conditionExpression);
	}
}
