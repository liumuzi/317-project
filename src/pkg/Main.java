package pkg;

import java.sql.*;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		/* DATABASE CONNECTION */
		String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db18";
		String dbUsername = "Group18";
		String dbPassword = "CSCI3170G18";

		Connection con = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			while(con == null)
				con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
		}catch (ClassNotFoundException e){
			System.out.println("[Error]: Java MySQL DB Driver not found!!");
			System.exit(0);
		}catch (SQLException e){
			System.out.println(e);
		}
		
		
		while(true){
			/* WELCOME INTERFACE */
			User user = null;
			Scanner scanner = new Scanner(System.in);
			
			System.out.print("Welcome! Who are you?\n1. An administrator\n2. An employee\n3. An employer\n4. Exit\nPlease enter [1-4].\n");
			boolean inputValid = false;
			while(!inputValid) {
				String position = scanner.nextLine();
				switch(position)
				{
					case "1":
						/* Administrator */
						inputValid = true;
						user = new Administrator();
						break;
					case"2":
						/* Employee */
						inputValid = true;
						user = new Employee();
						break;
					case "3":
						/* Employer */
						inputValid = true;
						user = new Employer();
						break;
					case "4":
						/* Exit */
						inputValid = true;
						scanner.close();
						System.out.print("System exited.");
						System.exit(0);
					default:
						System.out.print("Invalid input! Please enter again:\n");
				}
			}
			
			
			/* OPERATIONS */
			int goBack = 0;
			while(goBack == 0) {
				int operation = user.askForOperation();
				goBack = user.executeOperation(operation, con);
			}
		
		}
	}
}
