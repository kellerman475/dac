package com.kellerman.frame;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.kellerman.MySubject;
import com.kellerman.Permissions;
import com.kellerman.bufferio.BufferIn;

public class LoginDialog extends JDialog {
	private static final long serialVersionUID = 4143193992367331825L;
	JLabel name = new JLabel("Name:");
	JTextField nameField = new JTextField(10);
	JLabel passwd = new JLabel("Password:");
	JPasswordField passwdField = new JPasswordField(10);
	JButton newSubject = new JButton("New");
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");

	public LoginDialog() throws IOException, ClassNotFoundException {
		dialogInit();
		new BufferIn();
		Container cp = getContentPane();
		cp.add(name);
		cp.add(nameField);
		cp.add(passwd);
		cp.add(passwdField);
		cp.add(newSubject);
		cp.add(ok);
		cp.add(cancel);
		newSubject.addMouseListener(new NewSubjectListener());
		ok.addMouseListener(new OkButtonListener());
		cancel.addMouseListener(new CancelButtonListener());
		passwdField.setEchoChar('*');
		cp.setLayout(new FlowLayout(FlowLayout.RIGHT, 50, 40));
		setTitle("Login");
		setSize(350, 300);
		setResizable(false);
		setVisible(true);
	}

	// create a new Subject
	class NewSubjectListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			String user = nameField.getText();
			String pass = String.valueOf(passwdField.getPassword());
			if (user.length() > 0 && pass.length() > 0) {
				if (BufferIn.getUserPasswd().put(user, pass) != null) {
					JOptionPane.showMessageDialog(null,
							"This user has existed!", "Error",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					MySubject ms = new MySubject(user,
							new TreeMap<String, TreeMap<String, Permissions>>());
					BufferIn.getSubjectTable().put(user, ms);
					BufferIn.getUserPasswd().put(user, pass);
					JOptionPane.showMessageDialog(null,
							"The new user created success!");
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"The wrong user or password!");
			}
		}
	}

	// ready to login
	class OkButtonListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			String user = nameField.getText();
			String pass = String.valueOf(passwdField.getPassword());
			if (pass.equals(BufferIn.getUserPasswd().get(user))) {
				new Viewer();
			} else {
				JOptionPane.showMessageDialog(null,
						"The wrong user or password!", "Error",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	// ready to deny
	class CancelButtonListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			dispose();
		}
	}
}