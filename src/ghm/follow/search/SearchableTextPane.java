package ghm.follow.search;

import java.awt.Color;
import java.util.StringTokenizer;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

public class SearchableTextPane extends JTextPane {
	private int lastSearchPos = -1;

	private String lastSearchTerm;

	private MutableAttributeSet lineHighlighter = new SimpleAttributeSet();
	private MutableAttributeSet wordHighlighter = new SimpleAttributeSet();
	private MutableAttributeSet clearHighlighter = new SimpleAttributeSet();

	public final Style defaultStyle;

	private SearchEngine searchEngine;

	public SearchableTextPane() {
		TabSet tabSet = new TabSet(new TabStop[] {new TabStop(100)});
		// set up the styles you want to use in you JTextPane
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setTabSet(def,tabSet);
		setParagraphAttributes(def,true);
		defaultStyle = addStyle("regular", def);

		StyleConstants.setBackground(lineHighlighter, Color.YELLOW);
		StyleConstants.setBackground(wordHighlighter, Color.LIGHT_GRAY);
		// TODO set this to match the default style background
		StyleConstants.setBackground(clearHighlighter, Color.WHITE);
	}

	/**
	 * Gets the default style for the document
	 * 
	 * @return
	 */
	public Style getDefaultStyle() {
		return defaultStyle;
	}

	public LineResult[] highlight(String term, boolean caseSensitive, boolean useRegularExpression) {
		LineResult[] lineResults = null;
		// Remove all old highlights
		removeHighlights();
		// Search for pattern
		if ((term != null) && (term.length() > 0)) {
			// look for instances of the term in the text
			int flags = 0;
			if (caseSensitive) {
				flags |= SearchEngine.CASE_SENSITIVE;
			}
			if (useRegularExpression) {
				flags |= SearchEngine.REGEX;
			}

			lineResults = getSearchEngine().search(term, flags);
			for (int i = 0; i < lineResults.length; i++) {
				int lineStart = lineResults[i].start;
				int lineEnd = lineResults[i].end;
				// highlight the whole line
				addHighlight(lineStart, lineEnd - lineStart, lineHighlighter);
				WordResult[] wordResults = lineResults[i].getWordResults();
				for (int j = 0; j < wordResults.length; j++) {
					// highlight the searched term
					int wordStart = wordResults[j].start;
					int wordEnd = wordResults[j].end;
					addHighlight(wordStart, wordEnd - wordStart, wordHighlighter);
				}
			}
		}
		return lineResults;
	}

	/**
	 * Highlight a piece of text in the document
	 * 
	 * @param wordStart
	 * @param wordEnd
	 * @param highlighter
	 */
	private void addHighlight(int wordStart, int length, MutableAttributeSet highlighter) {
		getStyledDocument().setCharacterAttributes(wordStart, length, highlighter, true);
	}

	/**
	 * Removes highlights from text area
	 */
	public void removeHighlights() {
		getStyledDocument().setCharacterAttributes(0, getDocument().getLength(), clearHighlighter,
				true);
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

	/**
	 * Get search engine ensuring that only 1 instance is created and reentrant.
	 * 
	 * @return searchEngine associated to this text area
	 */
	private SearchEngine getSearchEngine() {
		if (searchEngine == null) {
			searchEngine = new SearchEngine(this);
		}
		return searchEngine;
	}
	
	public int getLineOfOffset(int offset) {
		StringTokenizer st = new StringTokenizer(getText(),"\n",true);
		int count = 0;
		int lineNumber = 0;
		while (st.hasMoreTokens() && (count < offset)) {
			String s = st.nextToken();
			count += s.length();
			if (s.equals("\n")) lineNumber++;
		}
		return lineNumber;
	}
	
	public int getLineStartOffset(int line) {
		StringTokenizer st = new StringTokenizer(getText(),"\n",true);
		int count = 0;
		int lineNumber = 0;
		while (st.hasMoreTokens() && (lineNumber < line)) {
			String s = st.nextToken();
			count += s.length();
			if (s.equals("\n")) lineNumber++;
		}
		return count;
	}
}