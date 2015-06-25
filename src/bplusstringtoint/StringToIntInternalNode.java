package bplusstringtoint;

public class StringToIntInternalNode implements StringToIntNode {

	public String[] keys;
	public StringToIntNode[] children;

	public StringToIntInternalNode() {
		this.keys = new String[6];
		this.children = new StringToIntNode[6];
	}

	public StringToIntInternalNode(String[] keys, StringToIntNode[] children) {
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
			if (keys[i] != null) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void print() {
		for (String i : keys) {
			System.out.println(i);
		}

	}

}
