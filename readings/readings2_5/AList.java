/** Array based list.
 *  @author Josh Hug
 */

public class AList {
    private int[] array;
    private int size;
    private int arrayCapacity;


    /** Creates an empty list. */
    public AList() {
        arrayCapacity = 100;
        array = new int[arrayCapacity];
        size = 0;
    }

    /** Inserts X into the back of the list. */
    public void addLast(int x) {
        if (size == arrayCapacity) {
            arrayCapacity *= 2;
            int[] temp = new int[arrayCapacity];
            System.arraycopy(array, 0, temp, 0, size);
            array = temp;
        }
        array[size] = x;
        size++;
    }

    /** Returns the item from the back of the list. */
    public int getLast() {
        return array[size - 1];
    }
    /** Gets the ith item in the list (0 is the front). */
    public int get(int i) {
        return array[i];
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }

    /** Deletes item from back of the list and
      * returns deleted item. */
    public int removeLast() {
        size--;
        return array[size];
    }
} 