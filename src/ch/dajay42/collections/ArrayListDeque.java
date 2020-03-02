package ch.dajay42.collections;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class provides an Array-backed implementation of both the {@link List} and {@link Deque} interfaces,
 * including all optional methods.
 * It serves as a {@link RandomAccess}-capable alternative for {@link LinkedList}.
 * <p/>
 * Instances of this class have the following properties:
 *
 * <ul>
 *  <li> There is no internal synchronization, so concurrent structural modifications require external synchronization.</li>
 *  <li> The capacity is dynamic and will be extended up to {@link Integer#MAX_VALUE} if necessary.</li>
 *  <br/>
 *  <li> Size-preserving random access operations (get/set) complete in constant time.</li>
 *  <li> Head/Tail retrievals/removals complete in constant time.</li>
 *  <li> Head/Tail additions (pushes) complete in amortised constant time.</li>
 *  <li> If the total number of elements never exceeds the initial capacity, an arbitrary number (and ratio) of Head/Tail additions (pushes) can complete in true constant time.</li>
 *  <br/>
 *  <li> Size-modifying (index-shifting) random access operations (random element insertion, random element removal) complete in linear time.</li>
 *  <li> Element search operations (contains, indexOf...) complete in linear time.</li>
 *  <br/>
 *  <li> Null elements are not permitted. Any attempt to insert one will cause a {@link NullPointerException} to be thrown.</li>
 *  <li> The {@link Iterator}s and {@link ListIterator}s are fail-fast, and may throw {@link ConcurrentModificationException} where applicable.</li>
 *  <li> Attempting to insert a total of more than {@link Integer#MAX_VALUE} elements will cause an {@link IllegalStateException} to be thrown,
 *       except when using methods that report capacity exhaustion through their return value, such as offerFirst/offerLast.</li>
 *  <li> The {@link ArrayListDeque#equals(Object)} method uses element-wise List equality, as defined in {@link AbstractList#equals(Object)}.</li>
 * </ul>
 *
 * @see LinkedList LinkedList (does not implement RandomAccess)
 * @see ArrayList ArrayList (does not implement Deque)
 * @see ArrayDeque ArrayDeque (does not implement List)
 */
public class ArrayListDeque<E> extends AbstractList<E> implements List<E>, Deque<E>,
		RandomAccess, Cloneable, java.io.Serializable{
	
	static final long serialVersionUID = 8888_8888L;
	
	////////////////////////////////////////////////////////////////
	///FIELDS
	
	private int start = 0, end = 0, length = 0;
	private Object[] data;
	
	////////////////////////////////////////////////////////////////
	/// CONSTRUCTORS
	
	/**
	 * Creates a new instance with default initial capacity of 16.
	 */
	public ArrayListDeque(){
		this.data = new Object[16];
	}
	
	/**Creates a new instance with at least the given initial capacity.
	 * @param minimalCapacity minimal initial capacity
	 * @throws NegativeArraySizeException iff minimalCapacity < 0
	 */
	public ArrayListDeque(int minimalCapacity) throws NegativeArraySizeException{
		if(minimalCapacity<0) throw new NegativeArraySizeException();
		this.data = new Object[bestMatchCapacity(minimalCapacity)];
	}
	
	/**Creates a new instance containing the elements of source and initial capacity at least source.size().
	 * @param source the collections whose elements are to be included
	 * @throws NullPointerException iff source or any element of source is null
	 */
	public ArrayListDeque(Collection<E> source) throws NullPointerException{
		this.end = this.length = source.size();
		this.data = new Object[bestMatchCapacity(this.length)];
		int i = 0;
		for(E e : source){
			data[i] = Objects.requireNonNull(e);
			i++;
		}
	}
	
	/**Creates a new instance containing the elements of source and initial capacity at least source.size().
	 * @param source the collections whose elements are to be included
	 * @throws NullPointerException iff source is null
	 */
	public ArrayListDeque(ArrayListDeque<E> source) throws NullPointerException{
		this.start = source.start;
		this.end = source.end;
		this.length = source.end;
		this.data = new Object[source.data.length];
		System.arraycopy(source.data, 0, this.data, 0, source.data.length);
	}
	
	
	////////////////////////////////////////////////////////////////
	/// INTERNAL STATE MANAGEMENT
	
	/**Try to extend the head of this by 1.
	 * @return true iff successful
	 */
	private boolean extendHead(){
		if(ensureCapacity(length+1)){
			start--;
			if(start < 0) start += data.length;
			length++;
			modCount++;
			assert invariants();
			return true;
		}
		else return false;
	}
	
	/**Try to extend the head of this by amount.
	 * @param amount the amount to be extended by
	 * @return true iff successful
	 */
	private boolean extendHead(int amount){
		if(ensureCapacity(length+amount)){
			start -= amount;
			if(start < 0) start += data.length;
			length += amount;
			modCount++;
			assert invariants();
			return true;
		}
		else return false;
	}
	
	/**Try to extend the tail of this by 1.
	 * @return true iff successful
	 */
	private boolean extendTail(){
		if(ensureCapacity(length+1)){
			end++;
			if(end >= data.length) end -= data.length;
			length++;
			modCount++;
			assert invariants();
			return true;
		}
		else return false;
	}
	
	/**Try to extend the tail of this by amount.
	 * @param amount the amount to extend by
	 * @return true iff successful
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean extendTail(int amount){
		if(ensureCapacity(length+amount)){
			end += amount;
			if(end >= data.length) end -= data.length;
			length += amount;
			modCount++;
			assert invariants();
			return true;
		}
		else return false;
	}
	
	/**
	 * Reduce the head of this by 1.
	 */
	private void reduceHead(){
		clean(start);
		start++;
		if(start >= data.length) start -= data.length;
		length--;
		modCount++;
		assert invariants();
	}
	
	/**Reduce the head of this by amount.
	 * @param amount the amount to reduce by
	 */
	private void reduceHead(int amount){
		clean(start,start+amount);
		start+=amount;
		if(start >= data.length) start -= data.length;
		length-=amount;
		modCount++;
		assert invariants();
	}
	
	/**
	 * Reduce the tail of this by 1.
	 */
	private void reduceTail(){
		end--;
		clean(end);
		if(end < 0) end += data.length;
		length--;
		modCount++;
		assert invariants();
	}
	
	/**Reduce the tail of this by amount.
	 * @param amount the amount to be reduced by
	 */
	private void reduceTail(int amount){
		end-=amount;
		clean(end,end+amount);
		if(end < 0) end += data.length;
		length-=amount;
		modCount++;
		assert invariants();
	}
	
	/**Transform the external index to an internal array-index.
	 * @param index the index to be transformed
	 * @return the corresponding index into the data array
	 */
	private int offsetIndex(int index){
		index += start;
		return (index < data.length) ? index : index - data.length;
	}
	
	/**Try to ensure that this can contain at least the given amount of elements,
	 * by enlarging the data array, if necessary.
	 * @param targetCapacity the minimal capacity to be satisfied
	 * @return true iff the demand can be met.
	 */
	private boolean ensureCapacity(int targetCapacity){
		if(targetCapacity < 0){ // check for integer overflow
			return false;
		} else {
			if(data.length < targetCapacity){
				int oldCap = data.length;
				int newCap = bestMatchCapacity(targetCapacity);
				Object[] newData = new Object[newCap];
				if(start > end){// handle roll-over
					int newStart = start+newCap-oldCap;
					System.arraycopy(data, 0, newData, 0, end);
					System.arraycopy(data, start, newData, newStart, data.length-start);
					start = newStart;
				}
				else {
					System.arraycopy(data, start, newData, start, length);
				}
				data = newData;
			}
			return true;
		}
	}
	
	/**Returns the capacity that best suits the given demand.
	 * @implNote The returned number is equal to the next larger power of two, or {@link Integer#MAX_VALUE},
	 * whichever is smaller. That is, as if it evaluated without numerical errors the expression:
	 * {@code (int) Math.min((long)Integer.MAX_VALUE, (long) Math.pow(2, 1 + Math.floor(Math.log(minimalCapacity)/Math.log(2))))}
	 * @param minimalCapacity the minimally desired capacity.
	 * @return the nest best capacity
	 */
	private int bestMatchCapacity(int minimalCapacity){
		int leadingZeroCount = Integer.numberOfLeadingZeros(minimalCapacity);
		return (leadingZeroCount > 1) ? (1 << (33 - leadingZeroCount)) : Integer.MAX_VALUE;
	}
	
	/**Sets the value at the given array index to null, preventing references to dead elements to persist.
	 * @param where array index. this index must not be reachable by the public API
	 */
	private void clean(int where){
		data[where] = null;
	}
	
	/**Sets the values at the given array index range to null, preventing references to dead elements to persist.<p/>
	 * If fromWhere is larger than toWhere, the range is assumed to wrap around at the boundaries of the array.
	 * @param fromWhere array index. this index must not be reachable by the public API
	 * @param toWhere array index, exclusive. the index one smaller than this must not be reachable by the public API
	 */
	private void clean(int fromWhere, int toWhere){
		if(fromWhere < 0) fromWhere += data.length;
		if(toWhere >= data.length) toWhere -= data.length;
		
		if(fromWhere <= toWhere)
			Arrays.fill(data, fromWhere, toWhere, null);
		else{
			Arrays.fill(data, fromWhere, data.length, null);
			Arrays.fill(data, 0, toWhere, null);
		}
	}
	
	/**Asserts the invariants of the data structure.
	 * @return true iff all invariants hold.
	 */
	private boolean invariants(){
		assert start >= 0;
		assert end >= 0;
		assert length >= 0;
		assert data != null;
		assert start < data.length;
		assert end < data.length;
		assert length <= data.length;
		assert length == ((start<=end) ? (end-start) : (end+data.length-start));
		return true;
	}
	
	/**Get the current capacity of this
	 * @return the current capacity.
	 */
	/*@TestOnly*/
	int getCapacity(){
		return data.length;
	}
	
	////////////////////////////////////////////////////////////////
	/// SIZE OPERATIONS
	///
	/// EFFICIENT - O(1)
	
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public int size(){
		return length;
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty(){
		return length == 0;
	}
	
	/** This is operation is linear-time-ish, as it must clear the underlying array.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public void clear(){
		start = 0;
		end = 0;
		length = 0;
		data = new Object[data.length]; // this seems to be more efficient than Arrays.fill(data, null);
	}
	
	////////////////////////////////////////////////////////////////
	/// RANDOM ACCESS
	///
	/// EFFICIENT - O(1)
	
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public E get(int index){
		Objects.checkIndex(index, length);
		return (E) data[offsetIndex(index)];
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E set(int index, E element){
		E e = get(index); // implicit index check
		data[offsetIndex(index)] = Objects.requireNonNull(element);
		return e;
	}
	
	////////////////////////////////////////////////////////////////
	/// HEAD ACCESS / DELETION
	///
	/// EFFICIENT - O(1)
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E peekFirst(){
		return length != 0 ? get(0) : null;
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E peek(){
		return peekFirst();
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E getFirst(){
		E e = peekFirst();
		if(e == null) throw new NoSuchElementException();
		return e;
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E element(){
		return getFirst();
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E pollFirst(){
		E e = peekFirst();
		if(e != null) reduceHead();
		return e;
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E poll(){
		return pollFirst();
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E removeFirst(){
		E e = pollFirst();
		if(e == null) throw new NoSuchElementException();
		return e;
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E remove(){
		return removeFirst();
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E pop(){
		return removeFirst();
	}
	
	////////////////////////////////////////////////////////////////
	/// TAIL ACCESS / DELETION
	///
	/// EFFICIENT - O(1)
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E peekLast(){
		return length != 0 ? get(length - 1) : null;
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E getLast(){
		E e = peekLast();
		if(e == null) throw new NoSuchElementException();
		return e;
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E pollLast(){
		E e = peekLast();
		if(e != null) reduceTail();
		return e;
	}
	
	/** This is a constant-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E removeLast(){
		E e = pollLast();
		if(e == null) throw new NoSuchElementException();
		return e;
	}
	
	////////////////////////////////////////////////////////////////
	/// HEAD APPEND
	///
	/// EFFICIENT - O(1)
	
	/** This is a constant-amortised-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean offerFirst(E e){
		if(extendHead()){
			set(0, e);
			return true;
		}
		else return false;
	}
	
	/** This is a constant-amortised-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public void addFirst(E e){
		if(!offerFirst(e))
			throw new IllegalStateException();
	}
	
	/** This is a constant-amortised-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public void push(E e){
		addFirst(e);
	}
	
	////////////////////////////////////////////////////////////////
	/// TAIL APPEND
	///
	/// EFFICIENT - O(1)
	
	/** This is a constant-amortised-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean offerLast(E e){
		if(extendTail()){
			set(length - 1, e);
			return true;
		}
		else return false;
	}
	
	/** This is a constant-amortised-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean offer(E e){
		return offerLast(e);
	}
	
	/** This is a constant-amortised-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public void addLast(E e){
		if(!offerLast(e))
			throw new IllegalStateException();
	}
	
	/** This is a constant-amortised-time operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(E e){
		if(offerLast(e))
			return true;
		else
			throw new IllegalStateException();
	}
	
	/** This is operation is linear-time in its argument's size.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends E> c){
		int increase = c.size(); // implicit null check
		if(increase > 0){
			int offset = end;
			if(!extendTail(increase))
				throw new IllegalStateException();
			for(E e : c){
				data[offset] = Objects.requireNonNull(e);
				offset++;
				if(offset>=data.length) offset-=data.length;
			}
			return true;
		}
		else
			return false;
	}
	
	
	
	
	////////////////////////////////////////////////////////////////
	/// ELEMENT SEARCH
	///
	/// INEFFICIENT - O(N)
	
	@Override
	public int indexOf(Object o){
		for(int i = 0; i < length; i++){
			if(data[offsetIndex(i)].equals(o)){
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(Object o){
		for(int i = length-1; i >= 0; i--){
			if(data[offsetIndex(i)].equals(o)){
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public boolean contains(Object o){
		for(int i = 0; i < length; i++){
			if(data[offsetIndex(i)].equals(o)){
				return true;
			}
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////
	/// RANDOM INSERTION
	///
	/// INEFFICIENT - O(N)
	
	
	/** This is a linear-time, O(size()) operation.<p/>
	 * {@inheritDoc}
	 * @throws IllegalStateException if the element cannot be added at this
	 *         time due to capacity restrictions
	 */
	@Override
	public void add(int index, E element){
		if(index == 0){
			addFirst(element);
		} else if(index == length){
			addLast(element);
		} else {
			Objects.checkIndex(index, length);
			Objects.requireNonNull(element);
			int offset = offsetIndex(index);
			if(offset < end){
				if(!extendTail())
					throw new IllegalStateException();
				int len = end - offset;
				System.arraycopy(data, offset, data, offset + 1, len);
			} else {
				if(!extendHead())
					throw new IllegalStateException();
				offset = offsetIndex(index + 1);
				int len = offset - start;
				System.arraycopy(data, start + 1, data, start, len);
			}
			data[offset] = element;
		}
		
	}
	
	/** This is a linear-time, O(size() + c.size()) operation.<p/>
	 * {@inheritDoc}
	 * @throws IllegalStateException if the element cannot be added at this
	 *         time due to capacity restrictions
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c){
		if(index == length) return addAll(c);
		Objects.checkIndex(index, length);
		
		int increase = c.size(); // implicit null check
		
		if(increase > 0){
			int offset = offsetIndex(index);
			
			if(offset < end){
				if(!extendTail(increase))
					throw new IllegalStateException();
				int len = end - offset;
				System.arraycopy(data, offset, data, offset + increase, len);
			} else {
				if(!extendHead(increase))
					throw new IllegalStateException();
				offset = offsetIndex(index+increase);
				int len = offset - start;
				System.arraycopy(data, start+increase, data, start, len);
			}
			
			for(E e : c){
				data[offset] = Objects.requireNonNull(e);
				offset++;
				if(offset>=data.length) offset-=data.length;
			}
		}
		return increase > 0;
	}
	
	
	
	////////////////////////////////////////////////////////////////
	/// RANDOM DELETION
	///
	/// INEFFICIENT - O(N)
	
	/** This is a linear-time, O(size()) operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public E remove(int index){
		if(index == 0) return removeFirst();
		if(index == length - 1) return removeLast();
		
		E e = get(index); // implicit index check
		
		int offset = offsetIndex(index);
		if(end <= offset){
			int len = offset - start - 1;
			System.arraycopy(data, start, data, start+1, len);
			reduceHead();
		}
		else {
			int len = end - offset - 1;
			System.arraycopy(data, offset+1, data, offset, len);
			reduceTail();
		}
		return e;
	}
	
	/** This is a linear-time, O(size()) operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeFirstOccurrence(Object o){
		int i = indexOf(o);
		if(i < 0)
			return false;
		else {
			remove(i);
			return true;
		}
	}
	
	/** This is a linear-time, O(size()) operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object o){
		return removeFirstOccurrence(o);
	}
	
	/** This is a linear-time, O(size()) operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeLastOccurrence(Object o){
		int i = lastIndexOf(o);
		if(i < 0)
			return false;
		else {
			remove(i);
			return true;
		}
	}
	
	/** This is a linear-time, O(size()) operation.<p/>
	 * {@inheritDoc}
	 */
	@Override
	protected void removeRange(int fromIndex, int toIndex){
		Objects.checkFromToIndex(fromIndex, toIndex, length);
		if(toIndex == fromIndex) return;
		
		int offset1 = offsetIndex(fromIndex);
		int offset2 = offsetIndex(toIndex);
		
		if(end <= offset1 && end <= offset2){
			int len = offset1 - start;
			int amount = offset2 - offset1;
			System.arraycopy(data, start, data, start+amount, len);
			reduceHead(amount);
		}
		else if(end > offset1 && end > offset2){
			int len = end - offset2;
			System.arraycopy(data, offset2, data, offset1, len);
			reduceTail(offset2 - offset1);
		}
		else {// if(end <= offset1 && end > offset2){
			int headLen = offset1 - start;
			int headAmount = data.length - offset1;
			System.arraycopy(data, start, data, start+headAmount, headLen);
			int tailLen = end - offset2;
			System.arraycopy(data, offset2, data, 0, tailLen);
			reduceHead(headAmount);
			reduceTail(offset2);
		}
	}
	
	/** This is a linear-time, O(size()) operation, assuming the filter runs in constant time.<p/>
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean removeIf(Predicate<? super E> filter){
		int moveTail = 0;
		int moveHead = 0;
		if(start <= end){
			int prev = 0;
			for(int i = start; i < end; i++){
				if(filter.test((E) data[i])){
					if(moveTail > 0){
						System.arraycopy(data, prev+moveTail, data, prev, i-prev);
					}
					prev = i;
					moveTail++;
				}
			}
			if(moveTail > 0){
				System.arraycopy(data, prev+moveTail, data, prev, end-prev);
			}
			reduceTail(moveTail);
		}
		else{
			int prev = 0;
			for(int i = 0; i < end; i++){
				if(filter.test((E) data[i])){
					if(moveTail > 0){
						System.arraycopy(data, prev+moveTail, data, prev, i-prev);
					}
					prev = i;
					moveTail++;
				}
			}
			if(moveTail > 0){
				System.arraycopy(data, prev+moveTail, data, prev, end-prev);
			}
			reduceTail(moveTail);
			
			int last = data.length-1;
			for(int i = data.length-1; i >= start; i--){
				if(filter.test((E) data[i])){
					if(moveHead > 0)
						System.arraycopy(data, i+1, data, i+1+moveHead, last-i);
					last = i;
					moveHead++;
				}
			}
			if(moveHead > 0)
				System.arraycopy(data, start, data, start+moveHead, last-start);
			reduceHead(moveHead);
		}
		return moveTail > 0 || moveHead > 0;
	}
	
	/** This is a at-least-linear-time, O(size() * f) operation, where f is the runtime of c.contains(Object)<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Collection<?> c){
		return removeIf(c::contains);
	}
	
	/** This is a at-least-linear-time, O(size() * f) operation, where f is the runtime of c.contains(Object)<p/>
	 * {@inheritDoc}
	 */
	@Override
	public boolean retainAll(Collection<?> c){
		return removeIf(e -> !c.contains(e));
	}
	
	
	////////////////////////////////////////////////////////////////
	/// ITERATORS
	
	
	@Override
	public Iterator<E> descendingIterator(){
		return new DescItr();
	}
	
	private class DescItr implements Iterator<E>{
		
		int i = size();
		int last = -1;
		int expectedModCount = modCount;
		
		@Override
		public boolean hasNext(){
			return i > 0;
		}
		
		@Override
		public E next(){
			checkForCoModification();
			if(hasNext()){
				--i;
				E e = get(i);
				last = i;
				return e;
			} else
				throw new NoSuchElementException();
		}
		
		@Override
		public void remove(){
			if(last < 0)
				throw new IllegalStateException();
			checkForCoModification();
			ArrayListDeque.this.remove(last);
			last = -1;
			expectedModCount = modCount;
		}
		
		
		final void checkForCoModification() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}
	
	
	
	////////////////////////////////////////////////////////////////
	/// TO ARRAY
	
	@Override
	public Object[] toArray(){
		Object[] array = new Object[length];
		if(start <= end){
			System.arraycopy(data, start, array, 0, length);
		}
		else {
			System.arraycopy(data, start, array, 0, data.length-start);
			System.arraycopy(data, 0, array, data.length-start, end);
		}
		return array;
	}
	
	@Override
	@SuppressWarnings("SuspiciousSystemArraycopy")
	public <R> R[] toArray(R[] a) throws ArrayStoreException{
		Objects.requireNonNull(a);
		R[] array;
		if(a.length >= length){
			array = a;
			Arrays.fill(array, length, array.length, null);
		}
		else{
			array = Arrays.copyOf(a, length);
		}
		
		if(start <= end){
			System.arraycopy(data, start, array, 0, length);
		}
		else {
			System.arraycopy(data, start, array, 0, data.length-start);
			System.arraycopy(data, 0, array, data.length-start, end);
		}
		return array;
	}
	
	
	
	
	////////////////////////////////////////////////////////////////
	/// CLONEABLE
	
	@Override
	public Object clone() {
		try{
			//noinspection unchecked
			ArrayListDeque<E> myClone = (ArrayListDeque<E>) super.clone();
			myClone.data = new Object[this.data.length];
			System.arraycopy(this.data, 0, myClone.data, 0, this.data.length);
			assert myClone.invariants();
			return myClone;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}
