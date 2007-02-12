package ghm.follow.search;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

/**
 * Searches through text based on flags passed at time of search.
 * 
 * @author chall
 */
public class SearchEngine {
	public static final int CASE_SENSITIVE = 1;

	public static final int REGEX = 2;

	protected JTextArea textArea;

	protected String text;

	/**
	 * Constructor for search text and tracking display elements for results
	 * list
	 * 
	 * @author chall
	 * @param textArea
	 */
	public SearchEngine(JTextArea textArea) {
		try {
			Document doc = textArea.getDocument();
			String text = doc.getText(0, doc.getLength());
			this.textArea = textArea;
			this.text = text;
		}
		catch (BadLocationException e) {

		}
	}

	/**
	 * Constructor for searching text without keeping track of any display
	 * elements
	 * 
	 * @author chall
	 * @param text
	 */
	public SearchEngine(String text) {
		this.text = text;
	}

	/**
	 * Search <code>text</code> for <code>term</code>. Use the constants of
	 * this class for flags.
	 * 
	 * @author chall
	 * @param term
	 * @param text
	 * @return An array of found positions of term
	 */
	public LineResult[] search(String term, int flags) {
		LineResult[] retval = null;
		// search using a case sensitive regular expression
		if (((flags & CASE_SENSITIVE) != 0) && ((flags & REGEX) != 0)) {
			Pattern p = Pattern.compile(term, Pattern.MULTILINE);
			retval = regexSearch(p, text);
		}
		// search using a case insensitive regular expression
		else if ((flags & REGEX) != 0) {
			Pattern p = Pattern.compile(term, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			retval = regexSearch(p, text);
		}
		// search by case sensitive text
		else if ((flags & CASE_SENSITIVE) != 0) {
			retval = textSearch(term, text);
		}
		// search by case insensitive text
		else if (flags == 0) {
			retval = textSearch(term.toLowerCase(), text.toLowerCase());
		}
		// fail due to unknown flags
		else {
			throw new IllegalArgumentException("Unknown search strategy requested [flags=" + flags);
		}
		return retval;
	}

	/**
	 * Searches <code>text</code> for <code>term</code> by performing a
	 * simple text search.
	 * 
	 * @author chall
	 * @param term
	 * @param text
	 * @return
	 */
	protected LineResult[] textSearch(String term, String text) {
		ArrayList results = new ArrayList();
		if (term != null && term.length() > 0 && text != null && text.length() > 0) {
			int pos = 0;
			int lastLine = -1;
			LineResult tempLine = null;
			while ((pos = text.indexOf(term, pos)) > -1) {
				try {
					int line = textArea.getLineOfOffset(pos);
					if (line != lastLine) {
						if (tempLine != null) {
							results.add(tempLine);
						}
						Element elem = Utilities.getParagraphElement(textArea, pos);
						int lineStart = elem.getStartOffset();
						int lineEnd = elem.getEndOffset();
						tempLine = new LineResult(line, lineStart, lineEnd);
					}
					buildWordResult(tempLine, pos, pos + term.length(), term, textArea);
					lastLine = line;
					pos += term.length();
				}
				catch (BadLocationException e) {
					// doesn't matter
				}
			}
			if (tempLine != null) {
				results.add(tempLine);
			}
		}
		LineResult[] retval = (LineResult[]) results.toArray(new LineResult[results.size()]);
		return retval;
	}

	/**
	 * Searches <code>text</code> using the provided <code>Pattern</code><br>
	 * <br>
	 * Thanks to prec in #regex for correcting the use of Matcher.
	 * 
	 * @author chall
	 * @param p
	 * @param text
	 * @return
	 */
	protected LineResult[] regexSearch(Pattern p, String text) {
		Matcher m = p.matcher(text);
		ArrayList results = new ArrayList();
		LineResult tempLine = null;
		int lastLine = -1;
		while (m.find()) {
			try {
				int line = textArea.getLineOfOffset(m.start());
				if (line != lastLine) {
					if (tempLine != null) {
						results.add(tempLine);
					}
					Element elem = Utilities.getParagraphElement(textArea, m.start());
					int lineStart = elem.getStartOffset();
					int lineEnd = elem.getEndOffset();
					tempLine = new LineResult(line, lineStart, lineEnd);
				}
				buildWordResult(tempLine, m.start(), m.end(), m.group(), textArea);
				lastLine = line;
			}
			catch (BadLocationException e) {
				// doesn't matter
			}
		}
		if (tempLine != null) {
			results.add(tempLine);
		}
		return (LineResult[]) results.toArray(new LineResult[results.size()]);
	}

	/**
	 * Creates a new <code>Result</code> and populates the line number based
	 * on the starting offset
	 * 
	 * @author chall
	 * @param start
	 * @param end
	 * @param term
	 * @param textArea
	 * @return
	 */
	private WordResult buildWordResult(LineResult lineResult, int start, int end, String term,
			JTextArea textArea) {
		WordResult r = new WordResult(start, end, term);
		lineResult.addResult(r);
		try {
			// increase by 1 because offset starts at 0.
			// 1 is clearer to the user since most people don't start counting
			// at 0
			int line = textArea.getLineOfOffset(start);
			r.parent.lineNumber = line + 1;
			int lineOffset = textArea.getLineStartOffset(line);
			r.setLineOffset(lineOffset);
		}
		catch (BadLocationException e) {
			r.parent.lineNumber = -1;
		}
		return r;
	}
}