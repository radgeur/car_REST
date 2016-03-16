package main.FTP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* 
 * Main server class. This class includes main(), and is the class that listens
 * for incoming connections and starts ServerThreads to handle those connections
 *
 */
public class Serveur {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ServerSocket server_socket = new ServerSocket(1028);
	
		ServerRequest server_request;
		while(true){
			Socket socket = new Socket();
			
			socket = server_socket.accept();
			server_request = new ServerRequest(socket);
			server_request.start();
		}
	}
}
