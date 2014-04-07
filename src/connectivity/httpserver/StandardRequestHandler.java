package connectivity.httpserver;


import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

/**
 * A request handler that handles the standard requests
 * @author Leif Andreas Rudlang
 * @version 0.1
 * @date 22.01.2014
 */
public abstract class StandardRequestHandler implements HttpAsyncRequestHandler<HttpRequest> {



	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";
	private static final String HTTP_HEAD = "HEAD";



	public StandardRequestHandler() {
		super();
	}

	public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request, final HttpContext context) {

		return new BasicAsyncRequestConsumer();
	}

	public void handle(
			final HttpRequest request,
			final HttpAsyncExchange httpexchange,
			final HttpContext context) throws HttpException, IOException {


		HttpResponse response = httpexchange.getResponse();
		handleInternal(request, response, context);
		httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
	}

	private void handleInternal(
			final HttpRequest request,
			final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {


		String httpMethod = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);

		if(httpMethod.equals(HTTP_GET)){
			onGET(request,response,context);
		}else if(httpMethod.equals(HTTP_POST)){
			onPOST(request,response,context);
		}else if(httpMethod.equals(HTTP_HEAD)){
			onHEAD(request,response,context);
		}else{

			throw new MethodNotSupportedException(httpMethod + " method not supported");
		}

	}


	/**
	 * Requests a representation of the specified resource.
	 * @param request
	 * @param response
	 * @param context
	 */
	public abstract void onGET(
			final HttpRequest request,
			final HttpResponse response,
			final HttpContext context);

	/**
	 * Requests that the server accept the entity enclosed in the request as a new subordinate of the web resource identified by the URI.
	 * @param request
	 * @param response
	 * @param context
	 */
	public abstract void onPOST(
			final HttpRequest request,
			final HttpResponse response,
			final HttpContext context);

	/**
	 * Asks for the response identical to the one that would correspond to a GET request, but without the response body.
	 * @param request
	 * @param response
	 * @param context
	 */
	public abstract void onHEAD(
			final HttpRequest request,
			final HttpResponse response,
			final HttpContext context);



}
