import java.util.Base64;
import java.util.Scanner;
import app.*;
import cu_exceptions.*;

public class Application {

	public static void main(String[] args){
		System.out.print("Welcome to enrolment system");
			
		Scanner scanner = new Scanner(System.in);
		Customers currentUser = null;
		currentUser = login(scanner, currentUser);
		switch ((String) currentUser.getColumn("type")) {
		case "Student":
			
			break;
		case "Staff":
			break;
		case "Admin":
			
			break;
		default:
			break;
		}
		scanner.close();
	}
	
	private static Customers login(String userNum, String encodePassword) throws CuLoginException {
		return Customers.login(userNum, encodePassword);
	}
	
	private static Customers signup(String userNum, String password, String type) throws InsertFailedException {
		return Customers.createCustomer(userNum, password, type);
	}
	
	private static Customers login(Scanner scanner, Customers currentUser) {
		System.out.println("Please login");
		System.out.println("Please enter your userNum :");
		String user = scanner.nextLine();
		while (user.isEmpty()) {
			System.out.println("userNum can not be empty, please enter your userNum :");
			user = scanner.nextLine();
		}
		
		System.out.println("Please enter your password :");
		String password = scanner.nextLine(); 
		while (password.isEmpty()) {
			System.out.println("Password can not be empty, please enter your password :");
			password = scanner.nextLine();
		}
		try {
			String encodePassword = Base64.getEncoder().encodeToString(password.getBytes());
			currentUser = login(user, encodePassword);
		} catch (CuLoginException le) {
			System.out.println("Login failed: " + le );
		}
		
		if(currentUser == null) {
			System.out.println("Please sign up");
			System.out.println("Please enter your userNum :");
			String signNum = scanner.nextLine();
			while (signNum.isEmpty()) {
				System.out.println("userNum can not be empty, please enter your userNum :");
				signNum = scanner.nextLine();
			}
			System.out.println("Please enter your password :");
			String sPassword = scanner.nextLine();
			while (sPassword.isEmpty()) {
				System.out.println("Password can not be empty, please enter your password :");
				sPassword = scanner.nextLine();
			}
			String cPassword = null;
			int enterCount = 0;
			do {
				System.out.println("Please confirm your password :");
				cPassword = scanner.nextLine(); 
				enterCount+=1;
				if(!(enterCount < 5)) {
					System.err.println("More than 5 times entering wrong password!");
					System.exit(0);
				}
			} while (!sPassword.equals(cPassword));
			String encodePassword = Base64.getEncoder().encodeToString(sPassword.getBytes());
			System.out.println("Please choose your account type : {student: 1, staff: 2}");
			int type = scanner.nextInt();
			System.out.println(type != 1);
			while (!(type == 1 || type == 2)) {
				System.err.println("Type must be 'student' or 'staff'!");
				System.out.println("Please enter your account type :");
				type = scanner.nextInt();
				if(type != 1 || type != 2) {
					System.err.println("Type wrong! Exit system");
					System.exit(0);
				}
			}
			try {
				String userType ;
				switch (type) {
				case 1:
					userType = "student";
					break;
				case 2:
					userType = "staff";
				default:
					throw new InsertFailedException("Wrong type!");
				}
				currentUser = signup(signNum, encodePassword, userType);
			} catch (InsertFailedException ie) {
				System.out.println("Signup user failed");				
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return currentUser;
	}
}
