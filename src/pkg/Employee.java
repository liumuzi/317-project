package pkg;

import java.sql.*;
import java.util.concurrent.TimeUnit;

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
			case 2:
				markPosition(con);
				break;
			case 3:
				workingTime(con);
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
		scanner.close();
	}
	
	void showAvailablePositions(Connection con) {
		/* Require & check user information */
		System.out.print("Please enter your ID:\n");
		String id = scanner.nextLine();
		if (!validEmployeeID(id, con))
			return;
		
		/* Database operation */
		String qry = "SELECT P.Position_ID, P.Position_Title, P.Salary, ER.Company, C.Size, C.Founded"
				+ " FROM Position P, Employee EE, Employer ER, Company C"
				+ " WHERE EE.Employee_ID = '" + id + "'"
				+ " AND P.Employer_ID = ER.Employer_ID"
				+ " AND ER.Company = C.Company"
				+ " AND P.Status = True"
				+ " AND EE.Skills LIKE CONCAT('%', P.Position_Title ,'%')"
				+ " AND P.Salary >= EE.Expected_Salary "
				+ " AND EE.Experience >= P.Experience";
		
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(qry); 
		
			/* Report results */
			if(rs.next()) {
				System.out.print("Your available positions are:\n"
						+ "Position_ID, Position_Title, Salary, Company, Size, Founded\n");
				System.out.print(rs.getString("Position_ID")+", ");
				System.out.print(rs.getString("Position_Title")+", ");
				System.out.print(rs.getString("Salary")+", ");
				System.out.print(rs.getString("Company")+", ");
				System.out.print(rs.getString("Size")+", ");
				System.out.print(rs.getString("Founded")+"\n");
			}
			else
			{
				System.out.print("No available position for employee " + id + "!\n");
			}
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

	void markPosition(Connection con) {
		/* Require & check user information */
		System.out.print("Please enter your ID:\n");
		String id = scanner.nextLine();
		if (!validEmployeeID(id, con))
			return;
		
		
		/* Database operation */
		
		/* 1. Search for positions that user may be interested */
		String qry = "SELECT P.Position_ID, P.Position_Title, P.Salary, ER.Company, C.Size, C.Founded"
				+ " FROM Position P, Employee EE, Employer ER, Company C"
				+ " WHERE P.Employer_ID = ER.Employer_ID"
				+ " AND EE.Employee_ID = '" + id + "'"
				+ " AND ER.Company = C.Company"
				/* -----available----- */
				+ " AND P.Status = True"
				+ " AND EE.Skills LIKE CONCAT('%', P.Position_Title ,'%')"
				+ " AND P.Salary >= EE.Expected_Salary "
				+ " AND EE.Experience >= P.Experience"
				/* -----not history company----- */
				+ " AND C.Company NOT IN "
					+ "(SELECT ER2.Company"
					+ " FROM Position P2, Employer ER2, Employment_History H"
					+ " WHERE P2.Employer_ID = ER2.Employer_ID"
					+ " AND H.Position_ID = P2.Position_ID"
					+ " AND H.Employee_ID = '"+ id +"')"
				/* -----not marked----- */
				+ " AND P.Position_ID NOT IN "
					+ "(SELECT M.Position_ID"
					+ " FROM marked M"
					+ " WHERE M.Employee_ID = '"+ id +"')";
		ResultSet rs = null;
		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(qry); 
		
			/* Report results */
			if(rs.next()) {
				System.out.print("Your interested positions are:\n"
						+ "Position_ID, Position_Title, Salary, Company, Size, Founded\n");
				System.out.print(rs.getString("Position_ID")+", ");
				System.out.print(rs.getString("Position_Title")+", ");
				System.out.print(rs.getString("Salary")+", ");
				System.out.print(rs.getString("Company")+", ");
				System.out.print(rs.getString("Size")+", ");
				System.out.print(rs.getString("Founded")+"\n");
			}
			else
			{
				System.out.print("No interested position for employee " + id + "!\n");
				return;
			}
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
		
		/* 2. Prompt the user to mark one position as interested */
		//user input
		boolean pidValid = false;
		String pid = null;
		while (!pidValid) {
			System.out.print("Please enter one interested Position_ID (Enter 'back' to go back):\n");
			pid = scanner.nextLine();
			if(pid.equals("back"))
				return;
			
			try {
				rs.beforeFirst();
				while(rs.next()){
					if(pid.equals(rs.getString("Position_ID"))) {
						pidValid = true;
						break;
					}
				}
				if(!pidValid)
					System.out.print("Invalid Position_ID! Please enter again.\n");
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		//insert record in database
		String qry2 = "INSERT INTO marked VALUE ('"+ pid + "', '" + id + "', false" +")";
		try {
			Statement stmt2 = con.createStatement();
			stmt2.executeUpdate(qry2);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		//insertion complete
		System.out.print("Done.\n");
	}
	
	void workingTime(Connection con) {
		long sum = 0;
		/* Require & check user information */
		System.out.print("Please enter your ID:\n");
		String id = scanner.nextLine();
		if (!validEmployeeID(id, con))
			return;
		
		/* Database operation */
		String qry = "SELECT * FROM Employment_History H"
				+ " WHERE H.Employee_ID = '" + id + "'"
				+ " AND H.End IS NOT NULL"
				+ " ORDER BY End DESC"
				+ " LIMIT 3";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(qry); 
		
			//check size of the result set
			rs.last();
			if(rs.getRow() < 3) {
				System.out.print("Less than 3 records.\n");
				return;
			}
			
			//calculate date sum
			rs.beforeFirst();
			while(rs.next()) {
				Date end = rs.getDate("End");
				Date start = rs.getDate("Start");
				long diff = end.getTime() - start.getTime();
				sum += diff;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		/* Report result */
		System.out.print("Your average working time is: " + TimeUnit.DAYS.convert(sum/3, TimeUnit.MILLISECONDS) + " days.\n");
	}
	
	boolean validEmployeeID(String id, Connection con) {
		String chk_id = "SELECT * FROM Employee E WHERE E.Employee_ID = '" + id +"'";
		try {
			Statement chk_stmt = con.createStatement();
			ResultSet chk_rs = chk_stmt.executeQuery(chk_id); 
			if(!chk_rs.next())
			{
				System.out.print("Invalid Employee ID!\n");
				return false;
			}
			System.out.print("Valid ID! Porcessing......\n");
			return true;
		}catch (SQLException e){
			e.printStackTrace();
			System.exit(0);
		}
		return false;
	}
}
