package ch.dajay42.application.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Setting<T>{
	
	private final Supplier<? extends T> getter;
	private final Consumer<? super T> setter;
	private final Parser<? extends T> parser;
	private final Printer<? super T> printer;
	
	
	public Setting(Supplier<? extends T> getter, Consumer<? super T> setter, Parser<? extends T> parser, Printer<? super T> printer){
		this.getter = getter;
		this.setter = setter;
		this.parser = parser;
		this.printer = printer;
	}
	
	public Setting(Supplier<? extends T> getter, Consumer<? super T> setter, Parser<? extends T> parser){
		this(getter, setter, parser, Printer.DEFAULT_PRINTER);
	}
	
	public T get(){
		return getter.get();
	}
	
	public void set(T value){
		setter.accept(value);
	}
	
	public String toString(){
		return printer.print(get());
	}
	
	public void parse(String value){
		setter.accept(parser.parse(value));
	}
}
