package dataStructures;

/**
 * A class that represents a min heap built using array
 * @author SarahHassouneh
 */
public class Heap {

	/**
	 * the main array to hold the objects
	 */
	private Object[] arr;

	/**
	 * the current size of heap or the number of elements present
	 */
	int size = 0;

	public int getSize() {
		return size;
	}

	

	/**
	 * the initial capacity of array
	 */
	public static int CAPACITY = 300;

	/**
	 * a constructor to make an empty heap
	 */
	public Heap() {
		// zero is not included, we start indexing from 1
		this.arr = new Object[CAPACITY + 1];
	}

	/**
	 * build a heap with a specific entail capacity
	 * 
	 * @param capacity represents the initial capacity of the heap
	 */
	public Heap(int capacity) {
		CAPACITY = capacity;
		this.arr = new Object[capacity + 1];
	}

	/**
	 * Build a heap from a given array
	 * 
	 * @param arr represnt the array tobuild heap from
	 */
	public Heap(Object[] arr) {
		this.size = arr.length;
		this.arr = new Object[size + 1];

		for (int i = 1; i < size + 1; i++) {
			this.arr[i] = arr[i - 1];
		}
		this.buildHeap();
	}

	/**
	 * A method to build a heap from array
	 */
	public void buildHeap() {
		for (int i = size / 2; i > 0; i--)
			mentHeapProperty(arr, i);

	}

	/**
	 * a method to fix array setup and re-order heap, force the min heap property
	 * 
	 * @param arr : the array that have objects of heap
	 * @param i   : the current index(node) to fix the heap property
	 */
	private void mentHeapProperty(Object[] arr, int i) {

		int L = left(i);
		int R = right(i);

		int smallest;

		// get the min element
		if (L <= size && ((Comparable) arr[L]).compareTo(arr[i]) < 0)
			smallest = L;
		else
			smallest = i;

		if (R <= size && ((Comparable) arr[R]).compareTo(arr[smallest]) < 0)
			smallest = R;

		if (i != smallest) {
			// swap the elements
			Object tmp = arr[i];
			arr[i] = arr[smallest];
			arr[smallest] = tmp;
			mentHeapProperty(arr, smallest);
		}

	}

	/**
	 * check if array reached max capacity
	 * 
	 * @return true if full
	 */
	public boolean isFull() {
		return size + 1 == CAPACITY;

	}

	/**
	 * check if heap is empty
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * get the parent index of current index(node)
	 * 
	 * @param index of the node to find parent
	 * @return index of the parent node
	 */
	private int parent(int index) {
		return index / 2;

	}

	/**
	 * get the right node index of a specific node
	 * 
	 * @param index of the node to find right of
	 * @return the right index of node
	 */
	private int right(int index) {
		return (2 * index) + 1;

	}

	/**
	 * get the right node index of a specific node
	 * 
	 * @param index of the node to find left of
	 * @return the left index of node
	 */
	private int left(int index) {
		return (2 * index);

	}

	/**
	 * check if a node is leaf or not
	 * 
	 * @param index of the node to be checked
	 * @return true if leaf
	 */
	private boolean isLeaf(int index) {
		if ((index >= size / 2) && (index <= size))
			return true;
		else
			return false;

	}

	/**
	 * insert an element in the heap (log n)
	 * 
	 * @param x is the object to be inserted
	 */
	public void insert(Object x) {
		if (isFull())
			throw new IllegalArgumentException("The heap capacity exceeded limit");
		else {
			int i = ++size;
			// i>=1
			while (i != 1 && ((Comparable) arr[i / 2]).compareTo(x) > 0) {
				arr[i] = arr[i / 2];// get parent
				i /= 2;

			}
			arr[i] = x;

		}

	}

	/**
	 * method to remove minimum from heap (log n)
	 * 
	 * @return the minimum element
	 */
	public Object removeMin() {
		int i, child;
		Object last, minElement = null;

		if (isEmpty())
			throw new IllegalArgumentException("The heap is Empty");
		else {
			minElement = arr[1];
			last = arr[size--];

			for (i = 1; i * 2 <= size; i = child) {
				child = 2 * i;
				if (child != size && ((Comparable) arr[child + 1]).compareTo(arr[child]) < 0)
					child++;
				if (((Comparable) last).compareTo(arr[child]) > 0)
					arr[i] = arr[child];
				else
					break;
			}
			arr[i] = last;
		}
		return minElement;

	}

	/**
	 * a method to print a heap in a line by line layout
	 */
	public void printHeap() {
		int line = 1;
		int count = 0;

		for (int i = 1; i < size + 1; i++) {
			System.out.print(arr[i].toString() + ",");
			count++;
			if (count >= line) {
				System.out.print("\n");
				count = 0;
				line = 2 * line;
			}

		}

		System.out.print("\n");

	}

	
}
