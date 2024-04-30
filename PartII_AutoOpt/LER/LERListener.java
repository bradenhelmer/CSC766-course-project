// Custom listener to build an LER object from the tree.

import java.text.ParseException;

public class LERListener extends GloryBaseListener {

	// Type of loop we are currently parsing
	private int currLoopType;

	// Parsing flags
	private boolean isParsingLoop = false;
	private boolean readyToExport = false;
	private boolean hasFoundR = false;

	// LER representation to be exported after.
	private LER ler;
	private LER.Loop currentLoop;

	public LERListener() {
		this.ler = new LER();
	}

	public LER exportLER() throws IllegalCallerException {
		if (!readyToExport) {
			throw new IllegalCallerException("Listener doesnt have any LER data to export!");
		}
		return ler;
	}

	@Override
	public void enterStatement(GloryParser.StatementContext ctx) {
		return;
	}

	@Override
	public void exitStatement(GloryParser.StatementContext ctx) {
		readyToExport = true;
	}

	@Override
	public void enterL(GloryParser.LContext ctx) {
		if (ctx.loopType == null) {
			return;
		}
		currLoopType = ctx.loopType.getType();
		isParsingLoop = true;
	}

	@Override
	public void enterConditionExpression(GloryParser.ConditionExpressionContext ctx) {
		if (currentLoop instanceof WhileLoop WL) {
			WL.setCondE(ctx.getText());
		}
	}

	// Internal enterForParam call
	private void __enterForParam(GloryParser.ForParamContext ctx) throws ParseException {
		if (!GloryUtil.isForType(currLoopType)) {
			throw new ParseException("Current loop type is not FOR!", 0);
		} else {
			try {
				ForLoop loop = new ForLoop(ctx.id().getText(), ctx.lBound().getText(),
						ctx.uBound().getText(), currLoopType);
				currentLoop = loop;
				ler.addLoop(loop);
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
	}

	@Override
	public void enterForParam(GloryParser.ForParamContext ctx) {
		try {
			__enterForParam(ctx);
		} catch (ParseException e) {
			System.err.println(e.getMessage() + "Exiting...");
			System.exit(1);
		}
	}

	@Override
	public void enterSubscript(GloryParser.SubscriptContext ctx) {
		if (GloryUtil.isWhileType(currLoopType) && isParsingLoop) {
			WhileLoop loop = new WhileLoop(__getIdFromSubscript(ctx.getText()));
			currentLoop = loop;
			ler.addLoop(loop);
			isParsingLoop = false;
		} else {

		}
	}

	private String __getIdFromSubscript(String string) {

		StringBuilder sb = new StringBuilder();

		boolean andSwitch = false;
		for (char ch : string.toCharArray()) {
			if (ch == '$') {
				andSwitch = !andSwitch;
				continue;
			}

			if (andSwitch) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	@Override
	public void enterE(GloryParser.EContext ctx) {
		ler.setE(ctx.getText());
		__parseOpsAndOperands(ctx.getText());
	}

	private boolean __isOp(char c) {
		return c == '+' || c == '-' || c == '/' || c == '*';
	}

	// Internal function to parse the expression string,
	// retreiving concrete ops and operands from the ANTLR
	// methods proved recursively difficult.
	private void __parseOpsAndOperands(String E) {
		StringBuilder sb = new StringBuilder();
		int foundOperands = 0;
		char arr[] = E.toCharArray();
		for (int i = 0; i < E.length(); i++) {
			sb.delete(0, sb.length());
			if (arr[i] == '(') {
				while (arr[i] != ')' && i < E.length()) {
					sb.append(arr[i++]);
				}
				sb.append(arr[i]);
				ler.addOperand(sb.toString());
				continue;
			}

			while (!__isOp(arr[i])) {
				sb.append(arr[i++]);
				if (i == E.length())
					break;
			}

			if (sb.length() != 0) {
				foundOperands++;
				ler.addOperand(sb.toString());
				if (foundOperands > 1) {
					ler.getLastOperand().setPrevOp(ler.getLastOp());
				}
			}

			if (i != E.length()) {
				String op = Character.toString(arr[i]);
				if (foundOperands > 0) {
					ler.getLastOperand().setNextOp(op);
				}
				ler.addOp(op);
			}
		}
	}

	@Override
	public void enterR(GloryParser.RContext ctx) {
		ler.setResult(ctx.getText());
	}
}
