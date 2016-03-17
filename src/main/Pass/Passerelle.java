package main.Pass;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

@Path("/home")
public class Passerelle {
	
	private FTPClient client;
	private String login = "toto";
	private String mdp = "toto";
	
	private FTPClient init(){
		try {
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_L8);
			conf.setUnparseableEntries(true);
			final FTPClient client = new FTPClient();
			client.configure(conf);
			client.connect("127.0.0.1", 1028);
			client.enterLocalPassiveMode();
			client.login(login, mdp);
			return client;
		} catch (IOException ex) {
			Logger.getLogger(Passerelle.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	@GET
	@Produces("text/html")
	public String home(){
		FTPClient client = init();
		String tmp = "";
		try {
			tmp = client.printWorkingDirectory();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return "<h1>" + tmp + "</h1>";
	}
	
	@GET
	@Path("")
	@Produces("text/html")
	public String List(@PathParam("path") String path) throws IOException{
		FTPClient client = init();
		System.out.println(path);
		String[] pathSplit = path.split("/");
		if(login.equals(pathSplit[0])){
			FTPFile[] files = client.listFiles(path);
			String clientFiles = "";
			for(FTPFile file : files) {
				clientFiles += "<a href=http://localhost:8080/rest/tp2/home/" + path + "/" + file.toString() + "/>" + file.toString()
							+ "</a><br />";
			}
			return clientFiles;
		}
		else
			return "<h1> ERROR </h1>";
	}
	
	@GET
	@Path("{path}")
	@Produces("application/octet-stream")
	public void downLoad(@PathParam("path") String path){
		FTPClient client = init();
		String[] pathSplit = path.split("/");
		if(login.equals(pathSplit[0])){
			
		}
	}
}
