package net.pyrohawk.phms;

import java.io.*;
import java.net.*;
import net.pyrohawk.phms.util.Base64;
import org.json.*;

public class User {
	
	private String username;
	private String password;
	private boolean authenticated = false;
	private HttpURLConnection connection;
	
	public User(String name, String pass) {
		try {
			
			String encName = URLEncoder.encode(name), encPass = URLEncoder.encode(Base64.encodeBytes(pass.getBytes()));
			connection = (HttpURLConnection)new URL(Project.ROOT_URL + "/user/login.php?json&username=" + encName + "&password=" + encPass).openConnection();
			connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.21 Safari/535.7");
			InputStream in = connection.getInputStream();
			String buffer = "";
			int bufferChar;
			while ((bufferChar = in.read()) != -1) {
				buffer += (char)bufferChar;
			}
			JSONObject userInfo = new JSONObject(buffer);
			
			if (userInfo.getBoolean("accepted") && Base64.encodeBytes(pass.getBytes()).equals(userInfo.getString("password")) && name.equals(userInfo.getString("username"))) {
				
				username = name;
				password = Base64.encodeBytes(pass.getBytes());
				authenticated = true;
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

}
