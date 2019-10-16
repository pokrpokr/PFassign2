import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import app.*;
import cu_exceptions.*;

public class Application {

	public static void main(String[] args){
		System.out.print("Welcome to enrolment system");
			
		Scanner scanner = new Scanner(System.in);
		Customers currentUser = null;
		currentUser = logIn(scanner, currentUser);
		if (currentUser == null) {
			System.out.println("Do you want to signup ? [Y:1, N:2]");
			int signToken = scanner.nextInt();
			scanner.nextLine();
			switch (signToken) {
			case 1:
				currentUser = signUp(scanner, currentUser);
				break;
			default:
				System.out.println("System exit");
				System.exit(0);
				break;
			}
		}
		
		switch ((String) currentUser.getColumn("type")) {
		case "Student":
			break;
		case "Staff":
			break;
		case "Admin":
			System.out.println("Menu :");
			adminMenu(scanner);
			break;
		default:
			System.err.println("Wrong user!");
			break;
		}
		scanner.close();
		System.exit(0);
	}
	
	private static Customers login(String userNum, String encodePassword) throws CuLoginException {
		return Customers.login(userNum, encodePassword);
	}
	
	private static Customers signup(String userNum, String password, String type) throws InsertFailedException {
		return Customers.createCustomer(userNum, password, type);
	}
	
	private static Customers logIn(Scanner scanner, Customers currentUser) {
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
		
		return currentUser;
	}
	
