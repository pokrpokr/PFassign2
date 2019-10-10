import java.util.*;
import app.*;
import java.io.Console;

public class Application {

	public static void main(String[] args) {
			Customers currentUser = new Customers();
			
			Scanner scanner = new Scanner(System.in);
			System.out.println("Welcome to enrolment system");
			System.out.println("Please login");
			System.out.println("Please enter your userNum :");
			String userNum = scanner.nextLine(); 
			Console console = System.console();
			if (console != null) {
				char[] passwords = console.readPassword("Please enter your password :");
				console.flush();
				String password = new String(passwords); 
				Arrays.fill(passwords, ' ');
				String encodePassword = Base64.getEncoder().encodeToString(password.getBytes());
				
			}			
		
	}
}
