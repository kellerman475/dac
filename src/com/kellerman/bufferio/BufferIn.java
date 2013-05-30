package com.kellerman.bufferio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.TreeMap;

import com.kellerman.MySubject;
import com.kellerman.Permissions;

public class BufferIn {
	private static TreeMap<String, String> userPasswd = new TreeMap<String, String>();
	private static TreeMap<String, MySubject> subjectTable = new TreeMap<String, MySubject>();

	public BufferIn() throws IOException, ClassNotFoundException {
		if (new File("STable.txt").exists() == false) {
			new File("STable.txt").createNewFile();
		}
		FileReader test = new FileReader("STable.txt");
		if (test.read() == -1) {
			test.close();
			return;
		}
		test.close();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"STable.txt"));
		subjectTable = (TreeMap<String, MySubject>) in.readObject();
		userPasswd = (TreeMap<String, String>) in.readObject();
		in.close();
	}

	public static void updateToLocalFile() throws IOException,
			ClassNotFoundException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				"STable.txt"));
		out.writeObject(subjectTable);
		out.writeObject(userPasswd);
		out.close();
	}

	// have to be sure toObject exist
	public static String revokeObjectPermission(MySubject fromSubject,
			MySubject toSubject, String toObject, int index) {
		if (toSubject.getTable().containsKey(toObject)) {
			TreeMap<String, Permissions> tempPer = toSubject.getTable().get(
					toObject);
			if (tempPer.containsKey(fromSubject.getName()) == false)
				return "Revoke fail without right"; // "revoke fail without right";
			if (tempPer.get(fromSubject.getName()).getValueIndex(index) == false)
				return "Cannot revoke the right that doesn't exist";
			if (fromSubject.getName().equals(toSubject.getName()))
				return "Cannot revoke the right from self";
			if (tempPer.get(fromSubject.getName()).getValueIndex(4)) {
				// have control right
				tempPer.get(fromSubject.getName()).setValueIndex(index, false);
				Iterator<String> toSubs = tempPer.keySet().iterator();
				while (toSubs.hasNext()) { // to judge whether it was grant by
											// others
					if (tempPer.get(toSubs.next()).getValueIndex(index)) {
						return "Revoke success";
					}
				}
				Iterator<String> subs = subjectTable.keySet().iterator();
				while (subs.hasNext()) {
					MySubject sub = subjectTable.get(subs.next());
					revokeObjectPermission(toSubject, sub, toObject, index);
				}
			} else {
				// revoke this auth without control right
				tempPer.get(fromSubject.getName()).setValueIndex(index, false);
			}
			return "Revoke success";
		}
		return "Cannot revoke the right that doesn't exist";
	}

	// only for the loginself,hava to input correct user and password
	public static void deleteSubject(String subjectStr) {
		getUserPasswd().remove(subjectStr);
		MySubject s = getSubjectTable().get(subjectStr);
		Iterator<String> itSub = getSubjectTable().keySet().iterator();
		Object[] itObs = s.getTable().keySet().toArray();
		int len = itObs.length;
		if (len <= 0)
			return;
		while (itSub.hasNext()) {
			MySubject sub = getSubjectTable().get(itSub.next());
			for (int i = 0; i < len; ++i) {
				revokeObjectPermission(s, sub, itObs[i].toString(), 1);
				revokeObjectPermission(s, sub, itObs[i].toString(), 2);
				revokeObjectPermission(s, sub, itObs[i].toString(), 3);
				revokeObjectPermission(s, sub, itObs[i].toString(), 4);
			}
		}
		for (int i = 0; i < len; ++i) {
			if (s.getTable().get(itObs[i].toString()).containsKey(s.getName()))
				deleteObjectFromAuth(itObs[i].toString());
		}
		getSubjectTable().remove(subjectStr);
	}

	public static String grantPermission(MySubject fromSubject,
			MySubject toSubject, String withObject, int index) {
		// firstly,fromSubject cannot equal to toSubject
		if (fromSubject.getName().equals(toSubject.getName()))
			return "Cannot grant permissions to self";
		if (fromSubject.getTable().containsKey(withObject) == false)
			return "The object doesn't exist in fromSubject";
		Object[] perValue = fromSubject.getTable().get(withObject).values()
				.toArray();
		int i;
		int j = 0, k = 0;
		for (i = 0; i < perValue.length; ++i) {
			if (((Permissions) perValue[i]).getValueIndex(4))
				j = 1;
			if (((Permissions) perValue[i]).getValueIndex(index))
				k = 1;
		}
		if (j == 0 || k == 0)
			return "The fromSubject doesn't have right for object";
		Permissions withPermission = new Permissions();
		withPermission.setValueIndex(index, true);
		// secondly,cannot grant permission to where you get
		if (getPathDFS(toSubject, fromSubject, withObject, index))
			return "Cannot grant permissions to someone you get from";
		else {
			if (toSubject.getTable().containsKey(withObject) == false) {
				toSubject.getTable().put(withObject,
						new TreeMap<String, Permissions>());
			}
			TreeMap<String, Permissions> p = toSubject.getTable().get(
					withObject);
			if (p.containsKey(fromSubject.getName()))
				p.get(fromSubject.getName()).setPermissions(withPermission);
			else
				p.put(fromSubject.getName(), new Permissions(withPermission));
		}
		return "Success";
	}

	public static boolean getPathDFS(MySubject one, MySubject two,
			String oneObject, int index) {
		if (two.getTable().containsKey(oneObject)) {
			TreeMap<String, Permissions> temp = two.getTable().get(oneObject);
			Iterator<String> it = temp.keySet().iterator();
			while (it.hasNext()) {
				String tempSub = it.next();
				if (tempSub.equals(two.getName()))
					continue;
				if (tempSub.equals(one.getName())
						&& temp.get(tempSub).getValueIndex(index))
					return true;
				if (getPathDFS(one, subjectTable.get(tempSub), oneObject, index)) {
					return true;
				}
			}
		}
		return false;
	}

	// only for the owner
	public static String deleteObject(String subjectValue, String objectValue) {
		MySubject getSub = subjectTable.get(subjectValue);
		if (getSub.getTable().containsKey(objectValue)) {
			TreeMap<String, Permissions> getAuth = getSub.getTable().get(
					objectValue);
			if (getAuth.containsKey(getSub.getName())) {
				deleteObjectFromAuth(objectValue);
				return "delete object success";
			}
		}
		return "delete object fail for subject without right";
	}

	public static void deleteObjectFromAuth(String objectValue) {
		Iterator<String> allSub = subjectTable.keySet().iterator();
		while (allSub.hasNext()) {
			String tempSub = allSub.next();
			subjectTable.get(tempSub).getTable().remove(objectValue);
		}
	}

	public static TreeMap<String, String> getUserPasswd() {
		return userPasswd;
	}

	public static TreeMap<String, MySubject> getSubjectTable() {
		return subjectTable;
	}
}