package pkg;

import java.sql.*;

public class Administrator implements User {
	@Override
	public int askForOperation() {
		System.out.println("Administrator, what would you like to do?");
        System.out.println("1. Create tables");
        System.out.println("2. Delete tables");
        System.out.println("3. Load tables");
        System.out.println("4. Check tables");
        System.out.println("5. Go back");
        System.out.println("Please enter [1-5].");
        int adChoice = Integer.parseInt(scanner.nextLine());
        
        //TODO: error handling
        
        return adChoice;
	}

	@Override
	public int executeOperation(int operationNum, Connection con) {
		switch (operationNum)
		{
			case 1:
				createTables(con);
				break;
			case 2:
				break;
			default:
				System.out.print("[Error] Invalid Operation NUmber!");
				System.exit(0);
		}
		return 0;
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	public void createTables(Connection con) {
        System.out.print("processing...");
        String createEmployee = "CREATE TABLE Employee(" +
                                "Employee_ID       CHAR(6) NOT NULL," +
                                "Name              CHAR(30) NOT NULL," +
                                "Expected_Salary   INTEGER UNSIGNED," +
                                "Experience        INTEGER UNSIGNED," +
                                "Skills            CHAR(50) NOT NULL," +
                                "PRIMARY KEY (Employee_ID)" +
                                ")";
        String createEmployer = "CREATE TABLE Employer(" +
                                "	Employer_ID      CHAR(6) NOT NULL," +
                                "	Name             CHAR(30) NOT NULL," +
                                "	Company          CHAR(30) NOT NULL," +
                                "	PRIMARY KEY (Employer_ID)" +
                                ")";
        String createCompany = "CREATE TABLE Company(" +
                                "	Company 	CHAR(30) NOT NULL," +
                                "	Size	INTEGER UNSIGNED," +
                                "	Founded	INT(4) UNSIGNED," +
                                "	PRIMARY KEY (Company)" +
                                ");";
        String employerCom = " ALTER TABLE Employer " +
                " ADD FOREIGN KEY (Company) REFERENCES Company(Company) ";
        String createPosition = "CREATE TABLE `Position`(" +
                                "	Position_ID    CHAR(6) NOT NULL," +
                                "	Position_Title CHAR(30) NOT NULL," +
                                "	Salary         INTEGER UNSIGNED," +
                                "	Experience     INTEGER," +
                                "	Status         BOOLEAN," +
                                "	Employer_ID    CHAR(6) NOT NULL," +
                                "	PRIMARY KEY (Position_ID)," +
                                "	CONSTRAINT chk_exp CHECK (Experence >= 0)" +
                                ")";
        String posEmployer = " ALTER TABLE Position " +
                " ADD FOREIGN KEY (Employer_ID) REFERENCES Employer(Employer_ID) ";
        String employmentHistory = " CREATE TABLE Employment_History( " +
                                   " Position_ID      CHAR(6) NOT NULL, " +
                                   " Employee_ID      CHAR(6) NOT NULL," +
                                   " Start            DATE NOT NULL," +
                                   " End              DATE," +
                                   " PRIMARY KEY (Position_ID)" +
                                   ")";
        String histroyFK = "ALTER TABLE Employment_History" + 
                " ADD FOREIGN KEY (Position_ID) REFERENCES `Position`(Position_ID)," +
                " ADD FOREIGN KEY (Employee_ID) REFERENCES Employee(Employee_ID)";
        String createMarked = "CREATE TABLE marked(" +
                              "Position_ID  CHAR(6) NOT NULL," +
                              "Employee_ID  CHAR(6) NOT NULL," +
                              "Status       BOOLEAN," +
                              "PRIMARY KEY (Position_ID, Employee_ID)" +
                              ")";
        String markedFK = "ALTER TABLE marked" + 
                " ADD FOREIGN KEY (Position_ID) REFERENCES `Position`(Position_ID)," +
                " ADD FOREIGN KEY (Employee_ID) REFERENCES Employee(Employee_ID)";
        
        try{
        	Statement stmt = con.createStatement();
            stmt.executeUpdate(createEmployee);
            stmt.executeUpdate(createCompany);
            stmt.executeUpdate(createEmployer);
            stmt.executeUpdate(employerCom);
            stmt.executeUpdate(createPosition);
            stmt.executeUpdate(posEmployer);
            stmt.executeUpdate(employmentHistory);
            stmt.executeUpdate(histroyFK);
            stmt.executeUpdate(createMarked);
            stmt.executeUpdate(markedFK);
            System.out.println("Done! Tables are created!");
        } catch (SQLException ex) {
            System.out.println();
            System.out.println(ex);
        }
        
    }


}
