package connectivity.liveserver;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class LiveServer {


	private static final int STANDARD_BACKLOG = 100;


	private LinkedList<LiveServerConnection> thread_pool;
	private ServerSocket server;
	private LiveServerHandler handler;
	
	private boolean running;
	private int port;


	public LiveServer(int port, LiveServerHandler handler) throws IOException{

		this.port = port;
		this.handler = handler;

		setup();
	}


	private void setup() throws IOException{

		running = false;
		server = new ServerSocket(port, STANDARD_BACKLOG);

	}


	public void start(){

		running = true;

		while(running){
			try{

				waitForConnection();

			}catch(EOFException eofException){
				// End of File	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		// closeCrap();
	}

	public synchronized void handleInternal(Object o, LiveServerConnection connection){
		handler.handleIncoming( o, connection);
	}



	private void waitForConnection() throws IOException{
		
		Socket connection = server.accept();

		LiveServerConnection c = new LiveServerConnection(connection,this,thread_pool.size());
		thread_pool.add(c);
		
		Thread tr = new Thread(c);
		tr.start();		

	}



	public synchronized void terminate(LiveServerConnection connection){

		connection.disconnect();
		thread_pool.remove(connection);
		
	}


	
	public void destroy() throws IOException{
		
		
		for(LiveServerConnection connection : thread_pool){
			
			connection.disconnect();		
		}
		
		thread_pool.clear();
		
		
		server.close();
	}
	

}

/*
package leifdev.schoolserver;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {

	private JTextField usertext;
	public JTextArea chatwindow;
	private ServerSocket server;

	public ArrayList<Connection> clients;

	// constructor
	public Server(){
		super("DAT SERVER");


		clients = new ArrayList<Connection>();


		usertext = new JTextField();

		usertext.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent event) {

						for(int i = 0; i < clients.size(); i++){
						clients.get(i).sendMessage(event.getActionCommand());
						}

						showMessage("\nSERVER - " + event.getActionCommand());
						usertext.setText("");
					}				
				});


		add(usertext,BorderLayout.SOUTH);
		chatwindow = new JTextArea();
		chatwindow.setEditable(false);
		add(new JScrollPane(chatwindow));
		setSize(640,400);   
		setVisible(true);	
	}

	// set up and run the server
	public void startServer(){
		try{
			server = new ServerSocket(7480, 100);
	        while(true){
	        	try{
	        		// connect and have conversation
	        		waitForConnection();


	        	}catch(EOFException eofException){
	        		showMessage("\n Server ended the connection");		
	        	}finally{
	        		// closeCrap();
	        	}
	        }
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
		closeServer();
	}

	// wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		showMessage(" Waiting for someone to connect... \n");
		Socket connection = server.accept();
		showMessage(" Now connected to "+connection.getInetAddress().getHostName());


		Connection c = new Connection(connection,this,clients.size());
		Thread tr = new Thread(c);
		tr.start();

		clients.add(c);


	}


	// close streams and sockets after you are done chatting
	private void closeServer(){
		showMessage("\n Closing connections.. \n");

		for(int i = 0; i < clients.size(); i++){
			clients.get(i).disconnect();
		}


	}



	// updates chatWindow
	public void showMessage(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					@Override
					public void run() {
					chatwindow.append(text);					
					}	
				});
	}


	public void terminate(int index){

		ArrayList<Connection> clientbuffer = new ArrayList<Connection>();

		for(int i = 0; i < clients.size(); i++){
			if(i!=index){
				clientbuffer.add(clients.get(i));
			}else{

				Connection cc = clients.get(i);
				cc = null;
			}
		}

		clients.clear();
		for(int i = 0; i < clientbuffer.size(); i++){
		clients.add(clientbuffer.get(i));
		clients.get(i).setIndex(i);
		}



	}

	public void relayMessage(String message, int INDEX) {
		for(int i = 0; i < clients.size(); i++){
			if(i != INDEX){
				clients.get(i).sendMessage(message);
			}

		}

	}

	// return the number of connections
	public int getConnectionSize(){
		return clients.size();
	}
	// return connections array
	public String[] getConnections(String id){
		String[] clientNames = new String[clients.size()-1];
		String uname = "";
		int inc = 0;
		for(int i = 0; i < clients.size(); i++){
			uname = clients.get(i).getUsername();

			if(!uname.equals(id)) {
			clientNames[i] = uname;
			}
		}				
			return clientNames;
	}


}
 */