import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {

	private ServerSocket port;
	
	public Serveur() throws IOException{
		try {
			port = new ServerSocket(1028);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.attendreCx();
	}
	
	public void attendreCx() throws IOException{
		while(true){
			Socket s=port.accept();
			Thread t = new Thread(new FtpRequest(s, "./FTP"));
			t.start();
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Serveur();
		
	}
}
