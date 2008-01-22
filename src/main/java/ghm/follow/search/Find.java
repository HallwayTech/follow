package ghm.follow.search;

import ghm.follow.FollowApp;
import ghm.follow.gui.FollowAppAction;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

public class Find extends FollowAppAction
{
	public static final String NAME = "find";

	private FindDialog _dialog;

	public Find(FollowApp app)
	{
		super(app, FollowApp.getResourceString("action.Find.name"),
				FollowApp.getResourceString("action.Find.mnemonic"),
				FollowApp.getResourceString("action.Find.accelerator"));
	}

	public void actionPerformed(ActionEvent e)
	{
		getApp().setCursor(Cursor.WAIT_CURSOR);
		if (_dialog == null)
		{
			_dialog = new FindDialog(this);
			_dialog.setLocationRelativeTo(getApp().getFrame());
			_dialog.setLocation(100, 100);
			_dialog.pack();
		}
		_dialog.initFocus();
		_dialog.setVisible(true);
		getApp().setCursor(Cursor.DEFAULT_CURSOR);
	}
}