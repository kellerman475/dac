package com.kellerman.frame;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.TitledBorder;

import com.kellerman.MySubject;
import com.kellerman.bufferio.BufferIn;

public class AuthorizationFrame extends JFrame {
	private static final long serialVersionUID = -3299150748913278070L;
	static JPasswordField passwd = new JPasswordField(10);
	JButton grant = new JButton("Grant");
	JButton revoke = new JButton("Revoke");
	JButton deleteSub = new JButton("DeleteSubject");
	JButton deleteOb = new JButton("DeleteObject");
	JButton addOb = new JButton("AddNewObject");
	JLabel subFrom = new JLabel("FromSubject:");
	JLabel subTo = new JLabel("ToSubject:");
	JLabel ob1 = new JLabel("Object:");
	JLabel rightLabel = new JLabel("Right:");
	JLabel sub = new JLabel("Subject:");
	JLabel ob2 = new JLabel("Object:");
	JComboBox<String> sBox1 = new JComboBox<String>();
	JComboBox<String> sBox2 = new JComboBox<String>();
	JComboBox<String> objBox1 = new JComboBox<String>();
	JComboBox<String> rightBox = new JComboBox<String>();
	JComboBox<String> sBox3 = new JComboBox<String>();
	JComboBox<String> objBox2 = new JComboBox<String>();

	public AuthorizationFrame() {
		setTitle("Modification");
		JPanel jpanel1 = new JPanel();
		jpanel1.add(subFrom);
		jpanel1.add(sBox1);
		jpanel1.add(subTo);
		jpanel1.add(sBox2);
		jpanel1.add(ob1);
		jpanel1.add(objBox1);
		jpanel1.add(rightLabel);
		jpanel1.add(rightBox);
		JPanel jpanel3 = new JPanel();
		jpanel3.setBorder(new TitledBorder("Operation"));
		jpanel3.add(grant);
		jpanel3.add(revoke);
		JPanel jpanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 10));
		jpanel2.setBorder(new TitledBorder("Delete"));
		jpanel2.add(sub);
		jpanel2.add(sBox3);
		jpanel2.add(ob2);
		jpanel2.add(objBox2);
		jpanel2.add(deleteSub);
		jpanel2.add(deleteOb);
		jpanel2.add(addOb);
		Container cp = getContentPane();
		cp.add(jpanel3);
		cp.add(jpanel1);
		cp.add(jpanel2);
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
		setUpdate();
		grant.addMouseListener(new grantMouseListener());
		revoke.addMouseListener(new revokeMouseListener());
		deleteSub.addMouseListener(new deleteSubMouseListener());
		deleteOb.addMouseListener(new deleteObMouseListener());
		addOb.addMouseListener(new addNewObjectMouseListener());
		setVisible(true);
		setSize(1000, 300);
	}

	public void setUpdate() {
		String[] rightStr = { "Write", "Read", "Execute", "Control" };
		Set<String> subSet = BufferIn.getSubjectTable().keySet();
		HashSet<String> obSet = new HashSet<String>();
		Iterator<String> subIt = subSet.iterator();
		while (subIt.hasNext()) {
			String temp = subIt.next();
			sBox1.addItem(temp);
			sBox2.addItem(temp);
			sBox3.addItem(temp);
			Iterator<String> obStrs = BufferIn.getSubjectTable().get(temp)
					.getTable().keySet().iterator();
			while (obStrs.hasNext()) {
				obSet.add(obStrs.next());
			}
		}
		for (int i = 0; i < 4; ++i) {
			rightBox.addItem(rightStr[i]);
		}
		Iterator<String> obIts = obSet.iterator();
		while (obIts.hasNext()) {
			String temp = obIts.next();
			objBox1.addItem(temp);
			objBox2.addItem(temp);
		}
	}

	public static String showInputDialog() {
		JOptionPane localJOptionPane = new JOptionPane(
				"Please input this subject's password:",
				JOptionPane.QUESTION_MESSAGE);
		localJOptionPane.add(passwd);
		passwd.setEchoChar('*');
		JDialog localJDialog = localJOptionPane.createDialog(localJOptionPane,
				"Input");
		localJDialog.setVisible(true);
		String localObject = String.valueOf(passwd.getPassword());
		localJDialog.dispose();
		return localObject;
	}

	class grantMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			MySubject subFrom = BufferIn.getSubjectTable().get(
					sBox1.getSelectedItem().toString());
			MySubject subTo = BufferIn.getSubjectTable().get(
					sBox2.getSelectedItem().toString());
			String toOb = objBox1.getSelectedItem().toString();
			int index = rightBox.getSelectedIndex() + 1;
			String message = BufferIn.grantPermission(subFrom, subTo, toOb,
					index);
			JOptionPane.showMessageDialog(null, message, "Attention",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	class revokeMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			MySubject subFrom = BufferIn.getSubjectTable().get(
					sBox1.getSelectedItem().toString());
			MySubject subTo = BufferIn.getSubjectTable().get(
					sBox2.getSelectedItem().toString());
			String toOb = objBox1.getSelectedItem().toString();
			int index = rightBox.getSelectedIndex() + 1;
			String message = BufferIn.revokeObjectPermission(subFrom, subTo,
					toOb, index);
			JOptionPane.showMessageDialog(null, message, "Attention",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	class deleteSubMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			String sub = sBox3.getSelectedItem().toString();
			String str = showInputDialog();
			if (BufferIn.getUserPasswd().get(sub).equals(str)) {
				BufferIn.deleteSubject(sub);
				sBox1.removeItem(sub);
				sBox2.removeItem(sub);
				sBox3.removeItem(sub);
				JOptionPane.showMessageDialog(null, "Delete completed!",
						"Attention", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "Password is wrong!",
						"Warning", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	class deleteObMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			String sub = sBox3.getSelectedItem().toString();
			String ob = objBox2.getSelectedItem().toString();
			String message = BufferIn.deleteObject(sub, ob);
			JOptionPane.showMessageDialog(null, message, "Attention",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	class addNewObjectMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			String userName = JOptionPane
					.showInputDialog("Please input new coming user name:");
			if (BufferIn.getSubjectTable()
					.get(sBox3.getSelectedItem().toString())
					.addObject(userName)) {
				objBox1.addItem(userName);
				objBox2.addItem(userName);
				JOptionPane.showMessageDialog(null,
						"New object created success!", "Attention",
						JOptionPane.INFORMATION_MESSAGE);
			} else
				JOptionPane.showMessageDialog(null,
						"The user has already existed!", "Warning",
						JOptionPane.INFORMATION_MESSAGE);
		}
	}
}