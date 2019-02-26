package ch.dajay42.collections;

import java.io.Serializable;
import java.util.Map;

public class ComparableTuple<K extends Comparable<? super K>,V> implements Comparable<ComparableTuple<K,V>>, Map.Entry<K,V>, Serializable {

	private static final long serialVersionUID = 1L;
	
	K key;
	V value;
	
	public ComparableTuple(K key, V value){
		this.key = key;
		this.value = value;
	}
	
	public K getKey(){
		return key;
	}
	
	public V getValue(){
		return value;
	}
	
	
	public int compareTo(ComparableTuple<K,V> other) {
		return key.compareTo(other.getKey());
	}

	@Override
	public V setValue(V value) {
		V v = this.value;
		this.value = value;
		return v;
	}


	@Override
	public String toString() {
		return "(" + key.toString() + ","+ value.toString() + ")";
	}

}
