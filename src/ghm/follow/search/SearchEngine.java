package ghm.follow.search;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

/**
 * Searches through text based on flags passed at time of search.
 * 
 * @author chall
 */
public class SearchEngine implements DocumentListener {
	public static final int CASE_SENSITIVE = 1;

	public static final int REGEX = 2;

	protected SearchableTextArea textPane;

	protected Document doc;
	
	protected String text;

	/**
	 * Constructor for search text and tracking display elements for results
	 * list
	 * 
	 * @author chall
	 * @param textArea
	 */
	public SearchEngine(SearchableTextArea textPane) {
		doc = textPane.getDocument();
		doc.addDocumentListener(this);
		this.textPane = textPane;
	}

	/**
	 * Constructor for searching text without keeping track of any display
	 * elements.
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
		try {
			if (doc != null) {
				text = doc.getText(0, doc.getLength());
			}
		} catch (BadLocationException e) {
			// can ignore as the search will stop if text is null
			text = null;
		}
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
		if (doc != null) {
			text = null;
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
				int line = textPane.getLineOfOffset(pos);
				if (line != lastLine) {
					if (tempLine != null) {
						results.add(tempLine);
					}
					Element elem = Utilities.getParagraphElement(textPane, pos);
					int lineStart = elem.getStartOffset();
					int lineEnd = elem.getEndOffset();
					tempLine = new LineResult(line, lineStart, lineEnd);
				}
				buildWordResult(tempLine, pos, pos + term.length(), term);
				lastLine = line;
				pos += term.length();
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
			int line = textPane.getLineOfOffset(m.start());
			if (line != lastLine) {
				if (tempLine != null) {
					results.add(tempLine);
				}
				Element elem = Utilities.getParagraphElement(textPane, m.start());
				int lineStart = elem.getStartOffset();
				int lineEnd = elem.getEndOffset();
				tempLine = new LineResult(line, lineStart, lineEnd);
			}
			buildWordResult(tempLine, m.start(), m.end(), m.group());
			lastLine = line;
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
	private WordResult buildWordResult(LineResult lineResult, int start, int end, String term) {
		WordResult r = new WordResult(start, end, term);
		lineResult.addResult(r);
//		try {
			// increase by 1 because offset starts at 0.
			// 1 is clearer to the user since most people don't start counting
			// at 0
			int line = textPane.getLineOfOffset(start);
			r.parent.lineNumber = line + 1;
			int lineOffset = textPane.getLineStartOffset(line);
			r.setLineOffset(lineOffset);
//		}
//		catch (BadLocationException e) {
//			r.parent.lineNumber = -1;
//		}
		return r;
	}

	/**
	 * Inherited from javax.swing.event.DocumentListener
	 */
	public void changedUpdate(DocumentEvent arg0) {
		clearText(true);
	}

	/**
	 * Inherited from javax.swing.event.DocumentListener
	 */
	public void insertUpdate(DocumentEvent arg0) {
		clearText(true);
	}

	/**
	 * Inherited from javax.swing.event.DocumentListener
	 */
	public void removeUpdate(DocumentEvent arg0) {
		clearText(true);
	}
	
	private void clearText(boolean requireDocument) {
		if (requireDocument && doc != null) {
			text = null;
		} else if (!requireDocument) {
			text = null;
		}
	}
}