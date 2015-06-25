package bplusstringtoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implements a B+ tree in which the keys are Strings (with maximum length 60
 * characters) and the values are integers
 */

public class BPlusTreeString60toInt {
	public String start = null;

	public StringToIntNode BRoot;
	private final static int N = 4;

	/**
	 * Returns the String associated with the given key, or null if the key is
	 * not in the B+ tree.
	 */

	public Integer find(String key) {
		if (BRoot == null) {
			return null;
		} else {
			return find(key, BRoot);
		}
	}

	/*
	 * secondary find method
	 */
	private Integer find(String key, StringToIntNode node) {
		if (node.isLeaf()) {
			return ((StringToIntLeafNode) node).values.get(key);
		} else {
			for (int i = 1; i <= ((StringToIntInternalNode) node).size(); i++) {
				if (key.compareTo(((StringToIntInternalNode) node).keys[i]) < 0) {
					return find(key, ((StringToIntInternalNode) node).children[i - 1]);
				}
			}
			return find(key, ((StringToIntInternalNode) node).children[node.size()]);

		}
	}

	/**
	 * Stores the value associated with the key in the B+ tree. If the key is
	 * already present, replaces the associated value. If the key is not
	 * present, adds the key with the associated value
	 * 
	 * @param key
	 * @param value
	 * @return whether pair was successfully added.
	 */
	public boolean put(String key, int value) {

		if (start == null) {
			start = key;
		} else if (key.compareTo(start) < 0) {
			start = key;
		}
		return add(key, value);
	}

	private boolean add(String key, int value) {
		if (BRoot == null) {
			BRoot = new StringToIntLeafNode(key, value);
		} else {
			TupleType t = add(key, value, BRoot);
			if (t != null) {
				StringToIntInternalNode node = new StringToIntInternalNode();
				node.children[0] = BRoot;
				node.keys[1] = t.key;
				node.children[1] = t.rightChild;
				BRoot = node;
			}
		}

		return true;
	}

	/**
	 * adds the particular key
	 * 
	 * @param key
	 *            number to be added
	 * @param value
	 *            value associated with that key
	 * @param node
	 *            to add key and value to
	 * @return the wrapper class containing a key and rightChild
	 */
	private TupleType add(String key, int value, StringToIntNode node) {

		if (node.isLeaf()) {
			if (node.size() < N) {
				((StringToIntLeafNode) node).values.put(key, value);
				return null;
			} else {
				return splitLeaf(key, value, node);
			}
		} else {

			for (int i = 1; i <= node.size(); i++) {
				if (key.compareTo(((StringToIntInternalNode) node).keys[i]) < 0) {
					TupleType t = add(key, value,
							((StringToIntInternalNode) node).children[i - 1]);
					if (t == null) {
						return null;
					} else {
						return dealWithPromote(t, node);
					}
				}
			}
			TupleType t = add(key, value,
					((StringToIntInternalNode) node).children[node.size()]);
			if (t == null) {
				return null;
			} else {
				return dealWithPromote(t, node);
			}
		}
	}

	/**
	 * splits the leaf if the node becomes too full
	 * 
	 * @param key
	 *            number to be added
	 * @param value
	 *            value associated with that key
	 * @param node
	 *            to add key and value to
	 * @return the wrapper class containing a key and rightChild
	 */
	private TupleType splitLeaf(String key, int value, StringToIntNode node) {

		((StringToIntLeafNode) node).values.put(key, value);
		StringToIntLeafNode sibling = new StringToIntLeafNode();

		String mid = (((StringToIntLeafNode) node).getMid());

		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, Integer> v : ((StringToIntLeafNode) node).values.entrySet()) {
			if (mid.compareTo(v.getKey()) <= 0) {
				sibling.values.put(v.getKey(), v.getValue());
				list.add(v.getKey());
			}
		}

		for (String i : list) {
			((StringToIntLeafNode) node).values.remove(i);
		}
		sibling.next = ((StringToIntLeafNode) node).next;
		((StringToIntLeafNode) node).next = sibling;

