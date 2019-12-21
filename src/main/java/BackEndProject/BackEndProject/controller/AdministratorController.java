package BackEndProject.BackEndProject.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import BackEndProject.BackEndProject.dto.TeacherDto;
import BackEndProject.BackEndProject.dto.UserDto;
import BackEndProject.BackEndProject.email.EmailObject;
import BackEndProject.BackEndProject.entites.AdministratorEntity;
import BackEndProject.BackEndProject.entites.ClassEntity;
import BackEndProject.BackEndProject.entites.ERole;
import BackEndProject.BackEndProject.entites.GradingEntity;
import BackEndProject.BackEndProject.entites.ParentEntity;
import BackEndProject.BackEndProject.entites.StudentEntity;
import BackEndProject.BackEndProject.entites.SubjectEntity;
import BackEndProject.BackEndProject.entites.TeacherEntity;
import BackEndProject.BackEndProject.entites.TeachingEntity;
import BackEndProject.BackEndProject.entites.UserEntity;
import BackEndProject.BackEndProject.repositories.AdministratorRepository;
import BackEndProject.BackEndProject.repositories.ClassRepository;
import BackEndProject.BackEndProject.repositories.GradingRepository;
import BackEndProject.BackEndProject.repositories.ParentRepository;
import BackEndProject.BackEndProject.repositories.StudentRepository;
import BackEndProject.BackEndProject.repositories.SubjectRepository;
import BackEndProject.BackEndProject.repositories.TeacherRepository;
import BackEndProject.BackEndProject.repositories.TeachingRepository;
import BackEndProject.BackEndProject.repositories.UserRepository;
import BackEndProject.BackEndProject.service.EmailService;
import BackEndProject.BackEndProject.util.Encryption;
import BackEndProject.BackEndProject.util.RESTError;


@RestController
@RequestMapping(path = "/dnevnik/")
public class AdministratorController {
	
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdministratorRepository administratorRepository;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private ParentRepository parentRepository;
	
	@Autowired
	private SubjectRepository subjectRepository;
	
	@Autowired
	private ClassRepository classRepository;
	
	@Autowired
	private TeachingRepository teachingRepository;
	
	@Autowired
	private GradingRepository gradingRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	

