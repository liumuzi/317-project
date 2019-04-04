package pkg;

import java.sql.Connection;
import java.util.Scanner;

interface User {
	Scanner scanner = new Scanner(System.in);
	
	/* the interface of asking for user operation.
	 * return the user input as an integer (operation number).
	 * error handling should be done here. */
	int askForOperation();
	
	/* execute the operation according to the operation number.
	 * call other methods to execute
	 * return 0 if the operation is not "Go back", return 1 if the operation is "Go back" */
	int executeOperation(int operationNum, Connection con);
	
	/* close the scanner and any other resources */
	void close();
}
