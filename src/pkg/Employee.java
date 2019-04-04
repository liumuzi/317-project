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
			case 4:
				//Go back
				return 1;
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
		/* Require & check user information */
		System.out.print("Please enter your ID:\n");
		String id = scanner.nextLine();
		//TODO check whether id exists
		String chk_id = "SELECT * FROM Employee E WHERE E.Employee_ID = '" + id +"'";
		try {
			Statement chk_stmt = con.createStatement();
			ResultSet chk_rs = chk_stmt.executeQuery(chk_id); 
			if(!chk_rs.next())
			{
				System.out.print("Invalid Employee ID!\n");
				return;
			}
			System.out.print("Valid ID! Porcessing......\n");
		}catch (SQLException e){
			e.printStackTrace();
			System.exit(0);
		}
		
		/* Database operation */
		String qry = "SELECT P.Position_ID, P.Position_Title, P.Salary, ER.Company, C.Size, C.Founded"
				+ " FROM Position P, Employee EE, Employer ER, Company C"
				+ " WHERE EE.Employee_ID = " + id
				+ " AND P.Employer_ID = ER.Employer_ID"
				+ " AND ER.Company = C.Company"
				+ " AND P.Status = True"
				+ " AND EE.Skills LIKE CONCAT('%;', P.Position_Title ,';%')"
				+ " AND P.Salary >= EE.Expected_Salary "
				+ " AND EE.Experience >= P.Experience";
		
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(qry); 
		
			/* Report results */
			System.out.print("Your available positions are:\n"
					+ "Position_ID, Position_Title, Salary, Company, Size, Founded\n");
			while(rs.next()) {
				System.out.print(rs.getString("Position_ID")+", ");
				System.out.print(rs.getString("Position_Title")+", ");
				System.out.print(rs.getString("Salary")+", ");
				System.out.print(rs.getString("Company")+", ");
				System.out.print(rs.getString("Size")+", ");
				System.out.print(rs.getString("Founded")+"\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
