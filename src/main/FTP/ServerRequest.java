package main.FTP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.*;

public class ServerRequest extends Thread {
	
	public Socket socket;
	public Socket socket_data;
	public InputStream input;
	public BufferedWriter bw;
	public 	BufferedReader br;
	public String current_dir;
	public int nb_commandes;
	public BufferedWriter bw_data;
	public 	BufferedReader br_data;
	public String file_type;
	public boolean user;
	public ServerSocket serverData;
	public int mode;
	
	public ServerRequest(Socket socket) throws IOException{
		this.socket=socket;	
		input = socket.getInputStream();
		bw= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		current_dir = new String("./FTP/toto");
		nb_commandes=0;
		user=true;
		mode = 0; //1 active    2 passive
	}
	
	
	public void run(){
	
		try {
			bw.write("220  \r\n");
			bw.flush();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

		///GESTION DES REPONSES AUX COMMANDES

		try {
			while(!socket.isClosed()){
				//System.out.println("Je dosi faire ca plusieurs fois");
				String s = this.br.readLine();
				if(s != null)
					handleRequest(s);
				nb_commandes++;
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
	//TODO remettre les verifications
	private void processUSER(String code[]/*, String password*/) throws IOException{
		String user = new String(code[1]);
		
		bw.write("331 \r\n");
		bw.flush();
		System.out.println(">>331");
		if(code.length>1)
			if (!code[1].equals("toto"))this.user=false;
		
		
	}

	private void processPASS(String code[]) throws IOException{
		if(code.length>1){
			if( code[1].equals("toto")){
				if(this.user){
					bw.write("230 \r\n");
					bw.flush();
					System.out.println(">>230");
					return;
				}		
			}
		}
		
		bw.write("530 Login incorrect\r\n");
		bw.flush();
		System.out.println(">> 530");
			
		
	}
	
	private void processLIST(String code[]) throws IOException{
		String[] dir = new java.io.File(this.current_dir).list( );
		String list = new String();
		if(dir[0]!=null){
			for(int i=0;i<dir.length;i++){
				list = dir[i]+" \r\n"+ list;
			}
			this.bw.write("150 \r\n");
			this.bw.flush();
			
			this.bw_data.write("test "+list+"\n");
			this.bw_data.flush();

			this.bw.write("226 \r\n");
			this.bw.flush();
			
			
			System.out.println(">> "+list);
			
		}
		else{
			this.bw_data.write(" \n");
			this.bw_data.flush();
			System.out.println(">> "+list);
			
		}
		this.socket_data.close();
		
		if (mode == 2){
			this.serverData.close();
		}
		
	}
	private void processSYST(String code[]) throws IOException{
		bw.write("215 UNIX Type: L8\r\n");
		bw.flush();
		
		System.out.println(">>215 UNIX Type : L8");
	}
	

	
	private void processPWD(String code[]) throws IOException{

		bw.write("257 "+this.current_dir+"\r\n");
		bw.flush();

		System.out.println(">> le path "+this.current_dir+"\r\n");
	}


	private void processCWD(String[] code) throws IOException {
	
		String tab[] ;
	
		//cas ou je reçois un point
		if(code[1].equals(".")){
			bw.write("200 "+this.current_dir+"\r\n");
			bw.flush();
		}
		///cas ou je reçois deux points
		else if(code[1].equals("..")){
			tab=this.current_dir.split("/");
			this.current_dir= new String("");
			for(int i=1;i<tab.length-1;i++){
				this.current_dir=this.current_dir+"/"+tab[i];
			}
			bw.write("200 "+this.current_dir+"\r\n");
			bw.flush();
			System.out.println(">> 200");
		}
		///cas ou je recois un chemin absolu
		else{
			if(code[1].substring(0,1) == "/"){
				/// cas ou le chemin existe
				if(new java.io.File(code[1]).list( ) != null){
					this.current_dir = new String(code[1]);
					bw.write("200 "+this.current_dir+"\r\n");
					bw.flush();
					System.out.println(">> 200 "+this.current_dir);
				}
				/// cas ou le chemin n'existe pas
				else {
					bw.write("200 le chemin n'existe pas\r\n");
					bw.flush();
					System.out.println(">>  200 chemin n'existe pas 1"+this.current_dir);
					
				}
			}
			/// cas ou je recois cd nom_rep
			else{
				if( new java.io.File(this.current_dir+"/"+code[1]).list( ) != null){
					
					this.current_dir=this.current_dir+"/"+code[1];
					bw.write("200 "+this.current_dir+"\r\n");
					bw.flush();
					System.out.println(">> 200 "+this.current_dir);
					
				}
				else{
					bw.write("200 le chemin n'existe pas\r\n");
					bw.flush();
					System.out.println(">> 200 chemin n'existe pas 2"+this.current_dir);	
				}	
			}	
		}
	}
	
	private void processQUIT(String code[]) throws IOException{
		
		bw.write("426 connexion closed\r\n");
		bw.flush();
		this.socket.close();
		
		System.out.println(">> 426");
		
	}
	
	/*TODO à verifier*/
	private void processTYPE(String code[])throws IOException{
		
		file_type = new String(code[1]);
		bw.write("200 Switching to Binary mode.\r\n");
		bw.flush();
		
		System.out.println(">> 200");
		
	}

	private void processSTOR(String code[])throws IOException{
		
		this.bw.write("150 \r\n");
		this.bw.flush();
		System.out.println(">> 150");
		
		Files.copy(socket_data.getInputStream(),Paths.get(current_dir+"/"+code[1]),REPLACE_EXISTING);
		
		this.bw.write("226 \r\n");
		this.bw.flush();
		System.out.println(">> 226");
				
		this.socket_data.close();
		if (mode == 2){
			serverData.close();
		}
		
	}
	

	private void processRETR(String code[])throws IOException{
		
		
		this.bw.write("150 \r\n");
		this.bw.flush();
		System.out.println(">> 150");
		
		File file = new File(current_dir+"/"+code[1]);
		if( file.exists() ) 
			Files.copy(Paths.get(current_dir+"/"+code[1]),socket_data.getOutputStream());
		
		this.bw.write("226 \r\n");
		this.bw.flush();
		System.out.println(">> 226");
		
		this.socket_data.close();
		if (mode == 2){
			this.serverData.close();
		}
	}

	private void processFEAT(String code[])throws IOException{
		
		bw.write("211\r\n");
		bw.flush();
		bw.write("UTF8\r\n");
		bw.flush();
		bw.write("211 End\r\n");
		bw.flush();
/*		bw.write("200\n");
		bw.flush();
	*/	
		//System.out.println(">> 211-Features:\nMDTM\nREST STREAM\nSIZE\nMLST type*;size*;modify*;\nMLSD\nUTF8\nCLNT\n211\n");
		
	}


	
	
	private void handleRequest(String message) throws IOException{

		
		String code[]; 
		//System.out.println("<<< " +message);
		code = message.split(" ");
		String num_code = new String (code[0]); 

		
		
		/*TODO
		 * 	implementer les fonctions qui vont verifier chaque etape
		 *    */
		System.out.println(nb_commandes+" - <<  "+ message);
		 
		switch(num_code){
			
			
			case "USER": {
				processUSER(code);
				break;
			}
			
			case "PASS": {
				processPASS(code);
				break;
			}
			
			case "SYST": {
				System.out.println("<< SYST "+ code[0]);
				processSYST(code);
				break;
			}
		
			case "PORT": {
				mode = 1;
				processPORT(code[1]);
				break;
			}

			case "LIST": {
				processLIST(code);
				break;
			}
		
			case "PWD": {
				processPWD(code);
				break;
			}
			
			case "CDUP" : {
				processCWD(code);
				break;
			}
			
			case "CWD" : {
				processCWD(code);
				break;
			}
			
			case "TYPE" : {
				processTYPE(code);
				break;
			}
			
			case "STOR" : {
				processSTOR(code);
				break;
			}
			
			case "RETR" : {
				processRETR(code);
				break;
			}
			
			case "PASV" : {
				processPASV(code);
				break;
			}
			
			case "FEAT" :{
				processFEAT(code);
				break;
			}
			
			case "QUIT" : {
				processQUIT(code);
			}
			
			
		}
	}

	private void processPORT(String code)throws IOException{
		
		String tab[] = new String[6];
		tab = code.split(",");
		String ip = new String("");
		int port ;
		
		for(int i=0;i<4;i++){
			ip = ip+tab[i];
			if (i<3)ip = ip+".";
		}
		port =(Integer.parseInt(tab[4])*256 + Integer.parseInt(tab[5]));
		System.out.println("connect to "+ip+ " " + port);
		
		this.socket_data = new Socket(ip,port);
	
		this.bw.write("200 PORT now Private.\r\n");
		this.bw.flush();
		
		this.bw_data= new BufferedWriter(new OutputStreamWriter(this.socket_data.getOutputStream()));
		this.br_data = new BufferedReader(new InputStreamReader(this.socket_data.getInputStream()));
	
		System.out.println(">> 200");

		
	}

	private void processPASV(String code[]) throws IOException{
		
		mode = 2;
		socket_data = new Socket();
		int port = (int)(Math.random()*(60000-1025) +1025);
		
		serverData = new ServerSocket(port);
		
		bw.write("227 Entering Passive Mode (127,0,0,1,"+port/256+","+port%256+").\r\n");
		bw.flush();

		socket_data = serverData.accept();
		this.bw_data= new BufferedWriter(new OutputStreamWriter(this.socket_data.getOutputStream()));
		this.br_data = new BufferedReader(new InputStreamReader(this.socket_data.getInputStream()));
	
		System.out.println(">> 227 Entering Passive Mode (127,0,0,1,4,0).");
		//processPORT("172,18,15,20,195,80");
		//bw.write("200\n");
		//bw.flush();
	}

}
