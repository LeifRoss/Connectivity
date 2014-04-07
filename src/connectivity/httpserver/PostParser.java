package connectivity.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;

/**
 * 
 * GCServer PostParser
 * 
 * @author Leif Andreas Rudlang
 * @date 28.02.2014
 * 
 * 
 */
public class PostParser {


	private static final String EMPTY_POST = "NULL";

	private HashMap<String,String> post;
	private HashMap<String,byte[]> file;
	private byte[] data;
	private String filename;
	private boolean has_file;

	private int status_code = HttpStatus.SC_NO_CONTENT;

	/**
	 * Parse a httprequest
	 * @param request
	 */
	public PostParser(HttpRequest request){

		parse(request);
	}


	private void parse(HttpRequest request){


		post = new HashMap<String,String>();
		file = new HashMap<String,byte[]>();


		if ( !(request instanceof HttpEntityEnclosingRequest) ) {
			status_code = HttpStatus.SC_BAD_REQUEST;
			return;
		}

		HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

		try {

			List<NameValuePair> list = URLEncodedUtils.parse(entity);


			if(list!=null){

				for(NameValuePair p : list){		
					if(p==null){
						continue;
					}

					post.put(p.getName(), p.getValue());
				}
			}

			String contentType = request.getFirstHeader("Content-Type").getValue();
			String boundary = contentType.substring(contentType.indexOf("boundary=") + "boundary=".length());

			MultipartStream multipartStream = new MultipartStream(entity.getContent(), boundary.getBytes(), boundary.length()+6, null);

			boolean has_next = multipartStream.skipPreamble();

			while (has_next) {

				String header = multipartStream.readHeaders();

				ByteArrayOutputStream output = new ByteArrayOutputStream();
				multipartStream.readBodyData(output);			
				byte[] buffer = output.toByteArray();
				output.close();     


				if(header.contains("filename=")){

					filename = seperate(header,"filename=");
					String name = seperate(header,"name=");

					this.data = buffer;
					has_file = true;

					file.put(name, buffer);
				}else{

					String name = seperate(header,"name=");		
					String value = new String(buffer);		
					value = value.trim();

					post.put(name, value);		
				}


				has_next = multipartStream.readBoundary();
			}


		} catch (IOException e) {
			e.printStackTrace();
		}finally{

			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				e.printStackTrace();
			}		

		}


		status_code = HttpStatus.SC_ACCEPTED;
	}



	private String seperate(String header, String value){

		try{
			if(header.contains(value)){

				int idx0 = header.indexOf(value);
				int idx1 = header.indexOf('"',idx0);
				int idx2 = header.indexOf('"',idx1+1);

				if(idx1 > 0 && idx2 > 0 && idx2 < header.length()){
					String result = header.substring(idx1+1, idx2);
					return result;
				}
			}		
		}catch(Exception e){
			e.printStackTrace();
		}

		return "";
	}


	/**
	 * Returns true if there is a file
	 * @return
	 */
	public boolean hasFile(){
		return has_file;
	}

	/**
	 * Return the filename of the last parsed file
	 * @return
	 */
	public String getFilename(){
		return filename;
	}


	/**
	 * Return the file content of the last parsed file
	 * @return
	 */
	public byte[] getFileContent(){
		return data;
	}

	/**
	 * Return the status code
	 * @return
	 */
	public int getStatus(){
		return status_code;
	}

	/**
	 * Return the post hashmap
	 * @return
	 */
	public HashMap<String,String> post(){

		return post;
	}

	/**
	 * Return the file hashmap
	 * @return
	 */
	public HashMap<String,byte[]> file(){

		return file;
	}

	/**
	 * Return post file given by input get
	 * @param in
	 * @return
	 */
	public byte[] file(String in){

		if(isset_f(in)){
			return file.get(in);
		}	

		return null;
	}

	/**
	 * Return post parameter by given input key
	 * @param in
	 * @return
	 */
	public String post(String in){

		if(isset(in)){
			return post.get(in);
		}

		return EMPTY_POST;
	}


	/**
	 * Check if this post parameter is set
	 * @param in
	 * @return
	 */
	public boolean isset(String in){

		return post!=null && post.containsKey(in);
	}


	/**
	 * Check if this post file is set
	 * @param in
	 * @return
	 */
	public boolean isset_f(String in){

		return file!=null && file.containsKey(in);
	}

	/**
	 * Clears this parser
	 */
	public void clear(){

		file.clear();
		file = null;
		post = null;
		filename = null;
		data = null;
		has_file = false;
		status_code = HttpStatus.SC_NO_CONTENT;

	}


}
