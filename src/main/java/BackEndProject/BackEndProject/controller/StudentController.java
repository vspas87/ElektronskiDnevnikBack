package BackEndProject.BackEndProject.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import BackEndProject.BackEndProject.entites.ERole;
import BackEndProject.BackEndProject.entites.GradingEntity;
import BackEndProject.BackEndProject.entites.StudentEntity;
import BackEndProject.BackEndProject.entites.UserEntity;
import BackEndProject.BackEndProject.repositories.GradingRepository;
import BackEndProject.BackEndProject.repositories.StudentRepository;
import BackEndProject.BackEndProject.repositories.UserRepository;
import BackEndProject.BackEndProject.util.RESTError;

@RestController
@RequestMapping(path = "/dnevnik")
public class StudentController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private GradingRepository gradingRepository;
	
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(this.getClass());
	
	
	
	//getAll GRADINGS
	
	@Secured({"ROLE_STUDENT", "ROLE_PARENT"})
	@RequestMapping(method = RequestMethod.GET, value="/grading/student") 
	public ResponseEntity<?> getGradingsForStudent() {
		
		try { 
			LOGGER.info("LOGGER: Getting current logged-in user details");
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();
			LOGGER.info("LOGGER: Current logged-in: username = {}; userID = {}, role = {}", username, userID, role);
			
			LOGGER.info("LOGGER: Checking if role = ROLE_STUDENT");
			if (role.equals((ERole.ROLE_STUDENT))) {
			
				LOGGER.info("LOGGER: ROLE_STUDENT confirmed. Access approved. Getting student's ID");
				StudentEntity student = studentRepository.findByUserId(userID);
				
				LOGGER.info("LOGGER: Student's ID: {}. Checking if there are gradings for the student", student.getId());
				List<GradingEntity> gradings = gradingRepository.findByStudent(student);
				if (!gradings.isEmpty()) {
					LOGGER.info("LOGGER: Gradings are found");
					return new ResponseEntity<List<GradingEntity>>(gradings, HttpStatus.OK);	
				}
				else {
					LOGGER.info("LOGGER: List is empty. No gradings are found");
					return new ResponseEntity<RESTError>(new RESTError(1, "List is empty. No gradings are found"), HttpStatus.NOT_FOUND);
				}
			}
			LOGGER.info("LOGGER: Access to data denied");
			return new ResponseEntity<RESTError>(new RESTError(2, "Access denied"), HttpStatus.FORBIDDEN);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(3, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Rest api za student profil
	@Secured({"ROLE_STUDENT"})
	@RequestMapping(method = RequestMethod.GET, value="/student/profil") 
	public ResponseEntity<?> getStudentProfil() {
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((User)principal).getUsername();
		Integer userID = userRepository.findUserByUsername(username).getId();
		ERole role = userRepository.findUserByUsername(username).getRole();

		if (role.equals(ERole.ROLE_STUDENT)) {
			List<StudentEntity> student = studentRepository.findListByUserId(userID);
			return new ResponseEntity<List<StudentEntity>>(student, HttpStatus.OK);
		}
		return new ResponseEntity<RESTError>(new RESTError(1, "Something doesnt work" ), HttpStatus.BAD_REQUEST);
}
	
	
	
}