package ch.dajay42.application.config;

import java.io.*;
import java.util.*;

public final class ConfigUtil{
	private ConfigUtil(){throw new UnsupportedOperationException();}
	
	public static void readFromFile(String filename, Map<String, Setting> settings) throws IOException{
		try(FileReader fileReader = new FileReader(filename)){
			try(BufferedReader bufferedReader = new BufferedReader(fileReader)){
				bufferedReader.lines()
						.map(s -> s.split("=", 2))
						.filter(strings -> settings.containsKey(strings[0]))
						.forEach(strings -> settings.get(strings[0]).parse(strings[1]));
			}
		}
	}
	
	public static void writeToFile(String filename, Map<String, Setting> settings) throws IOException{
		try(FileWriter fileWriter = new FileWriter(filename)){
			try(BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
				for(Map.Entry<String, Setting> entry : settings.entrySet()){
					bufferedWriter.write(entry.getKey());
					bufferedWriter.write('=');
					bufferedWriter.write(entry.getValue().toString());
					bufferedWriter.newLine();
				}
			}
		}
	}
	
	public static String prettyPrint(Map<String, Setting> settings, String lineSeparator){
		StringBuilder stringBuilder = new StringBuilder();
		for(Map.Entry<String, Setting> entry : settings.entrySet()){
			stringBuilder.append(entry.getKey());
			stringBuilder.append('=');
			stringBuilder.append(entry.getValue().toString());
			stringBuilder.append(lineSeparator);
		}
		return stringBuilder.toString();
	}
	
	public static String prettyPrint(Map<String, Setting> settings){
		 return prettyPrint(settings, System.lineSeparator());
	}
}
