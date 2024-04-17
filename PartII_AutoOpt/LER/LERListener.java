// Custom listener to build an LER object from the tree.

import java.text.ParseException;

public class LERListener extends GloryBaseListener {

	private int currLoopType;
	private boolean isParsingLoop = false;
	private boolean readyToExport = false;
	private LER ler;

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

	// Internal enterForParam call
	private void __enterForParam(GloryParser.ForParamContext ctx) throws ParseException {
		if (!GloryUtil.isForType(currLoopType)) {
			throw new ParseException("Current loop type is not FOR!", 0);
		} else {
			try {
				ForLoop loop = new ForLoop(ctx.id().getText(), ctx.lBound().getText(),
						ctx.uBound().getText(), currLoopType);
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
}
