package ghm.follow.search;

import ghm.follow.search.SearchEngine.Result;
import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class SearchableTextArea extends JTextArea {
	private int lastSearchPos = -1;
	private String lastSearchTerm;

	private static DefaultHighlightPainter painter = new DefaultHighlightPainter(Color.YELLOW);

	public int highlight(String term, boolean caseSensitive, boolean useRegularExpression) {
		// Remove all old highlights
		removeHighlights();
		// Get a highlighter
		Highlighter hilite = getHighlighter();
		// Search for pattern
		int numFound = 0;
		if ((term != null) && (term.length() > 0)) {
			try {
				Document doc = getDocument();
				String text = doc.getText(0, doc.getLength());
				// look for instances of the term in the text
				Result[] results = null;
				int flags = 0;
				if (caseSensitive) {
					flags |= SearchEngine.CASE_SENSITIVE;
				}
				if (useRegularExpression) {
					flags |= SearchEngine.REGEX;
				}

				results = new SearchEngine(text).search(term, flags);
				numFound = results.length;
				for (int i = 0; i < results.length; i++) {
					hilite.addHighlight(results[i].start, results[i].end, painter);
				}
			}
			catch (BadLocationException e) {
				// don't worry about it
			}
		}
		return numFound;
	}

	/**
	 * Removes highlights from text area
	 */
	public void removeHighlights() {
		Highlighter hilite = getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();

		for (int i = 0; i < hilites.length; i++) {
			hilite.removeHighlight(hilites[i]);
		}
	}

	/**
	 * Searches for a term. If the term provided matches the last searched term,
	 * the last found position is used as a starting point.<br>
	 * <br>
	 * Developer note: this method isn't currently used.
	 * 
	 * @param term
	 *            The string for which to search.
	 * @return The position where the term was found.<br>
	 *         If the term is null, empty or not found, -1 is returned.
	 */
	public int search(String term) {
		if (term != null && term.length() > 0) {
			if (term.equals(lastSearchTerm)) {
				// assume to start at the beginning
				int pos = 0;
				// if there is a previous search position, start there plus the
				// length
				// of the last term so that last term again isn't found again
				if (lastSearchPos != -1) {
					pos = lastSearchPos + lastSearchTerm.length();
				}
				lastSearchPos = search(lastSearchTerm, pos);
			}
			else {
				lastSearchPos = search(term, 0);
			}
		}
		// remember the term if it was found
		if (lastSearchPos == -1) {
			lastSearchTerm = null;
		}
		else {
			lastSearchTerm = term;
		}
		return lastSearchPos;
	}

	/**
	 * Searches for a term at the given starting position.<br>
	 * <br>
	 * Developer note: this method isn't currently used.
	 * 
	 * @param term
	 *            The string for which to search.
	 * @param startPos
	 *            Where to start.
	 * @return The position where the term was found.<br>
	 *         If the term is null, empty or not found, -1 is returned.
	 */
	public int search(String term, int startPos) {
		int pos = 0;
		try {
			Document doc = getDocument();
			String text = doc.getText(0, doc.getLength());

			// Search for pattern
			pos = text.indexOf(term, startPos);
		}
		catch (BadLocationException e) {
			// just return -1;
			pos = -1;
		}
		return pos;
	}
}