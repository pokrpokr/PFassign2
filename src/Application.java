import java.sql.SQLException;
import java.util.*;
import app.*;
import cu_exceptions.*;

public class Application {

	public static void main(String[] args){
		System.out.println("Welcome to enrolment system");
			
		Scanner scanner = new Scanner(System.in);
		Customers currentUser = null;
		currentUser = logIn(scanner, currentUser);
		if (currentUser == null) {
			System.out.println("Do you want to signup ? [Y:1, N:2]");
			int signToken = 0;
			
			do {
				try {
					signToken = 0;
					signToken = scanner.nextInt();
					scanner.nextLine();
				} catch (InputMismatchException ime) {
					System.err.println("Wrong input");
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {}
					System.out.println("Try again: ");
					signToken = -1;
					scanner.nextLine();
				}
			} while (signToken == -1);
			
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
			studentMenu(scanner, currentUser);
			break;
		case "Staff":
			staffMenu(scanner, currentUser);
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
		System.out.println("Have a good day " + currentUser.getColumn("userNum"));
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
			int type = 0;
			try {
				type = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
				System.err.println("Wrong input");
				try {
					Thread.sleep(3);
				} catch (InterruptedException ie) {}
				scanner.nextLine();
			}
			 
			while (!(type == 1 || type == 2)) {
				System.err.println("Type must be 'student' or 'staff'!");
				try {
					Thread.sleep(3);
				} catch (InterruptedException e1) {
				}
				System.out.println("Please enter your account type :");
				try {
					type = scanner.nextInt();
					scanner.nextLine();
				} catch (Exception e) {}
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
					ArrayList<Staff> staffs = new ArrayList<>();
					try {
						staffs = Staff.staffs();
					} catch (SQLException se) {
						throw se;
					}
					if (staffs.size() == 0) {
						throw new NoResultException("Please add staff first");
					}
					for (int i = 0; i < staffs.size(); i++) {
						if (!signNum.equals(staffs.get(i).getColumn("eNo"))) throw new NoResultException("Staff does not exist"); 
					}
					break;
				default:
					throw new InsertFailedException("Wrong type!");
				}
				currentUser = signup(signNum, encodePassword, userType);
			}
			catch (NoResultException nre) {
				System.err.println(nre);
			}
			catch (SQLException se) {
				System.err.println("SQL wrong");
			}
			catch (InsertFailedException ie) {
				System.out.println("Signup user failed");				
			} 
			catch (Exception e) {
				System.out.println(e);
			}
		}
		return currentUser;
	}

	private static void adminMenu(Scanner scanner) {
		int opToken = 0;
		do {
			String[] choices = new String[] {"Add Course", "Add Staff", "Add Venue", "Assign Staff to Lesson", "Assign venue manually", "Exit system"};
			for (int i = 0; i < choices.length; i++) {
				System.out.println(choices[i]+": "+ (i+1));
			}
			System.out.println("Please enter your operation :");
			do {
				try {
					opToken = scanner.nextInt();
					scanner.nextLine();
				} catch (InputMismatchException ime) {
					System.err.println("Wrong input");
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {}
					System.out.println("Try again: ");
					opToken = -1;
					scanner.nextLine();
				}
			} while (opToken == -1);
			
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
					do {
						try {
							cOperation = scanner.nextInt();
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException e) {}
							System.out.println("Try again: ");
							cOperation = -1;
							scanner.nextLine();
						}
					} while (cOperation == -1);
				
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
							do {
								try {
									ifContinue = scanner.nextInt();
									scanner.nextLine();
								} catch (InputMismatchException ime) {
									System.err.println("Wrong input");
									try {
										Thread.sleep(3);
									} catch (InterruptedException e) {}
									System.out.println("Try again: ");
									ifContinue = -1;
									scanner.nextLine();
								}
							} while (ifContinue == -1);
							
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
							coifContinue = 0;
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
								do {
									try {
										coifContinue = scanner.nextInt();
										scanner.nextLine();
									} catch (InputMismatchException ime) {
										System.err.println("Wrong input");
										try {
											Thread.sleep(3);
										} catch (InterruptedException e) {}
										System.out.println("Try again: ");
										coifContinue = -1;
										scanner.nextLine();
									}
								} while (coifContinue == -1);
							}
							if (coifContinue == 1) continue;
							if (coifContinue == 2) break;
							// Input CourseOffering args						
						
							System.out.println("Please enter maxNum: ");
							int maxNum = 0;
							do {
								try {
									maxNum = scanner.nextInt();
									scanner.nextLine();
								} catch (InputMismatchException ime) {
									System.err.println("Wrong input");
									try {
										Thread.sleep(3);
									} catch (InterruptedException e) {}
									System.out.println("Try again: ");
									maxNum = -1;
									scanner.nextLine();
								}
							} while (maxNum == -1);
							 
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
							do {
								try {
									coifContinue = scanner.nextInt();
									scanner.nextLine();
								} catch (InputMismatchException ime) {
									System.err.println("Wrong input");
									try {
										Thread.sleep(3);
									} catch (InterruptedException e) {}
									System.out.println("Try again: ");
									coifContinue = -1;
									scanner.nextLine();
								}
							} while (coifContinue == -1);
							
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
							leifcontinue = 0;
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
								do {
									try {
										leifcontinue = scanner.nextInt();
										scanner.nextLine();
									} catch (InputMismatchException ime) {
										System.err.println("Wrong input");
										try {
											Thread.sleep(3);
										} catch (InterruptedException e) {}
										System.out.println("Try again: ");
										leifcontinue = -1;
										scanner.nextLine();
									}
								} while (leifcontinue == -1);
							
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
								do {
									try {
										leifcontinue = scanner.nextInt();
										scanner.nextLine();
									} catch (InputMismatchException ime) {
										System.err.println("Wrong input");
										try {
											Thread.sleep(3);
										} catch (InterruptedException e) {}
										System.out.println("Try again: ");
										leifcontinue = -1;
										scanner.nextLine();
									}
								} while (leifcontinue == -1);

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
							catch (InputMismatchException ime) {
								System.err.println("Wrong input");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {}
								scanner.nextLine();
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
								do {
									try {
										coifContinue = scanner.nextInt();
										scanner.nextLine();
									} catch (InputMismatchException ime) {
										System.err.println("Wrong input");
										try {
											Thread.sleep(3);
										} catch (InterruptedException ie) {}
										System.out.println("Try again: ");
										coifContinue = -1;
										scanner.nextLine();
									}
								} while (coifContinue == -1);
								
								continue;
							}
							// If wrong input or error is catched, break loop
							if (day == -1 || dur == -1.0 ||  dur == -1.0) {
								System.out.println("Want to input again? [Y:1, N:2]");
								do {
									try {
										coifContinue = scanner.nextInt();
										scanner.nextLine();
									} catch (InputMismatchException ime) {
										System.err.println("Wrong input");
										try {
											Thread.sleep(3);
										} catch (InterruptedException ie) {}
										System.out.println("Try again: ");
										coifContinue = -1;
										scanner.nextLine();
									}
								} while (coifContinue == -1);
								
								if (coifContinue == 1) {
									continue;
								} else {
									break;
								}
							}
							System.out.println("Please enter lesson type : [Lecture: 1, Tutorial: 2]");
							int type = -1;
							do {
								try {
									type = scanner.nextInt();
									scanner.nextLine();
								} catch (InputMismatchException ime) {
									System.err.println("Wrong input");
									try {
										Thread.sleep(3);
									} catch (InterruptedException ie) {}
									System.out.println("Try again: ");
									type = -1;
									scanner.nextLine();
								}
							} while (type == -1);
							
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
										if (venue == null) {
											throw new NoResultException("No location!");
										} 
										switch (type) {
										// Lecture: 1, Tutorial: 2									
										case 1:
											int venueCap = (int) venue.getColumn("capacity");
											if ( venueCap < maxNum) {
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
												throw new CapacityException("Venue is too big for the lesson!");
											} else {
												if (venue.addLesson(lesson)) {
													sumVenNum += (int) venue.getColumn("capacity");
													System.out.println("Adding venue successful");
												}
											}
											break;
										}
									} catch (NoResultException nre) {
										System.err.println(nre);
									} catch (CapacityException ce) {
										System.err.println(ce);
										
									} catch (Exception e) {
										System.err.println("Something wrong: " + e);
									}
									try {
										Thread.sleep(3);
									} catch (Exception e) {
									}
									
									System.out.println("Want to continue to add venue to lesson(lesson id: " +((Lesson) lesson).getColumn("id") + ")? [Y: 1, N: 2]");
									do {
										try {
											lvIfcontinue = scanner.nextInt();
											scanner.nextLine();
										} catch (InputMismatchException ime) {
											System.err.println("Wrong input");
											try {
												Thread.sleep(3);
											} catch (InterruptedException ie) {}
											System.out.println("Try again: ");
											lvIfcontinue = -1;
											scanner.nextLine();
										}
									} while (lvIfcontinue == -1);
									
								} while (lvIfcontinue == 1 );
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
							do {
								try {
									coifContinue = scanner.nextInt();
									scanner.nextLine();
								} catch (InputMismatchException ime) {
									System.err.println("Wrong input");
									try {
										Thread.sleep(3);
									} catch (InterruptedException ie) {}
									System.out.println("Try again: ");
									coifContinue = -1;
									scanner.nextLine();
								}
							} while (coifContinue == -1);
							
						} while (coifContinue == 1);
						break;
					case 4:
						break;
					default:
						break;
					}
					System.out.println("Want to back to add course? [Y:0, N:3]");
					do {
						try {
							cOperation = scanner.nextInt();
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {}
							System.out.println("Try again: ");
							cOperation = -1;
							scanner.nextLine();
						}
					} while (cOperation == -1);
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
					do {
						try {
							stifcontinue = scanner.nextInt();
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {}
							System.out.println("Try again: ");
							stifcontinue = -1;
							scanner.nextLine();
						}
					} while (stifcontinue == -1);
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
					do {
						try {
							veifcontinue = scanner.nextInt();
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {}
							System.out.println("Try again: ");
							veifcontinue = -1;
							scanner.nextLine();
						}
					} while (veifcontinue == -1);
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
						do {
							try {
								astIfcontinue = scanner.nextInt();
								scanner.nextLine();
							} catch (InputMismatchException ime) {
								System.err.println("Wrong input");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {}
								System.out.println("Try again: ");
								astIfcontinue = -1;
								scanner.nextLine();
							}
						} while (astIfcontinue == -1);
					
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
						do {
							try {
								astIfcontinue = scanner.nextInt();
								scanner.nextLine();
							} catch (InputMismatchException ime) {
								System.err.println("Wrong input");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {}
								System.out.println("Try again: ");
								astIfcontinue = -1;
								scanner.nextLine();
							}
						} while (astIfcontinue == -1);
						
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
						do {
							try {
								astIfcontinue = scanner.nextInt();
								scanner.nextLine();
							} catch (InputMismatchException ime) {
								System.err.println("Wrong input");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {}
								System.out.println("Try again: ");
								astIfcontinue = -1;
								scanner.nextLine();
							}
						} while (astIfcontinue == -1);
						
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
						if (lessons.size() == 0) {
							System.err.println("No lessons!");
							try {
								Thread.sleep(3);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							selIfcontinue = 2;
							break;
						}
						System.out.println("Please choose your lesson :");
						for (int i = 0; i < lessons.size(); i++) {
							String info = ((String) cour.getColumn("name")) + ": " + 
									((String) lessons.get(i).getColumn("type")) + ": Day: " + ((int) lessons.get(i).getColumn("day")) +
									", Start: " + ((Double) lessons.get(i).getColumn("startHour"));
							System.out.println(info + " -- Selection: " + (i+1));
						}
						int leNo = -1;
						do {
							try {
								leNo = scanner.nextInt() - 1;
								scanner.nextLine();
							} catch (InputMismatchException ime) {
								System.err.println("Wrong input");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {}
								System.out.println("Try again: ");
								leNo = -1;
								scanner.nextLine();
							}
						} while (leNo == -1);
						
						try {
							lesson = lessons.get(leNo);
						} catch (Exception e) {
							System.out.println("Something wrong, back to select lesson");
							selIfcontinue = 1;
							break;
						}
					} while (selIfcontinue == 1);
					
					if (selIfcontinue == 2) break;
					
					if (lesson == null) {
						System.err.println("Lesson does not exist");
						try {
							Thread.sleep(3);
						} catch (InterruptedException ie) {
							// TODO Auto-generated catch block
							ie.printStackTrace();
						}
						System.out.println("Want to continue search lessons? [Y:1, N:2]");
						do {
							try {
								selIfcontinue = scanner.nextInt();
								scanner.nextLine();
							} catch (InputMismatchException ime) {
								System.err.println("Wrong input");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {}
								System.out.println("Try again: ");
								selIfcontinue = -1;
								scanner.nextLine();
							}
						} while (selIfcontinue == -1);
						
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
					do {
						try {
							selIfcontinue = scanner.nextInt();
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {}
							System.out.println("Try again: ");
							selIfcontinue = -1;
							scanner.nextLine();
						}
					} while (selIfcontinue == -1);
					
				} while (selIfcontinue == 1);
				try {
					Thread.sleep(5);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					ie.printStackTrace();
				}
				break;
			case 5:
				//TODO Adding venue manually
				System.out.println("Not finished");
				break;
			case 6:
				return;
			default:
				break;
			}
		} while (!(opToken == 6 || opToken == 0));
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

	private static void studentMenu(Scanner scanner, Customers student) {
		int stIfcontinue = 0;
		do {
			System.out.println("Welcome " + student.getColumn("userNum"));
			String[] operations = new String[] {"Enrol Course Offering", "Register Tutorial", "TimeTable", "Logout"};
			for (int i = 0; i < operations.length; i++) {
				System.out.println(operations[i] + ": " + (i+1));
			}
			
			int op = 0;
			try {
				op = scanner.nextInt();
				scanner.nextLine();
			} catch (InputMismatchException ime) {
				System.err.println("Wrong input");
				try {
					Thread.sleep(3);
				} catch (InterruptedException ie) {}
				System.out.println("Try again: ");
				op = 0;
				scanner.nextLine();
			}
			
			switch (op) {
			case 1:
				//Enrol CO
				// Do-While symbol
				int crtEnrolIfcontinue = 0;
				//Finding can be enroled courses(cos)				
				ArrayList<Course> courses = new ArrayList<>();
				ArrayList<CourseOffering> cos = new ArrayList<>();
				
				try {
					courses = Course.courses();
					cos = CourseOffering.cos();
					if (courses.size() == 0 || cos.size() ==0) {
						throw new NoResultException("No CourseOffering can be enroled");
					}
				}
				catch (NoResultException | SQLException e) {
					System.err.println(e);
					// Waitting err finish output
					try {
						Thread.sleep(3);
					} catch (InterruptedException ie) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					crtEnrolIfcontinue = 2;
					break;
				}
				if (crtEnrolIfcontinue == 2) break;
				
				do {
					// List Course Offering Infos		
					ArrayList<String> canEnrol = new ArrayList<>();
					for (int i = 0; i < courses.size(); i++) {
						for (int j = 0; j < cos.size(); j++) {
							// Format co infos						
							if (cos.get(j).getColumn("courseId").equals(courses.get(i).getColumn("id"))) {
								Lesson lecture = null;
								try {
									lecture = cos.get(j).lecture();
								} catch (NoResultException | SQLException e) {
									continue;
								}
								if (lecture == null) continue;
								
								String info ="Course Info:" + courses.get(i).getColumn("name") + ", CourseId: " + courses.get(i).getColumn("courseId") +
										", Lecture time(Day-StartHour): " + lecture.getColumn("day") + "-" + lecture.getColumn("startHour") +
										", CO number(select): " + (j+1);
							
								canEnrol.add(info);
								break;
							}
						}
					}
					
					//List 
					System.out.println("Please choose Course Offering: ");
					for (int i = 0; i < canEnrol.size(); i++) {
						System.out.println(canEnrol.get(i));
					}
					int offeringNum = -1;
					do {
						try {
							offeringNum = scanner.nextInt() - 1;
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {}
							System.out.println("Try again: ");
							offeringNum = -1;
							scanner.nextLine();
						}
					} while (offeringNum == -1);
					
					
					CourseOffering selectedOffering = cos.get(offeringNum);
					Lesson selectedLecture = null;
					try {
						selectedLecture = selectedOffering.lecture();
					} catch (NoResultException | SQLException e2) {
						// Do nothing due to it was catched at the above step
					}
					try {
						Enrolment.checkClash(student, selectedOffering);
						student.enrolCourseOffering(selectedOffering, selectedLecture);
					}catch (ClashException ce) {
						System.err.println(ce);
						try {
							Thread.sleep(3);
						} catch (InterruptedException ie) {
							// TODO Auto-generated catch block
							ie.printStackTrace();
						}
						System.out.println("Re enrol COs? [Y:1, N:2]");
						do {
							try {
								crtEnrolIfcontinue = scanner.nextInt();
								scanner.nextLine();
							} catch (InputMismatchException ime) {
								System.err.println("Wrong input");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {}
								System.out.println("Try again: ");
								crtEnrolIfcontinue = -1;
								scanner.nextLine();
							}
						} while (crtEnrolIfcontinue == -1);
						
						if (crtEnrolIfcontinue == 1) {
							continue;
						}else {
							break;
						}
					} 
					catch (Exception e) {
						System.err.println(e);
						try {
							Thread.sleep(3);
						} catch (InterruptedException ie) {
							// TODO Auto-generated catch block
							ie.printStackTrace();
						}
						break;
					}
					System.out.println("Enrolling sucessful");
					System.out.println("Continue enrol COs? [Y:1, N:2]");
					do {
						try {
							crtEnrolIfcontinue = scanner.nextInt();
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {}
							System.out.println("Try again: ");
							crtEnrolIfcontinue = -1;
							scanner.nextLine();
						}
					} while (crtEnrolIfcontinue == -1);
					
				} while (crtEnrolIfcontinue == 1);
				break;
			case 2:
				//Enrol Tutorial
				int enIfcontinue = 0;
				//Checking student whether has enrolments
				ArrayList<Enrolment> enrolments = new ArrayList<>();
				do {
					
					try {
						enrolments = student.enrolments();
					}
					catch (NoResultException nre) {
						System.err.println(nre);
						// Waitting err finish output
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
					catch (SQLException e) {
						System.err.println("Something wrong: " + e);
						try {
							Thread.sleep(3);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.out.println("Try again? [Y: 1, N: 2]");
						do {
							try {
								enIfcontinue = scanner.nextInt();
								scanner.nextLine();
							} catch (InputMismatchException ime) {
								System.err.println("Wrong input");
								try {
									Thread.sleep(3);
								} catch (InterruptedException ie) {}
								System.out.println("Try again: ");
								enIfcontinue = -1;
								scanner.nextLine();
							}
						} while (enIfcontinue == -1);
						
						break;
					}
				} while (enIfcontinue == 1);
				// 2 Exit system
				if (enIfcontinue == 2) break; 
				
				//Enrol preparation
				do {
					//List student's enrolments
					if (enrolments.size() == 0) {
						System.err.println("No enrolments");
						try {
							Thread.sleep(3);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						enIfcontinue = 2;
						break;
					}
					
					//Finding Tutorial
					Enrolment el = null;
					CourseOffering co = null;
					ArrayList<Lesson> lessons = new ArrayList<>();
					for (int i = 0; i < enrolments.size(); i++) {
						 el = enrolments.get(i);
						try {
							co = el.getCourseOffering();
							lessons = co.lessons();
						} catch (NoResultException nre) {
							System.err.println(nre);
							try {
								Thread.sleep(3);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							break;
						} catch (SQLException se) {
							System.err.println(se);
							try {
								Thread.sleep(3);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							break;
						}
					}
					ArrayList<Tutorial> tus = new ArrayList<>();
					for (int i = 0; i < lessons.size(); i++) {
						if (lessons.get(i).getColumn("type").equals("Tutorial")) {
								tus.add((Tutorial) lessons.get(i));
							}
						}
					if (el == null || co == null || lessons.size() == 0 || tus.size() ==0) {
						System.err.println("Can't find tutorial!");
						try {
							Thread.sleep(3);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						enIfcontinue = 2;
						break;
					}
					//Choose tutorial
					System.out.println("Choose tutorial: ");
					for (int i = 0; i < tus.size(); i++) {
						String infos = "Day: " + tus.get(i).getColumn("day") + " Start: " + tus.get(i).getColumn("startHour") + " -- Selection: " + (i + 1);
						System.out.println(infos);
					}
					int tuNum = -1;
					do {
						try {
							tuNum = scanner.nextInt() - 1;
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {}
							System.out.println("Try again: ");
							tuNum = -1;
							scanner.nextLine();
						}
					} while (tuNum == -1);
					
					Lesson lesson = tus.get(tuNum);
					int enroledNum = Enrolment.enrolNum(lesson);
					if (enroledNum == -1) {
						System.err.println("Something wrong");
						try {
							Thread.sleep(3);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						enIfcontinue = 2;
						break;
					}
					int maxNum = (int) co.getColumn("maxNum");
					
					if ((maxNum - enroledNum) < 0) {
						System.err.println("Lesson is full! Can not enrol!");
						try {
							Thread.sleep(3);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.out.println("Back to select lessons");
						enIfcontinue = 1;
						break;
					}
					// Enrol tutorial
					try {
						if(student.enrolTutorial(el, lesson)) {
							System.out.println("Enrolling successful");
						}
					} catch (Exception e) {
						System.err.println("Enrolling failed");
						try {
							Thread.sleep(3);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					System.out.println("Want to continue to enrol tutorial?: [Y:1, N:2]");
					do {
						try {
							enIfcontinue = scanner.nextInt();
							scanner.nextLine();
						} catch (InputMismatchException ime) {
							System.err.println("Wrong input");
							try {
								Thread.sleep(3);
							} catch (InterruptedException ie) {}
							System.out.println("Try again: ");
							enIfcontinue = -1;
							scanner.nextLine();
						}
					} while (enIfcontinue == -1);
					
				} while (enIfcontinue == 1);
				stIfcontinue = 1;
				break;
			case 3:
				student.getTimeTable();
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Wanna go back?[Y:1, N:2]");
				do {
					try {
						stIfcontinue = scanner.nextInt();
						scanner.nextLine();
					} catch (InputMismatchException ime) {
						System.err.println("Wrong input");
						try {
							Thread.sleep(3);
						} catch (InterruptedException ie) {}
						System.out.println("Try again: ");
						stIfcontinue = -1;
						scanner.nextLine();
					}
				} while (stIfcontinue == -1);
				
				break;
			case 4:
				stIfcontinue = 2;
				break;
			default:
				stIfcontinue = 1;
				break;
			}
		} while (stIfcontinue == 1);
	}

	private static void staffMenu(Scanner scanner, Customers staff) {
		System.out.println("Welcome " + staff.getColumn("userNum"));
		System.out.println("List Time Table: 1 ");
		System.out.println("Logout:          2 ");
		int opNum = -1;
		do {
			try {
				opNum = scanner.nextInt() - 1;
				scanner.nextLine();
			} catch (InputMismatchException ime) {
				System.err.println("Wrong input");
				try {
					Thread.sleep(3);
				} catch (InterruptedException ie) {}
				System.out.println("Try again: ");
				opNum = -1;
				scanner.nextLine();
			}
		} while (opNum == -1);
		
		switch (opNum) {
		case 1:
			ArrayList<Staff> staffs = new ArrayList<>();
			Staff stf = null;
			try {
				staffs = Staff.staffs();
			} catch (Exception e) {
				// Do nothing, if you can login as staff, staff must exist
			}
			for (int i = 0; i < staffs.size(); i++) {
				if (staff.getColumn("userNum").equals(staffs.get(i).getColumn("eNo"))) {
					stf = staffs.get(i);
					break;
				}
			}
			stf.getTimetable();
			break;
		case 2:
			break;
		default:
			break;
		}
	}
}
