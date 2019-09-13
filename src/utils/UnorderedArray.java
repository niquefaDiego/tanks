package utils;

/**
 * UnorderedArray is an indexed(from 0 to size-1) container.
 * It performs delete operation in O(1) and add operation in amortized O(1).
 * The delete operation might reorder the array. 
 * @author niquefadiego
 * @param <T>
 */

public class UnorderedArray<T>
{
	private Object data[];
	private int size = 0;
	
	/**
	 * Complexity: O(1)
	 * @return Number of elements stored.
	 * */
	public int size ( ) {return this.size; }
	
	/**
	 * Complexity: O(1)
	 * @return Element at position i.
	 * */
	@SuppressWarnings("unchecked")
	public T get ( int i ) { return (T)data[i]; }
	
	/**
	 * Complexity: O(1)
	 * Equivalent to size()==0
	 * */
	public boolean isEmpty() { return size==0; }
	
	/**
	 * Complexity: O(size)
	 * @return Index of the first appearance of element o, or -1 if none found. 
	 * */
	public int indexOf ( T o ) {
		for ( int i = 0; i < size; ++i )
			if ( o.equals(data[i]) )
				return i;
		return -1;
	}
	
	/**
	 * Removes the element at the given position.
	 * Complexity: O(1)
	 * */
	public void remove ( int i ) throws ArrayIndexOutOfBoundsException {
		if ( i < 0 || i >= size ) throw new ArrayIndexOutOfBoundsException();
		data[i] = data[size-1];
		size--;
	}
	
	/**
	 * Adds the given element to the container.
	 * Complexity: O(1)
	 * */
	public void add ( T o ) {
		if ( this.size == this.data.length ) {
			Object newData[] = new Object[2*this.size];
			for ( int i = 0; i < size; ++i )
				newData[i] = this.data[i];
			this.data = newData;
		}
		this.data[this.size++] = o;
	}
	
	/**
	 * Creates new empty UnorderedArray.
	 * Complexity: O(1)
	 * */
	public UnorderedArray ( ) {
		data = null;
		data = new Object[1];
	}
}
