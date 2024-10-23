package dataStructures;

import java.util.LinkedList;
import java.util.Queue;


public class BinaryTree {

	public TreeNode root;

	/**
	 * default constructor for the tree
	 */
	public BinaryTree() {
		root = null;
	}

	/**
	 * constructor given root
	 * 
	 * @param root
	 */
	public BinaryTree(TreeNode root) {
		super();
		this.root = root;
	}

	/**
	 * a getter for the root od tree
	 * 
	 * @return the root of tree
	 */
	public TreeNode getRoot() {
		return root;
	}

	
	
	
	public static boolean isFullTree(TreeNode root) {
        // Base case: an empty tree is a full tree
        if (root == null) {
            return true;
        }

        // If a node has only one child, it is not a full tree
        if ((root.left == null && root.right != null) || (root.left != null && root.right == null)) {
            return false;
        }

        // Recursively check left and right subtrees
        return isFullTree(root.left) && isFullTree(root.right);
    }
	
	
	// printing functions
	public void printTreeInOrder() {
		printIn(root);
		System.out.println();
	}

	public void printTreePostOrder() {
		printPost(root);
		System.out.println();

	}

	public void printTreePreOrder() {
		printPre(root);
		System.out.println();

	}

	private void printIn(TreeNode T) {
		
		if (T != null) {
			printIn(T.getLeft());
			System.out.print(T);
			printIn(T.getRight());
		}
	}

	private void printPost(TreeNode T) {
		if (T != null) {
			printPost(T.getLeft());
			printPost(T.getRight());
			System.out.print(T);
		}
	}

	private void printPre(TreeNode T) {
		if (T != null) {
			System.out.print(T);
			printPre(T.getLeft());
			printPre(T.getRight());
		}
	}

	// print tree in levels
	public void printLevelOrder() {
		printLevelOrder(root);
	}

	private void printLevelOrder(TreeNode root) {
		if (root == null) {
			return;
		}

		Queue<TreeNode> queue = new LinkedList<>();
		queue.offer(root);

		while (!queue.isEmpty()) {
			int levelSize = queue.size();

			for (int i = 0; i < levelSize; i++) {
				TreeNode current = queue.poll();
				System.out.print(current.toString() + " ");

				if (current.getLeft() != null) {
					queue.offer(current.getLeft());
				}

				if (current.getRight() != null) {
					queue.offer(current.getRight());
				}

			}
			System.out.println();
		}

	}

	 public int getHeight(TreeNode root) {
	        if (root == null) {
	            return 0;
	        } else {
	            // Calculate the height of the left and right subtrees
	            int leftHeight = getHeight(root.left);
	            int rightHeight = getHeight(root.right);

	            // Return the maximum of the left and right subtree heights plus 1 (for the current level)
	            return Math.max(leftHeight, rightHeight) + 1;
	        }
	    }
	 

}
