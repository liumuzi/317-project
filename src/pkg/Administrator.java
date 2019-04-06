package pkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

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
            String adChoice = scanner.next();
            int upper = 5;
            selectionError(upper, adChoice);

            return Integer.parseInt(adChoice);
	}

	@Override
	public int executeOperation(int operationNum, Connection con) {
            switch (operationNum)
            {
                    case 1:
                        createTables(con);
                        break;
                    case 2:
                        deleteTables(con);
                        break;
                    case 3:
                    {
                        try {
                            loadData(con);
                        } catch (ParseException ex) {
                            System.out.println("[Error]: " + ex);
                        }
                    }
                        break;
                    case 4:
                        checkData(con);
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
        
        public static boolean selectionError(int upper, String id) {
            boolean result = false;
            for(int i = 1; i <= upper; i++){
                if(id.equals(Integer.toString(i))){
                    result = true;
                    break;
                }
            }
            if(result == false)
                System.out.println("[Error]: Invalid input!!");
            //System.out.println("result: " + result);
            return result;
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
                    " ADD FOREIGN KEY (Company) REFERENCES Company(Company) ON DELETE CASCADE ON UPDATE CASCADE";
            String createPosition = "CREATE TABLE `Position`(" +
                                    "	Position_ID    CHAR(6) NOT NULL," +
                                    "	Position_Title CHAR(30) NOT NULL," +
                                    "	Salary         INTEGER UNSIGNED," +
                                    "	Experience     INTEGER," +
                                    "	Employer_ID    CHAR(6) NOT NULL," +
                                    "	Status         BOOLEAN," +
                                    "	PRIMARY KEY (Position_ID)," +
                                    "	CONSTRAINT chk_exp CHECK (Experence >= 0)" +
                                    ")";
            String posEmployer = " ALTER TABLE Position " +
                    " ADD FOREIGN KEY (Employer_ID) REFERENCES Employer(Employer_ID) ON UPDATE CASCADE";
            String employmentHistory = " CREATE TABLE Employment_History( " +
                                       " Employee_ID      CHAR(6) NOT NULL," +
                                       " Position_ID      CHAR(6) NOT NULL, " +
                                       " Start            DATE NOT NULL," +
                                       " End              DATE," +
                                       " PRIMARY KEY (Position_ID)" +
                                       ")";
            String histroyFK = "ALTER TABLE Employment_History" + 
                    " ADD FOREIGN KEY (Position_ID) REFERENCES `Position`(Position_ID) ON UPDATE CASCADE," +
                    " ADD FOREIGN KEY (Employee_ID) REFERENCES Employee(Employee_ID) ON UPDATE CASCADE";
            String createMarked = "CREATE TABLE marked(" +
                                  "Position_ID  CHAR(6) NOT NULL," +
                                  "Employee_ID  CHAR(6) NOT NULL," +
                                  "Status       BOOLEAN," +
                                  "PRIMARY KEY (Position_ID, Employee_ID)" +
                                  ")";
            String markedFK = "ALTER TABLE marked" + 
                    " ADD FOREIGN KEY (Position_ID) REFERENCES `Position`(Position_ID) ON UPDATE CASCADE," +
                    " ADD FOREIGN KEY (Employee_ID) REFERENCES Employee(Employee_ID) ON UPDATE CASCADE";

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
                System.out.println("[Error]: " + ex);
            }
        
    }
        
    public void deleteTables(Connection con) {
        System.out.print("processing...");
        String deleteHistory = "DROP TABLE IF EXISTS Employment_History";
        String deleteMarked = "DROP TABLE IF EXISTS marked";
        String deleteEmployee = "DROP TABLE IF EXISTS Employee";
        String deletePosition = "DROP TABLE IF EXISTS Position";
        String deleteEmployer = "DROP TABLE IF EXISTS Employer";
        String deleteCompany = "DROP TABLE IF EXISTS Company";
        try{
            Statement stmt = con.createStatement();
            stmt.executeUpdate(deleteHistory);
            stmt.executeUpdate(deleteMarked);
            stmt.executeUpdate(deleteEmployee);
            stmt.executeUpdate(deletePosition);
            stmt.executeUpdate(deleteEmployer);
            stmt.executeUpdate(deleteCompany);
            System.out.println("Done! Tables are deleted!");
        } catch (SQLException ex) {
            System.out.println();
            System.out.println("[Error]: " + ex);
        }
        
    }
    
    public void loadData(Connection con) throws ParseException {
        System.out.println("Please enter the folder path.");
        String path = scanner.next();
        //File folder = new File(path);
        //System.out.print(folder+"\n");
        System.out.print("Processing...");
        
        // inset company files
        try{
            File companyFile = new File(path + "\\company.csv");
            Scanner readCompanyFile = new Scanner(companyFile);
            //System.out.println(readFile.next());
            try{
                PreparedStatement pstmtCompany = con.prepareStatement("insert ignore into Company values(?,?,?)");
                //System.out.println("loading the company file");
                while(readCompanyFile.hasNext()){
                    String temp1 = readCompanyFile.nextLine();
                    //System.out.println(temp1);
                    String[] temp = temp1.split(",");
                    if(temp.length == 3){
                        pstmtCompany.setString(1, temp[0]);
                        pstmtCompany.setInt(2, Integer.parseInt(temp[1]));
                        pstmtCompany.setInt(3, Integer.parseInt(temp[2]));
                    }
                    pstmtCompany.execute();
                }
            }catch(SQLException e){
                System.out.println("[Error occurs when loading the company file]: " + e);
            }
        } catch (FileNotFoundException ex) {
            System.out.println();
            System.out.println("[Error]: " + ex);
        }
        
        // insert employee files 
        try{
            File employeeFile = new File(path + "\\employee.csv");
            Scanner readEmployeeFile = new Scanner(employeeFile);
            try{
                String query = "insert ignore into Employee values (?,?,?,?,?)";
                PreparedStatement pstmtEmployee = con.prepareStatement(query);
                while(readEmployeeFile.hasNext()){
                    String temp1 = readEmployeeFile.nextLine();
                    String[] temp = temp1.split(",");
                    if(temp.length == 5){
                        pstmtEmployee.setString(1, temp[0]);
                        pstmtEmployee.setString(2, (temp[1]));
                        pstmtEmployee.setInt(3, Integer.parseInt(temp[2]));
                        pstmtEmployee.setInt(4, Integer.parseInt(temp[3]));
                        pstmtEmployee.setString(5, (temp[4]));
                    }
                    pstmtEmployee.execute();
                }
            }catch(SQLException e){
                System.out.println("[Error occurs when loading the employee file]: " + e);
            }
        }catch (FileNotFoundException ex) {
            System.out.println();
            System.out.println("[Error]: " + ex);
        }
        
        // insert employer files
        try{
            File employerFile = new File(path + "\\employer.csv");
            Scanner readEmployerFile = new Scanner(employerFile);
            try{
                String query = "insert ignore into Employer values(?,?,?)";
                PreparedStatement pstmtEmployer = con.prepareStatement(query);
                while(readEmployerFile.hasNext()){
                    String temp1 = readEmployerFile.nextLine();
                    String[] temp = temp1.split(",");
                    if(temp.length == 3){
                        pstmtEmployer.setString(1, temp[0]);
                        pstmtEmployer.setString(2, temp[1]);
                        pstmtEmployer.setString(3, temp[2]);
                    }
                    pstmtEmployer.execute();
                }
            }catch(SQLException e){
                System.out.println("[Error occurs when loading the employer file]: " + e);
            }
        }catch (FileNotFoundException ex) {
            System.out.println();
            System.out.println("[Error]: " + ex);
        }
        
        // insert Position files
        try{
            File positionFile = new File(path + "\\position.csv");
            Scanner readPositionFile = new Scanner(positionFile);
            try{
                String query = "insert ignore into Position values(?,?,?,?,?,?)";
                PreparedStatement pstmtPosition = con.prepareStatement(query);
                while(readPositionFile.hasNext()){
                    String temp1 = readPositionFile.nextLine();
                    String[] temp = temp1.split(",");
                    if(temp.length == 6){
                        pstmtPosition.setString(1, temp[0]);
                        pstmtPosition.setString(2, temp[1]);
                        pstmtPosition.setInt(3, Integer.parseInt(temp[2]));
                        pstmtPosition.setInt(4, Integer.parseInt(temp[3]));
                        pstmtPosition.setString(5, temp[4]);
                        pstmtPosition.setBoolean(6, Boolean.parseBoolean(temp[5]));
                    }
                    pstmtPosition.execute();
                }
            }catch(SQLException e){
                System.out.println("[Error occurs when loading the position file]: " + e);
            }
        }catch (FileNotFoundException ex) {
            System.out.println();
            System.out.println("[Error]: " + ex);
        }
        
        
        // insert history files
        try{
            File historyFile = new File(path + "\\history.csv");
            Scanner readHistoryFile = new Scanner(historyFile);
            try{
                String query = "insert ignore into Employment_History values (?,?,?,?)";
                PreparedStatement pstmtHistory = con.prepareStatement(query);
                while(readHistoryFile.hasNext()){
                    String temp1 = readHistoryFile.nextLine();
                    String[] temp = temp1.split(",");
                    if(temp.length == 5){
                        pstmtHistory.setString(1, temp[0]);
                        pstmtHistory.setString(2, temp[2]);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        if(!temp[3].equals("NULL")){
                            java.util.Date start = format.parse(temp[3]);
                            long startTime = start.getTime();
                            //System.out.print("start: " + new java.sql.Date(startTime));
                            pstmtHistory.setDate(3, new java.sql.Date(startTime));
                        }
                        else{
                            pstmtHistory.setNull(3, Types.DATE);
                        }
                        if(!temp[4].equals("NULL")){
                            java.util.Date end = format.parse(temp[4]);
                            long endTime = end.getTime();
                            //System.out.println(" end: " + new java.sql.Date(endTime));
                            pstmtHistory.setDate(4, new java.sql.Date(endTime));
                        }
                        else{
                            pstmtHistory.setNull(4, Types.DATE);
                        }
                    }
                    pstmtHistory.execute();
                }
            }catch(SQLException e){
                System.out.println("[Error occurs when loading the history file]: " + e);
            }
        }catch (FileNotFoundException ex) {
            System.out.println();
            System.out.println("[Error]: " + ex);
        }
        
        System.out.println("Data is loaded!");
    }
    
    public void checkData(Connection con) {
        System.out.println("Number of records in each table:");
        String countEmployee = "SELECT COUNT(*)AS Employee_ROWS FROM Employee;";
        String countCompany = "SELECT COUNT(*) AS Company_ROWS FROM Company;";
        String countEmployer = "SELECT COUNT(*) AS Employer_ROWS FROM Employer;";
        String countHistory = "SELECT COUNT(*) AS History_ROWS FROM Employment_History;";
        String countPosition = "SELECT COUNT(*) AS Position_ROWS FROM Position;";
        
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(countEmployee);
            //System.out.println(rs);
            int num;
            while(rs.next()){
                num = rs.getInt("Employee_ROWS");
                System.out.println( "Employee: " + num);
            }
            
            rs = stmt.executeQuery(countCompany);
            while(rs.next()){
                num = rs.getInt("Company_ROWS");
                System.out.println( "Company: " + num);
            }
            
            rs = stmt.executeQuery(countEmployer);
            while(rs.next()){
                num = rs.getInt("Employer_ROWS");
                System.out.println( "Employer: " + num);
            }
            
            rs = stmt.executeQuery(countHistory);
            while(rs.next()){
                num = rs.getInt("History_ROWS");
                System.out.println( "Employment_History: " + num); 
            }
            
            
            rs = stmt.executeQuery(countPosition);
            while(rs.next()){
                num = rs.getInt("Position_ROWS");
                System.out.println( "Position: " + num);
            }
        } catch (SQLException ex) {
            System.out.println("[Error]: " + ex);
        }
    }


}
