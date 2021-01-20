import java.util.*;

public class PHPArray<V> implements Iterable<V> {

    private static final int INIT_CAPACITY = 4;

    private int N;           // number of key-value pairs in the symbol table
    private int M;           // size of linear probing table
    private Node<V>[] entries;  // the table
    private Node<V> head;       // head of the linked list
    private Node<V> tail;       // tail of the linked list
    private Iterator<Pair<V>> iterate;
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

    // insert the key-value pair into the symbol table
    public void put(String key, V val) {
        if (val == null) {
            unset(key);
        }

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
        //insert the node into the linked list
        if (head == null) {
            head = entries[i];
            tail = entries[i];
        } else {
            tail.next = entries[i];
            entries[i].prev = tail;
            tail = tail.next;
        }
        N++;
        iterate = new MyIterator1();
    }

    // Create a method that could implement key with integer type
    public void put(int key, V val) {
        String Key = Integer.toString(key);
        put(Key, val);
    }

    // return the value associated with the given key, null if no such value
    public V get(String key) {
        for (int i = hash(key); entries[i] != null; i = (i + 1) % M) {
            if (entries[i].key.equals(key)) {
                return entries[i].value;
            }
        }
        return null;
    }
    // return the value associated with the given (integer) key, null if no such value
    public V get(int key) {
        String Key = Integer.toString(key);
        return get(Key);
    }

    // resize the hash table to the given capacity by re-hashing all of the keys
    private void resize(int capacity) {
        PHPArray<V> temp = new PHPArray<V>(capacity);

        //rehash the entries in the order of insertion
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
        int i;
        for (i = hash(node.key); entries[i] != null; i = (i + 1) % M) {

        }
        // Notify when the key pairs need to rehash
        System.out.println("           " + "key " + node.key + " rehashed...");
        System.out.println();
        entries[i] = node;
    }

    // delete the key (and associated value) from the symbol table
    public void unset(String key) {
        if (get(key) == null) {
            return;
        }

        // find position i of key
        int i = hash(key);
        while (!key.equals(entries[i].key)) {
            i = (i + 1) % M;
        }

        // delete node from hash table
        Node<V> toDelete = entries[i];
        entries[i] = null;
        // TODO: delete the node from the linked list in O(1)
        if (toDelete.equals(head) && toDelete.equals(tail)) {
            head = null;
            tail = null;
        } else if (toDelete.equals(tail)) {
            toDelete.prev.next = null;
            tail = tail.prev;
            toDelete.prev = null;
        } else if (toDelete.equals(head)) {
            toDelete.next.prev = null;
            head = head.next;
            toDelete.next = null;
        } else {
            toDelete.prev.next = toDelete.next;
            toDelete.next.prev = toDelete.prev;
            toDelete.next = null;
            toDelete.prev = null;
        }

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
        if (N > 0 && N <= M / 8) {
            resize(M / 2);
        }
    }

    public void unset(Object key) {
        String Key = key.toString();
        unset(Key);
    }

    // hash function for keys - returns value between 0 and M-1
    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    //An inner class to store nodes of a doubly-linked list
    //Each node contains a (key, value) pair
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

    // Create an Iterator<V> object
    public Iterator<V> iterator() {
        return new MyIterator();
    }

    // Create an Iterator<Pair<V>> object
    public Iterator<Pair<V>> iterator1() {
        return new MyIterator1();
    }

    // An inner class that has V as generic type to implement iterator function
    // Each iterator could go through the linked list 
    private class MyIterator implements Iterator<V> {

        private Node<V> current;

        public MyIterator() {
            current = head;
        }

        public boolean hasNext() {
            return current != null;
        }

        @Override
        public V next() {
            V result = current.value;
            current = current.next;
            return result;
        }
    }

    // An inner class that has Pair<V> as generic type to implement iterator function
    // Each iterator could go through the linked list  
    private class MyIterator1 implements Iterator<Pair<V>> {

        private Node<V> current;

        public MyIterator1() {
            current = head;
        }

        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Pair<V> next() {
            Pair<V> result = new Pair(current.key, current.value);
            current = current.next;
            return result;
        }
    }

    // An inner static class to store key and value of each element
    // each pair contains (key, value)
    public static class Pair<V> {

        String key;
        V value;

