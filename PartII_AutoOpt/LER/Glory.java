import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;

public class Glory {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {

		ANTLRInputStream inputStream = new ANTLRInputStream(
				new FileInputStream(args[0]));

		// try {
		// Get our lexer
		GloryLexer lexer = new GloryLexer(inputStream);
		// Get a list of matched tokens
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// Pass the tokens to the parsercatcat
		GloryParser parser = new GloryParser(tokens);

		// Specify our entry point
		GloryParser.StatementContext drinkSentenceContext = parser.statement();
		ParseTreeWalker walker = new ParseTreeWalker();
		LERListener lerListener = new LERListener();
		walker.walk(lerListener, drinkSentenceContext);

		LER ler = lerListener.exportLER();

		// Print Original
		System.out.println("Original LER:");
		ler.outputLER();
		System.out.println();

		// Run Optimizer
		ler.optimize();

		// Print Optimized
		System.out.println("Optimized LER:");

		ler.outputLER();
		System.out.println();

		// Output C Code
		System.out.println("Optimized C Code:\n-----------------");
		ler.outputCCode();


	}

}
