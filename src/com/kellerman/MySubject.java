package com.kellerman;

import java.io.Serializable;
import java.util.TreeMap;

public class MySubject implements Serializable {
	private static final long serialVersionUID = 5070763825926945010L;
	private String name;
	// <objects,List>,the List with AuthSubjects-Permissions
	private final TreeMap<String, TreeMap<String, Permissions>> table;

	public MySubject(String n, TreeMap<String, TreeMap<String, Permissions>> t) {
		name = n;
		table = t;
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	public TreeMap<String, TreeMap<String, Permissions>> getTable() {
		return table;
	}

	// only for the owner
	public boolean addObject(String objectValue) {
		if (this.table.containsKey(objectValue))
			return false;
		else {
			TreeMap<String, Permissions> newAuth = new TreeMap<String, Permissions>();
			newAuth.put(name, new Permissions(true));
			table.put(objectValue, newAuth);
			return true;
		}
	}
}