package ch.dajay42.math;

public enum TernaryUnit{
	NO,
	MAYBE,
	YES;
	
	public TernaryUnit and(TernaryUnit that){
		return compareTo(that) <= 0 ? this : that;
	}
	
	public TernaryUnit or(TernaryUnit that){
		return compareTo(that) >= 0 ? this : that;
	}
	
	public static TernaryUnit not(TernaryUnit that){
		return values()[YES.ordinal() - that.ordinal()];
	} 
	
	public TernaryUnit not(){
		return not(this);
	}
	
	public TernaryUnit nand(TernaryUnit that){
		return not(this.and(that));
	}
	
	public TernaryUnit nor(TernaryUnit that){
		return not(this.or(that));
	}
	
	public TernaryUnit xnor(TernaryUnit that){
		return (this.and(that)).or(this.nor(that));
	}
	
	public TernaryUnit xor(TernaryUnit that){
		return (this.nand(that)).and(this.or(that));
	}
	
	public static TernaryUnit valueOf(Boolean b){
		return (b != null) ? (b ? YES : NO) : MAYBE;
	}
	
	public Boolean toBoolean(){
		switch(this){
			case NO:
				return false;
			case YES:
				return true;
		}
		return null;
	}
}
