package ghm.follow.gui;

import ghm.follow.search.SearchableTextPane;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.text.Element;
import javax.swing.text.PlainView;

public class LineView extends PlainView {
	public LineView(Element e) {
		super(e);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.PlainView#drawLine(int, java.awt.Graphics, int,
	 *      int)
	 */
	protected void drawLine(int lineIndex, Graphics g, int x, int y) {
		SearchableTextPane comp = (SearchableTextPane) getContainer();
		// highlight current line
		if (comp.getSelectedIndex() > -1 && comp.getSelectedIndex() == lineIndex) {
			FontMetrics fm = comp.getFontMetrics(comp.getFont());
			Rectangle rec = new Rectangle((int) x, (int) y - fm.getMaxAscent() + 1,
					comp.getWidth(), fm.getMaxAscent());
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(rec);
			g2d.setColor(Color.LIGHT_GRAY.darker());
			g2d.draw(rec);
		}
		super.drawLine(lineIndex, g, x, y);
	}

}