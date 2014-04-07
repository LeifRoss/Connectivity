package connectivity.httpserver;

import org.apache.http.nio.NHttpServerConnection;

public interface ServerLogger {



	public  void logConnected(NHttpServerConnection conn);


	public  void logDisconnected(NHttpServerConnection conn);

}
