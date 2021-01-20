import java.util.*;

public class PHPArray<V> implements Iterable<V> {
	private static final int INIT_CAPACITY = 4;
	private int N; // number of key-value pairs in the symbol table
	private int M; // size of linear probing table
	private Node<V>[] entries; // the table
	private Node<V> head; // head of the linked list
	private Node<V> tail; // tail of the linked list
	private Iterator<Pair<V>> iter; // iterator object return for each()

	// create an empty hash table - use 16 as default size
	public PHPArray() {
		this(INIT_CAPACITY);
	}

	// create a PHPArray of given capacity
	public PHPArray(int capacity) {
		M = capacity;
		@SuppressWarnings("unchecked")
		Node<V>[] temp = (Node<V>[]) new Node[M];
		entries = temp;
		head = tail = null;
		N = 0;
	}

	// inner class to return a new Iterator object
	public Iterator<V> iterator() {
		return new MyIterator();
	}

	// Iterator<Pair<V>> object
	public Iterator<Pair<V>> PIterator() {
		return new MyPIterator();
	}

	// insert the key-value pair into the symbol table
	public void put(String key, V val) {
		if (val == null)
			unset(key);

		// double table size if 50% full
		if (N >= M / 2) {
			System.out.println("           " + "Size: " + N + " -- resizing array from " + M + " to " + 2 * M);
			resize(2 * M);
		}

		// linear probing
		int i;
		for (i = hash(key); entries[i] != null; i = (i + 1) % M) {
			// update the value if key already exists
			if (entries[i].key.equals(key)) {
				entries[i].value = val;
				return;
			}
		}
		// found an empty entry
		entries[i] = new Node<V>(key, val);
		// insert the node into the linked list
		// TODO: Insert the node into the doubly linked list in O(1) time
		if (head == null) {
			head = entries[i];
			tail = entries[i];
		} else {
			tail.next = entries[i];
			entries[i].prev = tail;
			// tail = entries[i];
			tail = tail.next;
		}
		// tail.next = entries[i];
		// entries[i].prev = tail;

		N++;
		iter = new MyPIterator();
	}

	// implement key with integer type
	public void put(int key, V val) {
		String Key = Integer.toString(key);
		put(Key, val);
	}

	// return the value associated with the given key, null if no such value
	public V get(String key) {
		for (int i = hash(key); entries[i] != null; i = (i + 1) % M)
			if (entries[i].key.equals(key))
				return entries[i].value;
		return null;
	}

	// return the value with integer key, return null if the key isn't in the table
	public V get(int key) {
		String Key = Integer.toString(key);
		return get(Key);
	}

	// resize the hash table to the given capacity by re-hashing all of the keys
	private void resize(int capacity) {
		PHPArray<V> temp = new PHPArray<V>(capacity);

		// rehash the entries in the order of insertion
		Node<V> current = head;
		while (current != null) {
			temp.put(current.key, current.value);
			current = current.next;
		}
		entries = temp.entries;
		head = temp.head;
		tail = temp.tail;
		M = temp.M;
	}

	// rehash a node while keeping it in place in the linked list
	private void rehash(Node<V> node) {
		// TODO Write the implementation of this function
		int i;
		for (i = hash(node.key); entries[i] != null; i = (i + 1) % M) {

		}
		// check if the key pairs need to rehash
		System.out.println("           " + "key " + node.key + " rehashed...");
		System.out.println();
		entries[i] = node;
	}

	// delete the key (and associated value) from the symbol table
	public void unset(String key) {
		if (get(key) == null)
			return;

		// find position i of key
		int i = hash(key);
		while (!key.equals(entries[i].key)) {
			i = (i + 1) % M;
		}

		// delete node from hash table
		Node<V> toDelete = entries[i];
		entries[i] = null;
		// TODO: delete the node from the linked list in O(1)
		if (toDelete == head && toDelete == tail) {
			head = tail;
			head = null;
			tail = null;
		} else if (toDelete == head) {
			toDelete.next.prev = null;
			head = head.next;
			// toDelete.next = null;
		} else if (toDelete == tail) {
			tail.prev.next = null;
			tail = tail.prev;
			// toDelete.prev = null;
		} else {
			toDelete.prev.next = toDelete.next;
			toDelete.next.prev = toDelete.prev;

		}
		toDelete.prev = null;
		toDelete.next = null;

		// rehash all keys in same cluster
		i = (i + 1) % M;
		while (entries[i] != null) {
			// delete and reinsert
			Node<V> nodeToRehash = entries[i];
			entries[i] = null;
			rehash(nodeToRehash);
			i = (i + 1) % M;
		}

		N--;

		// halves size of array if it's 12.5% full or less
		if (N > 0 && N <= M / 8)
			resize(M / 2);
	}

