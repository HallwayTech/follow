package ghm.follow;

import java.util.ArrayList;

public abstract class FilterableOutputDestination implements OutputDestination {
	private ArrayList<OutputDestination> _views;
	
	public FilterableOutputDestination() {
		_views = new ArrayList<OutputDestination>();
	}

	/**
	 * Add a view that filters printed text. <code>dest</code> can be a
	 * standard output or a {@link FilteredDestination}
	 */
	public void addView(OutputDestination dest) {
		_views.add(dest);
	}

	/**
	 * Remove a view that filters printed text
	 */
	public void removeView(OutputDestination dest) {
		if (!_views.isEmpty() && _views.contains(dest)) {
			_views.remove(dest);
		}
	}

	protected void notifyViews(String s) {
		for (OutputDestination view : _views) {
			view.print(s);
		}
	}

	public void print(String s) {
		handlePrint(s);
		notifyViews(s);
	}

	protected abstract void handlePrint(String s);
}