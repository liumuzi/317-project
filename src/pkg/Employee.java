package pkg;

import java.sql.*;

public class Employee implements User {
	@Override
	public int askForOperation() {
		int operationNum = 0;
		boolean valid = false;
		while(!valid) {
			/* read input */
			System.out.print("Employee, what would you like to do?\n"
					+ "1. Show Available Positions\n"
					+ "2. Mark Interested Position\n"
					+ "3. Check Average Working Time\n"
					+ "4. Go back\n");
			String operationStr = scanner.nextLine();
			
			/* error handling */
			try
			{
				operationNum = Integer.parseInt(operationStr);
			}
			catch (NumberFormatException e)//non-numeric value
			{
				System.out.print("Invalid input! Please try again.\n");
				continue;
			}
			if(operationNum < 1 || operationNum > 4)//invalid numeric value
			{
				System.out.print("Invalid input! Please try again.\n");
				continue;
			}
			
			/* while loop ends */
			valid = true;
		}
		return operationNum;
	}

	@Override
	public int executeOperation(int operationNum, Connection con) {
		switch (operationNum)
		{
			case 1:
				showAvailablePositions(con);
				break;
			default:
				System.out.print("[Error] Invalid Operation Number!");
				System.exit(0);
		}
		return 0;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	void showAvailablePositions(Connection con) {
		/* Require user information */
		System.out.print("Please enter your ID:\n");
		String id = scanner.nextLine();
		
		/* Database operation */
		System.out.print("processing...");
		String qry = "SELECT * "
				+ "FROM Position P"
				+ "WHERE P.Status = True and ";
		
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(qry); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Report results */
	}

}
