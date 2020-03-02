package ch.dajay42.collections;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class RandomizedTreeMap<K,V> extends AbstractMap<K,V>{
	
	private final RandomizedTreeSet<Entry<K,V>> entries;
	
	public RandomizedTreeMap(){
		entries = new RandomizedTreeSet<>(new DefaultEntryComparator());
	}
	
	public RandomizedTreeMap(Comparator<K> keyComparator){
		entries = new RandomizedTreeSet<>(new EntryKeyComparator(keyComparator));
	}
	
	public RandomizedTreeMap(Map<K,V> source){
		entries = new RandomizedTreeSet<>(source.entrySet(), new DefaultEntryComparator());
	}
	
	public RandomizedTreeMap(Map<K,V> source, Comparator<K> keyComparator){
		entries = new RandomizedTreeSet<>(source.entrySet(), new EntryKeyComparator(keyComparator));
	}
	
	private class DefaultEntryComparator implements Comparator<Entry<K,V>>{
		@Override
		@SuppressWarnings("unchecked")
		public int compare(Entry<K, V> o1, Entry<K, V> o2){
			return ((Comparable<? super K>) o1.getKey()).compareTo(o2.getKey());
		}
	}
	
	private class EntryKeyComparator implements Comparator<Entry<K,V>>{
		
		private final Comparator<K> keyComparator;
		
		private EntryKeyComparator(Comparator<K> keyComparator){
			this.keyComparator = keyComparator;
		}
		
		@Override
		public int compare(Entry<K, V> o1, Entry<K, V> o2){
			return keyComparator.compare(o1.getKey(), o2.getKey());
		}
	}
	
	private BasicEntry<K, V> wrapKey(Object key) throws ClassCastException{
		//noinspection unchecked
		return new BasicEntry<>((K) key);
	}
	
	@Override
	public Set<Entry<K,V>> entrySet() {
		return entries;
	}
	
	@Override
	public V put(K key, V value) {
		Entry<K,V> entry = new BasicEntry<>(key, value);
		if(entries.contains(entry)){
			RandomizedTreeSet<Entry<K,V>>.Node node = entries.getNodeOf(entry);
			return node.getValue().setValue(value);
		}else{
			entries.add(entry);
			return null;
		}
	}
	
	@Override
	public boolean containsKey(Object key) throws ClassCastException{
		return entries.getNodeOf(wrapKey(key)) != null;
	}
	
	@Override
	public V get(Object key) throws ClassCastException{
		RandomizedTreeSet<Entry<K, V>>.Node node = entries.getNodeOf(wrapKey(key));
		return (node == null) ? null : node.getValue().getValue();
	}
	
	@Override
	public V remove(Object key) throws ClassCastException{
		RandomizedTreeSet<Entry<K,V>>.Node node = entries.getNodeOf(wrapKey(key));
		if(node != null){
			V value = node.getValue().getValue();
			entries.remove(node.getValue());
			return value;
		}else{
			return null;
		}
	}
}
