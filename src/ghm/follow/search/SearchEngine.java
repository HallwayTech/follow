package ghm.follow.search;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchEngine {
	public static final int CASE_SENSITIVE = 1;

	public static final int REGEX = 2;

	protected String text;

	public SearchEngine(String text) {
		this.text = text;
	}

	/**
	 * Search <code>text</code> for <code>term</code>. Use the constants of
	 * this class for flags.
	 * 
	 * @param term
	 * @param text
	 * @return An array of found positions of term
	 */
	public Result[] search(String term, int flags) {
		// search using a case sensitive regular expression
		if (((flags & CASE_SENSITIVE) != 0) && ((flags & REGEX) != 0)) {
			Pattern p = Pattern.compile(term, Pattern.MULTILINE);
			return regexSearch(p, text);
		}
		// search using a case insensitive regular expression
		else if ((flags & REGEX) != 0) {
			Pattern p = Pattern.compile(term, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			return regexSearch(p, text);
		}
		// search by case sensitive text
		else if ((flags & CASE_SENSITIVE) != 0) {
			return textSearch(term, text);
		}
		// search by case insensitive text
		else if (flags == 0) {
			return textSearch(term.toLowerCase(), text.toLowerCase());
		}
		// fail due to unknown flags
		else {
			throw new IllegalArgumentException(
					"Unknown search strategy requested [flags=" + flags);
		}
	}

	protected static Result[] textSearch(String term, String text) {
		ArrayList results = new ArrayList();
		if (term != null && term.length() > 0 && text != null
				&& text.length() > 0) {
			int pos = 0;
			while ((pos = text.indexOf(term, pos)) > -1) {
				results.add(new Result(pos, pos + term.length(), term));
				pos += term.length();
			}
		}
		Result[] retval = (Result[]) results
				.toArray(new Result[results.size()]);
		return retval;
	}

	/**
	 * Searches <code>text</code> using the provided <code>Pattern</code><br>
	 * <br>
	 * Thanks to prec in #regex for correcting the use of Matcher.
	 * 
	 * @param p
	 * @param text
	 * @return
	 */
	protected Result[] regexSearch(Pattern p, String text) {
		Matcher m = p.matcher(text);
		ArrayList results = new ArrayList();
		while (m.find()) {
			results.add(new Result(m.start(), m.end(), m.group()));
		}
		return (Result[]) results.toArray(new Result[results.size()]);
	}

	/**
	 * Container for positions of found terms.
	 * 
	 * @author chall
	 */
	public static class Result {
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