        public Pair(String key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // Work like an iterator, return the Pair<V> of each node.
    public Pair<V> each() {
        if (iterate.hasNext()) {
            return iterate.next();
        }
        return null;
    }

    // Get an arraylist that contains pair<V> of all nodes
    public ArrayList<Pair<V>> pairs() {
        ArrayList<Pair<V>> result = new ArrayList<>();
        Iterator<Pair<V>> iterm = new MyIterator1();
        while (iterm.hasNext()) {
            result.add(iterm.next());
        }
        return result;
    }

    // Get an arraylist that contains keys of all nodes
    public ArrayList<String> keys() {
        ArrayList<String> result = new ArrayList<>();
        Iterator<Pair<V>> iterm = new MyIterator1();
        while (iterm.hasNext()) {
            result.add(iterm.next().key);
        }
        return result;
    }

    // Get an arraylist that contains values of all nodes
    public ArrayList<V> values() {
        ArrayList<V> result = new ArrayList<>();
        Iterator<Pair<V>> iterm = new MyIterator1();
        while (iterm.hasNext()) {
            result.add(iterm.next().value);
        }
        return result;
    }

    // Print all elements in the table
    public void showTable() {
    	System.out.println("     " + "Raw Hash Table Contents: ");
        for (int i = 0; i < M; i++) {
            if (entries[i] == null) {
                System.out.println(i + ": " + null);
            } else {
                System.out.println(i + ": " + "Key: " + entries[i].key + " Value: " + entries[i].value);
            }
        }
    }

    // return the number of key-value pairs in the table
    public int length() {
        return N;
    }

    // set the iterator back to (start point of linked list) head node.
    public void reset() {
        iterate = new MyIterator1();
    }

    // sort the value of each element and set the key with index in ascending order
    public void sort() throws ClassCastException {
    	reset();
        ArrayList<V> valueList = values();
        // Constrcut a temporary arraylist to store casting elements
        ArrayList compare = new ArrayList();
        if (valueList.get(0) instanceof Comparable) {
            // cast element to comparable element
            for (int i = 0; i < valueList.size(); i++) {
                compare.add((Comparable) valueList.get(i));
            }
            // Use collections.sort to sort whole array list
            Collections.sort(compare);
            entries = (Node<V>[]) new Node[M];
            head = tail = null;
            N = 0;
            V[] result = (V[]) compare.toArray();
            for (int i = 0; i < result.length; i++) {
                put(i, result[i]);
            }
        } else {
            throw new ClassCastException();
        }

    }

    // Do the same thing as sort() instead of changing the key to index in ascending order
    public void asort() {
    	reset();
        String key;
        ArrayList<Pair<V>> pairList = pairs();
        ArrayList<V> valueList = values();
        // Constrcut a temporary arraylist to store casting elements
        ArrayList compare = new ArrayList();
        // check if the value is comparable
        if (valueList.get(0) instanceof Comparable) {
            for (int i = 0; i < valueList.size(); i++) {
                // cast element to comparable element
                compare.add((Comparable) valueList.get(i));
            }
            // Use collections.sort to sort whole array list
            Collections.sort(compare);
            V[] result = (V[]) compare.toArray();
            for (int i = 0; i < result.length; i++) {
                for (int j = 0; j < pairList.size(); j++) {
                    if (result[i].equals(pairList.get(j).value)) {
                        key = pairList.get(j).key;
                        set(key, result[i], i);
                    }
                }
            }
        } else {
            throw new ClassCastException();
        }
    }

    // reset the linked list without changing the arrangement in the table.
    public void set(String key, V value, int index) {
        Node<V> temp = new Node<V>(key, value);
        if (index == 0) {
            head = temp;
            tail = head;
        } else {
            tail.next = temp;
            temp.prev = tail;
            tail = tail.next;
        }
    }

    // flip the key and value only if the value is a string
    public PHPArray<String> array_flip() {
	     reset();
	     Pair<V> temp;
	     Pair<V> check = each();
	     reset();
	     PHPArray<String> result = new PHPArray<>(M);
	     if(!(check.value instanceof String)) {
	         throw new ClassCastException("Cannot convert class java.lang.Integer to String");
	     } else {
	         while((temp = each()) != null) {
	             result.put((String)temp.value, temp.key);
	         }
	     }
	    return result; 
	}

	// calculate summation fo values if the value could be countable.
	// We need to check if the value is in primitive data type, if not throw an exception
    public V array_sum() {
        reset();
        ArrayList<V> valueList = values();
        // check if the value is integer
        if (valueList.get(0) instanceof Integer) {
            int sum = 0;
            for (int i = 0; i < valueList.size(); i++) {
                sum += (int) valueList.get(i);
            }
            V result = (V) String.valueOf(sum);
            return result;
            // check if the value is double
        } else if (valueList.get(0) instanceof Double) {
            double sum = 0.0d;
            for (int i = 0; i < valueList.size(); i++) {
                sum += (double) valueList.get(i);
            }
            V result = (V) String.valueOf(sum);
            return result;
            // check if the value is float
        } else if (valueList.get(0) instanceof Float) {
            float sum = 0.0f;
            for (int i = 0; i < valueList.size(); i++) {
                sum += (float) valueList.get(i);
            }
            V result = (V) String.valueOf(sum);
            return result;
            // check if the value is short
        } else if (valueList.get(0) instanceof Short) {
            short sum = 0;
            for (int i = 0; i < valueList.size(); i++) {
                sum += (short) valueList.get(i);
            }
            V result = (V) String.valueOf(sum);
            return result;
            // check if the value is long
        } else if (valueList.get(0) instanceof Long) {
            long sum = 0;
            for (int i = 0; i < valueList.size(); i++) {
                sum += (long) valueList.get(i);
            }
            V result = (V) String.valueOf(sum);
            return result;
            // check if the value is byte
        } else if (valueList.get(0) instanceof Byte) {
            byte sum = 0;
            for (int i = 0; i < valueList.size(); i++) {
                sum += (byte) valueList.get(i);
            }
            V result = (V) String.valueOf(sum);
            return result;
        } else {
            throw new ClassCastException("Cannot do summation on non-primitive data types");
        }
    }

    // Reverse the order of the linked list wihtout change in table
    public PHPArray<V> array_reverse() {
    	reset();
    	Pair<V> curr;
    	ArrayList<Pair<V>> pairList = pairs();
    	ArrayList<Pair<V>> temp = new ArrayList<>();
    	int i = pairList.size() - 1;
    	while(i >= 0) {
    		temp.add(pairList.get(i));
    		i--; 
    	}
    	PHPArray<V> result = new PHPArray<>(M);
    	for(int j = 0; j < temp.size(); j++) {
    		result.put(temp.get(j).key, temp.get(j).value);
    	}
    	return result;
    }

    // calculate the multiplication of values of all nodes
    // We need to check if the value is in primitive data type, if not throw an exception
    public V array_product() {
    	reset();
        ArrayList<V> valueList = values();
        // check if the value is integer
        if (valueList.get(0) instanceof Integer) {
            long product = 1;
            for (int i = 0; i < valueList.size(); i++) {
                product *= (int) valueList.get(i);
            }
            V result = (V) Long.valueOf(product);
            return result;
            // check if the value is double
        } else if (valueList.get(0) instanceof Double) {
            double product = 1.0d;
            for (int i = 0; i < valueList.size(); i++) {
                product *= (double) valueList.get(i);
            }
            V result = (V) String.valueOf(product);
            return result;
            // check if the value is float
        } else if (valueList.get(0) instanceof Float) {
            float product = 1.0f;
            for (int i = 0; i < valueList.size(); i++) {
                product *= (float) valueList.get(i);
            }
            V result = (V) String.valueOf(product);
            return result;
            // check if the value is short
        } else if (valueList.get(0) instanceof Short) {
            long product = 1;
            for (int i = 0; i < valueList.size(); i++) {
                product *= (short) valueList.get(i);
            }
            V result = (V) String.valueOf(product);
            return result;
            // check if the value is long
        } else if (valueList.get(0) instanceof Long) {
            long product = 1;
            for (int i = 0; i < valueList.size(); i++) {
                product *= (long) valueList.get(i);
            }
            V result = (V) String.valueOf(product);
            return result;
            // check if the value is byte
        } else if (valueList.get(0) instanceof Byte) {
            long product = 1;
            for (int i = 0; i < valueList.size(); i++) {
                product *= (byte) valueList.get(i);
            }
            V result = (V) String.valueOf(product);
            return result;
        } else {
            throw new ClassCastException("Cannot do summation on non-primitive data types");
        }
    }
     // change keys in array to uppercase or lower case
    public void array_change_key_case(int decision) {
        Pair<V> curr;
        reset();
        ArrayList<Pair<V>> pairList = pairs();
        entries = (Node<V>[]) new Node[M];
        head = tail = null;
        N = 0;
        int i = 0;
        // To lowercase
        if (decision == 0) {
            while(i < pairList.size()) {
                curr = pairList.get(i);
                curr.key = (curr.key).toLowerCase();
                put(curr.key, curr.value);
                i++;
            }
        // To Uppercase
        } else {
            while(i < pairList.size()) {
                curr = pairList.get(i);
                curr.key = (curr.key).toUpperCase();
                put(curr.key, curr.value);
                i++;
            }
        }
    }

    // Test array_diff_assoc(two PHPArrays have same generic type)
	// array_diff_assoc() is to compare each element of array to find the difference between two PHPArray
	// This will return an arraylist that contain different key pairs.
    public ArrayList<Pair<V>> array_diff_assoc(PHPArray<V> arrayA, PHPArray<V> arrayB) {
        ArrayList<Pair<V>> first = arrayA.pairs();
        ArrayList<Pair<V>> second = arrayB.pairs();
        ArrayList<Pair<V>> temp = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < second.size(); j++) {
                if ((first.get(i).key.equals(second.get(j).key)) && (first.get(i).value.equals(second.get(j).value))) {
                    flag = true;
                }
            }
            if (!flag) {
                temp.add(first.get(i));
            }
        }
        return temp;
    }
}
