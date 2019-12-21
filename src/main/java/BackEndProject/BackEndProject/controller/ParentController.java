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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import BackEndProject.BackEndProject.entites.ERole;
import BackEndProject.BackEndProject.entites.GradingEntity;
import BackEndProject.BackEndProject.entites.ParentEntity;
import BackEndProject.BackEndProject.entites.StudentEntity;
import BackEndProject.BackEndProject.entites.TeacherEntity;
import BackEndProject.BackEndProject.repositories.GradingRepository;
import BackEndProject.BackEndProject.repositories.ParentRepository;
import BackEndProject.BackEndProject.repositories.StudentRepository;
import BackEndProject.BackEndProject.repositories.UserRepository;
import BackEndProject.BackEndProject.service.StudentDao;
import BackEndProject.BackEndProject.util.RESTError;


@RestController
@RequestMapping(path = "/dnevnik")
public class ParentController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ParentRepository parentRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private GradingRepository gradingRepository;
	
	@Autowired
	StudentDao studentDao;
	
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(this.getClass());
	
	
	//studentID - GRADINGS 
	
	@Secured({"ROLE_PARENT"})
	@RequestMapping(method = RequestMethod.GET, value="/parentview/{studentID}") 
	public ResponseEntity<?> geGradingsForStudent(@PathVariable Integer studentID) {
		
		try { 
			LOGGER.info("LOGGER: Getting current logged-in user details");
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();
			LOGGER.info("LOGGER: Current logged-in: username = {}; userID = {}, role = {}", username, userID, role);
			
			LOGGER.info("LOGGER: Checking if student ID {} exists",  studentID);
			if (!studentRepository.findById(studentID).isPresent()) {
				LOGGER.info("LOGGER: Student ID {} not found", studentID);
				return new ResponseEntity<RESTError>(new RESTError(1, "Student ID not found"), HttpStatus.NOT_FOUND);				
			}			
			
			LOGGER.info("LOGGER: Checking if role = ROLE_PARENT");
			if (role.equals(ERole.ROLE_PARENT)) {
				
				LOGGER.info("LOGGER: ROLE_PARENT confirmed. Access approved. Getting parent ID");
				ParentEntity parent = parentRepository.findByUserId(userID);
				Integer parentID = parent.getId();
				
				LOGGER.info("LOGGER: Parent ID = {}. Checking if student is his/her child", parentID, studentID);
				StudentEntity student = studentRepository.findById(studentID).get();
				LOGGER.info("LOGGER: Parent ID = {}", parentID);
				LOGGER.info("LOGGER: student.getParent() = {}", student.getParent().getId());
				
				if (parentID.equals(student.getParent().getId())) {
					LOGGER.info("LOGGER: Student's ID: {}. Checking if there are gradings for the student");
					List<GradingEntity> gradings = gradingRepository.findByStudent(student);
					if (!gradings.isEmpty()) {
						LOGGER.info("LOGGER: Gradings are found");
						return new ResponseEntity<List<GradingEntity>>(gradings, HttpStatus.OK);	
					}
					else {
					LOGGER.info("LOGGER: List is empty. No gradings are found");
					return new ResponseEntity<RESTError>(new RESTError(2, "List is empty. No gradings are found"), HttpStatus.NOT_FOUND);
					}
				
				}
			}
			LOGGER.info("LOGGER: Access to data denied");
			return new ResponseEntity<RESTError>(new RESTError(3, "Access denied"), HttpStatus.FORBIDDEN);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Find students by parent Id
	@RequestMapping(method = RequestMethod.GET, value="/parent/{parentId}") 
	public ResponseEntity<?> getParentStudents(@PathVariable Integer parentId) {
				return new ResponseEntity<List<StudentEntity>>(studentDao.findStudentsByParentId(parentId), HttpStatus.OK);
			
	}

	
	@Secured({"ROLE_PARENT"})
	@RequestMapping(method = RequestMethod.GET, value="/grading/parent") 
	public ResponseEntity<?> getParentGradings() {
		
		try { 
			LOGGER.info("LOGGER: Getting current logged-in user details");
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();
			
			
			LOGGER.info("LOGGER: Checking if role = ROLE_STUDENT");
			if (role.equals((ERole.ROLE_PARENT))) {
			
				LOGGER.info("LOGGER: ROLE_STUDENT confirmed. Access approved. Getting student's ID");
				ParentEntity parent= parentRepository.findByUserId(userID);
				List<GradingEntity>gradings= gradingRepository.findByParent(parent);
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
				
				
	//Rest api za parent profil
			@Secured({"ROLE_PARENT"})
			@RequestMapping(method = RequestMethod.GET, value="/parent/profil") 
			public ResponseEntity<?> getParentProfil() {
				
				Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				String username = ((User)principal).getUsername();
				Integer userID = userRepository.findUserByUsername(username).getId();
				ERole role = userRepository.findUserByUsername(username).getRole();

				if (role.equals(ERole.ROLE_PARENT)) {
					List<ParentEntity> parent = parentRepository.findListByUserId(userID);
					return new ResponseEntity<List<ParentEntity>>(parent, HttpStatus.OK);
				}
				return new ResponseEntity<RESTError>(new RESTError(1, "Something doesnt work" ), HttpStatus.BAD_REQUEST);
		}
		}		
			

	
