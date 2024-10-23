package dataStructures;

/**
 * a utility class to perform different type of sorting , on any array of
 * comparable objects
 * 
 * @author SarahHassouneh
 */
public class SortObjects {

	/**
	 * wrapper function for quick sort takes array of comparable objects and sorts
	 * it
	 * 
	 * @param <T> represent the type of objects in array
	 * @param arr reperesets array to be sorted
	 */
	public static <T extends Comparable<T>> void quickSort(T[] arr) {
		quickSort(arr, 0, arr.length - 1);
	}

	/**
	 * the recuive function of the quick sort
	 */
	private static <T extends Comparable<T>> void quickSort(T[] arr, int left, int right) {
		int i, j;
		T pivot;

		if (left < right) {
			pivot = median(arr, left, right);
			if (right - left != 1 && right - left != 2) { // change here
				i = left;
				j = right - 1;
				for (;;) {

					while (i < right && (arr[++i].compareTo(pivot) < 0)) // arr[++i] < pivot
						;
					while (j > left && (arr[--j].compareTo(pivot) > 0)) // arr[--j] > pivot
						;

					if (i < j)
						exchange(arr, i, j);// send indexes with array
					else
						break;
				}

				exchange(arr, i, right - 1);

				// if(i!=0)
				quickSort(arr, left, i - 1);

				quickSort(arr, i + 1, right);
			}
		}
	}

	/**
	 * a helper method to swap two elements in array , given their positions
	 */
	private static <T extends Comparable<T>> void exchange(T[] arr, int i, int j) {
		// swap the two elements in array
		T temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;

	}

	/**
	 * a helper method to chose pivot & interchange places
	 */
	private static <T extends Comparable<T>> T median(T[] arr, int left, int right) {
		// note that left and right are indexes
		int center = (left + right) / 2;

		if ((arr[left]).compareTo(arr[center]) > 0) /// left> center
			exchange(arr, left, center);

		if ((arr[left]).compareTo(arr[right]) > 0) // left> right
			exchange(arr, left, right);

		if ((arr[center]).compareTo(arr[right]) > 0) // center > right
			exchange(arr, center, right);

		exchange(arr, center, right - 1);

		return arr[right - 1];

	}
}
