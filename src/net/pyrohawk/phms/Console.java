package net.pyrohawk.phms;

import java.io.*;

public class Console {
	
	public static final String QUIT_PHRASE = "quit";
	
	static Project currentProject;
	
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public static String readLine(String msg) throws IOException {
		
		System.out.println(msg);
		String input;
		while (!(input = in.readLine()).replaceAll("\\W","").equals("")) {
			if (input.equals("quit")) System.exit(0);
			return input;
		}
		return "";
		
	}

	public static void main (String[] args) {
		
		// User name and pass, comma seperated to minimise time spent
		String username, password;
		User temp = null;
		try {
			
			do {
				username = readLine("Please enter your username and password seperated by a comma: ");
			} while (username.equals(""));
			
		password = username.split(",")[1];
		username = username.split(",")[0];
		
		temp = new User(username, password);
		if (!temp.isAuthenticated()) {
			System.out.println("Invalid user information. Please restart the program.");
			System.exit(0);
		}
		
		} catch (IOException e) {
			System.out.println("Invalid user information. Please restart the program.");
			System.exit(0);
		}
		
		// Get name of project to connect to:
		String projectName = "";
		try {
			
			do {
				projectName = readLine("Please enter the NAME of the project you want to open: ");
			} while (projectName.equals(""));
			
		} catch (IOException e) {
			System.out.println("Could not read project name: " + e.getMessage());
			System.out.println("Please restart the program.");
			System.exit(0);
		}
		// Lookup project name on server
		currentProject = new Project(temp, projectName);
		
		if (!currentProject.isAuthenticated()) {
			System.out.println("There was a problem, please restart the program.");
			System.exit(0);
		}
		
		System.out.println("Authenticated: " + projectName);
		
		currentProject.writeFilesToDisk();
		
	}
	
}
