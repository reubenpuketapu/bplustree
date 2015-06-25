package bplusinttostring;

import java.util.TreeMap;

public class IntToStringLeafNode implements IntToStringNode {

	public TreeMap<Integer, String> values;
	public IntToStringLeafNode next;

	public IntToStringLeafNode() {
		this.values = new TreeMap<Integer, String>();
	}

	public IntToStringLeafNode(int key, String value) {
		this.values = new TreeMap<Integer, String>();
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

	public int getMid() {
		int mid = 0;
		int max = values.size();
		for (Integer value : values.keySet()) {
			if (mid == (max / 2)) {
				return value;
			}
			mid++;
		}
		return 0;
	}

}
