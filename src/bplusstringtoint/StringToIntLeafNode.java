package bplusstringtoint;

import java.util.TreeMap;

public class StringToIntLeafNode implements StringToIntNode {

	public TreeMap<String, Integer> values;
	public StringToIntLeafNode next;

	public StringToIntLeafNode() {
		this.values = new TreeMap<String, Integer>();
	}

	public StringToIntLeafNode(String key, int value) {
		this.values = new TreeMap<String, Integer>();
		values.put(key, value);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public void print() {
		System.out.println(values.keySet());
	}

	public String getMid() {
		int mid = 0;
		int max = values.size();
		for (String value : values.keySet()) {
			if (mid == (max / 2)) {
				return value;
			}
			mid++;
		}
		return null;
	}

}