	public void unset(int key) {
		String Key = Integer.toString(key);
		unset(Key);
	}

	// hash function for keys - returns value between 0 and M-1
	private int hash(String key) {
		return (key.hashCode() & 0x7fffffff) % M;
	}

	// An inner class to store nodes of a doubly-linked list
	// Each node contains a (key, value) pair
	@SuppressWarnings("hiding")
	private class Node<V> {
		private String key;
		private V value;
		private Node<V> next;
		private Node<V> prev;

		Node(String key, V value) {
			this(key, value, null, null);
		}

		Node(String key, V value, Node<V> next, Node<V> prev) {
			this.key = key;
			this.value = value;
			this.next = next;
			this.prev = prev;
		}
	}

	// inner class to return a new Iterator object
	public class MyIterator implements Iterator<V> {
		private Node<V> current;

		public MyIterator() {
			current = head;
		}

		public boolean hasNext() {
			return current != null;
		}

		public V next() {
			V result = current.value;
			current = current.next;
			return result;
		}
	}

	// inner class implement iterator function Returns new Pair<V> object iterator
	private class MyPIterator implements Iterator<Pair<V>> {
		private Node<V> current;

		public MyPIterator() {
			current = head;
		}

		public boolean hasNext() {
			return current != null;
		}

		@Override
		public Pair<V> next() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Pair<V> result = new Pair(current.key, current.value);
			current = current.next;
			return result;
		}
	}

	public static class Pair<V> implements Comparable<Pair<V>> {
		String key;
		V value;

		public Pair(String key, V value) {
			this.key = key;
			this.value = value;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public int compareTo(Pair<V> p) throws ClassCastException {
			return ((Comparable) this.value).compareTo((Comparable) p.value);
		}
	}

	// iterate over Pair<V> objects and access their Key Value
	public Pair<V> each() {
		if (iter == null) {
			reset();
		}
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;
	}

	// returns an array list that contains all keys of all nodes in insertion order.
	public ArrayList<String> keys() {
		ArrayList<String> keys = new ArrayList<String>();
		Node<V> temp = head;
		while (temp != null) {
			keys.add(temp.key);
			temp = temp.next;
		}
		return keys;
	}

	// returns an array list that contains all values of all nodes in insertion
	// order.
	public ArrayList<V> values() {
		ArrayList<V> values = new ArrayList<V>();
		Node<V> temp = head;
		while (temp != null) {
			values.add(temp.value);
			temp = temp.next;
		}
		return values;
	}

	// print table
	public void showTable() {
		System.out.println("\tRaw Hash Table Contents: ");
		for (int i = 0; i < M; i++) {
			if (entries[i] == null) {
				System.out.println(i + ": " + null);
			} else {
				System.out.println(i + ": " + "Key: " + entries[i].key + " Value: " + entries[i].value);
			}
		}
	}

	// return current hash table capacity
	public int length() {
		return N;
	}

	// reset the iterator to head node.
	public void reset() {
		iter = new MyPIterator();
	}

	// return an arraylist that contains pair<V> of all nodes
	public ArrayList<Pair<V>> pairs() {
		ArrayList<Pair<V>> pairs = new ArrayList<>();
		Iterator<Pair<V>> cur = new MyPIterator();
		while (cur.hasNext()) {
			pairs.add(cur.next());
		}
		return pairs;

	}

	// clear table
	@SuppressWarnings("unchecked")
	public void clear() {
		entries = (Node<V>[]) new Node[M];
		N = 0;
		head = tail = null;
	}

	// Sort the values using the Comparable interface (if the data is not
	// Comparable you should throw an exception when this method is tried)
	// - Assign new keys to the values starting at 0 and ending at length()-1
	// - Have the linked access also be the sorted result (i.e. an iterator()
	// should iterate in sorted order)

	public void sort() throws ClassCastException {
		reset();
		ArrayList<Pair<V>> list = (ArrayList<Pair<V>>) pairs();

		for (Node<V> n = head; n != null; n = n.next) {
			if (!(n.value instanceof Comparable))
				throw new ClassCastException();
		}
		Collections.sort(list);
		clear();
		for (int i = 0; i < list.size(); i++) {
			put(i, list.get(i).value);
		}

	}
	// The asort() method will sort the values just like sort(), but instead of
	// reassigning the keys to ints starting at 0, it will keep the keys as they
	// were.

	public void asort() {
		reset();
		ArrayList<Pair<V>> list = (ArrayList<Pair<V>>) pairs();
		for (Node<V> n = head; n != null; n = n.next) {
			if (!(n.value instanceof Comparable))
				throw new ClassCastException();
		}
		Collections.sort(list);
		clear();
		for (int i = 0; i < list.size(); i++) {
			put(list.get(i).key, list.get(i).value);
		}
	}

	// flip keys and values of the original array in a new PHPArray
	public PHPArray<String> array_flip() {
		reset();
		PHPArray<String> NewArr = new PHPArray<>(M);
		Node<V> temp = head;
		// If the value of the original array is not a String, the array_flip() method
		// should throw a ClassCastException.

		for (Node<V> n = head; n != null; n = n.next) {
			if (!(n.value instanceof String))
				throw new ClassCastException("Cannot convert class java.lang.Integer to String ");
		}
		/*
		 * if(!temp.value.getClass().equals(String.class)){ throw new
		 * ClassCastException( "Cannot convert class java.lang.Integer to String "); }
		 */
		// flipped array only the last key will be preserved as a value
		while (temp != null) {
			NewArr.put((String) temp.value, temp.key);
			temp = temp.next;
		}
		return NewArr;
	}

	//////// Extra Credit/////

	// Calculate the sum of values in an array
	public Double array_sum() {
		reset();
		ArrayList<V> list = values();
		Double sum = 0.0;

		for (Node<V> n = head; n != null; n = n.next) {
			if (!(n.value instanceof Number))
				throw new ClassCastException();
		}

		for (int i = 0; i < list.size(); i++)
			sum += ((Number) list.get(i)).doubleValue();
		return sum;
	}

	// Return an array with elements in reverse order
	public PHPArray<V> array_reverse() throws ClassCastException {
		reset();
		PHPArray<V> rev = new PHPArray<>(M);
		Node<V> temp = tail;
		while (temp != null) {
			rev.put(temp.key, temp.value);
			temp = temp.prev;
		}
		return rev;
	}

	// Shuffle an array
	public void shuffle() throws ClassCastException {
		reset();
		ArrayList<Pair<V>> list = (ArrayList<Pair<V>>) pairs();
		for (Node<V> n = head; n != null; n = n.next) {
			if (!(n.value instanceof Comparable))
				throw new ClassCastException();
		}
		Collections.shuffle(list);
		clear();

		for (int i = 0; i < list.size(); i++) {
			this.put(list.get(i).key, list.get(i).value);
		}
	}

	// Calculate the product of values in an array
	public Double array_product() {
		reset();
		ArrayList<V> list = values();
		Double pro = 1.0;

		for (Node<V> n = head; n != null; n = n.next) {
			if (!(n.value instanceof Number))
				throw new ClassCastException();
		}

		for (int i = 0; i < list.size(); i++)
			pro *= ((Number) list.get(i)).doubleValue();
		return pro;
	}

	// Changes the case of all keys in an array
	// 1 for upper case 2 for lower case
	public void array_change_key_case(int cases) {
		Pair<V> curr;
		reset();
		ArrayList<Pair<V>> list = pairs();
		clear();

		if (cases == 1) {
			for (int i = 0; i < list.size(); i++) {
				curr = list.get(i);
				curr.key = (curr.key).toUpperCase();
				put(curr.key, curr.value);
			}

		} else if (cases == 2) {
			for (int i = 0; i < list.size(); i++) {
				curr = list.get(i);
				curr.key = (curr.key).toLowerCase();
				put(curr.key, curr.value);
			}
		} else {
			System.out.println("Please try again.... 1 for upper case, 2 for lower case");
		}
	}

}