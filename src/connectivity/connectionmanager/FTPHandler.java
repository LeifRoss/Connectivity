package connectivity.connectionmanager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.imageio.ImageIO;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import connectivity.utility.ErrorHandler;

public class FTPHandler {

	private static boolean has_connection;
	private static FTPClient ftp_connection;
	private static String hostname;
	private static String directory;

	
	// turn into a object and spam multiple connections??

	/**
	 * Establishes a connection and logs in to the FTP server
	 * @param un
	 * @param pw
	 * @return
	 */
	public static boolean login(String un, String pw){


		has_connection = false;

		try {


			ftp_connection = new FTPClient();
			ftp_connection.setControlEncoding("UTF-8");
			ftp_connection.connect(getHost());
			ftp_connection.login(un, pw);
			//ftp_connection.enterLocalPassiveMode();




			int status = ftp_connection.getReplyCode();

			if (FTPReply.isPositiveCompletion(status)) {
				has_connection = true;
				ftp_connection.setFileType(FTP.BINARY_FILE_TYPE);
				ftp_connection.changeWorkingDirectory(getDirectory());
				ftp_connection.enterLocalPassiveMode();
				ftp_connection.setControlKeepAliveTimeout(300); 
			} else {
				System.out.println("error logging in");
				destroy();
				return false;
			}

		} catch (SocketException e) {
			e.printStackTrace();
			destroy();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			destroy();
			return false;
		}


		return true;
	}




	/**
	 * Downloads a image from the FTP server
	 * @param filename
	 * @return
	 */
	public static BufferedImage downloadImage(String filename){


		if(!checkConnection()){
			ErrorHandler.handleError("No connection to FTP Server");
			return null;
		}


		InputStream input = null;
		BufferedImage img = null;

		try {

			input = ftp_connection.retrieveFileStream(filename);	
			if(input != null) {
				img = ImageIO.read(input);
				ftp_connection.completePendingCommand();
			}else{
				ErrorHandler.handleError("Error in ftpconnection:"+ftp_connection.getStatus());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally{

			try {
				if(input!=null){
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return img;
	}


	/**
	 * Uploads a file to the FTP server
	 * @param f
	 * @param filename
	 */
	public static void uploadFile(File f, String filename){



		if(!checkConnection()){
			ErrorHandler.handleError("No connection to FTP Server");
			//return;
		}

		FileInputStream inputStream = null;
		try {

			inputStream = new FileInputStream(f);
			ftp_connection.storeFile(filename, inputStream);


		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		System.out.println("uploaded");
	}


	/**
	 * Deletes a file from the FTP server
	 * @param name
	 */
	public static void delete(String name){

		if(!checkConnection()){
			ErrorHandler.handleError("No connection to FTP Server");
			return;
		}

		try {
			ftp_connection.deleteFile(name);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public static void setHost(String in){
		hostname = in;
	}

	public static String getHost(){
		return hostname;
	}


	public static void setDirectory(String in){
		directory = in;
	}

	public static String getDirectory(){
		return directory;
	}


	/**
	 * Checks the connection to the FTP server
	 * @return
	 */
	private static boolean checkConnection(){

		if(has_connection && ftp_connection.isConnected()){
			return true;
		}

		return false;
	}


	/**
	 * Safely destroys the connection to the FTP server
	 */
	public static void destroy(){

		if(ftp_connection == null){
			return;
		}

		try {
			if(ftp_connection.isConnected()){
				ftp_connection.logout();
				ftp_connection.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		has_connection = false;

	}



}
