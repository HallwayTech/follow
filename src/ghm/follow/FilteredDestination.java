package ghm.follow;

import ghm.follow.search.SearchEngine;

import javax.swing.JTextPane;

/**
 * An {@link OutputDestination} that filters what is shown.
 * 
 * @author chall
 */
public class FilteredDestination extends JTextPaneDestination {
	private String filterTerm;

	public FilteredDestination(JTextPane jTextPane, SearchEngine se, String filterTerm, boolean autoPositionCaret) {
		super(jTextPane, autoPositionCaret);
		this.filterTerm = filterTerm;
	}

	public void print(String s) {
		super.print(s);
	}
}