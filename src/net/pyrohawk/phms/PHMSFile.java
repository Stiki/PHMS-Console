package net.pyrohawk.phms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.net.*;
import org.json.*;

public class PHMSFile implements Serializable {

	private transient Project project;
	private String lastCommit;
	private int lastCommitId = -1;
	private String fileName;
	private int fileId;
	private Date lastModified;
	private Date lastModifiedLocal;
	
	public PHMSFile (Project p, String fN) {
		project = p;
		fileName = fN;
		lastModifiedLocal = new Date();
	}
	
	public boolean uploadFileInfo(boolean uploadFile) {
	
		try {
			
			String queryString = "username=" + URLEncoder.encode(project.getUser().getUsername()) + "&";
			queryString += "password=" + URLEncoder.encode(project.getUser().getPassword()) + "&";
			queryString += "filename=" + URLEncoder.encode(fileName) + "&";
			queryString += "fileid=" + URLEncoder.encode("" + fileId) + "&";
			queryString += "lastmodified=" + (lastModified == null ? "" : URLEncoder.encode("" + lastModified)) + "&";
			queryString += "lastcommit=" + URLEncoder.encode(lastCommit) + "&";
			queryString += "lastcommitid=" + URLEncoder.encode("" + lastCommitId) + "&";
			queryString += "project=" + URLEncoder.encode(project.getProjectName()) + "";
			//System.out.println(queryString);
			URLConnection conn = (new URL(Project.ROOT_URL + "/file/update.php?" + queryString)).openConnection();
			conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.21 Safari/535.7");
			InputStream in = conn.getInputStream();
			String buffer = "";
			int bufferChar;
			while ((bufferChar = in.read()) != -1) {
				buffer += (char)bufferChar;
			}
			System.out.println(buffer);
			JSONObject response = new JSONObject(buffer);
			return response.getBoolean("accepted");
			
		} catch (Exception e) {
			
		}
		
		return false;
		
	}
	
	public boolean authenticateFile() {
		
		try {
			
			URLConnection conn = (new URL(Project.ROOT_URL + "/file/authenticate.php?filename=" + fileName + "&username=" + project.getUser().getUsername() + "&password=" + project.getUser().getPassword() + "&project=" + project.getProjectName())).openConnection();
			conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.21 Safari/535.7");
			System.out.println(conn.getURL().toString());
			InputStream in = conn.getInputStream();
			String buffer = "";
			int bufferChar;
			while ((bufferChar = in.read()) != -1) {
				buffer += (char)bufferChar;
			}
			System.out.println("auth: " + buffer);
			JSONObject fileInfo = new JSONObject(buffer);
			if (fileInfo.getBoolean("valid")) {
				System.out.println("Valid");
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				System.out.println("Last Modified: " + fileInfo.getString("lastmodified"));
				lastModified = (Date)f.parse(fileInfo.getString("lastmodified"));
				lastCommit = fileInfo.getString("lastcommit");
				fileName = fileInfo.getString("filename");
				fileId = fileInfo.getInt("id");
			}
			
			return fileInfo.getBoolean("valid");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	public boolean isOutOfDate() {
		return lastModified.after(lastModifiedLocal);
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getLastCommit() {
		return fileName;
	}

	public void setLastCommit(String commit) {
		this.lastCommit = commit;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

}
