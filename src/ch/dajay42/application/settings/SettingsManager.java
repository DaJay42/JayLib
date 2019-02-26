package ch.dajay42.application.settings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface SettingsManager {

	String fileName();

	Serializable getValueOf(String name);

	<T extends Serializable> void setValueOf(String name, T value);
	
	Class<? extends Serializable> getTypeOf(String name);

	void registerSetting(Setting<?> setting);

	void registerSettings(Setting<?>... settings);
	
	boolean hasSetting(String name);
	
	default void readFromFile(Path path) throws IOException{
		List<String> lines = Files.readAllLines(path);
		valueOf(lines);
	}

	default void valueOf(List<String> lines) {
		for(String line : lines){
			//remove comments
			line = (line.indexOf(';') < 0) ? line : line.substring(0, line.indexOf(';'));
			
			//skip empty lines
			if(line.trim().isEmpty())
				continue;
			
			//filter category names
			else if(line.startsWith("["))
				continue;
			
			else{
				String[] s = line.split("=");
				String name = s[0];
				String value = s[1];
				
				setValueOf(name, value);
				
			}
			
		}
	}

	public abstract String toString();
	
	public default void writeToDisk() throws IOException{
		writeToFile(FileSystems.getDefault().getPath(fileName()));
	}
	
	public default void writeToFile(Path path) throws IOException{
		try(ByteArrayOutputStream bs = new ByteArrayOutputStream()){
			try(PrintStream ps = new PrintStream(bs)){
				ps.print(toString());
				ps.flush();
				bs.flush();
				byte[] bytes = bs.toByteArray();
				Files.write(path, bytes);
			}
		}
	}
	
	public default void readFromDisk() throws IOException{
		readFromFile(FileSystems.getDefault().getPath(fileName()));
	}

}