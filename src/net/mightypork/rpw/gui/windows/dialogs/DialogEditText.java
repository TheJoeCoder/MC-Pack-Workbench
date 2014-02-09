package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.gui.helpers.TextEditListener;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.SimpleConfig;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.logging.Log;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


public class DialogEditText extends DialogEditorBase {

	private JButton btnCancel;
	private JButton btnSave;
	private JButton btnFormatCodes;
	private JPopupMenu formatCodesPopup;

	private EAsset type;
	private TextEditListener listener;
	private String dlgTitle;
	private String dlgText;
	private boolean dlgFormattingCodes;


	public DialogEditText(final AssetTreeLeaf node) throws IOException {

		this();

		String path = Utils.fromLastChar(node.getAssetEntry().getPath(), '/');

		InputStream in = Projects.getActive().getAssetStream(node.getAssetKey());
		String text = FileUtils.streamToString(in);

		create(path, text, node.getAssetType(), true, new TextEditListener() {

			@Override
			public void onDialogClosed(String text) {


				File file = Projects.getActive().getAssetFile(node.getAssetKey());

				try {
					FileUtils.stringToFile(file, text);
				} catch (IOException e1) {
					Log.e(e1);

					Alerts.error(App.getFrame(), "Could not save file.");
				}

			}
		});
	}


	public DialogEditText(final File file) throws IOException {

		this();

		String path = Utils.fromLastChar(file.getPath(), '/');

		String text = FileUtils.fileToString(file);

		create(path, text, EAsset.forFile(file), true, new TextEditListener() {

			@Override
			public void onDialogClosed(String text) {

				try {
					FileUtils.stringToFile(file, text);
				} catch (IOException e1) {
					Log.e(e1);

					Alerts.error(App.getFrame(), "Could not save file.");
				}
			}
		});
	}


	public DialogEditText() {

		super();
	}


	private void create(String title, String text, EAsset type, boolean showFormatingCodes, TextEditListener listener) {

		this.dlgTitle = title + " - RPW Text editor";
		this.dlgText = text;
		this.dlgFormattingCodes = showFormatingCodes;

		this.type = type;

		this.listener = listener;

		createDialog();
	}


	private JPopupMenu buildCodesPopup() {

		JPopupMenu popup = new JPopupMenu();

		InputStream in = FileUtils.getResource("/data/misc/colorcodes.txt");
		String text = FileUtils.streamToString(in);
		Map<String, String> map = SimpleConfig.mapFromString(text);

		JMenuItem item;

		for (Entry<String, String> e : map.entrySet()) {
			if (e.getKey() == null || e.getValue() == null) {
				popup.addSeparator();
				continue;
			}

			String code = e.getKey();
			String label = e.getValue();

			String[] parts = label.split("[|]");

			String labelText = parts[0];
			String colorText = parts[1];

			int colorCode = Integer.parseInt(colorText, 16);

			popup.add(item = new JMenuItem(code.replace("\u00A7", "\u00A7 ") + " - " + labelText));

			item.setForeground(new Color(colorCode));
			item.setActionCommand(code);
			item.addActionListener(insertFormattingCodeListener);
		}

		return popup;
	}


	@Override
	protected void addActions() {

		btnCancel.addActionListener(closeListener);

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String text = getTextArea().getText();

				listener.onDialogClosed(text);

				closeDialog();
			}
		});

		btnFormatCodes.addMouseListener(new ClickListener() {

			boolean first = true;


			@Override
			public void mouseClicked(MouseEvent e) {

				if (first) {
					formatCodesPopup.show(getButtonsBox(), 0, 0);
					formatCodesPopup.setVisible(false);
					first = false;
				}

				formatCodesPopup.show(getButtonsBox(), btnFormatCodes.getBounds().x, btnFormatCodes.getBounds().y - formatCodesPopup.getHeight());
			}
		});
	}

	private ActionListener insertFormattingCodeListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String code = e.getActionCommand();

			RSyntaxTextArea ta = getTextArea();

			ta.insert(code, ta.getCaretPosition());

			ta.requestFocusInWindow();
		}
	};


	@Override
	protected String getTitleText() {

		return dlgTitle;
	}


	@Override
	protected void buildButtons(Box buttons) {

		btnFormatCodes = new JButton("Formatting codes", Icons.MENU_GENERATE);
		btnFormatCodes.setVisible(dlgFormattingCodes);

		btnCancel = new JButton("Discard", Icons.MENU_CANCEL);
		btnSave = new JButton("Save", Icons.MENU_YES);

		buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		if (type == EAsset.TEXT || type == EAsset.LANG) {
			buttons.add(btnFormatCodes);
		}

		buttons.add(Box.createHorizontalGlue());
		buttons.add(btnSave);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(btnCancel);

		formatCodesPopup = buildCodesPopup();
		buttons.add(formatCodesPopup);
	}


	@Override
	protected String getInitialText() {

		return dlgText;
	}


	@Override
	protected void configureTextarea(RSyntaxTextArea ta) {

		switch (type) {
			case CFG:
			case INI:
			case LANG:
			case PROPERTIES:
				configureTextareaConfig(ta);
				break;

			case JSON:
				configureTextareaJSON(ta);
				break;


			default:
				configureTextareaPlain(ta);
		}
	}

}