	@RequestMapping(method = RequestMethod.GET, value="/login")
	public UserEntity getLoggedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((User)principal).getUsername();
		UserEntity loggedUser = userRepository.findUserByUsername(username);
		LOGGER.info("LOGGER: Logged user: {}", loggedUser);
		return loggedUser;
	}



	// principal
	@RequestMapping(method = RequestMethod.GET, value="/principal") 
	public String currentUsername (Principal principal) {
		return principal.getName();
	}
	
	//autentication
	@RequestMapping(method = RequestMethod.GET, value = "/authentication")
	public String currentUserName (Authentication authentication) {
		return authentication.getName();
	}
	
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/downloadLogFile")
	public void getLogFile(HttpSession session, HttpServletResponse response) throws Exception {
		
		try {
	        String filePathToBeServed = "C:\\Users\\Vesna Sovilj\\Documents\\workspace-sts-3.9.9.RELEASE\\Project_2\\logs\\spring-boot-logging.log";
	        File fileToDownload = new File(filePathToBeServed);
	        InputStream inputStream = new FileInputStream(fileToDownload);
	        response.setContentType("application/force-download");
	        response.setHeader("Content-Disposition", "attachment; filename=spring-boot-logging.log"); 
	        IOUtils.copy(inputStream, response.getOutputStream());
	        response.flushBuffer();
	        inputStream.close();
	        
	    } catch (Exception e) {
	    	LOGGER.info("Request could not be completed at this moment. Please try again.");
	        e.printStackTrace();
	    }
	}
			
	
	//USERS
	@RequestMapping(method = RequestMethod.GET, value="/user") 
	public ResponseEntity<?> getAllUsers() {
		LOGGER.info("LOGGER: Getting all users");
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
	}
	
	//Pronadji usera i sve njegove podatke
	@RequestMapping(method = RequestMethod.GET, value = "/allusers/{id}")
	public ResponseEntity<?> findAllUsersById(@PathVariable Integer id) {
			for( UserEntity user :  userRepository.findAll()) {
				if (user.getId().equals(id)) {
					StudentEntity student= studentRepository.findByUserId(id);
					if( student.getUser().getId()==id) {
					return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
				}
				}
			return new ResponseEntity<RESTError>(new RESTError(1, "User ID not found"), HttpStatus.NOT_FOUND);
		}
			return null; 
	}

	
	// 
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/user/{id}")
	public ResponseEntity<?> findUserById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Searching for user ID: {}", id);
			for (UserEntity user : userRepository.findAll()) {
				if (user.getId().equals(id)) {
					return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: User ID {} not found", id);
			return new ResponseEntity<RESTError>(new RESTError(1, "User ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//findBy username
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/user/username/{username}")
	public ResponseEntity<?> findUserByUsername(@PathVariable String username) {
		try {
			LOGGER.info("LOGGER: Searching for user with username: {}", username);
			for (UserEntity user : userRepository.findAll()) {
				if (user.getUsername().equals(username)) {
					return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Username {} not found", username);
			return new ResponseEntity<RESTError>(new RESTError(1, "Username not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// findBy role
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/user/role/{role}")
	public ResponseEntity<?> findUserByRole(@PathVariable String role) {
		try {
			LOGGER.info("LOGGER: Checking the existence of the role: {}", role);			
			for (ERole userRole : ERole.values()) {
				if (userRole.toString().equals(role)) {
					LOGGER.info("LOGGER: Role {} exsists. Getting a list of user(s) with requested role", role);
					for (UserEntity user : userRepository.findAll()) {
						if (user.getRole().equals(userRole)) {
							List<UserEntity> users = userRepository.findByRole(userRole);
							if (!users.isEmpty())
								return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
						}
					}
				}
			}
			LOGGER.info("LOGGER: Role {} not found", role);
			return new ResponseEntity<RESTError>(new RESTError(1, "Role not found"), HttpStatus.NOT_FOUND);	
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//update USER password
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value="/user/update/{id}")
	public ResponseEntity<?> updateUserPassword(@PathVariable Integer id, 
											    @RequestParam String oldPassword,
											    @RequestParam String newPassword, 
											    @RequestParam String confirmedPassword) {
		try {			
			
			LOGGER.info("LOGGER: Checking if user ID {} exists", id);
			if (!userRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: User ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "User ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Checking if password is correct");
			if (oldPassword == null || oldPassword.isEmpty() || passwordEncoder.matches(oldPassword, userRepository.findById(id).get().getPassword()) == false) {
				LOGGER.info("LOGGER: Password is not correct");
				return new ResponseEntity<RESTError>(new RESTError(2, "Username and/or Password is not correct"), HttpStatus.BAD_REQUEST);	
			}						
			
			LOGGER.info("LOGGER: Checking if password and confirmed password are the same");
			if (!newPassword.equals(confirmedPassword)) {
				LOGGER.info("LOGGER: Password and confirmed password are not the same");
				return new ResponseEntity<RESTError>(new RESTError(3, "Password and confirmed password must be the same"), HttpStatus.BAD_REQUEST);
			}	
			
			else {
				UserEntity user = userRepository.findById(id).get();
				user.setPassword(Encryption.getPassEncoded(newPassword));
				userRepository.save(user);
				LOGGER.info("LOGGER: Password for user ID {} updated", id);
				return new ResponseEntity<RESTError>(new RESTError(4, "Password for requested user ID updated successfully"), HttpStatus.OK);						
			}
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(5, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Admin i sve vezano za admina
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value="/admin") 
	public ResponseEntity<?> getAllAdministrators() {
		LOGGER.info("LOGGER: Getting all administrators");
		List<AdministratorEntity> administrators = (List<AdministratorEntity>) administratorRepository.findAll();
		return new ResponseEntity<List<AdministratorEntity>>(administrators, HttpStatus.OK);
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/{id}")
	public ResponseEntity<?> findAdministratorById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Searching for administrator ID: {}", id);
			for (AdministratorEntity administrator : administratorRepository.findAll()) {
				if (administrator.getId().equals(id)) {
					return new ResponseEntity<AdministratorEntity>(administrator, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Administrator ID {} not found", id);
			return new ResponseEntity<RESTError>(new RESTError(1, "Administrator ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/user/{userID}")
	public ResponseEntity<?> findAdministratorByUserID(@PathVariable Integer userID) {
		try {
			LOGGER.info("LOGGER: Checking if user ID {} exists", userID);
			for (UserEntity user : userRepository.findAll()) {
				if (user.getId().equals(userID)) {
					LOGGER.info("LOGGER: User ID {} exists. Searching for administrator with requested user ID", userID);
					for (AdministratorEntity administrator : administratorRepository.findAll()) {
						if (administrator.getUser().getId().equals(userID)) {
							return new ResponseEntity<AdministratorEntity>(administrator, HttpStatus.OK);
						}
					}
				}
			} 
			LOGGER.info("LOGGER: Administrator with user ID {} not found", userID);
			return new ResponseEntity<RESTError>(new RESTError(1, "Administrator with requested user ID not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/administrator/last_name/{lastName}")
	public ResponseEntity<?> findAdministrtorByLastName(@PathVariable String lastName) {
		try {
			LOGGER.info("LOGGER: Getting a list of administrator(s) with last name: {}", lastName);
			List<AdministratorEntity> administrators = administratorRepository.findByLastNameIgnoreCase(lastName);
			
			if (!administrators.isEmpty())
				return new ResponseEntity<List<AdministratorEntity>>(administrators, HttpStatus.OK);
			else
				LOGGER.info("LOGGER: Administrator(s) with last name {} not found", lastName);
				return new ResponseEntity<RESTError>(new RESTError(1, "Administrator(s) with requested last name not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/administrator/first_name/{firstName}/and/last_name/{lastName}")
	public ResponseEntity<?> findAdministrtorByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
		try {
			LOGGER.info("LOGGER: Getting a list of administrator(s) with name: {} {}", firstName, lastName);
			List<AdministratorEntity> administrators = administratorRepository.findByFirstNameAndLastNameAllIgnoreCase(firstName, lastName);
			
			if (!administrators.isEmpty())
				return new ResponseEntity<List<AdministratorEntity>>(administrators, HttpStatus.OK);
			else
				LOGGER.info("LOGGER: Administrator(s) with first name {} AND last name {} not found", firstName, lastName);
				return new ResponseEntity<RESTError>(new RESTError(1, "Administrator(s) with requested name not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/administrator/first_name/{firstName}/or/last_name/{lastName}")
	public ResponseEntity<?> findAdministrtorByFirstNameOrLastName(@PathVariable String firstName, @PathVariable String lastName) {
		try {
			LOGGER.info("LOGGER: Getting a list of administrator(s) with first name {} or last name {}", firstName, lastName);
			List<AdministratorEntity> administrators = administratorRepository.findByFirstNameOrLastNameAllIgnoreCase(firstName, lastName);
				
			if (!administrators.isEmpty())
				return new ResponseEntity<List<AdministratorEntity>>(administrators, HttpStatus.OK);
			else
				LOGGER.info("LOGGER: Administrator(s) with first name {} OR last name {} not found", firstName, lastName);
				return new ResponseEntity<RESTError>(new RESTError(1, "Administrator(s) with requested first name or last name not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	
	
	//add USER && ADMINISTRATOR - radi
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/user/addnew")
	public ResponseEntity<?> addNewAdministrator(@RequestParam String username, @RequestParam String password, @RequestParam ERole role,
			@RequestParam String firstname, @RequestParam String lastname) {
				UserEntity user= new UserEntity();
				if (userRepository.findUserByUsername(user.getUsername())!=null) {
					LOGGER.info("LOGGER: Username {} already exists", user.getUsername());
					return new ResponseEntity<RESTError>(new RESTError(1, "Username already exists"), HttpStatus.BAD_REQUEST);
				} else{
				user.setUsername(username);
				user.setPassword(Encryption.getPassEncoded(password));
				user.setRole(role);
				userRepository.save(user);
				
				AdministratorEntity newAdministrator = new AdministratorEntity();
				newAdministrator.setFirstName(firstname);
				newAdministrator.setLastName(lastname);	
				newAdministrator.setUser(user);
				administratorRepository.save(newAdministrator);
				LOGGER.info("LOGGER: Administrator created successfully");
				return new ResponseEntity<RESTError>(new RESTError(2, "Administrator with user account added successsfully"), HttpStatus.CREATED);	
		} 

	}
	
	
	
	//update ADMINISTRATOR
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/admin/update/{id}")
	public ResponseEntity<?> updateAdministratorById(@PathVariable Integer id, 
			@RequestParam String firstName, 
			@RequestParam String lastName
			) {
			
			LOGGER.info("LOGGER: Checking if Administrator ID {} exists", id);
			for (AdministratorEntity administrator : administratorRepository.findAll()) {
				if (administrator.getId().equals(id)) {
					LOGGER.info("LOGGER: Administrator ID {} exists", id);
					administrator.setFirstName(firstName); 
					administrator.setLastName(lastName); 
					administratorRepository.save(administrator);
					LOGGER.info("LOGGER: Administrator ID {} updated", id);
					return new ResponseEntity<RESTError>(new RESTError(1, "Administrator ID updated successsfully"), HttpStatus.OK);
					}
				}
				LOGGER.info("LOGGER: Administrator ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(2, "Administrator ID not found"), HttpStatus.NOT_FOUND);
		}

	
	//delete USER & ADMINISTRATOR
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/delete/{userID}")
	public ResponseEntity<?> deleteAdministratorAndUserByUserId(@PathVariable Integer userID) {
		try { 
			
			LOGGER.info("LOGGER: Checking if user ID {} exists", userID);
			for (UserEntity user : userRepository.findAll()) {
				if (user.getId().equals(userID)) {
					LOGGER.info("LOGGER: User ID {} exists", userID);
					
					LOGGER.info("LOGGER: Checking if user ID {} has role ROLE_ADMINISTRATOR", userID);
					AdministratorEntity administrator = administratorRepository.findByUserId(userID);
					if (administrator==null) {
						LOGGER.info("LOGGER: User ID {} does not have role ROLE_ADMINISTRATOR", userID);
						return new ResponseEntity<RESTError>(new RESTError(1, "User ID does not have role ROLE_ADMINISTRATOR"), HttpStatus.BAD_REQUEST);
					}
					
					LOGGER.info("LOGGER: Administrator with user ID {} found", userID);
					administratorRepository.delete(administrator);
					LOGGER.info("LOGGER: Administrator with user ID {} deleted", userID);	
					
					userRepository.delete(user);
					LOGGER.info("LOGGER: User ID {} deleted", userID);
					return new ResponseEntity<RESTError>(new RESTError(2, "User ID and administrator to whom user ID belongs deleted successsfully"), HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: User ID {} not found", userID);
			return new ResponseEntity<RESTError>(new RESTError(3, "User ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//TEACHERS
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value="/teacher") 
	public ResponseEntity<?> getAllTeachers() {
		LOGGER.info("LOGGER: Getting all teachers");
		List<TeacherEntity> teachers = (List<TeacherEntity>) teacherRepository.findAll();
		return new ResponseEntity<List<TeacherEntity>>(teachers, HttpStatus.OK);
	}
	
	//
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/teacher/{id}")
	public ResponseEntity<?> findTeacherById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Searching for a teacher ID: {}", id);
			for (TeacherEntity teacher : teacherRepository.findAll()) {
				if (teacher.getId().equals(id)) {
					return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Teacher ID {} not found", id);
			return new ResponseEntity<RESTError>(new RESTError(1, "Teacher ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// dto
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/teacher_dto/{id}")
	public ResponseEntity<?> findTeacherDtoById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", id);
			if (!teacherRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}
			TeacherEntity teacher = teacherRepository.findById(id).get();
			
			TeacherDto teacherDto = new TeacherDto();
			teacherDto.setFirstName(teacher.getFirstName());
			teacherDto.setLastName(teacher.getLastName());
			return new ResponseEntity<TeacherDto>(teacherDto, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Find teachers subject they teach
	@Secured("ROLE_TEACHER")
	@RequestMapping(method=RequestMethod.GET, value="/teacher/subject")
	public ResponseEntity<?> findTeachersSubject() {
		
	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	String username = ((User)principal).getUsername();
	Integer userID = userRepository.findUserByUsername(username).getId();
	ERole role = userRepository.findUserByUsername(username).getRole();
	
	if (role.equals((ERole.ROLE_TEACHER))) {
	
		TeacherEntity teacher= teacherRepository.findByUserId(userID);
		List<SubjectEntity>subjects= subjectRepository.findByTeacher(teacher);
					if (!subjects.isEmpty()) {
						LOGGER.info("LOGGER: Gradings are found");
						return new ResponseEntity<List<SubjectEntity>>(subjects, HttpStatus.OK);	
					}
					else {
						LOGGER.info("LOGGER: List is empty. No gradings are found");
						return new ResponseEntity<RESTError>(new RESTError(1, "List is empty. No gradings are found"), HttpStatus.NOT_FOUND);
					}
				}
				LOGGER.info("LOGGER: Access to data denied");
				return new ResponseEntity<RESTError>(new RESTError(2, "Access denied"), HttpStatus.FORBIDDEN);
			}
			
		
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/teacher/subject/{subjectID}")
	public ResponseEntity<?> findTeacherBySubjectID(@PathVariable Integer subjectID) {
		try {
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			for (SubjectEntity subject : subjectRepository.findAll()) {
				if (subject.getId().equals(subjectID)) {
					LOGGER.info("LOGGER: Subject ID {} exists. Searching for teacher with requested subject ID", subjectID);
					for (TeacherEntity teacher : teacherRepository.findAll()) {
						if (teacher.getUser().getId().equals(subjectID)) {
							return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
						}
					}
				}
			} 
			LOGGER.info("LOGGER: Teacher with subject ID {} not found", subjectID);
			return new ResponseEntity<RESTError>(new RESTError(1, "Teacher with requested subject ID not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// add USER & TEACHER
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/user/addteacher")
	public ResponseEntity<?> addnewTeacher(@RequestParam String username, @RequestParam String password, @RequestParam ERole role,
			@RequestParam String firstname, @RequestParam String lastname) {
				UserEntity user= new UserEntity();
				if (userRepository.findUserByUsername(user.getUsername())!=null) {
					LOGGER.info("LOGGER: Username {} already exists", user.getUsername());
					return new ResponseEntity<RESTError>(new RESTError(1, "Username already exists"), HttpStatus.BAD_REQUEST);
				} else{
				user.setUsername(username);
				user.setPassword(Encryption.getPassEncoded(password));
				user.setRole(role);
				userRepository.save(user);
				
				TeacherEntity teacher = new TeacherEntity();
				teacher.setFirstName(firstname);
				teacher.setLastName(lastname);
				teacher.setUser(user);
				teacherRepository.save(teacher);
				LOGGER.info("LOGGER: Administrator created successfully");
				return new ResponseEntity<RESTError>(new RESTError(2, "Administrator with user account added successsfully"), HttpStatus.CREATED);	
		} 

	}
	//add USER & PARENT -RADIIIIIIIIIIIIIIIIIIii
		@Secured("ROLE_ADMIN")
		@RequestMapping(method = RequestMethod.POST, value="/user/addparent")
		public ResponseEntity<?> addNewParent(@RequestParam String username, @RequestParam String password, @RequestParam ERole role,
				@RequestParam String firstname, @RequestParam String lastname, @RequestParam String email) {
					UserEntity user= new UserEntity();
					if (userRepository.findUserByUsername(user.getUsername())!=null) {
						LOGGER.info("LOGGER: Username {} already exists", user.getUsername());
						return new ResponseEntity<RESTError>(new RESTError(1, "Username already exists"), HttpStatus.BAD_REQUEST);
					} else{
					user.setUsername(username);
					user.setPassword(Encryption.getPassEncoded(password));
					user.setRole(role);
					userRepository.save(user);
					
					ParentEntity parent = new ParentEntity();
					parent.setFirstName(firstname);
					parent.setLastName(lastname);	
					parent.setEmail(email);
					parent.setUser(user);
					parentRepository.save(parent);
					LOGGER.info("LOGGER: Administrator created successfully");
					return new ResponseEntity<RESTError>(new RESTError(2, "Parent with user account added successsfully"), HttpStatus.CREATED);	
			} 

		}
	
		//USER &&& STUDENT 
				@Secured("ROLE_ADMIN")
				@RequestMapping(method = RequestMethod.POST, value="/user/addstudent")
				public ResponseEntity<?> addNewStudent(@RequestParam String username, @RequestParam String password, @RequestParam ERole role,
						@RequestParam String firstname, @RequestParam String lastname, @RequestParam ParentEntity parent,
						@RequestParam ClassEntity classID) {
							UserEntity user= new UserEntity();
							if (userRepository.findUserByUsername(user.getUsername())!=null) {
								LOGGER.info("LOGGER: Username {} already exists", user.getUsername());
								return new ResponseEntity<RESTError>(new RESTError(1, "Username already exists"), HttpStatus.BAD_REQUEST);
							} else{
							user.setUsername(username);
							user.setPassword(Encryption.getPassEncoded(password));
							user.setRole(role);
							userRepository.save(user);
							
							StudentEntity student = new StudentEntity();
							student.setFirstName(firstname);
							student.setLastName(lastname);	
							student.setParent(parent);
							student.setSchoolClass(classID);
							student.setUser(user);
							studentRepository.save(student);
						
							return new ResponseEntity<RESTError>(new RESTError(2, "Student with user account added successsfully"), HttpStatus.CREATED);	
					} 

				}
	
	
	// update TEACHER
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/teacher/update/{id}")
	public ResponseEntity<?> updateTeacherById(@PathVariable Integer id, 
											   @RequestParam String firstName, @RequestParam String lastName, @RequestParam SubjectEntity subjectID) {
		try { 			
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", id);
			for (TeacherEntity teacher : teacherRepository.findAll()) {
				if (teacher.getId().equals(id)) {
					LOGGER.info("LOGGER: Teacher ID {} exists", id);
					teacher.setFirstName(firstName); 
					teacher.setLastName(lastName); 
					teacher.setSubject(subjectID);
					teacherRepository.save(teacher);
					LOGGER.info("LOGGER: Teacher ID {} updated", id);
					return new ResponseEntity<RESTError>(new RESTError(1, "Teacher ID updated successsfully"), HttpStatus.OK);
					}
				}
				LOGGER.info("LOGGER: Teacher ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(2, "Teacher ID not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//delete USER & TEACHER
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/teacher/delete/{userID}")
	public ResponseEntity<?> deleteTeacherAndUserByUserId(@PathVariable Integer userID) {
		try { 
			
			LOGGER.info("LOGGER: Checking if user ID {} exists", userID);
			for (UserEntity user : userRepository.findAll()) {
				if (user.getId().equals(userID)) {
					LOGGER.info("LOGGER: User ID {} exists", userID);
					
					LOGGER.info("LOGGER: Checking if user ID {} has role ROLE_TEACHER", userID);
					TeacherEntity teacher = teacherRepository.findByUserId(userID);
					if (teacher==null) {
						LOGGER.info("LOGGER: User ID {} does not have role ROLE_TEACHER", userID);
						return new ResponseEntity<RESTError>(new RESTError(1, "User ID does not have role ROLE_TEACHER"), HttpStatus.BAD_REQUEST);
					}
					
					LOGGER.info("LOGGER: Teacher with user ID {} found", userID);
					teacherRepository.delete(teacher);
					LOGGER.info("LOGGER: Teacher with user ID {} deleted", userID);	
					
					userRepository.delete(user);
					LOGGER.info("LOGGER: User ID {} deleted", userID);
					return new ResponseEntity<RESTError>(new RESTError(2, "User ID and teacher to whom user ID belongs deleted successsfully"), HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: User ID {} not found", userID);
			return new ResponseEntity<RESTError>(new RESTError(3, "User ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//STUDENT
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value="/student") 
	public ResponseEntity<?> getAllStudents() {
		LOGGER.info("LOGGER: Getting all students");
		List<StudentEntity> students = (List<StudentEntity>) studentRepository.findAll();
		return new ResponseEntity<List<StudentEntity>>(students, HttpStatus.OK);
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/student/{id}")
	public ResponseEntity<?> findStudentById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Searching for a student ID: {}", id);
			for (StudentEntity student : studentRepository.findAll()) {
				if (student.getId().equals(id)) {
					return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Student ID {} not found", id);
			return new ResponseEntity<RESTError>(new RESTError(1, "Student ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/student/class/{classID}")
	public ResponseEntity<?> findStudentByClassID(@PathVariable Integer classID) {
		try {
			LOGGER.info("LOGGER: Checking if class ID {} exists", classID);
			for (ClassEntity classs : classRepository.findAll()) {
				if (classs.getId().equals(classID)) {
					LOGGER.info("LOGGER: Class ID {} exists. Searching for student with requested class ID", classID);
					for (StudentEntity student : studentRepository.findAll()) {
						if (student.getSchoolClass().getId().equals(classID)) {
							return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
						}
					}
				}
			} 
			LOGGER.info("LOGGER: Student with class ID {} not found", classID);
			return new ResponseEntity<RESTError>(new RESTError(1, "Student with requested class ID not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@Secured({"ROLE_ADMIN", "ROLE_PARENT"})
	@RequestMapping(method = RequestMethod.GET, value = "/student/parent/{parentID}")
	public ResponseEntity<?> findStudentByParentID(@PathVariable Integer parentID) {
		try {
			LOGGER.info("LOGGER: Checking if parent ID {} exists", parentID);
			for (ParentEntity parent : parentRepository.findAll()) {
				if (parent.getId().equals(parentID)) {
					LOGGER.info("LOGGER: Parent ID {} exists. Searching for student with requested parent ID", parentID);
					for (StudentEntity student : studentRepository.findAll()) {
						if (student.getParent().getId().equals(parentID)) {
							return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
						}
					}
				}
			} 
			LOGGER.info("LOGGER: Student with parent ID {} not found", parentID);
			return new ResponseEntity<RESTError>(new RESTError(1, "Student with requested parent ID not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//add USER & STUDENT
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/user/student/add")
	public ResponseEntity<?> addNewStudent(@RequestBody UserDto user,
										   @RequestParam String firstName, @RequestParam String lastName,
										   @RequestParam Integer classID, @RequestParam Integer parentID) {
		try {	
			LOGGER.info("LOGGER: Checking if class ID {} exists", classID);
			if (!classRepository.findById(classID).isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(1, "Class with provided ID not found"),	HttpStatus.NOT_FOUND);
			}
			
			LOGGER.info("LOGGER: Checking if parent ID {} exists", parentID);
			if (!parentRepository.findById(parentID).isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Parent with provided ID not found"),	HttpStatus.NOT_FOUND);
			}
			
			LOGGER.info("LOGGER: Checking if password and confirmed password are the same");
			if (!user.getPassword().equals(user.getConfirmedPassword())) {
				return new ResponseEntity<RESTError>(new RESTError(3, "Password and confirmed password must be the same"), HttpStatus.BAD_REQUEST);
			}
			
			LOGGER.info("LOGGER: Checking if submitted role = ROLE_STUDENT");
			if (user.getRole().toString().equals(ERole.ROLE_STUDENT.toString())) {				
				LOGGER.info("LOGGER: Role appropriate. Checking if username {} exists", user.getUsername());				
				if (userRepository.findUserByUsername(user.getUsername())!=null) {
					LOGGER.info("LOGGER: Username {} already exists", user.getUsername());
					return new ResponseEntity<RESTError>(new RESTError(4, "Username already exists"), HttpStatus.BAD_REQUEST);
				}
				LOGGER.info("LOGGER: Username {} does not exsits. Creating new user", user.getUsername());
				UserEntity newUser = new UserEntity();
				newUser.setUsername(user.getUsername());
				newUser.setPassword(Encryption.getPassEncoded(user.getPassword()));
				newUser.setRole(ERole.valueOf(user.getRole().name()));
				userRepository.save(newUser);
				LOGGER.info("LOGGER: User created successfully");
							
				LOGGER.info("LOGGER: Creating new student");
				ClassEntity schoolClass = classRepository.findById(classID).get();
				ParentEntity parent = parentRepository.findById(parentID).get();				
				StudentEntity newStudent = new StudentEntity();
				
				newStudent.setFirstName(firstName);
				newStudent.setLastName(lastName);	
				newStudent.setUser(newUser);
				newStudent.setSchoolClass(schoolClass);
				newStudent.setParent(parent);
				studentRepository.save(newStudent);
				LOGGER.info("LOGGER: Student created successfully");
				return new ResponseEntity<RESTError>(new RESTError(5, "Student with user account added successsfully"), HttpStatus.CREATED);	
		} 
			else {
				LOGGER.info("LOGGER: Wrong role!");
				return new ResponseEntity<RESTError>(new RESTError(6, "Wrong role"), HttpStatus.BAD_REQUEST);
			}
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(7, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//update STUDENT
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/student/update/{id}")
	public ResponseEntity<?> updateStudentById(@PathVariable Integer id, 
											   @RequestParam String firstName, @RequestParam String lastName, 
											   @RequestParam ParentEntity parentID,
											   @RequestParam ClassEntity classID) {
		try { 			
			LOGGER.info("LOGGER: Checking if student ID {} exists", id);
			for (StudentEntity student : studentRepository.findAll()) {
				if (student.getId().equals(id)) {
					LOGGER.info("LOGGER: Student ID {} exists", id);
					student.setFirstName(firstName); 
					student.setLastName(lastName); 
					student.setParent(parentID);
					student.setSchoolClass(classID);
					studentRepository.save(student);
					LOGGER.info("LOGGER: Student ID {} updated", id);
					return new ResponseEntity<RESTError>(new RESTError(1, "Student ID updated successsfully"), HttpStatus.OK);
					}
				}
				LOGGER.info("LOGGER: Student ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(2, "Student ID not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//delete STUDENT & USER
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/student/delete/{userID}")
	public ResponseEntity<?> deleteStudentAndUserByUserId(@PathVariable Integer userID) {
		try { 
			
			LOGGER.info("LOGGER: Checking if user ID {} exists", userID);
			for (UserEntity user : userRepository.findAll()) {
				if (user.getId().equals(userID)) {
					LOGGER.info("LOGGER: User ID {} exists", userID);
					
					LOGGER.info("LOGGER: Checking if user ID {} has role ROLE_STUDENT", userID);
					StudentEntity student = studentRepository.findByUserId(userID);
					if (student==null) {
						LOGGER.info("LOGGER: User ID {} does not have role ROLE_STUDENT", userID);
						return new ResponseEntity<RESTError>(new RESTError(1, "User ID does not have role ROLE_STUDENT"), HttpStatus.BAD_REQUEST);
					}
					
					LOGGER.info("LOGGER: Student with user ID {} found", userID);
					studentRepository.delete(student);
					LOGGER.info("LOGGER: Student with user ID {} deleted", userID);	
					
					userRepository.delete(user);
					LOGGER.info("LOGGER: User ID {} deleted", userID);
					return new ResponseEntity<RESTError>(new RESTError(2, "User ID and student to whom user ID belongs deleted successsfully"), HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: User ID {} not found", userID);
			return new ResponseEntity<RESTError>(new RESTError(3, "User ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	//PARENTS
	//getAll PARENTS
	//@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value="/parent") 
	public ResponseEntity<?> getAllParents() {
		LOGGER.info("LOGGER: Getting all parents");
		List<ParentEntity> parents = (List<ParentEntity>) parentRepository.findAll();
		return new ResponseEntity<List<ParentEntity>>(parents, HttpStatus.OK);
	}
	

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/parent/{id}")
	public ResponseEntity<?> findParentById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Searching for a parent ID: {}", id);
			for (ParentEntity parent : parentRepository.findAll()) {
				if (parent.getId().equals(id)) {
					return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Parent ID {} not found", id);
			return new ResponseEntity<RESTError>(new RESTError(1, "Parent ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/parent/email/{email}")
	public ResponseEntity<?> findParentByEmail(@PathVariable String email) {
		try {
			LOGGER.info("LOGGER: Getting a parent with email: {}", email);
			for (ParentEntity parent : parentRepository.findAll()) {
				if (parent.getEmail().equals(email)) {
					return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Email {} not found", email);
			return new ResponseEntity<RESTError>(new RESTError(1, "Email not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//add USER & PARENT - 2 put
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/user/parent/add")
	public ResponseEntity<?> addNewUser(@RequestBody UserDto user,
										@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
		try {				
			LOGGER.info("LOGGER: Checking if submitted role = ROLE_PARENT");
			if (user.getRole().toString().equals(ERole.ROLE_PARENT.toString())) {				
				LOGGER.info("LOGGER: Role appropriate. Checking if username {} exists", user.getUsername());				
				if (userRepository.findUserByUsername(user.getUsername())!=null) {
					LOGGER.info("LOGGER: Username {} already exists", user.getUsername());
					return new ResponseEntity<RESTError>(new RESTError(1, "Username already exists"), HttpStatus.BAD_REQUEST);
				}
				LOGGER.info("LOGGER: Username {} does not esists. Creating new user", user.getUsername());
				UserEntity newUser = new UserEntity();
				newUser.setUsername(user.getUsername());
				newUser.setPassword(Encryption.getPassEncoded(user.getPassword()));
				newUser.setRole(ERole.valueOf(user.getRole().name()));
				userRepository.save(newUser);
				LOGGER.info("LOGGER: User created successfully");
							
				LOGGER.info("LOGGER: Creating new parent");
				ParentEntity newParent = new ParentEntity();
				newParent.setFirstName(firstName);
				newParent.setLastName(lastName);
				newParent.setEmail(email);			
				newParent.setUser(newUser);
				parentRepository.save(newParent);
				LOGGER.info("LOGGER: Parent created successfully");
				return new ResponseEntity<RESTError>(new RESTError(2, "Parent with user account added successsfully"), HttpStatus.CREATED);	
		} 
			else {
				LOGGER.info("LOGGER: Wrong role!");
				return new ResponseEntity<RESTError>(new RESTError(3, "Wrong role"), HttpStatus.BAD_REQUEST);
			}
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//update PARENT
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/parent/update/{id}")
	public ResponseEntity<?> updateParentById(@PathVariable Integer id, 
											  @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
		try { 			
			LOGGER.info("LOGGER: Checking if parent ID {} exists", id);
			for (ParentEntity parent : parentRepository.findAll()) {
				if (parent.getId().equals(id)) {
					LOGGER.info("LOGGER: Parent ID {} exists", id);
					parent.setFirstName(firstName); 
					parent.setLastName(lastName); 
					parent.setEmail(email); 
					parentRepository.save(parent);
					LOGGER.info("LOGGER: Parent ID {} updated", id);
					return new ResponseEntity<RESTError>(new RESTError(1, "Parent ID updated successsfully"), HttpStatus.OK);
					}
				}
				LOGGER.info("LOGGER: Parent ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(2, "Parent ID not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//delete USER & PARENT
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/parent/delete/{userID}")
	public ResponseEntity<?> deleteParentAndUserByUserId(@PathVariable Integer userID) {
		try { 
			
			LOGGER.info("LOGGER: Checking if user ID {} exists", userID);
			for (UserEntity user : userRepository.findAll()) {
				if (user.getId().equals(userID)) {
					LOGGER.info("LOGGER: User ID {} exists", userID);
					
					LOGGER.info("LOGGER: Checking if user ID {} belongs to parent", userID);
					ParentEntity parent = parentRepository.findByUserId(userID);
					if (parent==null) {
						LOGGER.info("LOGGER: User ID {} does not belong to parent", userID);
						return new ResponseEntity<RESTError>(new RESTError(1, "User ID does not belong to parent"), HttpStatus.BAD_REQUEST);
					}
					
					LOGGER.info("LOGGER: Parent with user ID {} found", userID);
					parentRepository.delete(parent);
					LOGGER.info("LOGGER: Parent with user ID {} deleted", userID);	
					
					userRepository.delete(user);
					LOGGER.info("LOGGER: User ID {} deleted", userID);
					return new ResponseEntity<RESTError>(new RESTError(2, "User ID and parent to whom user ID belongs deleted successsfully"), HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: User ID {} not found", userID);
			return new ResponseEntity<RESTError>(new RESTError(3, "User ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//SUBJECT
	//getAll SUBJECTS
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value="/subject") 
	public ResponseEntity<?> getAllSubjects() {
		LOGGER.info("LOGGER: Getting all subjects");
		List<SubjectEntity> subjects = (List<SubjectEntity>) subjectRepository.findAll();
		return new ResponseEntity<List<SubjectEntity>>(subjects, HttpStatus.OK);
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/subject/{id}")
	public ResponseEntity<?> findSubjectById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Searching for a subject ID: {}", id);
			for (SubjectEntity subject : subjectRepository.findAll()) {
				if (subject.getId().equals(id)) {
					return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Subject ID {} not found", id);
			return new ResponseEntity<RESTError>(new RESTError(1, "Subject ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@Secured("ROLE_ADMINR")
	@RequestMapping(method = RequestMethod.GET, value = "/subject/name/{name}")
	public ResponseEntity<?> findSubjectByName(@PathVariable String name) {
		try {
			LOGGER.info("LOGGER: Getting a subject with name: {}", name);
			for (SubjectEntity subject : subjectRepository.findAll()) {
				if (subject.getName().equalsIgnoreCase(name)) {
					return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Subject {} not found", name);
			return new ResponseEntity<RESTError>(new RESTError(1, "Subject not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/subject/weekly_fund/{weeklyFund}")
	public ResponseEntity<?> findSubjectsByWeeklyFund(@PathVariable Integer weeklyFund) {
		try {
			LOGGER.info("LOGGER: Getting subject(s) with weekly fund: {}", weeklyFund);
			List<SubjectEntity> subjects = subjectRepository.findByWeeklyFund(weeklyFund);
			
			if (!subjects.isEmpty())
				return new ResponseEntity<List<SubjectEntity>>(subjects, HttpStatus.OK);
			else
				LOGGER.info("LOGGER: Subject(s) with weekly fund {} not found", subjects);
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject(s) with requested weekly fund not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/subject/add")
	public ResponseEntity<?> addNewSubject(@RequestParam String name,  @RequestParam Integer weeklyFund) {
		try { 			
			LOGGER.info("LOGGER: Checking if subject with name {} exists", name);
			if (subjectRepository.findByNameIgnoreCase(name)!=null) {
				LOGGER.info("LOGGER: Subject with name {} already exists", name);
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject with that name already exists"), HttpStatus.BAD_REQUEST);
			}
			else {
				LOGGER.info("LOGGER: Subject with name {} does not exist. Creating subject",name);
				SubjectEntity newSubject = new SubjectEntity();
				newSubject.setName(name); 
				newSubject.setWeeklyFund(weeklyFund); 
				subjectRepository.save(newSubject);
				LOGGER.info("LOGGER: Subject with name {} added", name);
				return new ResponseEntity<RESTError>(new RESTError(2, "Subject added successsfully"), HttpStatus.OK);
			}
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//update SUBJECT
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/subject/update/{id}")
	public ResponseEntity<?> updateSubjectById(@PathVariable Integer id, @RequestParam String name, @RequestParam Integer weeklyFund) {
		try { 			
			LOGGER.info("LOGGER: Checking if subject ID {} exists", id);
			if (!subjectRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Subject ID {} exists", id);
			SubjectEntity subject = subjectRepository.findById(id).get();
			subject.setName(name); 
			subject.setWeeklyFund(weeklyFund); 
			subjectRepository.save(subject);
			LOGGER.info("LOGGER: Subject ID {} updated", id);
			return new ResponseEntity<RESTError>(new RESTError(2, "Subject ID updated successsfully"), HttpStatus.OK);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//delete SUBJECT
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/subject/delete/{id}")
	public ResponseEntity<?> deleteSubjectById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Checking if subject ID {} exists", id);
			if (!subjectRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Subject ID {} exists", id);
			SubjectEntity subject = subjectRepository.findById(id).get();
			subjectRepository.delete(subject);
			LOGGER.info("LOGGER: Subject ID {} deleted", id);
			return new ResponseEntity<RESTError>(new RESTError(2, "Subject ID deleted successsfully"), HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//CLASS 
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value="/class") 
	public ResponseEntity<?> getAllClasses() {
		LOGGER.info("LOGGER: Getting all classes");
		List<ClassEntity> schoolClasses = (List<ClassEntity>) classRepository.findAll();
		return new ResponseEntity<List<ClassEntity>>(schoolClasses, HttpStatus.OK);
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/class/{id}")
	public ResponseEntity<?> findClassById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Searching for a class ID: {}", id);
			for (ClassEntity schoolClass : classRepository.findAll()) {
				if (schoolClass.getId().equals(id)) {
					return new ResponseEntity<ClassEntity>(schoolClass, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Class ID {} not found", id);
			return new ResponseEntity<RESTError>(new RESTError(1, "Class ID not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/class/name/{name}")
	public ResponseEntity<?> findClassByName(@PathVariable String name) {
		try {
			LOGGER.info("LOGGER: Getting a class with name: {}", name);
			for (ClassEntity schoolClass : classRepository.findAll()) {
				if (schoolClass.getClassName().equalsIgnoreCase(name)) {
					return new ResponseEntity<ClassEntity>(schoolClass, HttpStatus.OK);
				}
			}
			LOGGER.info("LOGGER: Class {} not found", name);
			return new ResponseEntity<RESTError>(new RESTError(1, "Class not found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/class/school_year/{schoolYear}")
	public ResponseEntity<?> findClassesBySchoolYear(@PathVariable Integer schoolYear) {
		try {
			LOGGER.info("LOGGER: Getting class(es) of school year: {}", schoolYear);
			List<ClassEntity> classes = classRepository.findBySchoolYear(schoolYear);
			
			if (!classes.isEmpty())
				return new ResponseEntity<List<ClassEntity>>(classes, HttpStatus.OK);
			else
				LOGGER.info("LOGGER: Class(es) of school year {} not found", schoolYear);
				return new ResponseEntity<RESTError>(new RESTError(1, "Class(es) of requested school year not found"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//ADD NEW CLASS
	//Add all classes this school has
			@RequestMapping(method=RequestMethod.POST, value="/class/addnew")
			@Secured(value = { "ROLE_ADMIN" })
			public ResponseEntity<?> addNewClas(@RequestParam String className, @RequestParam Integer schoolYear) {

			if (classRepository.findByClassNameIgnoreCase(className) != null){
				return new ResponseEntity<RESTError>(new RESTError(1, "Class with existing class number already exists!"), HttpStatus.BAD_REQUEST);
			} else {
					ClassEntity clas= new ClassEntity();
					clas.setClassName(className);
					clas.setSchoolYear(schoolYear);
					classRepository.save(clas);
					return new ResponseEntity<ClassEntity>(clas, HttpStatus.CREATED);
				} 
					
				}

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value="/class/update/{id}")
	public ResponseEntity<?> updateClassById(@PathVariable Integer id, @RequestParam String className, @RequestParam Integer schoolYear) {
		try { 			
			LOGGER.info("LOGGER: Checking if class ID {} exists", id);
			if (!classRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Class ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Class ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Class ID {} exists", id);
			ClassEntity schoolClass = classRepository.findById(id).get();
			schoolClass.setClassName(className); 
			schoolClass.setSchoolYear(schoolYear); 
			classRepository.save(schoolClass);
			LOGGER.info("LOGGER: Class ID {} updated", id);
			return new ResponseEntity<RESTError>(new RESTError(2, "Class ID updated successsfully"), HttpStatus.OK);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
	//delete CLASS

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/class/delete/{id}")
	public ResponseEntity<?> deleteClassById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Checking if class ID {} exists", id);
			if (!classRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Class ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Class ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Class ID {} exists", id);			
			ClassEntity schoolClass = classRepository.findById(id).get();
			classRepository.delete(schoolClass);
			LOGGER.info("LOGGER: Class ID {} deleted", id);
			return new ResponseEntity<RESTError>(new RESTError(2, "Class ID deleted successsfully"), HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
		//TEACHING 
		//get teachers teaching....
		@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
		@RequestMapping(method=RequestMethod.GET, value="/teacher/class")
		public ResponseEntity<?> findTeachersClass() {
			
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((User)principal).getUsername();
		Integer userID = userRepository.findUserByUsername(username).getId();
		ERole role = userRepository.findUserByUsername(username).getRole();
		
		if (role.equals((ERole.ROLE_TEACHER))) {
		
			TeacherEntity teacher= teacherRepository.findByUserId(userID);
			List<TeachingEntity>teaching= teachingRepository.findByTeacher(teacher);
						if (!teaching.isEmpty()) {
							LOGGER.info("LOGGER: Gradings are found");
							return new ResponseEntity<List<TeachingEntity>>(teaching, HttpStatus.OK);	
						}
						else {
							LOGGER.info("LOGGER: List is empty. No gradings are found");
							return new ResponseEntity<RESTError>(new RESTError(1, "List is empty. No gradings are found"), HttpStatus.NOT_FOUND);
						}
					}
					LOGGER.info("LOGGER: Access to data denied");
					return new ResponseEntity<RESTError>(new RESTError(2, "Access denied"), HttpStatus.FORBIDDEN);
				}
	
	
		
		//Get me teachers students and their grades
				@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
				@RequestMapping(method=RequestMethod.GET, value="/teacher/student/grades")
				public ResponseEntity<?> findTeachersStudentGrades() {
					
				Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				String username = ((User)principal).getUsername();
				Integer userID = userRepository.findUserByUsername(username).getId();
				ERole role = userRepository.findUserByUsername(username).getRole();
				
				if (role.equals((ERole.ROLE_TEACHER))) {
				
					TeacherEntity teacher= teacherRepository.findByUserId(userID);
					List<GradingEntity>grading= gradingRepository.findByTeacher(teacher);
								if (!grading.isEmpty()) {
									
									return new ResponseEntity<List<GradingEntity>>(grading, HttpStatus.OK);	
								}
								else {
									
									return new ResponseEntity<RESTError>(new RESTError(1, "List is empty. No gradings are found"), HttpStatus.NOT_FOUND);
								}
							}
							
							return new ResponseEntity<RESTError>(new RESTError(2, "Access denied"), HttpStatus.FORBIDDEN);
						}
			
				
				//Get me teachers students and their grades in class you choose
				@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
				@RequestMapping(method=RequestMethod.GET, value="/teacher/student/grades/{classID}")
				public ResponseEntity<?> findTeachersStudentGradesinClasses(@PathVariable ClassEntity classID) {
					
				Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				String username = ((User)principal).getUsername();
				Integer userID = userRepository.findUserByUsername(username).getId();
				ERole role = userRepository.findUserByUsername(username).getRole();
				
				if (role.equals((ERole.ROLE_TEACHER))) {
				
					TeacherEntity teacher= teacherRepository.findByUserId(userID);
					List<GradingEntity>grading= gradingRepository.findByTeacherAndClasses(teacher, classID);
								if (!grading.isEmpty()) {
									return new ResponseEntity<List<GradingEntity>>(grading, HttpStatus.OK);	
								}
								else {
									return new ResponseEntity<RESTError>(new RESTError(1, "List is empty. No gradings are found"), HttpStatus.NOT_FOUND);
								}
							}
							return new ResponseEntity<RESTError>(new RESTError(2, "Access denied"), HttpStatus.FORBIDDEN);
						}		
	
				
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/teaching/{id}")
	public ResponseEntity<?> findTeachingById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Checking if teaching ID {} exists", id);
			if (!teachingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Teaching ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Teaching ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Teaching ID {} exists", id);			
			TeachingEntity teaching = teachingRepository.findById(id).get();
			return new ResponseEntity<TeachingEntity>(teaching, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/teaching/subject/{subjectID}")
	public ResponseEntity<?> findTeachingBySubjectID(@PathVariable Integer subjectID) {
		try { 			
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Subject ID {} exists. Checking if any teaching ID is related to this subject", subjectID);		
			SubjectEntity subject = subjectRepository.findById(subjectID).get();
			if (teachingRepository.findBySubject(subject).isEmpty()) {
				LOGGER.info("LOGGER: Teaching(s) related to subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Teaching related to requested subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Getting teachings with subject ID {}", subjectID);
			List <TeachingEntity> teachings = teachingRepository.findBySubject(subject);
			return new ResponseEntity<List <TeachingEntity>>(teachings, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/teaching/teacher/{teacherID}")
	public ResponseEntity<?> findTeachingByTeacherID(@PathVariable Integer teacherID) {
		try {
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", teacherID);
			if (!teacherRepository.findById(teacherID).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Teacher ID {} exists. Checking if any teaching ID is related to this subject", teacherID);		
			TeacherEntity teacher = teacherRepository.findById(teacherID).get();
			if (teachingRepository.findByTeacher(teacher).isEmpty()) {
				LOGGER.info("LOGGER: Teaching(s) related to teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Teaching related to requested teacher ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Getting teachings with teacher ID {}", teacherID);
			List <TeachingEntity> teachings = teachingRepository.findByTeacher(teacher);
			return new ResponseEntity<List <TeachingEntity>>(teachings, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.GET, value = "/teacher/class/{classID}")
	public ResponseEntity<?> findTeachingByClassID(@PathVariable Integer classID) {
	
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();
			
			if (role.equals((ERole.ROLE_TEACHER))) {
		
				if (!classRepository.findById(classID).isPresent()) {
				LOGGER.info("LOGGER: Class ID {} not found", classID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Class ID not found"), HttpStatus.NOT_FOUND);				
			}
				
			ClassEntity classes = classRepository.findById(classID).get();
			if (teachingRepository.findBySchoolClass(classes).isEmpty()) {
				LOGGER.info("LOGGER: Teaching(s) related to class ID {} not found", classID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Teaching related to requested class ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Getting teachings with class ID {}", classID);
			List <GradingEntity> grading = gradingRepository.findByClasses(classes);
			return new ResponseEntity<List <GradingEntity>>(grading, HttpStatus.OK);
		} 
			else { 
			
			return new ResponseEntity<RESTError>(new RESTError(3, "Bla bla"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/teaching/subject/{subjectID}/teacher/{teacherID}/class/{classID}")
	public ResponseEntity<?> findTeachingBySubjectIDTeacherIDClassID(@PathVariable Integer subjectID, 
																	 @PathVariable Integer teacherID,
																	 @PathVariable Integer classID) {
		try { 
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", teacherID);
			if (!teacherRepository.findById(teacherID).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			if (!classRepository.findById(classID).isPresent()) {
				LOGGER.info("LOGGER: Class ID {} not found", classID);
				return new ResponseEntity<RESTError>(new RESTError(3, "Class ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Subject ID {}, teacher ID {} and classID {} exist", subjectID, teacherID, classID);		
			SubjectEntity subject = subjectRepository.findById(subjectID).get();
			TeacherEntity teacher = teacherRepository.findById(teacherID).get();
			ClassEntity schoolClass = classRepository.findById(classID).get();
			TeachingEntity teaching = teachingRepository.findBySubjectAndTeacherAndSchoolClass(subject, teacher, schoolClass);
			return new ResponseEntity<TeachingEntity>(teaching, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//add - TEACHING
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/teaching/add")
	public ResponseEntity<?> addNewTeaching(@RequestParam Integer subjectID, 
											@RequestParam Integer teacherID,
											@RequestParam Integer classID) {
		try { 
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", teacherID);
			if (!teacherRepository.findById(teacherID).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Checking if teacher ID {} teaches subject ID {}", teacherID, subjectID);
			TeacherEntity teacher = teacherRepository.findById(teacherID).get();
			SubjectEntity subject = subjectRepository.findById(subjectID).get();
			if (!teacher.getSubject().equals(subject)) {
				LOGGER.info("LOGGER: Teacher ID {} does not teach subject ID {}", teacherID, subjectID);
				return new ResponseEntity<RESTError>(new RESTError(3, "Teacher does not teach requested subject"), HttpStatus.BAD_REQUEST);				
			}
			
			if (!classRepository.findById(classID).isPresent()) {
				LOGGER.info("LOGGER: Class ID {} not found", classID);
				return new ResponseEntity<RESTError>(new RESTError(4, "Class ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Finding if teaching already exist");
			SubjectEntity sub = subjectRepository.findById(subjectID).get();
			TeacherEntity tea = teacherRepository.findById(teacherID).get();
			ClassEntity scClass = classRepository.findById(classID).get();
			TeachingEntity teach = teachingRepository.findBySubjectAndTeacherAndSchoolClass(sub, tea, scClass);
			if (teach!=null) {
				LOGGER.info("Teaching already exists");
				return new ResponseEntity<RESTError>(new RESTError(5, "Teaching already exists"), HttpStatus.BAD_REQUEST);				
			}			
			else {
				LOGGER.info("LOGGER: Subject ID {}, teacher ID {} and classID {} exist. Creating teaching", subjectID, teacherID, classID);		
				SubjectEntity s = subjectRepository.findById(subjectID).get();
				TeacherEntity t = teacherRepository.findById(teacherID).get();
				ClassEntity sc = classRepository.findById(classID).get();

				TeachingEntity newTeaching = new TeachingEntity();
				newTeaching.setSubject(s);
				newTeaching.setTeacher(t); 
				newTeaching.setSchoolClass(sc);
				teachingRepository.save(newTeaching);
				LOGGER.info("LOGGER: Teaching added");
				return new ResponseEntity<RESTError>(new RESTError(6, "Teaching added successsfully"), HttpStatus.OK);
			}
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(7, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//update - TEACHING
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/teaching/update/{id}")
	public ResponseEntity<?> updateTeaching(@PathVariable Integer id, 
											@RequestParam Integer subjectID, 
											@RequestParam Integer teacherID,
											@RequestParam Integer classID) {
		try { 
			LOGGER.info("LOGGER: Checking if teaching ID {} exists", id);
			if (!teachingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Teaching ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Teaching ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", teacherID);
			if (!teacherRepository.findById(teacherID).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(3, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Checking if teacher ID {} teaches subject ID {}", teacherID, subjectID);
			TeacherEntity teach = teacherRepository.findById(teacherID).get();
			SubjectEntity sub = subjectRepository.findById(subjectID).get();
			if (!teach.getSubject().equals(sub)) {
				LOGGER.info("LOGGER: Teacher ID {} does not teach subject ID {}", teacherID, subjectID);
				return new ResponseEntity<RESTError>(new RESTError(4, "Teacher does not teach requested subject"), HttpStatus.BAD_REQUEST);				
			}
			
			if (!classRepository.findById(classID).isPresent()) {
				LOGGER.info("LOGGER: Class ID {} not found", classID);
				return new ResponseEntity<RESTError>(new RESTError(5, "Class ID not found"), HttpStatus.NOT_FOUND);				
			}
			else {
				LOGGER.info("LOGGER: Teaching ID {} exists. Parametars are correct. Updating teaching ID ", id);		
				SubjectEntity s = subjectRepository.findById(subjectID).get();
				TeacherEntity t = teacherRepository.findById(teacherID).get();
				ClassEntity sc = classRepository.findById(classID).get();
				TeachingEntity teaching = teachingRepository.findById(id).get();
				teaching.setSubject(s);
				teaching.setTeacher(t); 
				teaching.setSchoolClass(sc);
				teachingRepository.save(teaching);
				LOGGER.info("LOGGER: Teaching ID {} updated", id);
				return new ResponseEntity<RESTError>(new RESTError(6, "Teaching updated successsfully"), HttpStatus.OK);
			}
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(7, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//delete TEACHING

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/teaching/delete/{id}")
	public ResponseEntity<?> deleteTeachingById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Checking if teaching ID {} exists", id);
			if (!teachingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Teaching ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Teaching ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Teaching ID {} exists", id);			
			TeachingEntity teaching = teachingRepository.findById(id).get();
			teachingRepository.delete(teaching);
			LOGGER.info("LOGGER: Teaching ID {} deleted", id);
			return new ResponseEntity<RESTError>(new RESTError(2, "Teaching ID deleted successsfully"), HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//GRADING
	//getAll GRADING
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value="/grading") 
	public ResponseEntity<?> getAllGradings() {
		try { 
			LOGGER.info("LOGGER: Checking if there are gradings");
			List<GradingEntity> gradings = (List<GradingEntity>) gradingRepository.findAll();
		
			if (!gradings.isEmpty()) {
				LOGGER.info("LOGGER: Gradings are found");
				return new ResponseEntity<List<GradingEntity>>(gradings, HttpStatus.OK);
			}
			LOGGER.info("LOGGER: List is empty. No gradings are found");
			return new ResponseEntity<RESTError>(new RESTError(1, "List is empty. No gradings are found"), HttpStatus.NOT_FOUND);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/grading/{id}")
	public ResponseEntity<?> findGradingById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Checking if grading ID {} exists", id);
			if (!gradingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Grading ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Grading ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Grading ID {} exists", id);			
			GradingEntity grading = gradingRepository.findById(id).get();
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(2, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/grading/student/{studentID}")
	public ResponseEntity<?> findGradingByStudentID(@PathVariable Integer studentID) {
		try { 
			LOGGER.info("LOGGER: Checking if student ID {} exists", studentID);
			if (!studentRepository.findById(studentID).isPresent()) {
				LOGGER.info("LOGGER: Student ID {} not found", studentID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Student ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Student ID {} exists. Checking if any grading ID is related to this student", studentID);		
			StudentEntity student = studentRepository.findById(studentID).get();
			if (gradingRepository.findByStudent(student).isEmpty()) {
				LOGGER.info("LOGGER: Grading(s) related to student ID {} not found", studentID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Grading related to requested student ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Getting gradings with student ID {}", studentID);
			List <GradingEntity> gradings = gradingRepository.findByStudent(student);
			return new ResponseEntity<List <GradingEntity>>(gradings, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/grading/subject/{subjectID}")
	public ResponseEntity<?> findGradingBySubjectID(@PathVariable Integer subjectID) {
		try { 
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Subject ID {} exists. Checking if any grading ID is related to this subject", subjectID);		
			SubjectEntity subject = subjectRepository.findById(subjectID).get();
			if (gradingRepository.findBySubject(subject).isEmpty()) {
				LOGGER.info("LOGGER: Grading(s) related to subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Grading related to requested subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Getting gradings with subject ID {}", subjectID);
			List <GradingEntity> gradings = gradingRepository.findBySubject(subject);
			return new ResponseEntity<List <GradingEntity>>(gradings, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/grading/teacher/{teacherID}")
	public ResponseEntity<?> findGradingByTeacherID(@PathVariable Integer teacherID) {
		try { 
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", teacherID);
			if (!teacherRepository.findById(teacherID).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Teacher ID {} exists. Checking if any grading ID is related to this teacher", teacherID);		
			TeacherEntity teacher = teacherRepository.findById(teacherID).get();
			if (gradingRepository.findByTeacher(teacher).isEmpty()) {
				LOGGER.info("LOGGER: Grading(s) related to teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Grading related to requested teacher ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Getting gradings with teacher ID {}", teacherID);
			List <GradingEntity> gradings = gradingRepository.findByTeacher(teacher);
			return new ResponseEntity<List <GradingEntity>>(gradings, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/grading/student/{studentID}/subject/{subjectID}/teacher/{teacherID}")
	public ResponseEntity<?> findTeachingByStudentIDSubjectIDTeacherID(@PathVariable Integer studentID, 
																	   @PathVariable Integer subjectID,
																	   @PathVariable Integer teacherID) {
		try { 
			
			LOGGER.info("LOGGER: Checking if student ID {} exists", studentID);
			if (!studentRepository.findById(studentID).isPresent()) {
				LOGGER.info("LOGGER: Student ID {} not found", studentID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Student ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", teacherID);
			if (!teacherRepository.findById(teacherID).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(3, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}		
			LOGGER.info("LOGGER: Student ID {}, subject ID {} and teacher {} exist", studentID, subjectID, teacherID);
			StudentEntity student = studentRepository.findById(studentID).get();
			SubjectEntity subject = subjectRepository.findById(subjectID).get();
			TeacherEntity teacher = teacherRepository.findById(teacherID).get();
			List <GradingEntity> gradings = gradingRepository.findByStudentAndSubjectAndTeacher(student, subject, teacher);
			return new ResponseEntity<List<GradingEntity>>(gradings, HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/grading/add")
	public ResponseEntity<?> updateGrading(@RequestParam Integer studentID,
										   @RequestParam Integer subjectID, 
										   @RequestParam Integer teacherID,
										   @RequestParam String examType, 
										   @RequestParam String examDate,
										   @RequestParam Integer examGrade)  {
		try { 		
			
			LOGGER.info("LOGGER: Checking if student ID {} exists", studentID);
			LOGGER.info("LOGGER: datum {}", examDate);
			if (!studentRepository.findById(studentID).isPresent()) {
				LOGGER.info("LOGGER: Student ID {} not found", studentID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Student ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", teacherID);
			if (!teacherRepository.findById(teacherID).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(3, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}	
			LOGGER.info("LOGGER: Checking if teacher ID {} teaches subject ID {}", teacherID, subjectID);
			TeacherEntity t = teacherRepository.findById(teacherID).get();
			SubjectEntity s = subjectRepository.findById(subjectID).get();
			if (!t.getSubject().equals(s)) {
				LOGGER.info("LOGGER: Teacher ID {} does not teach subject ID {}", teacherID, subjectID);
				return new ResponseEntity<RESTError>(new RESTError(4, "Teacher does not teach requested subject"), HttpStatus.BAD_REQUEST);				
			}
			LOGGER.info("LOGGER: Checking if teacher ID {} teaches subject ID {} to student ID {}", teacherID, subjectID, studentID);
			StudentEntity stu = studentRepository.findById(studentID).get();
			SubjectEntity sub = subjectRepository.findById(subjectID).get();
			TeacherEntity tea = teacherRepository.findById(teacherID).get();
			TeachingEntity tch = teachingRepository.findBySubjectAndTeacherAndSchoolClass(sub, tea, stu.getSchoolClass());
			if (tch==null) {
				LOGGER.info("LOGGER: Teacher ID {} does not teach subject ID {} to student ID {}", teacherID, subjectID, studentID);
				return new ResponseEntity<RESTError>(new RESTError(5, "Teacher does not teach subject to requested student"), HttpStatus.BAD_REQUEST);				
			}			
			else {
				LOGGER.info("LOGGER: Parametars are correct. Creating grading");	
				StudentEntity student = studentRepository.findById(studentID).get();
				SubjectEntity subject = subjectRepository.findById(subjectID).get();
				TeacherEntity teacher = teacherRepository.findById(teacherID).get();
				GradingEntity grading = new GradingEntity();
				grading.setStudent(student);
				grading.setTeacher(teacher); 
				grading.setSubject(subject);
				grading.setExamType(examType);
				grading.setExamDate(LocalDate.parse(examDate));
				grading.setExamGrade(examGrade);
				gradingRepository.save(grading);
				
				LOGGER.info("LOGGER: Sending an email to parent");
				EmailObject object = new EmailObject();
				object.setTo(student.getParent().getEmail());
				object.setSubject("Grading notification (NEW GRADE) " + LocalDate.now());
				String mailText = "";
				mailText = mailText + "\nSTUDENT: " + student.getFirstName() + " " + student.getLastName();
				mailText = mailText + "\n\nDate of exam: " + grading.getExamDate();
				mailText = mailText + "\nGrade: " + grading.getExamGrade();
				mailText = mailText + "\nSubject: " + subject.getName();
				mailText = mailText + "\nTeacher: " + teacher.getFirstName() + " " + teacher.getLastName();
				object.setText(mailText);
				emailService.sendSimpleMessage(object);
				
				LOGGER.info("LOGGER: Grading added. Email sent successfully");
				return new ResponseEntity<RESTError>(new RESTError(6, "Grading added and and email sent successsfully"), HttpStatus.OK);
			}
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(7, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/grading/update/{id}")
	public ResponseEntity<?> updateGrading(@PathVariable Integer id, 
										   @RequestParam Integer studentID,
										   @RequestParam Integer subjectID, 
										   @RequestParam Integer teacherID,
										   @RequestParam String examType, 
										   @RequestParam String examDate,
										   @RequestParam Integer examGrade)  {
		try { 		
			
			LOGGER.info("LOGGER: Checking if grading ID {} exists", id);
			if (!gradingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Grading ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Grading ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Checking if student ID {} exists", studentID);
			if (!studentRepository.findById(studentID).isPresent()) {
				LOGGER.info("LOGGER: Student ID {} not found", studentID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Student ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Checking if subject ID {} exists", subjectID);
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(3, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Checking if teacher ID {} exists", teacherID);
			if (!teacherRepository.findById(teacherID).isPresent()) {
				LOGGER.info("LOGGER: Teacher ID {} not found", teacherID);
				return new ResponseEntity<RESTError>(new RESTError(4, "Teacher ID not found"), HttpStatus.NOT_FOUND);				
			}	
			LOGGER.info("LOGGER: Checking if teacher ID {} teaches subject ID {}", teacherID, subjectID);
			TeacherEntity t = teacherRepository.findById(teacherID).get();
			SubjectEntity s = subjectRepository.findById(subjectID).get();
			if (!t.getSubject().equals(s)) {
				LOGGER.info("LOGGER: Teacher ID {} does not teach subject ID {}", teacherID, subjectID);
				return new ResponseEntity<RESTError>(new RESTError(5, "Teacher does not teach requested subject"), HttpStatus.BAD_REQUEST);				
			}
			LOGGER.info("LOGGER: Checking if teacher ID {} teaches subject ID {} to student ID {}", teacherID, subjectID, studentID);
			StudentEntity stu = studentRepository.findById(studentID).get();
			SubjectEntity sub = subjectRepository.findById(subjectID).get();
			TeacherEntity tea = teacherRepository.findById(teacherID).get();
			TeachingEntity tch = teachingRepository.findBySubjectAndTeacherAndSchoolClass(sub, tea, stu.getSchoolClass());
			if (tch==null) {
				LOGGER.info("LOGGER: Teacher ID {} does not teach subject ID {} to student ID {}", teacherID, subjectID, studentID);
				return new ResponseEntity<RESTError>(new RESTError(6, "Teacher does not teach requested subject"), HttpStatus.BAD_REQUEST);				
			}
			else {
				LOGGER.info("LOGGER: Grading ID {} exists. Parametars are correct. Updating grading ID ", id);	
				StudentEntity student = studentRepository.findById(studentID).get();
				SubjectEntity subject = subjectRepository.findById(subjectID).get();
				TeacherEntity teacher = teacherRepository.findById(teacherID).get();
				GradingEntity grading = gradingRepository.findById(id).get();
				grading.setStudent(student);
				grading.setTeacher(teacher); 
				grading.setSubject(subject);
				grading.setExamType(examType);
				grading.setExamDate(LocalDate.parse(examDate));
				grading.setExamGrade(examGrade);
				gradingRepository.save(grading);
				
				LOGGER.info("LOGGER: Sending an email to parent");
				EmailObject object = new EmailObject();
				object.setTo(student.getParent().getEmail());
				object.setSubject("Grading notification (GRADE UPDATE) " + LocalDate.now());
				String mailText = "";
				mailText = mailText + "\nSTUDENT: " + student.getFirstName() + " " + student.getLastName();
				mailText = mailText + "\n\nDate of exam: " + grading.getExamDate();
				mailText = mailText + "\nGrade: " + grading.getExamGrade();
				mailText = mailText + "\nSubject: " + subject.getName();
				mailText = mailText + "\nTeacher: " + teacher.getFirstName() + " " + teacher.getLastName();
				object.setText(mailText);
				emailService.sendSimpleMessage(object);
				
				LOGGER.info("LOGGER: Grading ID {} updated. Email sent successfully", id);
				return new ResponseEntity<RESTError>(new RESTError(7, "Grading updated and email sent successsfully"), HttpStatus.OK);
			}
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(8, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/grading/grade/update/{id}")
	public ResponseEntity<?> updateGrading(@PathVariable Integer id,
										   @RequestParam Integer newExamGrade)  {
		try { 		
			
			LOGGER.info("LOGGER: Checking if grading ID {} exists", id);
			if (!gradingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Grading ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Grading ID not found"), HttpStatus.NOT_FOUND);				
			}
			else {			
				LOGGER.info("LOGGER: Grading ID {} exists. Updating grade", id);	
				GradingEntity grading = gradingRepository.findById(id).get();
				StudentEntity student = grading.getStudent();
				SubjectEntity subject = grading.getSubject();
				TeacherEntity teacher = grading.getTeacher();
				Integer oldExamGrade = grading.getExamGrade();
				grading.setExamGrade(newExamGrade);
				gradingRepository.save(grading);
				
				LOGGER.info("LOGGER: Sending an email to parent");
				EmailObject object = new EmailObject();
				object.setTo(student.getParent().getEmail());
				object.setSubject("Grading notification (GRADE UPDATE) " + LocalDate.now());
				String mailText = "";
				mailText = mailText + "\nSTUDENT: " + student.getFirstName() + " " + student.getLastName();
				mailText = mailText + "\n\nDate of exam: " + grading.getExamDate();
				mailText = mailText + "\nOld grade: " + oldExamGrade;
				mailText = mailText + "\nNew grade: " + newExamGrade;
				mailText = mailText + "\nSubject: " + subject.getName();
				mailText = mailText + "\nTeacher: " + teacher.getFirstName() + " " + teacher.getLastName();
				object.setText(mailText);
				emailService.sendSimpleMessage(object);
				
				LOGGER.info("LOGGER: Grade for grading id {} updated. Email sent successfully", id);
				return new ResponseEntity<RESTError>(new RESTError(2, "Grading updated and email sent successsfully"), HttpStatus.OK);
			}
		}
		catch (Exception e) { 
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/grading/delete/{id}")
	public ResponseEntity<?> deleteGradingById(@PathVariable Integer id) {
		try { 
			LOGGER.info("LOGGER: Checking if grading ID {} exists", id);
			if (!gradingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Grading ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(1, "Grading ID not found"), HttpStatus.NOT_FOUND);				
			}
			LOGGER.info("LOGGER: Grading ID {} exists", id);			
			GradingEntity grading = gradingRepository.findById(id).get();
			gradingRepository.delete(grading);
			LOGGER.info("LOGGER: Grading ID {} deleted", id);
			return new ResponseEntity<RESTError>(new RESTError(2, "Grading ID deleted successsfully"), HttpStatus.OK);
		} 
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
