package connectivity.liveserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LiveServerConnection implements Runnable{

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private LiveServer server;

	private final int id;
	private boolean running;

	public LiveServerConnection(Socket connection, LiveServer server, int id){

		this.id = id;
		this.server = server;
		this.connection = connection;
		running = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {

			setupStreams();
			connected();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			disconnect();
		}


	}


	private void setupStreams() throws IOException{

		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}


	private void connected() throws ClassNotFoundException, IOException{

		running = true;

		while(running){
			
			Object inputObject =  input.readObject();
			server.handleInternal(inputObject,this);
		}
		
	}

	public void sendObject(Object object){

		try{

			output.writeObject(object);
			output.flush();			

		}catch(IOException ioe){
			ioe.printStackTrace();
		}

	}



	public LiveServer getServer(){
		return server;
	}

	public int getID(){
		return id;
	}


	public void destroy(){

		running = false;
		
		try {
			input.close();
			output.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		server.terminate(this);
	}

	public void disconnect(){

		running = false;
		
		//mainframe.showMessage("\n Closing connection "+connection.getRemoteSocketAddress().toString()+".. \n");
		try {
			input.close();
			output.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//mainframe.showMessage("\n Connection closed  \n");
		//mainframe.relayMessage("DEL "+username,INDEX);
		
	}

}

/*
package leifdev.schoolserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Runnable  {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private Server mainframe;
	private int INDEX;
	private String username;
	private float posX,posY;

	public Connection(Socket connection, Server mainframe, int index){

		this.mainframe = mainframe;
		this.connection = connection;		
		this.INDEX = index;

		posX = 10.0f;
		posY = 10.0f;

		username = "";
	}


	// get stream to send and receive data
		private void setupStreams() throws IOException{
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());

			mainframe.showMessage("\n Client "+connection.getRemoteSocketAddress().toString()+" connected \n");
		}


		public void run(){
			try{

				setupStreams();
				whileChatting();

				}catch(IOException ioe){

				}finally{
					disconnect();
				}

		}


		// during the chat conversation
		private void whileChatting() throws IOException{
			String message = " You are now connected! ";
			sendMessage(message);

			do{
				try{
					message = (String) input.readObject();
					handleCommand(message);
				}catch(ClassNotFoundException classNotFoundException){
					mainframe.showMessage("\n ERROR: invalid object");
				}

			}while(!message.equals("CLIENT - END"));
		}


		public void handleCommand(String command){
			String[] cmd = command.split(" ");
			String cmdObj = "";

			switch(cmd[0]){
				case "NAME":
					if(cmd.length>1){
						username = cmd[1];

						mainframe.relayMessage("ADD "+username,INDEX);

						String[] otherusers = mainframe.getConnections(username);
						String sender = "";
						for(String s : otherusers){
							sender += s+" ";
						}

						sendMessage("ADDALL "+sender);
					}
					break;
				case "SAY":
			for(int i = 1; i < cmd.length; i++){
				cmdObj+= cmd[i] + " ";
			}

			mainframe.showMessage("\n"+username+": "+cmdObj);
			mainframe.relayMessage("SAY "+username+": "+cmdObj,INDEX);


					break;
				case "POS":
					if(cmd.length>2){

						posX = Float.parseFloat( cmd[1] );
						posY = Float.parseFloat( cmd[2] );

						mainframe.relayMessage("CPOS "+posX+" "+posY+" "+username,INDEX);
					}

					break;				
			}

		}


		public void sendMessage(String message){
			try{
				output.writeObject(message);
				output.flush();			
			}catch(IOException ioException){
				mainframe.chatwindow.append("\n ERROR: "+ioException.getMessage());
			}
		}





	public void disconnect(){

		mainframe.showMessage("\n Closing connection "+connection.getRemoteSocketAddress().toString()+".. \n");
		try {
			input.close();
			output.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mainframe.showMessage("\n Connection closed  \n");
		mainframe.relayMessage("DEL "+username,INDEX);
		mainframe.terminate(INDEX);
	}


	public void setIndex(int i) {
		this.INDEX = i;		
	}

	public String getUsername(){
		return username;
	}



}
 */