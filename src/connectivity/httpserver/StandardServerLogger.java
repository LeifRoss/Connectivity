package connectivity.httpserver;

import java.util.LinkedList;

import org.apache.http.nio.NHttpServerConnection;

public class StandardServerLogger implements ServerLogger{



	private static final int MAX_LENGTH = 100;
	private LinkedList<String> log;


	public StandardServerLogger(){

		log = new LinkedList<String>();	
	}


	@Override
	public void logConnected(NHttpServerConnection conn) {

		appendToLog(conn+" has been connected");
	}

	@Override
	public void logDisconnected(NHttpServerConnection conn) {

		appendToLog(conn+" has been disconnected");
	}


	private void appendToLog(String in){

		if(log.size() > MAX_LENGTH){
			log.removeFirst();
		}

		log.addLast(in);
	}

	
	
	@Override
	public String toString(){


		StringBuilder str = new StringBuilder();

		for(String line : log){
			str.append(line+'\n');
		}

		return str.toString();
	}


}
