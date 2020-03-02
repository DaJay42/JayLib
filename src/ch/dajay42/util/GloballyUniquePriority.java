package ch.dajay42.util;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaJay42
 *
 */
public class GloballyUniquePriority implements Comparable<GloballyUniquePriority>{

	public static final GloballyUniquePriority TOP = new GloballyUniquePriority(){
		
		@Override
		Long get(int i){
			return 0xFFFF_FFFF_FFFF_FFFFL;
		}
		@Override
		public int compareTo(GloballyUniquePriority other) {
			if(this == other)
				return 0;
			else
				return 1;
		}
		@Override
		public String toString(){
			return "0b1";
		}
	};
	
	public static final GloballyUniquePriority BOTTOM = new GloballyUniquePriority(){

		@Override
		Long get(int i){
			return 0L;
		}
		@Override
		public int compareTo(GloballyUniquePriority other) {
			if(this == other)
				return 0;
			else
				return -1;
		}
		@Override
		public String toString(){
			return "0b0";
		}
	};
	
	private ArrayList<Long> value;
	
	/**
	 * 
	 */
	public GloballyUniquePriority() {
		value = new ArrayList<>(1);
	}

	Long get(int i){
		while(value.size() <= i){
			value.add(ThreadLocalRandom.current().nextLong());
		}
		return value.get(i);
	}


	@Override
	public int compareTo(GloballyUniquePriority other) {
		
		if(this == other)
			return 0;
		
		else if(TOP == other)
			return -1;
		
		else if(BOTTOM == other)
			return 1;
		
		else {
			int parity = 0;
			
			for(int i = 0; parity == 0; i++){
				parity = Long.compareUnsigned(this.get(i), other.get(i));
			}
			
			return parity;
		}
	}
	
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder("0b0.");
		for(Long l : value){
			for(int i = 63; i >= 0; i--){
				boolean b = ((1L << i) & l) != 0L;
				s.append(b ? '1' : '0');
			}
		}
		s.append("...");
		return s.toString();
	}
}
