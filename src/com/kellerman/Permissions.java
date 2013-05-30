package com.kellerman;

import java.io.Serializable;
import java.util.ArrayList;

public class Permissions implements Serializable {
	private static final long serialVersionUID = -665521025598900317L;
	// sequence of permissions collection : own,write,read,execute,control
	private final ArrayList<Boolean> per = new ArrayList<Boolean>();

	public Permissions() {
		per.add(false);
		per.add(false);
		per.add(false);
		per.add(false);
		per.add(false);
	}

	public Permissions(boolean boo) {
		per.add(boo);
		per.add(boo);
		per.add(boo);
		per.add(boo);
		per.add(boo);
	}

	public Permissions(Permissions fromPer) {
		per.addAll(fromPer.getPer());
	}

	public void setPermissions(Permissions fromPer) {
		int index = 0;
		while (index < 5) {
			per.set(index, per.get(index) | fromPer.getValueIndex(index));
			index += 1;
		}
	}

	public void setValueList(Boolean[] valueList) {
		int index = 0;
		while (index < 5) {
			per.set(index, valueList[index]);
			index += 1;
		}
	}

	public void setValueList(String valueString) {
		int index = 0;
		while (index < 5) {
			per.set(index, (valueString.charAt(index) == '1'));
			index += 1;
		}
	}

	public void setValueIndex(int index, Boolean value) {
		per.set(index, value);
	}

	public ArrayList<Boolean> getValueList() {
		return per;
	}

	public String getValueToString() {
		StringBuffer str = new StringBuffer();
		int index = 0;
		while (index < 5) {
			str.append(per.get(index) ? "1" : "0");
			index += 1;
		}
		return str.toString();
	}

	public Boolean getValueIndex(int index) {
		return per.get(index);
	}

	public ArrayList<Boolean> getPer() {
		return per;
	}
}