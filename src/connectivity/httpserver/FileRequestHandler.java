package connectivity.httpserver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HttpContext;

public class FileRequestHandler extends StandardRequestHandler{

	private final File root;


	/**
	 * Handles regular file server requests
	 * @param documentRoot
	 */
	public FileRequestHandler(String documentRoot){
		super();

		root = new File(documentRoot);		
	}


	@Override
	public void onGET(HttpRequest request, HttpResponse response,
			HttpContext context) {

		String target = request.getRequestLine().getUri();
		File file = null;
		try {
			file = new File(this.root, URLDecoder.decode(target, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			// error		
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			NStringEntity entity = new NStringEntity( "<html><body><h1>Error</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			response.setEntity(entity);

			return;
		}

		
		if (!file.exists()) {

			// file does not exist
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			NStringEntity entity = new NStringEntity( "<html><body><h1>File" + file.getPath() + " not found</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			response.setEntity(entity);

		} else if (!file.canRead() || file.isDirectory()) {

			// access to file forbidden
			response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			NStringEntity entity = new NStringEntity( "<html><body><h1>Access denied</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			response.setEntity(entity);

		} else {

			// serve the file
			response.setStatusCode(HttpStatus.SC_OK);
			NFileEntity body = new NFileEntity(file);
			response.setEntity(body);

		}

	}

	@Override
	public void onPOST(HttpRequest request, HttpResponse response,
			HttpContext context) {

		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
		NStringEntity entity = new NStringEntity( "<html><body><h1>POST not yet implemented</h1></body></html>", ContentType.create("text/html", "UTF-8"));
		response.setEntity(entity);

	}

	@Override
	public void onHEAD(HttpRequest request, HttpResponse response,
			HttpContext context) {

		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
		NStringEntity entity = new NStringEntity( "<html><body><h1>HEAD not yet implemented</h1></body></html>", ContentType.create("text/html", "UTF-8"));
		response.setEntity(entity);

	}



}
