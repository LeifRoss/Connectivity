package connectivity.utility;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * A standard utility class with commonly used functions
 * 
 * @author Leif Andreas Rudlang
 * @date 22.01.2014
 */
public class Utility {


	/**
	 * 
	 * @param filepath
	 * @param content
	 * @throws FileNotFoundException
	 */
	public static void writeFile(String filepath, byte[] content) throws FileNotFoundException{

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {

			fos = new FileOutputStream(new File(filepath));
			bos = new BufferedOutputStream(fos);

			bos.write(content);

		} catch (IOException e) {

			e.printStackTrace();
		} finally{

			try {

				if(bos!=null)
					bos.close();

				if(fos!=null)
					fos.close();

			} catch (IOException e) {
				e.printStackTrace();
			}


		}

	}

	/**
	 * 
	 * @param filepath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static byte[] readBytes(String filepath) throws FileNotFoundException{

		FileInputStream fis = null;
		File file = new File(filepath);

		byte[] data = new byte[(int) file.length()];

		try {

			fis = new FileInputStream(file);
			fis.read(data);

		}catch(Exception e){

			e.printStackTrace();
		}finally{

			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return data;
	}


	/**
	 * Write string to file
	 * @param filepath
	 * @param content
	 */
	public static void writeFile(String filepath, String content){

		try {

			File file = new File(filepath);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Read file to string
	 * @param filename
	 * @return
	 */
	public static String readFile(String filename){


		StringBuilder source = new StringBuilder();
		File f = new File(filename);

		if(!f.exists()){
			return "File not found: "+f.getAbsolutePath();
		}

		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			while ((line = reader.readLine()) != null) {
				source.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
		}

		return source.toString();
	}


	private static String assetsLocation;

	/**
	 * Returns the location to the asset folder
	 * @return
	 */
	public static String getAssetsLocation(){


		if(assetsLocation != null && !assetsLocation.isEmpty()){
			return assetsLocation;			
		}


		String path = "";

		try {

			File jarpath = new File(Utility.class.getProtectionDomain().getCodeSource().getLocation().toURI());

			path = jarpath.getPath();
			path = path.replace("\\", "/");

			if(path.endsWith("bin/")){
				path = path.replaceAll("bin/", "");
			}else if(path.endsWith("bin//")){
				path = path.replaceAll("bin//", "");
			}else if(path.endsWith("bin")){
				path = path.replaceAll("bin", "");
			}

			if(path.endsWith(".jar")){			
				path = path.substring(0, path.lastIndexOf("/")+1);		
			}

			assetsLocation = path + "assets//";		

		}  catch (URISyntaxException e) {
			e.printStackTrace();
		}



		return assetsLocation;
	}



	public static final String LINE_SEPARATOR_UNIX = "\n";
	public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
	private static final String PROPERTY_LINESEPERATOR = "line.separator";

	/**
	 * Returns the used line separator in the given string
	 * @param in
	 * @return
	 */
	public static String getLineSeparator(String in){

		if(in.contains(LINE_SEPARATOR_WINDOWS)){
			return LINE_SEPARATOR_WINDOWS;
		}

		if(in.contains(LINE_SEPARATOR_UNIX)){
			return LINE_SEPARATOR_UNIX;
		}

		return System.getProperty(PROPERTY_LINESEPERATOR);
	}




	/**
	 * 
	 * @param image
	 * @param fileformat
	 * @return
	 */
	public static byte[] imageToByteArray(BufferedImage image, String fileformat){

		byte[] data = null;

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {

			ImageIO.write(image, fileformat, output);
			output.flush();
			data = output.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return data;
	}


	/**
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage byteArrayToImage(byte[] data){

		InputStream in = null;
		BufferedImage image = null;

		try{

			in = new ByteArrayInputStream(data);
			image = ImageIO.read(in);

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return image;
	}





	/**
	 * Read and consume the entity
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	public static String readEntityString(HttpEntity entity) throws IOException{


		BufferedReader rd = new BufferedReader( new InputStreamReader( entity.getContent()));

		StringBuffer str = new StringBuffer();
		String line = "";

		while ((line = rd.readLine()) != null) {
			str.append(line);
		}


		EntityUtils.consume(entity);		
		rd.close();


		return str.toString();
	}

	/**
	 * Read and consume the entity
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	public static byte[] readEntityBytes(HttpEntity entity) throws IOException{

		byte[] data = EntityUtils.toByteArray(entity);		
		EntityUtils.consume(entity);		

		return data;
	}



	//byte[] entityContent = EntityUtils.toByteArray(entity);

}
