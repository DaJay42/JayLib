package ch.dajay42.application.config;

@FunctionalInterface
public interface Printer<T>{
	String print(T t);
	
	
	Printer<Object> DEFAULT_PRINTER = Object::toString;
}
