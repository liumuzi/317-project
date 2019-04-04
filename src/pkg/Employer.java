
package pkg;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Employer implements User {

	@Override
	public int askForOperation() {
		int operationNum = 0;
		boolean valid = false;
		while(!valid) {
			/* read input */
			System.out.print("Employer, what would you like to do?\n"
					+ "1. Post Position Recruitment\n"
					+ "2. Check employees and arrange an interview\n"
					+ "3. Accept an employee\n"
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
				postNewPosition(con);
				break;
                        case 2:
                                checkAndInterview(con);
                                break;
                        case 3:
                                acceptEmployee(con);
                                break;
                        case 4:
                                //go back
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

        
        void postNewPosition(Connection con){
            /*Require Employer Information*/
            System.out.println("Please enter your ID.");
            String employerID = scanner.nextLine();
            
            /*Chech whether the employerID exists*/
            String getNumber = "SELECT * "
                    +"FROM Employer E "
                    +"WHERE E.Employer_ID = '%s';";
            
            getNumber = String.format(getNumber, employerID);
            try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(getNumber);
                        if(rs.isBeforeFirst()){
                            System.out.println("Error: EmployerID not found");
                            return;
                        }
                        
                        /*input the required position information*/
                        System.out.println("Please enter the position title.");
                        String title = scanner.nextLine();
                        System.out.println("Please enter an upper bound of salary.");
                        String salaryStr = scanner.nextLine();
                        int salary = Integer.parseInt(salaryStr);
                        System.out.println("Please enter the required experience(Press enter to skip).");
                        String experience = scanner.nextLine();
                        int exp;
                        if (experience == "")
                            exp = 0;
                        else
                            exp = Integer.parseInt(experience);
                        
                        /*find the eligible employees and post*/
                        String query = "SELECT COUNT(*) "
                                +"FROM Employee E "
                                +"WHERE E.Skills LIKE '%" + title + "%' "
                                +"AND E.Expected_Salary <=" + salary +"' "
                                +"AND E.Experience >=" + exp+";";

                        ResultSet result = stmt.executeQuery(query); 
                        result.next();
                        if(result.getInt(1)==0){
                            System.out.println("Error: No satisfied empolyee");
                            return;
                        }
                        String newPosition = "INSERT INTO Position "
                                            +"VALUES(NULL,'"+ title + "',"
                                            + salary + "," + exp + ",True,'" + employerID +"');" ;
                        stmt.executeUpdate(newPosition);
                         
                        System.out.println(result.getInt(1)+" potential employees are found. The position recruitment is posted.");    
		} catch (SQLException e) {
			e.printStackTrace();
                        System.exit(0);
                        
		}
            
        }
        
        void checkAndInterview(Connection con){
                /*Require Employer Information*/
            System.out.println("Please enter your ID.");
            String employerID = scanner.nextLine();
            
            /*Chech whether the employerID exists*/
            String getNumber = "SELECT * "
                    +"FROM Employer E "
                    +"WHERE E.Employer_ID = '%s';";
            
            getNumber = String.format(getNumber, employerID);
            try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(getNumber);
                        if(!rs.isBeforeFirst()){
                            System.out.println("Error: EmployerID not found");
                            return;
                        }
                        
                        /*display the position posted by this id*/
                        String getPosition = "SELECT P.Position_ID "
                                +"FROM Position P "
                                +"WHERE P.Employer_ID = '" + employerID + "';";
                        ResultSet rsPosition = stmt.executeQuery(getPosition);
                        if(!rsPosition.isBeforeFirst()){
                            System.out.println("No record is found");
                            return;
                        }
                        else{
                            rsPosition.next();
                            System.out.println("The id of position recruitments posted by you are:");
                            while(rsPosition.next()){
                                System.out.println(rsPosition.getString(1));
                            }
                        }
                        
                        /*display the employees who are interested in a selected position*/
                        System.out.println("Please pick one Position id.");
                        String positionID = scanner.nextLine();
                        
                        String getEmployee = "SELECT * "
                                + "FROM Employee E "
                                +"WHERE E.Employee_ID IN ("
                                +"SELECT E.Employee_ID "
                                +"FROM marked M "
                                +"WHERE M.Position_ID = '" + positionID + "');";
                        
                        ResultSet rsEmployee = stmt.executeQuery(getEmployee);
                        if(!rsEmployee.isBeforeFirst()){
                            System.out.println("No record is found");
                            return;
                        }
                        else{
                            System.out.println("The employees who mark interested in this position recruitments are:");
                            while(rsEmployee.next()){
                                for (int i = 1; i < 6; i++)
                                    System.out.print(rsEmployee.getString(i)+"  ");
                                
                                System.out.println();
                            }
                        }
                        
                        /*Pick one Employee*/
                        System.out.println("Please pick one employee by Employee_ID.");
                        String employeeID = scanner.nextLine();
                        
                        //arrange an interview and record the employee is interviewed
                        String interview = "UPDATE marked SET status = True "
                                + "WHERE Position_ID = '" + positionID + "' "
                                +"AND Employee_ID = '" + employeeID + "';";
                        stmt.executeUpdate(interview);
                        System.out.println("An IMMEDIATE interview has done.");
                        
            } catch (SQLException e) {
			e.printStackTrace();
                        System.exit(0);
                        
		}
        }
        
        void acceptEmployee(Connection con){
            try {
                        /*Require Employer Information*/
                        System.out.println("Please enter your ID.");
                        String employerID = scanner.nextLine();
            
                        /*Check whether the employerID exists*/
                        String getNumber = "SELECT * "
                        +"FROM Employer E "
                        +"WHERE E.Employer_ID = '%s';";

                        getNumber = String.format(getNumber, employerID);
            
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(getNumber);
                        if(!rs.isBeforeFirst()){
                            System.out.println("Error: EmployerID not found");
                            return;
                        }
                        
                        //ask for employee id
                        System.out.println("Please enter the Employee_ID you want to hire.");
                        String employeeID = scanner.nextLine();
                        
                        //check whether the employee is suitable 
                        String count = "SELECT M.Position_ID "
                        +"FROM marked M, Position P "
                        +"WHERE P.Position_ID = M.Position_ID AND P.Employer_ID = '%s' AND "
                        + "M.Employee_ID = '%s'AND M.status = True;";
            
                        count = String.format(count, employerID, employeeID);
                        ResultSet rsSuitable = stmt.executeQuery(count);
                        
                        if(!rsSuitable.isBeforeFirst()){
                            System.out.println("Error: This employee is not suitable.");
                            return;
                        }
                        // insert the employee into record (one question, what it applied for multi position in this company)
                        rsSuitable.next();
                        String positionID = rsSuitable.getString(1);
                        
                        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
                        Date date = new Date();
 
                        String record = "INSERT INTO Employment_History "
                                +"VALUES('"+ positionID + "', '" + employeeID + "', "
                                + dateFormat.format(date) + ", NULL);"; 
                        
                        //update the position information
                        String update = "UPDATE Position SET Status = False "
                                + "WHERE Position_ID = '" + positionID + "';";
                        stmt.executeUpdate(record);
                        stmt.executeUpdate(update);
                        
                        //display the result to employer
                        String display = "SELECT H.Employee_ID, E.Company, H.Position_ID, H.Start, H.End "
                                +"FROM Employment_History H, Position P, Employer E "
                                +"WHERE H.Position_ID = '" + positionID + "' AND "
                                +"H.Position_ID = P.Position_ID AND P.Employer_ID = E.Employer_ID;";
                        ResultSet rsInfo = stmt.executeQuery(display);
                        
                        System.out.println("An Employment History record is created, details are:");
                        while(rsInfo.next()){
                            for (int i = 1; i < 6; i++)
                                System.out.print(rsInfo.getString(i)+"  ");
                                
                            System.out.println();
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        System.exit(0);
                        
		}
        }
}

