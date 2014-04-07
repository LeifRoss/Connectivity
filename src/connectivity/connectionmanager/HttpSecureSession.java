package connectivity.connectionmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * A class to handle HTTP connections from a client
 * @author Leif Andreas Rudlang
 * @version 0.2
 * @date 22.01.2014
 */
public class HttpSecureSession {


	private HttpClient client;
	private CookieStore cookieStore;
	private HttpClientContext httpContext;
	private boolean https_enabled = false;


	/**
	 * 
	 */
	public HttpSecureSession(){
		this(false);
	}


	/**
	 * NB: Https specific functions are not yet implemented
	 * @param https
	 */
	public HttpSecureSession(boolean https){
		this.https_enabled = https;
		setup();
	}



	private void setup(){


		RequestConfig globalConfig = RequestConfig.custom().setConnectTimeout(4000).setCookieSpec(CookieSpecs.BEST_MATCH).build();
		cookieStore = new BasicCookieStore();
		httpContext = HttpClientContext.create();
		httpContext.setCookieStore(cookieStore);

		client = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).build();

		

		if(https_enabled){

			// add https functionality here

		}
	}


	/**
	 * Requests that the server accept the entity enclosed in the request as a new subordinate of the web resource identified by the URI.
	 * @param url
	 * @param postParams
	 * @param debug
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public String post(String url, List<NameValuePair> postParams, boolean debug) throws IllegalStateException, IOException{

		HttpPost post = new HttpPost(url);

		post.setHeader("Connection", "keep-alive");
		post.setEntity(new UrlEncodedFormEntity(postParams));

		HttpResponse response = client.execute(post, httpContext);

		int responseCode = response.getStatusLine().getStatusCode();

		if(debug){
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + postParams);
			System.out.println("Response Code : " + responseCode);
		}

		return getStringResponse(response);
	}



	/**
	 * Requests that the server accept the entity enclosed in the request as a new subordinate of the web resource identified by the URI.
	 * @param url
	 * @param filepath
	 * @param mimeType
	 * @param postParams
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String postFile(String url, String filepath, String filepostparam, String mimeType, List<NameValuePair> postParams) throws ClientProtocolException, IOException{

		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Connection", "keep-alive");

		File file = new File(filepath);
		ContentBody cbFile = new FileBody(file);


		MultipartEntityBuilder mb = MultipartEntityBuilder.create();
		mb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		mb.addPart(filepostparam, cbFile); 

		for(NameValuePair pair : postParams){
			mb.addTextBody(pair.getName(), pair.getValue(),ContentType.DEFAULT_TEXT); 	
		}

		HttpEntity mpEntity = mb.build();

		httppost.setEntity(mpEntity);		
		HttpResponse response = client.execute(httppost);

		return getStringResponse(response);

	}


	/**
	 * Requests that the enclosed entity be stored under the supplied URI.
	 * @param url
	 * @param entity
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String put(String url, HttpEntity entity) throws ClientProtocolException, IOException{


		HttpPut put = new HttpPut(url);
		put.setEntity(entity);
		HttpResponse response = client.execute(put, httpContext);

		return getStringResponse(response);
	}


	/**
	 * Requests a representation of the specified resource.
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public byte[] get(String url) throws IOException{

		HttpGet get = new HttpGet(url);

		HttpResponse response = client.execute(get, httpContext);
		HttpEntity entity = response.getEntity();

		byte[] data = EntityUtils.toByteArray(entity);

		EntityUtils.consume(entity);		


		return data;
	}


	/**
	 * Asks for the response identical to the one that would correspond to a GET request, but without the response body.
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String head(String url) throws IOException{

		HttpHead head = new HttpHead(url);
		HttpResponse response = client.execute(head, httpContext);

		return getStringResponse(response);
	}


	/**
	 * Deletes the specified resource.
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String delete(String url) throws ClientProtocolException, IOException{

		HttpDelete delete = new HttpDelete(url);
		HttpResponse response = client.execute(delete, httpContext);

		return getStringResponse(response);
	}


	/**
	 * Echoes back the received request so that a client can see what (if any) changes or additions have been made by intermediate servers.
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String trace(String url) throws ClientProtocolException, IOException{

		HttpTrace trace = new HttpTrace(url);
		HttpResponse response = client.execute(trace, httpContext);

		return getStringResponse(response);
	}


	/**
	 * Returns the HTTP methods that the server supports for the specified URL.
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String options(String url) throws ClientProtocolException, IOException{

		HttpOptions options = new HttpOptions(url);
		HttpResponse response = client.execute(options, httpContext);

		return getStringResponse(response);
	}



	/**
	 * Returns the response as a string, and then consumes the response entity
	 * @param response
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private String getStringResponse(HttpResponse response) throws IllegalStateException, IOException{

		HttpEntity resEntity = response.getEntity();

		BufferedReader rd = new BufferedReader( new InputStreamReader( resEntity.getContent()));

		StringBuffer str = new StringBuffer();
		String line = "";

		boolean lines = false;


		while ((line = rd.readLine()) != null) {

			if(lines){
				str.append("\n");
			}

			str.append(line);
			lines = true;
		}		

		EntityUtils.consume(resEntity);		

		rd.close();

		return str.toString();
	}


	/**
	 * Destroys the session (..Partly deprecated)
	 */
	@SuppressWarnings("deprecation")
	public void destroy(){

		cookieStore.clear();
		client.getConnectionManager().shutdown();
	}

}
