package ch.dajay42.application.config;

@FunctionalInterface
public interface Parser<T>{
	T parse(String s);
	
	
	Parser<String> STRING_PARSER = String::toString;
	
	Parser<Boolean> BOOLEAN_PARSER = Boolean::parseBoolean;
	Parser<Byte> BYTE_PARSER = Byte::parseByte;
	Parser<Short> SHORT_PARSER = Short::parseShort;
	Parser<Character> CHARACTER_PARSER = s -> s.charAt(0);
	Parser<Integer> INTEGER_PARSER = Integer::parseInt;
	Parser<Long> LONG_PARSER = Long::parseLong;
	Parser<Float> FLOAT_PARSER = Float::parseFloat;
	Parser<Double> DOUBLE_PARSER = Double::parseDouble;
}
