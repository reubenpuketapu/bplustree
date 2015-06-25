package bplusinttostring;


public class IntToStringInternalNode implements IntToStringNode {

	public int[] keys;
	public IntToStringNode[] children;

	public IntToStringInternalNode() {
		this.keys = new int[6];
		this.children = new IntToStringNode[6];
	}

	public IntToStringInternalNode(int[] keys, IntToStringNode[] children) {
		this.keys = keys;
		this.children = children;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public int size() {
		int count = 0;
		for (int i = 1; i < keys.length; i++) {
			if (keys[i] != 0) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void print() {
		for (Integer i : keys) {
			System.out.println(i);
		}

	}

}