		return new TupleType(sibling.values.firstKey(), sibling);

	}

	/**
	 * deals when a node needs to be promoted when a node is full
	 * 
	 * @param tuple
	 *            wrapper class containing key and rightChild
	 * @param node
	 *            node which is getting dealt to
	 * @return tuple now containing the new promoted key and new right sibling
	 */
	private TupleType dealWithPromote(TupleType tuple, StringToIntNode node) {

		if (tuple == null) {
			return null;
		}
		int size = node.size();

		if (tuple.key.compareTo(((StringToIntInternalNode) node).keys[size]) > 0) {
			((StringToIntInternalNode) node).keys[size + 1] = tuple.key;
			((StringToIntInternalNode) node).children[size + 1] = tuple.rightChild;

		} else {
			for (int i = 1; i <= size; i++) {
				if (tuple.key.compareTo(((StringToIntInternalNode) node).keys[i]) < 0) {
					moveRight(((StringToIntInternalNode) node).keys, i, size);
					moveRightChild(((StringToIntInternalNode) node).children, i, size);

					((StringToIntInternalNode) node).keys[i] = tuple.key;
					((StringToIntInternalNode) node).children[i] = tuple.rightChild;
					break;
				}
			}
		}
		if (node.size() <= N) {
			return null;
		}

		StringToIntInternalNode sibling = new StringToIntInternalNode();
		int mid = (node.size() / 2) + 1;
		int nodeSize = node.size();
		for (int i = mid + 1; i <= nodeSize; i++) {
			sibling.keys[i - mid] = ((StringToIntInternalNode) node).keys[i];
			((StringToIntInternalNode) node).keys[i] = null;
		}
		for (int i = mid; i <= nodeSize; i++) {
			sibling.children[i - mid] = ((StringToIntInternalNode) node).children[i];
			((StringToIntInternalNode) node).children[i] = null;
		}
		String promoteKey = ((StringToIntInternalNode) node).keys[mid];
		((StringToIntInternalNode) node).keys[mid] = null;
		return new TupleType(promoteKey, sibling);

	}

	/**
	 * moves all of a nodes children to the right
	 * 
	 * @param children
	 *            to be move
	 * @param start
	 *            where to start moving
	 * @param end
	 *            where to end moving
	 */
	private void moveRightChild(StringToIntNode[] children, int start, int end) {
		for (int i = end + 1; i > start; i--) {
			children[i] = children[i - 1];
		}

	}

	/**
	 * moves all of a nodes keys to the right
	 * 
	 * @param keys
	 *            to be move
	 * @param start
	 *            where to start moving
	 * @param end
	 *            where to end moving
	 */
	private void moveRight(String[] keys, int start, int end) {
		for (int i = end + 1; i > start; i--) {
			keys[i] = keys[i - 1];
		}

	}

	/**
	 * finds the first leaf in the tree
	 * 
	 * @return return the first leaf
	 */
	public StringToIntLeafNode findFirst() {

		StringToIntNode node = (StringToIntInternalNode) BRoot;
		while (((StringToIntInternalNode) node).children[0] instanceof StringToIntInternalNode) {
			node = ((StringToIntInternalNode) node).children[0];
		}
		StringToIntLeafNode leaf = (StringToIntLeafNode) ((StringToIntInternalNode) node).children[0];

		return leaf;

	}

	/*
	 * iterate through all the leaves printing them all out
	 */
	public void iterateAll() {

		StringToIntLeafNode node = findFirst();

		while (node != null && node instanceof StringToIntLeafNode) {
			for (Map.Entry<String, Integer> value : node.values.entrySet()) {
				System.out.println(value.getValue());
			}
			System.out.println();
			node = node.next;
		}

	}

	/**
	 * prints out all the internal keys and the leaves in order
	 */
	public void printKeys() {

		if (BRoot instanceof StringToIntInternalNode) {
			System.out.println("******");
			BRoot.print();
			System.out.println("******");
			for (StringToIntNode i : ((StringToIntInternalNode) BRoot).children) {

				if (i instanceof StringToIntInternalNode) {
					System.out.println("-----");

					i.print();
					System.out.println("-----");

					for (StringToIntNode j : ((StringToIntInternalNode) i).children) {
						if (j instanceof StringToIntLeafNode) {
							System.out.println("...");
							j.print();
							System.out.println("...");
						}
					}
				}

			}
		}
	}

	/*
	 * tuple type that stores just two fields, key and rightChild
	 */
	private class TupleType {
		public String key;
		public StringToIntNode rightChild;

		public TupleType(String key, StringToIntNode rightChild) {
			this.key = key;
			this.rightChild = rightChild;
		}

	}

}
