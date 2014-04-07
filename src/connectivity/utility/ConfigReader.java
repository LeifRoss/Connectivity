package connectivity.utility;

import java.util.HashMap;

/**
 * 
 * @author Leif Andreas Rudlang
 * @version 1.0
 * @date 22.01.2014
 */
public class ConfigReader {


	private static final String NOT_FOUND = "File not found: ";
	private static final String LINE_COMMENT = "//";
	private static final String LINE_SPACE = " ";
	
	
	private String location;
	private HashMap<String,String> values;
	private int size;
	private boolean hasConfig;
	private String line_separator;
	

	/**
	 * 
	 */
	public ConfigReader(){
		clear();
	}
	
	/**
	 * 
	 * @param configlocation
	 */
	public ConfigReader(String configlocation){

		clear();		

		this.location = configlocation;
		hasConfig = read(location);
	}

	/**
	 * Clears the configuration reader
	 */
	public void clear(){

		
		location = "";
		line_separator = Utility.getLineSeparator("");
		values = new HashMap<String,String>();	
		hasConfig = false;
		size = 0;
	}


	/**
	 * 
	 * @param path
	 * @return
	 */
	public boolean read(String path){


		this.location = path;		
		String config = Utility.readFile(location);


		if(config == null || config.isEmpty() || config.contains(NOT_FOUND)){
			return false;
		}

		line_separator = Utility.getLineSeparator(config);
		
		String[] lines = config.split(line_separator);
		String parameter = "";
		String name = "";

		if(lines.length <= 0){
			return false;
		}

		for(String line : lines){

			if(line == null || line.isEmpty()){
				continue;
			}

			String[] expl = line.split(LINE_SPACE);


			if(expl.length > 1){

				name = expl[0];
				parameter = expl[1];						

				if(!valueExists(name) && !name.contains(LINE_COMMENT)){
					size++;
					values.put(name, parameter);
				}
			}

		}

		return true;
	}


	public void write(String path){
		
		Utility.writeFile(path, this.toString());		
	}
	
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean valueExists(String key){

		return values.containsKey(key);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key){

		return values.get(key);
	}

	
	public void setValue(String key, String param){
		
		values.put(key, param);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasConfiguration(){

		return hasConfig;
	}

	/**
	 * 
	 * @return
	 */
	public int size(){

		return size;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEmpty(){

		return values.isEmpty();
	}

	/**
	 * 
	 * @return
	 */
	public String lastPath(){

		return location;
	}


	
	@Override
	public String toString(){
		
		StringBuilder str = new StringBuilder();
		
		
		for(String key : values.keySet()){
			
			str.append(key+" "+getValue(key)+line_separator);
		}
		
			
		return str.toString();
	}

	
	
	
}