	private static Customers signUp(Scanner scanner, Customers currentUser) {
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
			scanner.nextLine();
			System.out.println(type != 1);
			while (!(type == 1 || type == 2)) {
				System.err.println("Type must be 'student' or 'staff'!");
				System.out.println("Please enter your account type :");
				type = scanner.nextInt();
				scanner.nextLine();
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

	private static void adminMenu(Scanner scanner) {
		int opToken = 0;
		do {
			String[] choices = new String[] {"Add Course", "Add Staff", "Add Venue", "Assign Staff to Lesson", 
					"Enrol Student in Course Offering", "Register Student in Tutorial", "Exit system"};
			for (int i = 0; i < choices.length; i++) {
				System.out.println(choices[i]+": "+ (i+1));
			}
			System.out.println("Please enter your operation :");
			opToken = scanner.nextInt();
			scanner.nextLine();
			switch (opToken) {
			case 1:
				int cOperation = 0;
				do {
					ArrayList<Course> courses = new ArrayList<>();
					String[] coChoiceStrings = new String[] {"Add Course", "Add Course Offering", "Add Lecture", "Add Tutorial"};
					for (int i = 0; i < coChoiceStrings.length; i++) {
					System.out.println(coChoiceStrings[i] + ": " + (i+1));
					}
					System.out.println("Enter your operation :");
					cOperation = scanner.nextInt();
					scanner.nextLine();
				
					switch (cOperation) {
					case 1:
						int ifContinue = 0;
						do {
							System.out.println("Please enter courseId :");
							String courseId = scanner.nextLine();
							System.out.println("Please enter course name :");
							String name = scanner.nextLine();
							System.out.println("Please enter course objective");
							String objective = scanner.nextLine();
							try {
								if (addCourse(new String[] {courseId, name, objective})) {
									System.out.println("Adding course successful");
								}
							} catch (Exception e) {
								System.err.println("Adding course failed : " + e);
							}
							try {
								Thread.sleep(3);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("Want to continue to add course? [Y:1, N:2]");
							ifContinue = scanner.nextInt();
							scanner.nextLine();
						} while (ifContinue == 1);
						break;
					case 2:
						try {
							courses = Course.courses();
						} catch (SQLException se) {
							System.out.println("Cant find courses! " + se);
							System.out.println("Please adding courses first");
							break;
						}
						if (courses.size() == 0) break;
						int coifContinue = 0;
						Course course = null;
						do {
							System.out.println("Please enter courseId: ");
							String cId = scanner.nextLine();
							for (int i = 0; i < courses.size(); i++) {
								if (courses.get(i).getColumn("courseId").equals(cId)) {
									course = courses.get(i);
									break;
								} 
							}
							if (course == null) {
								System.err.println("Course does not exist");
								try {
									Thread.sleep(3);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println("Want to continue?[Y:1, N:2]");
								coifContinue = scanner.nextInt();
								scanner.nextLine();
							}
							
							System.out.println("Please enter maxNum: ");
							int maxNum = scanner.nextInt();
							scanner.nextLine();
							try {
								if (addCourseOffering(maxNum, course)) {
									System.out.println("Adding course offering successful");
								}
							} catch (Exception e) {
								System.err.println("Adding course offering failed : " + e);
							}
							try {
								Thread.sleep(3);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("Want to continue to add course offering? [Y:1, N:2]");
							coifContinue = scanner.nextInt();
							scanner.nextLine();
						} while (coifContinue == 1);
						break;
					case 3:
						ArrayList<CourseOffering> cos = new ArrayList<>();
						try {
							cos = CourseOffering.cos();
						} catch (SQLException se) {
							System.out.println("Cant find course offering! " + se);
							System.out.println("Please adding course offering first");
							break;
						}
						if(cos.size() == 0) break;
						int leifcontinue = 0;
						CourseOffering co = null;
						do {
							System.out.println("Please enter courseId: ");
							String cId = scanner.nextLine();
//							TODO
							for (int i = 0; i < cos.size(); i++) {
								if (courses.get(i).getColumn("courseId").equals(cId)) {
									course = courses.get(i);
									break;
								} 
							}
							if (course == null) {
								System.err.println("Course does not exist");
								try {
									Thread.sleep(3);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println("Want to continue?[Y:1, N:2]");
								coifContinue = scanner.nextInt();
								scanner.nextLine();
							}
							
							System.out.println("Please enter maxNum: ");
							int maxNum = scanner.nextInt();
							scanner.nextLine();
							try {
								if (addCourseOffering(maxNum, course)) {
									System.out.println("Adding course offering successful");
								}
							} catch (Exception e) {
								System.err.println("Adding course offering failed : " + e);
							}
							try {
								Thread.sleep(3);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("Want to continue to add course offering? [Y:1, N:2]");
							coifContinue = scanner.nextInt();
							scanner.nextLine();
						} while (coifContinue == 1);
						
						break;
					case 4:
						break;
					default:
						break;
					}
					System.out.println("Want to back to add course? [Y:0, N:3]");
					cOperation = scanner.nextInt();
				} while (cOperation == 0);
				break;
			case 2:
				int stifcontinue = 0;
				do {
					System.out.println("please enter eNo :");
					String eNo = scanner.nextLine();
					System.out.println("Please enter name :");
					String name = scanner.nextLine();
					System.out.println("Please enter position :");
					String position = scanner.nextLine();
					System.out.println("Please enter office :");
					String office = scanner.nextLine();
					try {
						if (addStaff(new String[]{eNo, name, position, office})) {
							System.out.println("Adding staff successful");
						} 
					} catch (Exception e) {
						System.err.println("Adding staff failed : " + e);
					}
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Want to continue to add staff? [Y:1, N:2]");
					stifcontinue = scanner.nextInt();
					scanner.nextLine();
				} while (stifcontinue == 1);
				break;
			case 3:
				int veifcontinue = 0;
				do {
					System.out.println("Please enter location :");
					String location = scanner.nextLine();
					System.out.println("Please enter capacity :");
					String capacity = scanner.nextLine();
					System.out.println("Please enter purpose :");
					String purpose = scanner.nextLine();
					try {
						if (addVenue(location, capacity, purpose)) {
							System.out.println("Adding venue successful");
						} 
					} catch (Exception e) {
						System.err.println("Adding venue failed");
					}
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Want to continue to add venue? [Y:1, N:2]");
					veifcontinue = scanner.nextInt();
					scanner.nextLine();
				} while (veifcontinue == 1);
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				return;
			default:
				break;
			}
		} while (!(opToken == 5 || opToken == 0));
	}
	
	private static boolean addCourse(String[] args) {
		try {
			Course.createCourse(args);
		} catch (Exception e) {
			System.err.println(e);
			return false;
		}
		return true;
	}
	
	private static boolean addStaff(String[] args) throws InsertFailedException {
		Staff.createStaff(args);
		return true;
	}
	
	private static boolean addVenue(String location, String capacity, String purpose) throws InsertFailedException {
		Venue.createVenue(new String[] { location, capacity, purpose });
		return true;
	}
	
	private static boolean addCourseOffering(int maxNum, Course course) throws InsertFailedException, SQLException {
		return course.createOffering(maxNum);
	}
	
	private static boolean addLecture() {
		return true;
	}
	
	private static boolean addTutorial() {
		return true;
	}
}
