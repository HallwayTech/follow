package ghm.follow.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
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
import javax.swing.text.Utilities;

public class SearchableTextPane extends JTextPane {
	private int lastSearchPos = -1;
	private String lastSearchTerm;
	private final MutableAttributeSet lineHighlighter = new SimpleAttributeSet();
	private final MutableAttributeSet wordHighlighter = new SimpleAttributeSet();
	private final MutableAttributeSet clearHighlighter = new SimpleAttributeSet();
	private int tabSize;
	private ArrayList highlights;
	private int selectedIndex = -1;

	public SearchableTextPane(Font font, int tabSize) {
		StyleConstants.setBackground(lineHighlighter, Color.YELLOW.brighter());
		StyleConstants.setBackground(wordHighlighter, Color.YELLOW);
		StyleConstants.setBold(wordHighlighter, true);
		StyleConstants.setBackground(clearHighlighter, Color.WHITE);
		StyleConstants.setBold(clearHighlighter, false);
		// set the display font
		setFont(font);
		setTabSize(tabSize);
		addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				Document doc = getDocument();
				Element map = doc.getDefaultRootElement();
				if (selectedIndex > -1) {
					try {
						// unhighlight previous selected line
						Element previous = map.getElement(selectedIndex);
						if (previous != null) {
							Rectangle rec = modelToView(previous.getStartOffset());
							if (rec != null) {
								rec.width = getWidth();
								repaint(rec);
							}
						}
					}
					catch (BadLocationException e1) {
					}
				}
				// highlight current selected line
				// the work for this is done by LineView
				int index = map.getElementIndex(e.getDot());
				if (getSelectionStart() == getSelectionEnd()) {
					selectedIndex = index;
				}
				else {
					selectedIndex = -1;
				}
				Element selected = map.getElement(index);
				getUI().damageRange(SearchableTextPane.this, selected.getStartOffset(),
						selected.getEndOffset() - 1);
			}
		});
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
	public LineResult[] highlight(String term, int flags) {
		LineResult[] lineResults = new LineResult[0];
		// Remove all old highlights
		removeHighlights();
		// Search for pattern
		if ((term != null) && (term.length() > 0)) {
			// look for instances of the term in the text
//			int flags = 0;
//			if (caseSensitive) {
//				flags |= SearchEngine.CASE_SENSITIVE;
//			}
//			if (useRegularExpression) {
//				flags |= SearchEngine.REGEX;
//			}

			try {
				Document doc = getDocument();
				String text = doc.getText(0, doc.getLength());
				WordResult[] searchResults = new SearchEngine(flags).search(term, text);
				lineResults = convertWords2Lines(searchResults);
				for (int i = 0; i < lineResults.length; i++) {
					// int lineStart = lineResults[i].start;
					// int lineEnd = lineResults[i].end;
					// highlight the whole line
					// addHighlight(lineStart, lineEnd - lineStart,
					// lineHighlighter);
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
			catch (BadLocationException e) {
				lineResults = new LineResult[0];
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
		getStyledDocument().setCharacterAttributes(wordStart, length, highlighter, false);
		if (highlights == null) {
			highlights = new ArrayList();
		}
		highlights.add(new Highlight(wordStart, length));
	}

	/**
	 * Removes highlights from text area
	 */
	public void removeHighlights() {
		if (highlights != null) {
			StyledDocument sdoc = getStyledDocument();
			Iterator hs = highlights.iterator();
			while (hs.hasNext()) {
				Highlight h = (Highlight) hs.next();
				sdoc.setCharacterAttributes(h.start, h.length, clearHighlighter, false);
			}
			highlights.clear();
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

	/**
	 * Converts word results from search into line results
	 * 
	 * @param words
	 * @return
	 */
	private LineResult[] convertWords2Lines(WordResult[] words) {
		ArrayList lines = new ArrayList();
		LineResult tempLine = null;
		int lastLine = -1;
		for (int i = 0; i < words.length; i++) {
			WordResult word = words[i];
			int line = getLineOfOffset(word.start);
			if (line != lastLine) {
				if (tempLine != null) {
					lines.add(tempLine);
				}
				Element elem = Utilities.getParagraphElement(this, word.start);
				int lineStart = elem.getStartOffset();
				int lineEnd = elem.getEndOffset();
				tempLine = new LineResult(line, lineStart, lineEnd);
			}
			updateWordResult(word, tempLine);
			lastLine = line;
			// allow other things to happen in case the search takes a while
			Thread.yield();
		}
		return (LineResult[]) lines.toArray(new LineResult[lines.size()]);
	}

	/**
	 * Adds word result to line resuls and updates line information
	 * 
	 * @param wordResult
	 * @param lineResult
	 */
	private void updateWordResult(WordResult wordResult, LineResult lineResult) {
		lineResult.addWord(wordResult);
		// increase by 1 because offset starts at 0.
		// 1 is clearer to the user since most people don't start counting
		// at 0
		int line = getLineOfOffset(wordResult.start);
		wordResult.parent.lineNumber = line + 1;
		int lineOffset = getLineStartOffset(line);
		wordResult.setLineOffset(lineOffset);
	}

	private class Highlight {
		int start;
		int length;

		Highlight(int start, int length) {
			this.start = start;
			this.length = length;
		}
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}
}