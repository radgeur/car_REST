import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class FtpRequest implements Runnable{
	
	private final static String USER = "toto";
	private final static String PASSWORD = "toto";
	
	private Socket s;
	private PrintStream print;
	private BufferedReader br;
	private File currentRepository;
	private boolean isUserValid;
	private boolean isConnected;
	
	public FtpRequest(Socket s, String repo) throws IOException{
		this.s=s;
		print = new PrintStream(this.s.getOutputStream());
		this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		currentRepository = new File(repo);
		isUserValid = false;
		isConnected = false;
	}

	public void write(String string) {
		print.println(string);
	}
	
	public String receive() throws IOException {
		return br.readLine();
	}
	
	public void ProcessRequest(String string) throws IOException{
		String[] tab = string.split(" ");
		switch(tab[0].toLowerCase()) {
			case "user":
				processUSER(tab[1]);
				break;
			case "pass":
				processPASS();
				break;
			case "retr":
				if(userIsConnected())
					processRETR();
				break;
			case "store":
				if(userIsConnected())
					processSTOR();
				break;
			case "list":
				if(userIsConnected())
					processLIST();
				break;
			case "quit":
				processQUIT();
				break;
			default:
				write("Commande invalide");
				return;
		}
	}
	
	public boolean userIsConnected(){
		if(!isConnected)
			write("Vous nêtes pas encore connecté pour pouvoir effectuer cette action");
		return isConnected;
	}
	
	public void processUSER(String string) {
		if(string.equals(USER)){
			isUserValid = true;
			write("Nom d'utilisateur valide");
		} else 
			write("Nom d'utilisateur non valide");
	}
	
	public void processPASS() {
		if(!isUserValid){
			write("Vous n'avez pas encore saisi votre nom d'utilisateur ou il n'est pas valide");
			return;
		}
		String string = null;
		try {
			string = receive();
		} catch (IOException e1) {}
		if(string.equals(PASSWORD)){
			isConnected = true;
			write("Mot de Passe correct");
		} else 
			write("Mot de Passe incorrect");
	}
	
	public void processRETR() {
		
	}
	
	public void processSTOR() {
		
	}
	
	public void processLIST() {
		Set<File> files = new HashSet<File>(Arrays.asList(currentRepository.listFiles()));
		for (File f : files) {
			write(f.toString());
		}
	}
	
	public void processQUIT() throws IOException {
		s.close();
	}
	
	@Override
	public void run() {
		this.write("200 Service OK, veuillez vous identifiez");
		String string = null;
		try {
			string = receive();
		} catch (IOException e1) {}
		while(string != "quit") {
			try {
				ProcessRequest(string);
			} catch (IOException e) {}
			try {
				string = receive();
			} catch (IOException e) {}
		}
		try {
			ProcessRequest(string);
		} catch (IOException e) {}
	}
}
