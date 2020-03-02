package ch.dajay42.math.linAlg;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class SerializedMatrix implements Serializable{
	public static final long serialVersionUID = 0L;
	
	String data;
	
	public SerializedMatrix(){
	}
	
	public SerializedMatrix(String data){
		this.data = data;
	}
	
	Object readResolve() throws ObjectStreamException{
		return Matrix.valueOf(data);
	}
}
