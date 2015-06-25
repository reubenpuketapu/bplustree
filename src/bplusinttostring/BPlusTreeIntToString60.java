package bplusinttostring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implements a B+ tree in which the keys are integers and the values are
 * Strings (with maximum length 60 characters)
 */

public class BPlusTreeIntToString60 {

	public int start = Integer.MAX_VALUE;

	public IntToStringNode BRoot;
	private final static int N = 4;

	/**
	 * Returns the String associated with the given key, or null if the key is
	 * not in the B+ tree.
	 */

	public String find(int key) {
		if (BRoot == null) {
			return null;
		} else {
			return find(key, BRoot);
		}
	}

	/*
	 * secondary find method
	 */
	private String find(int key, IntToStringNode node) {
		if (node.isLeaf()) {
			return ((IntToStringLeafNode) node).values.get(key);
		} else {
			for (int i = 1; i <= ((IntToStringInternalNode) node).size(); i++) {
				if (key < ((IntToStringInternalNode) node).keys[i]) {
					return find(key,
							((IntToStringInternalNode) node).children[i - 1]);
				}
			}
			return find(key,
					((IntToStringInternalNode) node).children[node.size()]);

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
	public boolean put(int key, String value) {

		if (key < start) {
			start = key;
		}
		return add(key, value);
	}

	private boolean add(int key, String value) {
		if (BRoot == null) {
			BRoot = new IntToStringLeafNode(key, value);
		} else {
			TupleType t = add(key, value, BRoot);
			if (t != null) {
				IntToStringInternalNode node = new IntToStringInternalNode();
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
	private TupleType add(int key, String value, IntToStringNode node) {

		if (node.isLeaf()) {
			if (node.size() < N) {
				if (((IntToStringLeafNode) node).values.containsKey(key)) {
					((IntToStringLeafNode) node).values.put(key,
							((IntToStringLeafNode) node).values.get(key) + " "
									+ value);

				} else {
					((IntToStringLeafNode) node).values.put(key, value);
				}
				return null;
			} else {
				return splitLeaf(key, value, node);
			}
		} else {

			for (int i = 1; i <= node.size(); i++) {
				if (key < ((IntToStringInternalNode) node).keys[i]) {
					TupleType t = add(key, value,
							((IntToStringInternalNode) node).children[i - 1]);
					if (t == null) {
						return null;
					} else {
						return dealWithPromote(t, node);
					}
				}
			}
			TupleType t = add(key, value,
					((IntToStringInternalNode) node).children[node.size()]);
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
	private TupleType splitLeaf(int key, String value, IntToStringNode node) {

		if (((IntToStringLeafNode) node).values.containsKey(key)) {
			((IntToStringLeafNode) node).values.put(key,
					((IntToStringLeafNode) node).values.get(key) + " " + value);

		} else {
			((IntToStringLeafNode) node).values.put(key, value);
		}
		IntToStringLeafNode sibling = new IntToStringLeafNode();

		int mid = (((IntToStringLeafNode) node).getMid());

		List<Integer> list = new ArrayList<Integer>();
		for (Map.Entry<Integer, String> v : ((IntToStringLeafNode) node).values
				.entrySet()) {
			if (v.getKey() >= mid) {
				if (((IntToStringLeafNode) sibling).values.containsKey(key)) {
					((IntToStringLeafNode) sibling).values.put(key,
							((IntToStringLeafNode) sibling).values.get(key)
									+ " " + value);

				} else {
					((IntToStringLeafNode) sibling).values.put(key, value);
				}
				list.add(v.getKey());
			}
		}

		for (Integer i : list) {
			((IntToStringLeafNode) node).values.remove(i);
		}
		sibling.next = ((IntToStringLeafNode) node).next;
		((IntToStringLeafNode) node).next = sibling;

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
	private TupleType dealWithPromote(TupleType tuple, IntToStringNode node) {

		if (tuple == null) {
			return null;
		}
		int size = node.size();

		if (tuple.key > ((IntToStringInternalNode) node).keys[size]) {
			((IntToStringInternalNode) node).keys[size + 1] = tuple.key;
			((IntToStringInternalNode) node).children[size + 1] = tuple.rightChild;

		} else {
			for (int i = 1; i <= size; i++) {
				if (tuple.key < ((IntToStringInternalNode) node).keys[i]) {
					moveRight(((IntToStringInternalNode) node).keys, i, size);
					moveRightChild(((IntToStringInternalNode) node).children,
							i, size);

					((IntToStringInternalNode) node).keys[i] = tuple.key;
					((IntToStringInternalNode) node).children[i] = tuple.rightChild;
					break;
				}
			}
		}
		if (node.size() <= N) {
			return null;
		}

		IntToStringInternalNode sibling = new IntToStringInternalNode();
		int mid = (node.size() / 2) + 1;
		int nodeSize = node.size();
		for (int i = mid + 1; i <= nodeSize; i++) {
			sibling.keys[i - mid] = ((IntToStringInternalNode) node).keys[i];
			((IntToStringInternalNode) node).keys[i] = 0;
		}
		for (int i = mid; i <= nodeSize; i++) {
			sibling.children[i - mid] = ((IntToStringInternalNode) node).children[i];
			((IntToStringInternalNode) node).children[i] = null;
		}
		int promoteKey = ((IntToStringInternalNode) node).keys[mid];
		((IntToStringInternalNode) node).keys[mid] = 0;
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
	private void moveRightChild(IntToStringNode[] children, int start, int end) {
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
	private void moveRight(int[] keys, int start, int end) {
		for (int i = end + 1; i > start; i--) {
			keys[i] = keys[i - 1];
		}

	}

	/**
	 * finds the first leaf in the tree
	 * 
	 * @return return the first leaf
	 */
	public IntToStringLeafNode findFirst() {

		IntToStringNode node = (IntToStringInternalNode) BRoot;
		while (((IntToStringInternalNode) node).children[0] instanceof IntToStringInternalNode) {
			node = ((IntToStringInternalNode) node).children[0];
		}
		IntToStringLeafNode leaf = (IntToStringLeafNode) ((IntToStringInternalNode) node).children[0];

		return leaf;

	}

	/*
	 * iterate through all the leaves printing them all out
	 */
	public void iterateAll() {

		IntToStringLeafNode node = findFirst();

		while (node != null && node instanceof IntToStringLeafNode) {
			for (Map.Entry<Integer, String> value : node.values.entrySet()) {
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

		if (BRoot instanceof IntToStringInternalNode) {
			System.out.println("******");
			BRoot.print();
			System.out.println("******");
			for (IntToStringNode i : ((IntToStringInternalNode) BRoot).children) {

				if (i instanceof IntToStringInternalNode) {
					System.out.println("-----");

					i.print();
					System.out.println("-----");

					for (IntToStringNode j : ((IntToStringInternalNode) i).children) {
						if (j instanceof IntToStringLeafNode) {
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
		public int key;
		public IntToStringNode rightChild;

		public TupleType(int key, IntToStringNode rightChild) {
			this.key = key;
			this.rightChild = rightChild;
		}

	}

}
