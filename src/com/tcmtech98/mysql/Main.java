package com.tcmtech98.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/*
 * Program Name: SQLConsole-Java
 * Author: tcmtech98
 * Website: tcmtech98.com
 * GitHub: github.com/tcmtech98
 * Description: Program for sending MySQL statements to a user-defined host, port, and database.
 * Classpath: JDBC
 * Last Modified: 12/6/23
 * Created: 12/3/23
 * Version: v1.0
 */

public class Main {
	// Left blank by default, so user can specify which host, etc. to connect to.
	static String host = "";
	static String port = "";
	static String database = "";
	static String username = "";
	static String password = "";
	static Connection connection;
	static Scanner scanner = new Scanner(System.in);
	/*
	 * initializeConnection() is called primarily and everything else continues between both methods.
	 * Main function only requires two methods because sql() is recursive.
	 */
	public static void main(String[] args) {
		initializeConnection();
		sql();
	}
	static void sql() {
		System.out.println("\nType \"help\" for more options. \nMySQL: ");
		String input = scanner.nextLine();
		String[] inputSplit = input.split(" ");
		
		// Returns a list of potential commands/functions.
		if ((inputSplit.length == 1) && (inputSplit[0].equalsIgnoreCase("HELP"))) {
			System.out.println(
					"\n - Statement (Starting with SELECT, INSERT, UPDATE, etc.)\n"
				+	" - \"HELP\" - Displays this menu.\n"
				+	" - \"STATUS\" - Gets connection status and attempts to reconnect if inactive.\n"
				+ 	" - \"RECONNECT\" - Attempts a new connection.\n"
				+ 	" - \"CLOSE\" - Closes connection and program.\n");
		}
		/* 
		 * If connection is not active, Attempts to reconnect, utilizing previously stored login
		 * credentials/information.
		 * Utilizes connect() rather than initializeConnection() because user was already logged in.
		 */	
		else if((inputSplit.length == 1) && (inputSplit[0].equalsIgnoreCase("STATUS"))) {
			if(getConnection() != null) {
				System.out.println("\nConnection is active!");
			} else {
				System.out.println("\nConnection is no longer active! Attempting to reconnect now!");
				connect();
			}
		}
		// Attempts to reconnect to another server, utilizing initializeConnection() once again.
		else if((inputSplit.length == 1) && (inputSplit[0].equalsIgnoreCase("RECONNECT"))) {
			close();
			initializeConnection();
		}
		// Closes both the connection and program. Safe practice.
		else if((inputSplit.length == 1) && (inputSplit[0].equalsIgnoreCase("CLOSE"))) {
			close();
			System.exit(0);
		}
		// Sends a query utilizing SELECT command through Query() method.
		else if((inputSplit.length > 1) && (inputSplit[0].equalsIgnoreCase("SELECT"))) {
			String columns = "";
			String result = "";
			try {
				ResultSet rs = Query(input);
				ResultSetMetaData rsmd = rs.getMetaData();
				for(int i = 1; i <= rsmd.getColumnCount(); i++) {
					columns += "(" + i + ")" + rsmd.getColumnName(i) + "\t";
				}
				while(rs.next()) {
					for(int j = 1; j <= rsmd.getColumnCount(); j++) {
						result += "(" + j + ")" + rs.getString(j) + "\t";
					}
				}
			} catch(SQLException e) {
				
			}
			System.out.println("\n---------Fields---------");
			System.out.println(columns);
			System.out.println("----------Data----------");
			System.out.println(result + "\n");
		}
		/*
		 * Because every other function of MySQL could be considered a non-output command,
		 * anything entered that is more than 1 in length is sent using the Update() method.
		 */
		else if((inputSplit.length > 1) && !(inputSplit[0].equalsIgnoreCase("SELECT"))) {
			Update(input);
		}
		sql();
	}
	static void initializeConnection() {
		System.out.println("\nPlease enter information in this format: \"host:port:database:username:password\": ");
		String information = scanner.nextLine();
		String[] info = information.split(":");
		if(info.length == 5) {
			/*
			 * Changes with every instance initializeConnection() is used.
			 * Prevents need for restarting program for switching between databases/hosts.
			 */
			host = info[0].trim();
			port = info[1].trim();
			database = info[2].trim();
			username = info[3].trim();
			password = info[4].trim();
			System.out.println(
					"\nNow attempting to connect to the database using info:\n"
				+ 	" Host: " + host + "\n"
				+ 	" Port: " + port + "\n"
				+	" Database: " + database + "\n"
				+	" Username: " + username + "\n"
				+	" Password: " + password + "\n");
			connect();
		} else {
			System.out.println("\nPlease try again, following correct syntax.");
			initializeConnection();
		}
	}
	static void connect() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
		} catch(SQLException e) {
			System.out.println("\nMySQL has encountered an error attempting to close.");
			e.printStackTrace();
		}
		if(connection != null) {
			System.out.println("\nConnection successful!");
		} else {
			System.out.println("\nIssue connecting to server! Please look at errors reported above.");
			initializeConnection();
		}
	}
	static void close() {
		try {
			if(!connection.isClosed()) {
				connection.close();
			}
		} catch(SQLException e) {
			System.out.println("\nMySQL has encountered an error attempting to close.");
			e.printStackTrace();
		}
	}
	static void Update(String str) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(str);
			statement.close();
		} catch(SQLException e) {
			connect();
			sql();
			System.out.println("\nMySQL has encountered an error attempting to update.");
			e.printStackTrace();
		}
	}
	static ResultSet Query(String str) {
		ResultSet rs = null;
		try {
			Statement statement = connection.createStatement();
			rs = statement.executeQuery(str);
		} catch(SQLException e) {
			connect();
			sql();
			System.out.println("\nMySQL has encountered an error attempting to query.");
			e.printStackTrace();
		}
		return rs;
	}
	static Connection getConnection() {
		return connection;
	}
}