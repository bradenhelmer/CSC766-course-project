// Utility methods class.

public class GloryUtil {

	public static String getGloryTokenString(int tokenType) {
		return GloryParser.VOCABULARY.getSymbolicName(tokenType);
	}

	public static boolean isForType(int tokenType) {
		return tokenType == GloryParser.NORMAL || tokenType == GloryParser.SUMMATION
				|| tokenType == GloryParser.PRODUCT;
	}

	public static boolean isWhileType(int tokenType) {
		return tokenType == GloryParser.OTHER;
	}

	public static String getLoopSymbol(int tokenType) {
		switch (tokenType) {
			case GloryParser.NORMAL:
				return "Γ";
			case GloryParser.OTHER:
				return "Ψ";
			case GloryParser.PRODUCT:
				return "Π";
			case GloryParser.SUMMATION:
				return "Σ";
		}

		return "";
	}

}
