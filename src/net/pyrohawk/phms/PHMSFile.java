package net.pyrohawk.phms;

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
	
	public void uploadFileInfo(boolean uploadFile) {
	
		try {
			
			String queryString = "username=" + project.getUser().getUsername() + "&";
			queryString += "password=" + project.getUser().getPassword() + "&";
			queryString += "filename=" + fileName + "&";
			queryString += "fileid=" + fileId + "&";
			queryString += "lastmodified=" + lastModified + "&";
			queryString += "lastcommit=" + lastCommit + "&";
			queryString += "lastcommitid=" + lastCommitId + "&";
			queryString += "project=" + project.getProjectName() + "";
			URLConnection conn = (new URL(Project.ROOT_URL + "/file/update.php?" + queryString)).openConnection();
			conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.21 Safari/535.7");
			InputStream in = conn.getInputStream();
			String buffer = "";
			int bufferChar;
			while ((bufferChar = in.read()) != -1) {
				buffer += (char)bufferChar;
			}
			System.out.println(buffer);
			
		} catch (Exception e) {
			
		}
		
	}
	
	public void authenticatedFile() {
		
		try {
			
			URLConnection conn = (new URL(Project.ROOT_URL + "/file/authenticate.php?filename=" + fileName + "&username=" + project.getUser().getUsername() + "&password=" + project.getUser().getPassword())).openConnection();
			conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.21 Safari/535.7");
			InputStream in = conn.getInputStream();
			String buffer = "";
			int bufferChar;
			while ((bufferChar = in.read()) != -1) {
				buffer += (char)bufferChar;
			}
			System.out.println(buffer);
			JSONObject fileInfo = new JSONObject(buffer);
			if (fileInfo.getBoolean("valid")) {
				lastModified = new Date(fileInfo.getString("lastmodified"));
				lastCommit = fileInfo.getString("lastcommit");
				fileName = fileInfo.getString("filename");
				fileId = fileInfo.getInt("fileid");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
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

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

}
