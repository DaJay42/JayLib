package ch.dajay42.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BiMap<L, R> extends HashMap<L, R>{
	
	private static final long serialVersionUID = 1L;
	
	private BiMap<R, L> inverse;

	public BiMap() {
		inverse = new BiMap<>(this);
	}
	
	private BiMap(BiMap<R, L> inverse) {
		this.inverse = inverse;
		inverse.forEach((r,l) -> put(l,r));
	}
	
	public BiMap<R, L> getInverse() {
		return inverse;
	}
	
	
	private void put_(L key, R value){
		super.put(key, value);
	}
	
	@Override
	public R put(L key, R value){
		if(containsKey(key))
			inverse.remove(get(key));
		if(inverse.containsKey(value))
			remove(inverse.get(value));
		inverse.put_(value, key);
		return super.put(key, value);
	}
	
	@Override
	public void putAll(Map<? extends L, ? extends R> m){
		m.forEach(this::put);
	}
	
	private void remove_(Object key){
		super.remove(key);
	}
	
	@Override
	public R remove(Object key){
		inverse.remove_(get(key));
		return super.remove(key);
	}
	
	private void remove_(Object key, Object value){
		super.remove(key, value);
	}
	
	@Override
	public boolean remove(Object key, Object value){
		inverse.remove_(value, key);
		return super.remove(key, value);
	}
	
	private void clear_(){
		super.clear();
	}
	
	@Override
	public void clear(){
		inverse.clear_();
		super.clear();
	}
	
	@Override
	public boolean containsValue(Object value){
		return inverse.containsKey(value);
	}
	
	@Override
	public Collection<R> values(){
		return inverse.keySet();
	}
	
	@Override
	public R getOrDefault(Object key, R defaultValue){
		return containsKey(key) ? get(key) : defaultValue;
	}
	
	@Override
	public R putIfAbsent(L key, R value){
		return containsKey(key) ? get(key) : put(key, value);
	}
	
	@Override
	public boolean replace(L key, R oldValue, R newValue){
		boolean b = super.replace(key, oldValue, newValue);
		if(b)
			inverse.put(newValue, key);
		return b;
	}
	
	@Override
	public R replace(L key, R value){
		R r = get(key);
		if(containsKey(key))
			put(key, value);
		return r;
	}
	
	@Override
	public R computeIfAbsent(L key, Function<? super L, ? extends R> mappingFunction){
		R r = null;
		if(get(key) == null){
			r = mappingFunction.apply(key);
			if(r != null)
				put(key, r);
		}
		return r;
	}
	
	@Override
	public R computeIfPresent(L key, BiFunction<? super L, ? super R, ? extends R> remappingFunction){
		R r = get(key);
		if(r != null){
			r = remappingFunction.apply(key,r);
			if(r != null)
				put(key, r);
			else
				remove(key);
		}
		return r;
	}
	
	@Override
	public R compute(L key, BiFunction<? super L, ? super R, ? extends R> remappingFunction){
		R r = remappingFunction.apply(key, get(key));
		if(r == null){
			remove(key);
		}
		else {
			put(key, r);
		}
		return r;
	}
	
	@Override
	public R merge(L key, R value, BiFunction<? super R, ? super R, ? extends R> remappingFunction){
		R r = get(key);
		if(r == null){
			put(key, value);
		}
		else {
			r = remappingFunction.apply(r, value);
			if(r == null){
				remove(key);
			}
			else {
				put(key, r);
			}
		}
		return r;
	}
	
	@Override
	public void replaceAll(BiFunction<? super L, ? super R, ? extends R> function){
		forEach((l, r) -> {
			R r2 = function.apply(l,r);
			remove(l,r);
			put(l,r2);
		});
	}
	
	@Override
	public Object clone(){
		BiMap<L,R> map = (BiMap<L, R>) super.clone();
		map.inverse = new BiMap<>(map);
		return map;
	}
}
