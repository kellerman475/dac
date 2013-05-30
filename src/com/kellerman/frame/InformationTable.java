package com.kellerman.frame;

import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.table.DefaultTableModel;

import com.kellerman.MySubject;
import com.kellerman.Permissions;
import com.kellerman.bufferio.BufferIn;

public class InformationTable extends DefaultTableModel {
	private static final long serialVersionUID = -3403717320065115283L;
	static String[] columnNames = { "Subject", "Object", "Owner", "Grantor",
			"Write", "Grantor", "Read", "Grantor", "Execute", "Grantor",
			"Control", "Grantor" };

	public InformationTable() {
		super(columnNames, 40);
		setUpdate();
	}

	public void setUpdate() {
		TreeMap<String, MySubject> subjectsTable = BufferIn.getSubjectTable();
		Iterator<String> subjectsKey = subjectsTable.keySet().iterator();
		int rowNow = 0;
		while (subjectsKey.hasNext()) {
			String tempKey = subjectsKey.next();
			MySubject tempSub = subjectsTable.get(tempKey);
			// get temp subject's name
			this.setValueAt(tempSub.getName(), rowNow, 0);
			Iterator<String> tempAllObjects = tempSub.getTable().keySet()
					.iterator();
			while (tempAllObjects.hasNext()) {
				String tempObject = tempAllObjects.next();
				// get one object
				this.setValueAt(tempObject, rowNow, 1);
				// to count authSubjects data in a row below
				TreeMap<String, Permissions> tempAuthSubjects = tempSub
						.getTable().get(tempObject);
				Iterator<String> tempkeys = tempAuthSubjects.keySet()
						.iterator();
				while (tempkeys.hasNext()) {
					String key = tempkeys.next();
					Permissions value = tempAuthSubjects.get(key);
					for (int i = 0; i <= 8; i += 2) {
						// to count permissions and auths
						Object temp = this.getValueAt(rowNow, i + 2);
						boolean boo = value.getValueIndex(i / 2)
								| (temp != null ? temp.toString().contains("t")
										: false);
						this.setValueAt(boo, rowNow, i + 2);
						if (value.getValueIndex(i / 2)) {
							Object temp2 = this.getValueAt(rowNow, i + 3);
							this.setValueAt(temp2 != null ? temp2.toString()
									+ ";" + key : key, rowNow, i + 3);
						}
					}
				}
				rowNow += 1;
			}
		}
	}
}