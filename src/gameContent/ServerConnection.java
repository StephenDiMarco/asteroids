package gameContent;

import java.net.*;
import java.io.*;

//The ServerConnection class will allow the applet to talk to the server ,recording highscores by users
public class ServerConnection {
	private URLConnection conn ;
	private int score;
	
	public ServerConnection(int score){
		this.score = score;
		post();
	}
	//Connecting to server 
	private void connect() { 
		try{
			URL url = new URL("http://web.cs.dal.ca/~dimarco/Firelight/asteroids.php");
			conn = url.openConnection();
		}catch(Exception e){
			System.out.println("Failure");
		}
	}
	//Posting Data
	public void post() {
		connect();
		conn.setDoOutput(true);

		try{
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection)conn).setRequestMethod("POST");
			}
			//Creating output stream to POST   
			DataOutputStream oStream = new DataOutputStream(conn.getOutputStream());     
			//Creating post and converting to bytes
			byte[] parameterAsBytes = ("score="+score).getBytes(); 
			System.out.println(parameterAsBytes);
			oStream.write(parameterAsBytes);   
			oStream.flush(); 
	
			oStream.close();
			//Input script
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	
			String s = null;
			while ((s = in.readLine()) != null) {
				System.out.println(s);
			}
			in.close();
		}catch(Exception e){
			System.out.println("Failure");
		}
	}
		
}
