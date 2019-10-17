import java.sql.SQLException;
import java.util.*;
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
					"Enrol Student in Course Offering", "Register Student in Tutorial", "Assign venue manually", "Exit system"};
			for (int i = 0; i < choices.length; i++) {
				System.out.println(choices[i]+": "+ (i+1));
			}
			System.out.println("Please enter your operation :");
			opToken = scanner.nextInt();
			scanner.nextLine();
			switch (opToken) {
			case 1:
				// Adding Course/CourseOffering/Lesson				
				int cOperation = 0;
				do {
					ArrayList<Course> courses = new ArrayList<>();
					ArrayList<CourseOffering> cos = new ArrayList<>();
					String[] coChoiceStrings = new String[] {"Add Course", "Add Course Offering", "Add Lesson", "Back"};
					for (int i = 0; i < coChoiceStrings.length; i++) {
					System.out.println(coChoiceStrings[i] + ": " + (i+1));
					}
					System.out.println("Enter your operation :");
					cOperation = scanner.nextInt();
					scanner.nextLine();
				
					switch (cOperation) {
					case 1:
						//Adding Course						
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
						// Adding CourseOffering
						// Finding Course before creating CourseOffering
						try {
							courses = Course.courses();
						} catch (SQLException se) {
							System.err.println("Cant find courses! " + se);
							System.err.println("Please adding courses first");
							break;
						}
						if (courses.size() == 0) {
							System.err.println("Please adding courses first");
							break;
						}
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
								System.out.println("Want to search again?[Y:1, N:2]");
								coifContinue = scanner.nextInt();
								scanner.nextLine();
							}
						} while (coifContinue == 1);
						if (coifContinue == 2) break;
						// Input CourseOffering args						
						do {
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
						// Adding Lesson
						try {
							courses = Course.courses();
						} catch (SQLException se) {
							System.err.println("Cant find courses! " + se);
							System.err.println("Please adding courses first");
							break;
						}
						if(courses.size() == 0) {
							System.err.println("Please adding courses first");
							break;
						}
						try {
							cos = CourseOffering.cos();
						} catch (SQLException se) {
							System.err.println("Cant find courses! " + se);
							System.err.println("Please adding course offering first");
							break;						
						}
						if (cos.size() == 0) {
							System.err.println("Please adding course offering first");
							break;
						}
						int leifcontinue = 0;
						Course cour = null;
						CourseOffering co = null;
						// Finding course
						do {
							System.out.println("Please enter courseId: ");
							String cId = scanner.nextLine();
							for (int i = 0; i < courses.size(); i++) {
								if (courses.get(i).getColumn("courseId").equals(cId)) {
									cour = courses.get(i);
									break;
								} 
							}
							if (cour == null) {
								System.err.println("Course does not exist");
								try {
									Thread.sleep(3);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println("Want to continue search course?[Y:1, N:2]");
								leifcontinue = scanner.nextInt();
								scanner.nextLine();
							}
						} while (leifcontinue == 1);
						if(leifcontinue == 2) break;
						// Finding this course's course offering
						do {
							for (int i = 0; i < cos.size(); i++) {
								if (cos.get(i).getColumn("courseId").equals(cour.getColumn("id"))) {
									co = cos.get(i);
									break;
								}
							}
							if (co == null) {
								System.err.println("Course offering does not exist");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {
									// TODO Auto-generated catch block
									ie.printStackTrace();
								}
								System.out.println("Want to continue search course offering?[Y:1, N:2]");
								leifcontinue = scanner.nextInt();
								scanner.nextLine();
							}
						} while (leifcontinue == 1);
						if(leifcontinue == 2) break;
						// Adding Lesson
						int maxNum = (int) co.getColumn("maxNum");
						// For computing tutorial student number						
						int sumVenNum = 0;
						int day = -1;
						double startHr = -1.0;
						double dur = -1.0;
						do {
							try {
								System.out.println("Please enter lesson's day(1 - 7) : ");
								day = scanner.nextInt();
								scanner.nextLine();
								if (day < 1 || day >7) {
									throw new InsertFailedException("Wrong day!");
								}
								System.out.println("Please enter start hour(0.0-23.59) : ");
								startHr = scanner.nextDouble();
								scanner.nextLine();
								if (startHr < 0.0 || startHr > 23.59) {
									throw new InsertFailedException("Wrong time!");
								}
								System.out.println("Please enter duration hour(1.0 - 3.0) : ");
								dur = scanner.nextDouble();
								scanner.nextLine();
								if (dur < 1.0 || dur > 3.0) {
									throw new InsertFailedException("Wrong duration time!");
								}
								
							}
							// TODO debug 
							catch (InputMismatchException ime) {
								System.err.println("Wrong input" + ime);
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {
									// TODO Auto-generated catch block
									ie.printStackTrace();
								}
							}
							catch (Exception e) {
								System.err.println("Wrong input" + e);
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {
									// TODO Auto-generated catch block
									ie.printStackTrace();
									
								}
								System.out.println("Want to continue to add lesson? [Y:1, N:2]");
								coifContinue = scanner.nextInt();
								scanner.nextLine();
								continue;
							}
							// If wrong input or error is catched, break loop
							if (day == -1 || dur == -1.0 ||  dur == -1.0) {
								System.out.println("Want to input again? [Y:1, N:2]");
								coifContinue = scanner.nextInt();
								scanner.nextLine();
								if (coifContinue == 1) {
									continue;
								} else {
									break;
								}
							}
							System.out.println("Please enter lesson type : [Lecture: 1, Tutorial: 2]");
							int type = scanner.nextInt();
							scanner.nextLine();
							int lvIfcontinue = 0;
							try {
								Lesson lesson = addLesson(day, startHr, dur, type, co);
								System.out.println("Adding lesson successful");
								// Setting Venue								
								do {
									ArrayList<Venue> venues = new ArrayList<>();
									Venue venue = null;
									try {
										venues = Venue.venues(); 
									} catch (Exception e) {
										// if break raise lesson id for adding venue to lesson manually
										System.err.println("No venue, please add venue first, lesson id for adding venue(importent): " + lesson.getColumn("id"));
										break;
									}
									System.out.println("Please enter location");
									String location = scanner.nextLine();
									if (venues.size() == 0) {
										System.err.println("No venue, please add venue first");
										break;
									}
									
									for (int i = 0; i < venues.size(); i++) {
										if(venues.get(i).getColumn("location").equals(location)) {
											venue = venues.get(i);
											break;
										}
									}
									try {
										switch (type) {
										// Lecture: 1, Tutorial: 2									
										case 1:
											if ((int) venue.getColumn("capacity") > maxNum) {
												throw new CapacityException("Venue is too small for the lesson!");
											} else {
												if(venue.addLesson(lesson)) {
													System.out.println("Adding venue successful");
												}
											}
											break;
										case 2:
											int canAddNum = maxNum - sumVenNum;
											if ((int) venue.getColumn("capacity") > canAddNum) {
												throw new CapacityException("Venue is too small for the lesson!");
											} else {
												if (venue.addLesson(lesson)) {
													sumVenNum += (int) venue.getColumn("capacity");
													System.out.println("Adding venue successful");
												}
											}
											break;
										}
									} catch (CapacityException ce) {
										System.err.println(ce);
										
									} catch (Exception e) {
										System.err.println("Something wrong: " + e);
									}
									try {
										Thread.sleep(3);
									} catch (Exception e) {
									}
									
									System.out.println("Want to continue to add venue to lesson(lesson id: " + lesson.getColumn("id") + ")? [Y: 1, N: 2]");
									lvIfcontinue = scanner.nextInt();
									scanner.nextLine();
									
								} while (lvIfcontinue ==1 );
							} catch (Exception e) {
								System.err.println("Adding lesson failed" + e);
							}
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {
								// TODO Auto-generated catch block
								ie.printStackTrace();
							}
							System.out.println("Want to continue to add lesson? [Y:1, N:2]");
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
				//Adding staff				
				int stifcontinue = 0;
				do {
					ArrayList<Staff> staffs = new ArrayList<>();
					try {
						staffs = Staff.staffs();
					} catch (Exception e) {
						System.err.println("No staffs! " + e);
						break;					
					}
					
					System.out.println("please enter eNo :");
					String eNo = scanner.nextLine();
					try {
						for (int i = 0; i < staffs.size(); i++) {
							if (staffs.get(i).getColumn("eNo").equals(eNo)) {
								throw new PreExistException("Staff already exist");
							}
						}
					} catch (Exception e) {
						System.err.println(e);
						stifcontinue = 1;
						continue;
					}
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
				// Adding venue
				int veifcontinue = 0;
				do {
					ArrayList<Venue> venues = new ArrayList<>();
					try {
						venues = Venue.venues();
					} catch (Exception e) {
						System.err.println("No venues! " + e);
						break;
					}
					
					System.out.println("Please enter location :");
					String location = scanner.nextLine();
					try {
						for (int i = 0; i < venues.size(); i++) {
							if (venues.get(i).getColumn("location").equals(location)) {
								throw new PreExistException("Location already exist");
							}
						}
					} catch (Exception e) {
						System.err.println(e);
						veifcontinue = 1;
						continue;
					}
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
				// Assign Staff
				ArrayList<Staff> staffs = new ArrayList<>();
				ArrayList<Course> courses = new ArrayList<>();
				ArrayList<CourseOffering> cos = new ArrayList<>();
				try {
					staffs = Staff.staffs();
				} catch (Exception e) {
					System.err.println("Cant find staffs! Please adding staffs first!");
					break;
				}
				if (staffs.size() == 0) break;
				int astIfcontinue = 0;
				Staff staff = null;
				do {
					System.out.println("Please enter staff eNo : ");
					String eNo = scanner.nextLine();
					for (int i = 0; i < staffs.size(); i++) {
						if (staffs.get(i).getColumn("eNo").equals(eNo)) {
							staff = staffs.get(i);
							break;
						}
					}
					if (staff == null) {
						System.err.println("Staff does not exist");
						try {
							Thread.sleep(3);
						} catch (InterruptedException ie) {
							// TODO Auto-generated catch block
							ie.printStackTrace();
						}
						System.out.println("Want to continue search staff?[Y:1, N:2]");
						astIfcontinue = scanner.nextInt();
						scanner.nextLine();
					}
				} while (astIfcontinue == 1);
				if(astIfcontinue == 2) break;
				// Finding courses				
				try {
					courses = Course.courses();
				} catch (SQLException se) {
					System.err.println("Cant find courses! " + se);
					System.err.println("Please adding courses first");
					break;
				}
				if(courses.size() == 0) {
					System.err.println("Please adding courses first");
					break;
				}
				try {
					cos = CourseOffering.cos();
				} catch (SQLException se) {
					System.err.println("Cant find courses! " + se);
					System.err.println("Please adding course offering first");
					break;						
				}
				if (cos.size() == 0) {
					System.err.println("Please adding course offering first");
					break;
				}
			
				Course cour = null;
				CourseOffering co = null;
				// Finding course
				do {
					System.out.println("Please enter courseId: ");
					String cId = scanner.nextLine();
					for (int i = 0; i < courses.size(); i++) {
						if (courses.get(i).getColumn("courseId").equals(cId)) {
							cour = courses.get(i);
							break;
						} 
					}
					if (cour == null) {
						System.err.println("Course does not exist");
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("Want to continue search course?[Y:1, N:2]");
						astIfcontinue = scanner.nextInt();
						scanner.nextLine();
					}
				} while (astIfcontinue == 1);
				if(astIfcontinue == 2) break;
				// Finding this course's course offering
				do {
					for (int i = 0; i < cos.size(); i++) {
						if (cos.get(i).getColumn("courseId").equals(cour.getColumn("id"))) {
							co = cos.get(i);
							break;
						}
					}
					if (co == null) {
						System.err.println("Course offering does not exist");
						try {
							Thread.sleep(3);
						} catch (InterruptedException ie) {
							// TODO Auto-generated catch block
							ie.printStackTrace();
						}
						System.out.println("Want to continue search course offering?[Y:1, N:2]");
						astIfcontinue = scanner.nextInt();
						scanner.nextLine();
					}
				} while (astIfcontinue == 1);
				if(astIfcontinue == 2) break;
				// List course offering's lessons
				ArrayList<Lesson> lessons = new ArrayList<>();
				try {
					lessons = co.lessons();
				} 
				catch (Exception e) {
					System.err.println("Cant find lessons! Please adding lesson first!");
					break;	
				}
				try {
					Thread.sleep(3);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					ie.printStackTrace();
				}
				int selIfcontinue = 0;
				Lesson lesson = null;
				do {
					do {
						System.out.println("Please choose your lesson :");
						for (int i = 0; i < lessons.size(); i++) {
							String info = ((String) cour.getColumn("name")) + ": " + 
									((String) lessons.get(i).getColumn("type")) + ": Day: " + ((int) lessons.get(i).getColumn("day")) +
									", Start: " + ((Double) lessons.get(i).getColumn("startHour"));
							System.out.println(info + " -- Selection: " + (i+1));
						}
						int leNo = scanner.nextInt();
						scanner.nextLine();
						
						switch (leNo) {
						case 1:
							lesson = lessons.get(1);
							break;
						case 2:
							lesson = lessons.get(2);
							break;
						default:
							System.out.println("No choice, back to select lesson");
							selIfcontinue = 1;
							break;
						}
					} while (selIfcontinue == 1);
					
					if (lesson == null) {
						System.err.println("Lesson does not exist");
						try {
							Thread.sleep(3);
						} catch (InterruptedException ie) {
							// TODO Auto-generated catch block
							ie.printStackTrace();
						}
						System.out.println("Want to continue search lessons? [Y:1, N:2]");
						selIfcontinue = scanner.nextInt();
						scanner.nextLine();
					}
				} while (selIfcontinue == 1);
				if (selIfcontinue == 2) break;
				do {
					try {
						if (setStaff(staff, lesson)) {
						System.out.println("Assigning staff successful");	
						}
					} catch (Exception e) {
						System.err.println("Assigning staff failed");
						try {
							Thread.sleep(3);
						} catch (InterruptedException ie) {
							// TODO Auto-generated catch block
							ie.printStackTrace();
						}
					}
					System.out.println("Want to continue to add staff? [Y:1, N:2]");
					selIfcontinue = scanner.nextInt();
					scanner.nextLine();
				} while (selIfcontinue == 1);
				try {
					Thread.sleep(5);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					ie.printStackTrace();
				}
				break;
			case 5:
				//Enrol Student in Course Offering				
				break;
			case 6:
				//Register Student in Tutorial
				break;
			case 7:
				//TODO Adding venue manually
				break;
			case 8:
				return;
			default:
				break;
			}
		} while (!(opToken == 8 || opToken == 0));
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
	
	private static Lesson addLesson(int day, double startHr, double dur, int type, CourseOffering co) throws Exception {
		Lesson lesson = null;
		switch (type) {
		case 1:
			lesson = co.addLecture(day, startHr, dur);
			break;
		case 2:
			lesson = co.addTutorial(day, startHr, dur);
			break;
		default:
			throw new InsertFailedException("Wrong type");
		}
		return lesson;
	}
	
	private static boolean setStaff(Staff staff, Lesson lesson) throws InsertFailedException {
		return staff.assign(lesson);
	}
}
