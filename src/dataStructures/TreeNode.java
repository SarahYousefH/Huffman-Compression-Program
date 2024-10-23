package dataStructures;


/**
 * Tree node to be used while building tree of codes
 * 
 */
public class TreeNode implements Comparable<TreeNode> {

	/**
	 * The byte value (that will have a specific code) stored in the leaf nodes
	 */
	private int value; // although it is byte value, I choose int not byte because of the simplicity of
						// using it in java and is also unsigned, uing bytes have caused many problems,

	/**
	 * Frequency of the leaf nodes. For internal it is the sum of the two children.
	 */
	private int frequency;

	/**
	 * The left child of the node
	 */
	public TreeNode left;

	/**
	 * The right child of the node
	 */
	public TreeNode right;

	
	
	/**
	 * default constructor
	 */
	public TreeNode() {
	}

	/**
	 * a constructor with value
	 * @param value of the node
	 */
	
	public TreeNode(int value) {
		this.value = value;
	}
	
	
	/**
	 * constructor leaf node with no children
	 * 
	 * @param value is the byte value stored in the leaf node , (the value is of no
	 *              importance for internal nodes)
	 * @param freq  is number of times value occurred
	 */
	public TreeNode(int value, int frequency) {
		this.value = value;
		this.frequency = frequency;
	}

	
	

	/**
	 * constructor for internal node from children. The internal node frequency will
	 * be the sum of the frequencies of left child and right child .
	 * 
	 * @param left  is left subtree
	 * @param right is right subtree
	 */

	public TreeNode(TreeNode left, TreeNode right) {
		this.left = left;
		this.right = right;
		this.frequency = left.frequency + right.frequency;
	}

	/**
	 * Compare tree nodes based on the frequencies
	 * 
	 * @return (value < 0) if (this < other), (value > 0) if (this > other), and
	 *         (value= 0) if (this = other)
	 */

	public int compareTo(TreeNode other) {

		if (frequency != other.frequency)
			return frequency - other.frequency;

		else
			return value - other.value;
	}

	/**
	 * Return a String version of this node.
	 * 
	 * @return A String representation of node
	 */
	public String toString() {
		return "(" + frequency + ">> " + value + ")";
	}

	/**
	 * Get the byte value stored in this node, if it is leaf
	 * 
	 * @return the value stored in this node
	 */
	public int getValue() {

		if (this.isLeaf())
			return value;
		else
			throw new IllegalStateException("Non-leaf node do not save value");
	}

	/**
	 * Get the frequency of this node.
	 * 
	 * @return the frequency of the node
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Get the left child of node
	 * 
	 * @return The left child of this node. If no left child, returns null.
	 */
	public TreeNode getLeft() {
		return left;
	}

	/**
	 * Get the right child of node
	 * 
	 * @return The right child of this node. If no right child, returns null.
	 */
	public TreeNode getRight() {
		return right;
	}

	/**
	 * Is this node a leaf ?
	 * 
	 * @return true if this node is a leaf, false if it is an internal node
	 */
	public boolean isLeaf() {
		return left == null && right == null;
	}

	/**
	 * Set the left child of this TreeNode.
	 * 
	 * @param left: The new left child for this TreeNode.
	 */
	public void setLeft(TreeNode left) {
		this.left = left;
	}

	/**
	 * Set the right child of this TreeNode.
	 * 
	 * @param right: The new right child for this TreeNode.
	 */
	public void setRight(TreeNode newRight) {
		this.right = newRight;
	}

}
