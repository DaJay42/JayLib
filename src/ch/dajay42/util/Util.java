package ch.dajay42.util;

import java.util.*;
import java.util.function.*;

/**An uninstantiable state-free class with the purpose of providing various utility functions that do not
 * logically belong to any other class.
 * @author DaJay42
 *
 */
public final class Util {
	/**Cannot be instantiated.*/
	private Util() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	/**Packs the provided elements into an array,
	 * skipping all that boilerplate code...
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] array(T...elems){
		return elems;
	}
	
	/**Fills the provided {@code array} by calling {@code source} once for every element.
	 * @param array array to be filled
	 * @param source Method that generates elements.
	 */
	public static <T> void fill(T[] array, Supplier<T> source){
		for(int i = 0; i < array.length; i++){
			array[i] = source.get();
		}
	}

	/**Join multiple arrays into one large array.<br/>
	 * Returns an array of bytes with length equal to the sum of lengths of the provided {@code arrays},
	 * with its contents equal to the concatenated contents of the provided {@code arrays},
	 * in the order provided.
	 * @param arrays 
	 * @return concatenated array
	 */
	public static byte[] joinByteArrays(byte[]... arrays) {
		int totalLength = sum(arrayMap((a)->a.length, arrays, new Integer[arrays.length]));
		byte[] out = new byte[totalLength];
		for(int i = 0, k = 0; k < totalLength && i < arrays.length; k += arrays[i].length, i++)
			System.arraycopy(arrays[i], 0, out, k, arrays[i].length);
		return out;
	}
	
	
	/**Returns sum of the elements of the array, by simple iteration. {@code null} is considered 0.
	 * @param summands
	 * @return the sum
	 */
	public static int sum(Integer[] summands){
		int s = 0;
		for(Integer i : summands)
			s += (i == null) ? 0 : i;
		return s;
	}
	
	/**Returns sum of the elements of the array, by simple iteration.
	 * @param summands
	 * @return
	 */
	public static int sum(int[] summands){
		int s = 0;
		for(int i : summands)
			s += i;
		return s;
	}
	
	
	/**Fills array {@code out by applying the function {@code f} to the corresponding element of {@code in}.<br/>
	 * If {@code in.length > out.length}, the additional elements are ignored.
	 * If {@code out.length > in.length}, the additional elements are filled with {@code null}.
	 * @param f
	 * @param in
	 * @param out
	 * @return
	 */
	public static <T,R> R[] arrayMap(Function<T, R> f, T[] in, R[] out){
		for(int i = 0; i < out.length; i++){
			out[i] = (i < in.length) ? f.apply(in[i]) : null;
		}
		return out;
	}

	
	/**Fills a List by applying the function {@code f} to every element of {@code in},
	 * in the order returned by the collection's iterator. <br/>
	 */
	public static <T,R> List<R> collectionMap(Function<T,R> f, Collection<T> in){
		List<R> out = new ArrayList<>(in.size());
		for(T t : in){
			out.add(f.apply(t));
		}
		return out;
	}
}
