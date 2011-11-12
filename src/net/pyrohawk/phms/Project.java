package net.pyrohawk.phms;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import org.json.*;

public class Project implements Serializable{
	
	public static final String ROOT_URL = "http://phms.pyrohawk.com";

	private int projectId;
	private String projectName;
	private String projectPassword;
	private boolean usesPassword;
	private boolean authenticated;
	private User user;
	private URLConnection connection;
	
	public Project (User usr, String name) {
		
		if (!usr.equals(null) && !name.equals("")) {
			
			user = usr;
			
			try {
				
				connection = new URL(ROOT_URL + "/handshake.php?name=" + URLEncoder.encode(name) + "&username=" + URLEncoder.encode(usr.getUsername()) + "&password=" + URLEncoder.encode(usr.getPassword())).openConnection();
				connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.21 Safari/535.7");
				InputStream in = connection.getInputStream();
				System.out.println(connection.getURL().toString());
				String buffer = "";
				int bufferChar;
				
				while ((bufferChar = in.read()) != -1) {
					
					buffer += (char)bufferChar;
					
				}
				
				System.out.println(buffer);
				JSONObject handshake = new JSONObject(buffer);
				
				if (handshake.getBoolean("valid")) {
					authenticated = true;
					projectName = name;
					projectId = handshake.getInt("id");
					if (!readFilesFromDisk()) {
						System.out.println("Project files could not be located.");
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void writeFilesToDisk() {
		
		File path = new File(System.getProperty("user.home") + "/phms/" + projectName);
		path.mkdirs();
		System.out.println(path.toString());
		
		try {
			
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path.toString()+ "/files.dat"));
			out.writeObject(getFileArrayList());
				
		} catch (IOException e) {
				
			System.out.print("Error writing to file: " + e.getMessage());
			e.printStackTrace();
				
		}
		
	}
	
	// Also authenticates files from server and gets relative last-modified
	public boolean readFilesFromDisk() {
		
		ArrayList<PHMSFile> list = new ArrayList<PHMSFile>();
		File path = new File(System.getProperty("user.home") + "/phms/" + projectName + "/files.dat");
		
		if (path.exists()) {
		
			try {
				
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
				list = (ArrayList<PHMSFile>)in.readObject();
				System.out.println("Length: " + list.size());
				PHMSFile[] lFiles = new PHMSFile[list.size()];
				list.toArray(lFiles);
				int i = 0;
				
				for (i = 0; i < lFiles.length; i++) {
					
					PHMSFile file = lFiles[i];
					System.out.println("\n\t" + i + "\n");
					file.setProject(this);
					if (!file.authenticateFile()) {
						file.setLastCommit("Initial Commit.");
						System.out.println("Authenticating file: " + file.getFileName());
						System.out.println("\t\t" + file.getProject().getProjectName());
						System.out.println("\t\t" + file.getLastModified());
						System.out.println(file.uploadFileInfo(true));
					}
					lFiles[i] = file;
					System.out.println("Date:\t\t" + file.getLastModified());
					System.out.println("Commit:\t\t" + file.getLastCommit());
					
				}
				
				return true;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
		return false;
		
	}

	private ArrayList<PHMSFile> getFileArrayList() {
		// TODO Auto-generated method stub
		ArrayList<PHMSFile> list = new ArrayList<PHMSFile>();
		list.add(new PHMSFile(this, "/fileOne.php"));
		list.add(new PHMSFile(this, "/fileTwo.txt"));
		list.add(new PHMSFile(this, "/fileThree.project"));
		return list;
	}

	public int getProjectId() {
		return projectId;
	}

	public User getUser() {
		return user;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}

	public boolean usesPassword() {
		return usesPassword;
	}

	public String getProjectPassword() {
		return projectPassword;
	}

	public String getProjectName() {
		return projectName;
	}
	
}
