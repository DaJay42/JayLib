package ch.dajay42.collections;

import java.util.Map;

class BasicEntry<K,V> implements Map.Entry<K,V>{
	private final K key;
	private V value = null;
	
	BasicEntry(K key){
		this.key = key;
	}
	
	BasicEntry(K key, V value){
		this.key = key;
		this.value = value;
	}
	
	@Override
	public K getKey(){
		return key;
	}
	
	@Override
	public V getValue(){
		return value;
	}
	
	@Override
	public V setValue(V value){
		V v = this.value;
		this.value = value;
		return v;
	}
}
