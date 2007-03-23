package ghm.follow.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

public class SearchableTextPane extends JTextPane {
	private int lastSearchPos = -1;
	private String lastSearchTerm;
	private final MutableAttributeSet lineHighlighter = new SimpleAttributeSet();
	private final MutableAttributeSet wordHighlighter = new SimpleAttributeSet();
	private final MutableAttributeSet clearHighlighter = new SimpleAttributeSet();
	private SearchEngine searchEngine;
	private int tabSize;
	private LineResult[] lineResults;

	public SearchableTextPane(Font font, int tabSize) {
		StyleConstants.setBackground(lineHighlighter, Color.YELLOW);
		StyleConstants.setBackground(wordHighlighter, Color.LIGHT_GRAY);
		StyleConstants.setBackground(clearHighlighter, Color.WHITE);
		// set the display font
		setFont(font);
		setTabSize(tabSize);
	}

	/**
	 * Override this to keep the text from wrapping and to make the viewable
	 * area as wide as the tabbed pane
	 */
	public boolean getScrollableTracksViewportWidth() {
		Component parent = getParent();
		ComponentUI ui = getUI();

		return parent != null ? (ui.getPreferredSize(this).width <= parent.getSize().width) : true;
	}

	/**
	 * Sets the display font used and updates the font widths.
	 * 
	 * @param font
	 */
	public void setFont(Font font) {
		setFont(font, false);
	}

	/**
	 * Sets the display font used and updates the font widths.
	 * 
	 * @param font
	 * @param updateTabs
	 */
	public void setFont(Font font, boolean updateTabs) {
		super.setFont(font);
		Document doc = getDocument();
		StyledDocument sdoc = getStyledDocument();
		// apply the style to the document
		if (sdoc != null && doc != null) {
			Style style = StyleContext.getDefaultStyleContext()
					.getStyle(StyleContext.DEFAULT_STYLE);
			StyleConstants.setFontFamily(style, font.getFamily());
			StyleConstants.setFontSize(style, font.getSize());
			StyleConstants.setBold(style, font.isBold());
			StyleConstants.setItalic(style, font.isItalic());
			sdoc.setCharacterAttributes(0, doc.getLength(), style, false);
		}
		if (updateTabs) {
			setTabs();
		}
	}

	public int getTabSize() {
		return tabSize;
	}

	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
		setTabs();
	}

	private void setTabs() {
		Document doc = getDocument();
		StyledDocument sdoc = getStyledDocument();
		if (doc != null && sdoc != null) {
			FontMetrics fm = getFontMetrics(getFont());
			int charWidth = fm.charWidth('o');
			int tabWidth = charWidth * tabSize;

			TabStop[] tabs = new TabStop[10];

			for (int j = 0; j < tabs.length; j++) {
				int tab = j + 1;
				tabs[j] = new TabStop(tab * tabWidth);
			}

			TabSet tabSet = new TabSet(tabs);
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setTabSet(attributes, tabSet);
			int length = doc.getLength();
			sdoc.setParagraphAttributes(0, length, attributes, false);
		}
	}

	/**
	 * Highlight <code>term</code> wherever it is found in the view. Also
	 * highlights the entire line on which the term is found.
	 * 
	 * @param term
	 * @param caseSensitive
	 * @param useRegularExpression
	 * @return
	 */
	public LineResult[] highlight(String term, boolean caseSensitive, boolean useRegularExpression) {
		lineResults = null;
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
					Thread.yield();
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
		if (lineResults != null) {
			StyledDocument sdoc = getStyledDocument();
			// Document doc = getDocument();
			for (int i = 0; i < lineResults.length; i++) {
				int start = lineResults[i].start;
				int end = lineResults[i].end;
				sdoc.setCharacterAttributes(start, end - start, clearHighlighter, false);
				Thread.yield();
			}
		}
		// getStyledDocument().setCharacterAttributes(0,
		// getDocument().getLength(), clearHighlighter,
		// true);
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

	/**
	 * Get the line where <code>offset</code> is found. Taken from JTextArea.
	 * 
	 * @param offset
	 * @return
	 */
	public int getLineOfOffset(int offset) {
		Element map = getDocument().getDefaultRootElement();
		return map.getElementIndex(offset);
	}

	/**
	 * Get the starting caret position of a line. Taken from JTextArea.
	 * 
	 * @param line
	 * @return
	 */
	public int getLineStartOffset(int line) {
		Element map = getDocument().getDefaultRootElement();
		Element lineElem = map.getElement(line);
		return lineElem.getStartOffset();
	}

	/**
	 * Get the ending caret position of a line. Taken from JTextArea.
	 * 
	 * @param line
	 * @return
	 */
	public int getLineEndOffset(int line) {
		Element map = getDocument().getDefaultRootElement();
		Element lineElem = map.getElement(line);
		return lineElem.getEndOffset();
	}
}