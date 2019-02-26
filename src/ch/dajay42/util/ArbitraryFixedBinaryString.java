/**
 * 
 */
package ch.dajay42.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaJay42
 *
 */
public class ArbitraryFixedBinaryString implements Comparable<ArbitraryFixedBinaryString>, Serializable{

	private static final long serialVersionUID = 1L;

	public static final ArbitraryFixedBinaryString TOP = new ArbitraryFixedBinaryString(){

		private static final long serialVersionUID = 1L;
		
		@Override
		protected Boolean get(int i){
			return true;
		}
		@Override
		public int compareTo(ArbitraryFixedBinaryString other) {
			if(this == other)
				return 0;
			else
				return 1;
		}
		@Override
		public String toString(){
			return "0b0";
		}
	};
	
	public static final ArbitraryFixedBinaryString BOTTOM = new ArbitraryFixedBinaryString(){

		private static final long serialVersionUID = 1L;
		
		@Override
		protected Boolean get(int i){
			return false;
		}
		@Override
		public int compareTo(ArbitraryFixedBinaryString other) {
			if(this == other)
				return 0;
			else
				return -1;
		}
		@Override
		public String toString(){
			return "0b1";
		}
	};
	
	private ArrayList<Boolean> value;
	
	/**
	 * 
	 */
	public ArbitraryFixedBinaryString() {
		value = new ArrayList<>(16);
		get(4);
	}

	protected Boolean get(int i){
		while(value.size() <= i){
			value.add(ThreadLocalRandom.current().nextBoolean());
		}
		return value.get(i);
	}


	@Override
	public int compareTo(ArbitraryFixedBinaryString other) {
		if(this == other)
			return 0;
		
		if(TOP == other)
			return -1;
		
		if(BOTTOM == other)
			return 1;
		
		int parity = 0;
		
		for(int i = 0; parity == 0; i++){
			parity = this.get(i).compareTo(other.get(i));
		}
		
		return parity;
	}
	
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder("0b0.");
		for(Boolean b : value){
			s.append(b ? '1' : '0');
		}
		s.append("...");
		return s.toString();
	}
}
