package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTextField;


public class DialogSaveAs extends RpwDialog {

	private List<String> options;

	private JXTextField field;
	private JButton buttonOK;
	private SimpleStringList list;

	private JButton buttonCancel;


	public DialogSaveAs() {

		super(App.getFrame(), "Save As...");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb;
		VBox vb = new VBox();
		vb.windowPadding();

		vb.heading("Save Project As...");

		vb.titsep("Your Projects");
		vb.gap();

		options = Projects.getProjectNames();

		vb.add(list = new SimpleStringList(options, true));
		list.getList().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				String s = list.getSelectedValue();
				if (s != null) field.setText(s);
			}
		});

		vb.gapl();

		//@formatter:off
		hb = new HBox();
			JXLabel label = new JXLabel("Name:");
			hb.add(label);
			hb.gap();
	
			field = Gui.textField();
						
			field.addKeyListener(TextInputValidator.filenames());
			
			hb.add(field);
		vb.add(hb);

		
		vb.gapl();
		
		hb = new HBox();
			hb.glue();
	
			buttonOK = new JButton("Save", Icons.MENU_SAVE_AS);
			hb.add(buttonOK);
	
			hb.gap();
	
			buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
			hb.add(buttonCancel);
		vb.add(hb);
		//@formatter:on

		return vb;
	}


	@Override
	public void onClose() {

		// do nothing
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(saveListener);
		buttonCancel.addActionListener(closeListener);
	}


	private ActionListener saveListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String name = field.getText().trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid name", "Missing project name!");
			}

			if (options.contains(name)) {
				Alerts.error(self(), "Invalid name", "Project named \"" + name + "\" already exists!");
			} else {
				// OK name				
				Tasks.taskSaveProjectAs(name);
				closeDialog();
			}

		}
	};
}
