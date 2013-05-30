package com.kellerman.frame;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import com.kellerman.bufferio.BufferIn;

public class Viewer extends JFrame {
	private static final long serialVersionUID = 8904664764783809335L;
	private final JButton edit = new JButton("Edit");
	private final JButton fllush = new JButton("Fllush");
	private final JButton save = new JButton("Save");
	private JTable table = null;
	private InformationTable model = null;
	JScrollPane scroll = new JScrollPane();

	public Viewer() {
		model = new InformationTable();
		table = new JTable(model) {
			private static final long serialVersionUID = -60793358358809091L;

			// cannot edit the table
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		scroll.setViewportView(table);
		scroll.setBorder(new TitledBorder("Control Table"));
		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder("Menu"));
		panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 100, 50));
		panel1.add(edit);
		panel1.add(fllush);
		panel1.add(save);
		edit.addMouseListener(new editMouseListener());
		fllush.addMouseListener(new fflushMouseListener());
		save.addMouseListener(new saveMouseListener());
		Container cp = getContentPane();
		cp.add(panel1);
		cp.add(scroll);
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
		setTitle("Access Control System");
		setVisible(true);
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	class editMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			myEditThread one = new myEditThread();
			one.start();
		}
	}

	class fflushMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			for (int i = 0; i < 40; ++i) {
				model.removeRow(0);
			}
			model.setRowCount(40);
			model.setUpdate();
		}
	}

	class saveMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			mySaveThread one = new mySaveThread();
			one.start();
		}
	}

	class myEditThread extends Thread {
		@Override
		public void run() {
			new AuthorizationFrame();
		}
	}

	class mySaveThread extends Thread {
		@Override
		public synchronized void run() {
			try {
				BufferIn.updateToLocalFile();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}