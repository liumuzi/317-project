
package pkg;

import java.nio.charset.Charset;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
            
            /*Check whether the employerID exists*/
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
                        
                        /*input the required position information*/
                        System.out.println("Please enter the position title.");
                        String title = scanner.nextLine();
                        System.out.println("Please enter an upper bound of salary.");
                        String salaryStr = scanner.nextLine();
                        int salary = Integer.parseInt(salaryStr);
                        System.out.println("Please enter the required experience(Press enter to skip).");
                        String experience = scanner.nextLine();
                        int exp;
                        if ("".equals(experience))
                            exp = 0;
                        else
                            exp = Integer.parseInt(experience);
                        
                        /*find the eligible employees and post*/
                        String query = "SELECT COUNT(*) "
                                +"FROM Employee E "
                                +"WHERE E.Skills LIKE '%" + title + "%' "
                                +"AND E.Expected_Salary <=" + salary 
                                +" AND E.Experience >=" + exp+";";
                        
                        ResultSet result = stmt.executeQuery(query); 
                        result.next();
                        if(result.getInt(1)==0){
                            System.out.println("Error: No satisfied empolyee");
                            return;
                        }
                        else {
                        	System.out.println(result.getInt(1)+" potential employees are found. The position recruitment is posted."); 
                        }
                        String AlphaString = "abcdefghijklmnopqrstuvwxyz";
                        StringBuilder pID = new StringBuilder(6);
                        boolean flag = false;
                        ResultSet rsPList = stmt.executeQuery("SELECT Position_ID FROM Position");
                        while(!flag) {
	                        for (int i=0; i<6; i++) {
	                        	int index = (int)(AlphaString.length()*Math.random());
	                        	pID.append(AlphaString.charAt(index));
	                        }
	                        pID.toString();
	                        while(rsPList.next()) {
	                        	if(pID.equals(rsPList.getString(1)))
	                        		break;
	                        	flag = true;
	                        }
                        }
                        String newPosition = "INSERT INTO Position (Position_ID, Position_title, Salary, Experience, Status, Employer_ID) "
                                            +"VALUES('"+ pID +"','"+ title + "',"
                                            + salary + "," + exp + ",True,'" + employerID +"');" ;
     
                        stmt.executeUpdate(newPosition);
                            
		} catch (SQLException e) {
			e.printStackTrace();
                        System.exit(0);
                        
	}
            
        }
        
        void checkAndInterview(Connection con){
                /*Require Employer Information*/
            System.out.println("Please enter your ID.");
            String employerID = scanner.nextLine();
            
            /*Check whether the employerID exists*/
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
                                +"WHERE P.Employer_ID = '" + employerID + "' AND P.Status = True;";
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
                        boolean flag = false;
                        ResultSet rsEmployee = null;
                        String positionID = ""; 
                        while(!flag) {
                        System.out.println("Please pick one Position id (Enter 'back' to go back).");
                        positionID = scanner.nextLine();
                        if (positionID.equals("back"))
                        	return;
                        String getEmployee = "SELECT * "
                                + "FROM Employee E "
                                +"WHERE E.Employee_ID IN ("
                                +"SELECT M.Employee_ID "
                                +"FROM marked M "
                                +"WHERE M.Position_ID = '" + positionID + "');";
                        
                        rsEmployee = stmt.executeQuery(getEmployee);
                        if(!rsEmployee.isBeforeFirst()){
                            System.out.println("No record is found. Please try another Position.");
                        }
                        else{
                        	flag = true;
                        }
                        }
                            System.out.println("The employees who mark interested in this position recruitments are:");
                            System.out.println("Employer_ID, Name, Expected_Salary, Experience, Skils");
                            while(rsEmployee.next()){
                                for (int i = 1; i < 6; i++)
                                    System.out.print(rsEmployee.getString(i)+"  ");
                                System.out.println();
                            }
                        flag = false;
                        String employeeID = "";
                        /*Pick one Employee*/
                            while (!flag) {
                    			System.out.print("Please pick one employee by Employee_ID (Enter 'back' to go back):\n");
                    			employeeID = scanner.nextLine();
                    			if(employeeID.equals("back"))
                    				return;
                    			
                    			try {
                    				rsEmployee.beforeFirst();
                    				while(rsEmployee.next()){
                    					if(employeeID.equals(rsEmployee.getString("Employee_ID"))) {
                    						flag= true;
                    						break;
                    					}
                    				}
                    				if(!flag)
                    					System.out.print("Invalid Employee_ID! Please enter again.\n");
                    			} catch (SQLException e) {
                    				e.printStackTrace();
                    				System.exit(0);
                    			}
                    		}
                        //arrange an interview and record the employee is interviewed
                        String interview = "UPDATE marked SET Status = True "
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
                        
                        //ask for employee id and check whether the employee is suitable 
                        boolean flag = false;
                        String employeeID = "";
                        String countInitial = "SELECT M.Position_ID "
                                +"FROM marked M, Position P "
                                +"WHERE P.Position_ID = M.Position_ID AND P.Employer_ID = '%s' AND "
                                + "M.Employee_ID = '%s'AND M.status = True;";
                        ResultSet rsSuitable = null;
                            while (!flag) {
                    			System.out.print("Please enter the Employee_ID you want to hire.(Enter 'back' to go back):\n");
                    			employeeID = scanner.nextLine();
                    			if(employeeID.equals("back"))
                    				return;
                    			
                    			try {
                    				String count = String.format(countInitial, employerID, employeeID);
                                    rsSuitable = stmt.executeQuery(count);
                                    if(rsSuitable.isBeforeFirst()){
                                        flag = true;
                                    }
                    				
                    				if(!flag)
                    					System.out.print("This employee is not suitable! Please enter again.\n");
                    			} catch (SQLException e) {
                    				e.printStackTrace();
                    				System.exit(0);
                    			}
                            }
                   
                        // insert the employee into record (one question, what it applied for multiple position in this company)
                        rsSuitable.next();
                        String positionID = rsSuitable.getString(1);
                        
                        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("YYYY-MM-dd");
                        LocalDate date = LocalDate.now();
 
                        System.out.print(positionID);
                        String record = "INSERT INTO Employment_History "
                                +"VALUES('"+ positionID + "', '" + employeeID + "', '"
                                + dateFormat.format(date) + "', NULL);"; 
                        
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

