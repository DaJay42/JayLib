package ch.dajay42.collections;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class RandomizedTreeMap<K extends Comparable<? super K>,V> extends AbstractMap<K,V> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	RandomizedTreeSet<ComparableTuple<K,V>> entries = new RandomizedTreeSet<>();
	
	ComparableTuple<K,V> mockTuple(K key){
		return new ComparableTuple<K, V>(key, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Set entrySet() {
		return (Set<? extends Map.Entry<K, V>>) entries;
	}
	
	@Override
	public V put(K key, V value) {
		ComparableTuple<K, V> tuple = new ComparableTuple<K, V>(key, value);
		if(entries.contains(tuple)){
			RandomizedTreeSet<ComparableTuple<K, V>>.Node node = entries.getNodeOf(tuple);
			return node.getValue().setValue(value);
		}else{
			entries.add(new ComparableTuple<K, V>(key, value));
			return null;
		}
	}
	@Override
	public boolean containsKey(Object key) throws ClassCastException{
		return entries.getNodeOf(mockTuple((K) key)) != null;
	}
	
	@Override
	public V get(Object key) throws ClassCastException{
		RandomizedTreeSet<ComparableTuple<K, V>>.Node node = entries.getNodeOf(mockTuple((K) key));
		return (node == null) ? null : node.getValue().getValue();
	}
	
	@Override
	public V remove(Object key) throws ClassCastException{
		RandomizedTreeSet<ComparableTuple<K, V>>.Node node = entries.getNodeOf(mockTuple((K) key));
		if(node != null){
			V value = node.getValue().getValue();
			entries.remove(node.getValue());
			return value;
		}else{
			return null;
		}
	}
}
