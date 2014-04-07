package connectivity.httpserver;



import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.DefaultHttpServerIODispatch;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.impl.nio.DefaultNHttpServerConnectionFactory;
import org.apache.http.impl.nio.SSLNHttpServerConnectionFactory;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncService;
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import connectivity.utility.ErrorHandler;


/**
 * A class which manages one Asynchronous HTTP server
 * @author Leif Andreas Rudlang
 * @version 0.1
 * @date 22.01.2014
 */
public class HttpServer {

	public static final int HTTP_STANDARD_PORT = 80;
	private static final int HTTP_STANDARD_TIMEOUT = 3000;
	private static final int HTTP_STANDARD_POOL_SIZE = 2;
	public static final String HTTP_VERSION = "HTTP/1.1";
	
	



	
	private HttpAsyncRequestHandler<HttpRequest> handler;
	private IOEventDispatch ioEventDispatch;
	private IOReactorConfig config;
	private ListeningIOReactor ioReactor;
	private ServerLogger logger;
	private UriHttpAsyncRequestHandlerMapper reqistry;
	private URL key_url;

	private int port;
	private int timeout;
	private int pool_size;
	
	private boolean running;
	private boolean pause;
	private boolean secure;

	char[] keystorePW = null;

	
	public HttpServer(int port, HttpAsyncRequestHandler<HttpRequest> handler) throws IOReactorException{
		this(port, handler, false, null, null, null, HTTP_STANDARD_TIMEOUT, HTTP_STANDARD_POOL_SIZE);
	}


	public HttpServer(int port, HttpAsyncRequestHandler<HttpRequest> handler, boolean secure, ServerLogger logger, URL keystore, char[] pw, int timeout, int pool_size) throws IOReactorException{

		this.keystorePW = pw;
		this.logger = logger;
		this.port = port;
		this.handler = handler;
		this.key_url = keystore;
		this.secure = secure;
		this.timeout = timeout;
		this.pool_size = pool_size;
		this.running = false;
		this.pause = false;
		

		
		
		setup();	
	}



	private void setup() throws IOReactorException{

		if(logger == null){
			logger = new StandardServerLogger();
		}

		// Create HTTP protocol processing chain
		HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new ResponseDate())
				.add(new ResponseServer(HTTP_VERSION))
				.add(new ResponseContent())
				.add(new ResponseConnControl()).build();


		// Create request handler registry
		reqistry = new UriHttpAsyncRequestHandlerMapper();
		// Register the default handler for all URIs
		
		if(handler!=null){
		reqistry.register("*", handler);
		}
		
		// Create server-side HTTP protocol handler
		HttpAsyncService protocolHandler = new HttpAsyncService(httpproc, reqistry) {

			@Override
			public void connected(final NHttpServerConnection conn) {
				logger.logConnected(conn);
				super.connected(conn);
			}

			@Override
			public void closed(final NHttpServerConnection conn) {
				logger.logDisconnected(conn);
				super.closed(conn);
			}

		};

		// Create HTTP connection factory
		NHttpConnectionFactory<DefaultNHttpServerConnection> connFactory = null;

		if (secure && key_url!=null) {
			// Initialize SSL context
			boolean success = false;

			try {

				connFactory = setupSSL();
				success = true;

			} catch (UnrecoverableKeyException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (CertificateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(!success || connFactory == null){
				secure = false;
				connFactory = new DefaultNHttpServerConnectionFactory( ConnectionConfig.DEFAULT );
			}

		}else{

			connFactory = new DefaultNHttpServerConnectionFactory( ConnectionConfig.DEFAULT );
		}


		// Create server-side I/O event dispatch
		ioEventDispatch = new DefaultHttpServerIODispatch(protocolHandler, connFactory);

		// Set I/O reactor defaults, create set for timeout and number of threads!
		config = IOReactorConfig.custom()
				.setIoThreadCount(pool_size)
				.setSoTimeout(timeout)
				.setConnectTimeout(timeout)
				.build();

		ioReactor = new DefaultListeningIOReactor(config);

	}


	private SSLNHttpServerConnectionFactory setupSSL() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException{
		

		
		KeyStore keystore  = KeyStore.getInstance("jks");
		keystore.load(key_url.openStream(), keystorePW);
		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmfactory.init(keystore, keystorePW);
		KeyManager[] keymanagers = kmfactory.getKeyManagers();
		
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(keymanagers, null, null);
		
		
		return new SSLNHttpServerConnectionFactory(sslcontext,null, ConnectionConfig.DEFAULT);	
	}

	
	
	
	public void registerHandler(HttpAsyncRequestHandler<HttpRequest> in, String uri){
		
		reqistry.register(uri, in);
	}
	
	
	public void registerUniformHandler(HttpAsyncRequestHandler<HttpRequest> in){
		
		this.handler = in;	
		reqistry.register("*", handler);
	}
	
	
	
	
	
	
	/**
	 * 
	 */
	public void reset(){
		try {
			setup();
		} catch (IOReactorException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Starts the server
	 * @throws Exception
	 */
	public synchronized void start() throws Exception{


		if(isRunning()){
			ErrorHandler.handleError("HTTPSERVER: Already running");
			shutdown();
		}

		try {
			pause = false;
			running = true;
			ioReactor.listen(new InetSocketAddress(port));
			ioReactor.execute(ioEventDispatch);


		} catch (InterruptedIOException ex) {
			ErrorHandler.handleError("HTTPSERVER: "+ex.getMessage());
		} catch (IOException e) {
			ErrorHandler.handleError("HTTPSERVER: "+e.getMessage());
		}

	}



	/**
	 * Shuts the server down
	 * @throws IOException
	 */
	public void shutdown() throws IOException{

		pause = false;
		running = false;
		ioReactor.shutdown();	
	}

	/**
	 * Pauses the server
	 * @throws IOException
	 */
	public synchronized void pause() throws IOException{
		pause = true;
		ioReactor.pause();
	}

	/**
	 * Resumes the server
	 * @throws IOException
	 */
	public synchronized void resume() throws IOException{
		pause = false;
		ioReactor.resume();
	}

	/**
	 * Returns the server logger
	 * @return
	 */
	public ServerLogger getServerLogger(){

		return logger;
	}

	/**
	 * Returns true if the server is running with a secure connection
	 * @return
	 */
	public boolean hasSSL(){

		return secure;
	}


	/**
	 * Returns true if the server is started, this is unaffected by PAUSE / RESUME
	 * @return
	 */
	public boolean isRunning(){

		return running;
	}

	public boolean isPaused(){
		
		return pause;
	}


	/**
	 * Starts the server in a new thread
	 */
	public void startThreaded(){
		
		
		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				
				try {
					start();
				} catch (Exception e) {
					e.printStackTrace();
					running = false;
				}	
			}
		
		};
		
		Thread t = new Thread(runnable);
		t.start();
				
		
	}
	
	
	public int getPort(){
		return port;
	}
	

	
}
