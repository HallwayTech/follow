package ghm.follow.search;

public abstract class SearchStrategy {
	public static final int CASE_SENSITIVE = 1;
	public static final int REGEX = 2;

	protected String text;

	public static SearchStrategy getInstance(String text) {
		return getInstance(text, 0);
	}

	public static SearchStrategy getInstance(String text, int flags) {
		if ((flags & (CASE_SENSITIVE | REGEX)) != 0) {
			return new RegexSensitiveSearchStrategy(text);
		}
		else if ((flags & CASE_SENSITIVE) != 0) {
			return new CaseSensitiveSearchStrategy(text);
		}
		else if ((flags & REGEX) != 0) {
			return new RegexInsensitiveSearchStrategy(text);
		}
		else if (flags == 0) {
			return new CaseInsensitiveSearchStrategy(text);
		}
		else {
			throw new IllegalArgumentException("Unknown search strategy requested [flags=" + flags);
		}
	}

	public SearchStrategy(String text) {
		this.text = text;
	}

	/**
	 * Search <code>text</code> for <code>term</code>.
	 * 
	 * @param term
	 * @param text
	 * @return An array of found positions of term
	 */
	public abstract Result[] search(String term);

	/**
	 * Container for positions of found terms.
	 * 
	 * @author chall
	 */
	public class Result {
		public int start;
		public int end;
		public String term;

		public Result(int start, int end, String term) {
			this.start = start;
			this.end = end;
			this.term = term;
		}
	}
}