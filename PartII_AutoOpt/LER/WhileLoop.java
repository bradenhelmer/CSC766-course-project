// While loop Abstraction

public class WhileLoop implements LER.Loop {

	private String subscript;
	private String conditionExpression;
	private String raw;

	public WhileLoop(String subscript) {
		this.subscript = subscript;
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
}
