package BackEndProject.BackEndProject.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import BackEndProject.BackEndProject.email.EmailObject;
import BackEndProject.BackEndProject.entites.ClassEntity;
import BackEndProject.BackEndProject.entites.ERole;
import BackEndProject.BackEndProject.entites.GradingEntity;
import BackEndProject.BackEndProject.entites.ParentEntity;
import BackEndProject.BackEndProject.entites.StudentEntity;
import BackEndProject.BackEndProject.entites.SubjectEntity;
import BackEndProject.BackEndProject.entites.TeacherEntity;
import BackEndProject.BackEndProject.entites.TeachingEntity;
import BackEndProject.BackEndProject.repositories.ClassRepository;
import BackEndProject.BackEndProject.repositories.GradingRepository;
import BackEndProject.BackEndProject.repositories.ParentRepository;
import BackEndProject.BackEndProject.repositories.StudentRepository;
import BackEndProject.BackEndProject.repositories.SubjectRepository;
import BackEndProject.BackEndProject.repositories.TeacherRepository;
import BackEndProject.BackEndProject.repositories.TeachingRepository;
import BackEndProject.BackEndProject.repositories.UserRepository;
import BackEndProject.BackEndProject.service.EmailService;
import BackEndProject.BackEndProject.service.StudentDaoImpl;
import BackEndProject.BackEndProject.util.RESTError;

@RestController
@RequestMapping(path = "/dnevnik")
public class TeacherController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private SubjectRepository subjectRepository;
	
	@Autowired
	private TeachingRepository teachingRepository;
	
	@Autowired
	private GradingRepository gradingRepository;
	
	@Autowired
	private ClassRepository classRepository;
	
	@Autowired
	private ParentRepository parentRepository;
	
	@Autowired
	private EmailService emailService;
	
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(this.getClass());
	
	

	//GRADINGS 
	
	@Secured({"ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.GET, value="grading/teacher") 
	public ResponseEntity<?> getGradingsForTeacher(@RequestParam Integer studentID,
												   @RequestParam Integer subjectID) {
		
		try { 
			LOGGER.info("LOGGER: Getting current logged-in user details");
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();
			LOGGER.info("LOGGER: Current logged-in: username = {}; userID = {}, role = {}", username, userID, role);
			
			LOGGER.info("LOGGER: Checking if role = ROLE_TEACHER");
			if (!role.equals(ERole.ROLE_TEACHER)) {
				LOGGER.info("LOGGER: Access to data denied");
				return new ResponseEntity<RESTError>(new RESTError(1, "Access denied. Wrong role"), HttpStatus.FORBIDDEN);
			}
			
			LOGGER.info("LOGGER: ROLE_TEACHER confirmed. Access approved. Checking inputs");
			
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			if (!studentRepository.findById(studentID).isPresent()) {
				LOGGER.info("LOGGER: Student ID {} not found", studentID);
				return new ResponseEntity<RESTError>(new RESTError(3, "Student ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			SubjectEntity subject = subjectRepository.findById(subjectID).get();
			StudentEntity student = studentRepository.findById(studentID).get();
			TeacherEntity teacher = teacherRepository.findByUserId(userID);
			ClassEntity schoolClass = classRepository.findById(student.getSchoolClass().getId()).get();
			TeachingEntity teaching = teachingRepository.findBySubjectAndTeacherAndSchoolClass(subject, teacher, schoolClass);
						
			LOGGER.info("LOGGER: Teacher ID = {}. Checking if he/she teaches the subject ID {} to student {}", teacher.getId(), subjectID, studentID);
			if (teaching == null) {
				LOGGER.info("LOGGER: Access to data denied");
				return new ResponseEntity<RESTError>(new RESTError(2, "Access denied"), HttpStatus.FORBIDDEN);
			}
	
			LOGGER.info("LOGGER: Checking if there are gradings");
			List <GradingEntity> gradings = gradingRepository.findByStudentAndSubjectAndTeacher (student, subject, teacher);
			if (!gradings.isEmpty()) {
				LOGGER.info("LOGGER: Gradings are found");
				return new ResponseEntity<List<GradingEntity>>(gradings, HttpStatus.OK);	
			}
			else {			
			LOGGER.info("LOGGER: List is empty. No gradings are found");
			return new ResponseEntity<RESTError>(new RESTError(3, "List is empty. No gradings are found"), HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	//add - GRADINGS 
	
	@Secured({"ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.POST, value="/grading/teacher/add") 
	public ResponseEntity<?> addGradingsForTeacher(@RequestParam Integer studentID,
												   @RequestParam Integer subjectID, 
												   @RequestParam String examType,
												   @RequestParam Integer examGrade,
												   @RequestParam Integer parentID,
												   @RequestParam Integer classID
												   ) {
		
		try { 
			LOGGER.info("LOGGER: Getting current logged-in user details");
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();
			LOGGER.info("LOGGER: Current logged-in: username = {}; userID = {}, role = {}", username, userID, role);
			
			LOGGER.info("LOGGER: Checking if role = ROLE_TEACHER");
			if (!role.equals(ERole.ROLE_TEACHER)) {
				LOGGER.info("LOGGER: Access denied");
				return new ResponseEntity<RESTError>(new RESTError(1, "Access denied. Wrong role"), HttpStatus.FORBIDDEN);
			}
			
			LOGGER.info("LOGGER: ROLE_TEACHER confirmed. Access approved. Checking inputs");
			
			if (!subjectRepository.findById(subjectID).isPresent()) {
				LOGGER.info("LOGGER: Subject ID {} not found", subjectID);
				return new ResponseEntity<RESTError>(new RESTError(2, "Subject ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			if (!studentRepository.findById(studentID).isPresent()) {
				LOGGER.info("LOGGER: Student ID {} not found", studentID);
				return new ResponseEntity<RESTError>(new RESTError(3, "Student ID not found"), HttpStatus.NOT_FOUND);				
			}
			
			if (examType == null || examGrade == null) {
				LOGGER.info("LOGGER: Some fields are empty");
				return new ResponseEntity<RESTError>(new RESTError(4, "Some fields are empty"), HttpStatus.BAD_REQUEST);	
			}				
			
			SubjectEntity subject = subjectRepository.findById(subjectID).get();
			StudentEntity student = studentRepository.findById(studentID).get();
			TeacherEntity teacher = teacherRepository.findByUserId(userID);
			ParentEntity parent= parentRepository.findById(parentID).get();
			ClassEntity schoolClass = classRepository.findById(student.getSchoolClass().getId()).get();
			TeachingEntity teaching = teachingRepository.findBySubjectAndTeacherAndSchoolClass(subject, teacher, schoolClass);
						
			LOGGER.info("LOGGER: Teacher ID = {}. Checking if he/she teaches the subject ID {} to student {}", teacher.getId(), subjectID, studentID);
			if (teaching == null) {
				LOGGER.info("LOGGER: Access to data denied");
				return new ResponseEntity<RESTError>(new RESTError(2, "Access denied"), HttpStatus.FORBIDDEN);
			}
			
			LOGGER.info("LOGGER: Parametars are correct. Creating grading");	
			GradingEntity grading = new GradingEntity();
			grading.setStudent(student);
			grading.setTeacher(teacher); 
			grading.setParent(parent);
			grading.setClasses(schoolClass);
			grading.setSubject(subject);
			grading.setExamType(examType);
			grading.setExamGrade(examGrade);
			gradingRepository.save(grading);
			
			return new ResponseEntity<RESTError>(new RESTError(6, "Grading added and and email sent successsfully"), HttpStatus.OK);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.POST, value="/grading/update/{id}")
	public ResponseEntity<?> updateGrade(@PathVariable Integer id, @RequestParam Integer newGrade,
			@RequestParam String examType) {
			
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((User)principal).getUsername();
		Integer userID = userRepository.findUserByUsername(username).getId();
		ERole role = userRepository.findUserByUsername(username).getRole();
		TeacherEntity teacher = teacherRepository.findByUserId(userID);

			if (!gradingRepository.findById(id).isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(1, "Class ID not found"), HttpStatus.NOT_FOUND);				
			}
			GradingEntity grading = gradingRepository.findById(id).get();
			grading.setExamGrade(newGrade);
			grading.setExamType(examType);
			gradingRepository.save(grading);
			return new ResponseEntity<RESTError>(new RESTError(2, "grading ID updated successsfully"), HttpStatus.OK);
		}
	
	
	@Secured({"ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.POST, value="/grading/teacher/update/{id}") 
	public ResponseEntity<?> upgradeGradingsForTeacher(@PathVariable Integer id,
												   @RequestParam Integer newExamGrade) {
		
		try { 
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();
			TeacherEntity teacher = teacherRepository.findByUserId(userID);
	
			if (!role.equals(ERole.ROLE_TEACHER)) {
				LOGGER.info("LOGGER: Access denied");
				return new ResponseEntity<RESTError>(new RESTError(1, "Access denied. Wrong role"), HttpStatus.FORBIDDEN);
			}
			
			LOGGER.info("LOGGER: ROLE_TEACHER confirmed. Checking if grading exists");
			if (!gradingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Grading ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(2, "Grading ID not found"), HttpStatus.NOT_FOUND);				
			}	
			
			LOGGER.info("LOGGER: Grading exists. Checking if teacher can make changes");
			GradingEntity grading = gradingRepository.findById(id).get();
			if (!grading.getTeacher().equals(teacher)) {
				LOGGER.info("LOGGER: No authorities for making changes on this grading");
				return new ResponseEntity<RESTError>(new RESTError(3, "No authorities for making changes on this grading"), HttpStatus.FORBIDDEN);				
			}			
			
			StudentEntity student = grading.getStudent();
			SubjectEntity subject = grading.getSubject();
			Integer oldExamGrade = grading.getExamGrade();
			grading.setExamGrade(newExamGrade);
			gradingRepository.save(grading);
			
			return new ResponseEntity<RESTError>(new RESTError(2, "Grading updated and email sent successsfully"), HttpStatus.OK);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(4, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	
	//delete - GRADINGS 
	
	@Secured({"ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.DELETE, value="/grading/teacher/delete/{id}") 
	public ResponseEntity<?> deleteGradingsForTeacher(@PathVariable Integer id) {
		
		try { 
			LOGGER.info("LOGGER: Getting current logged-in user details");
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();
			TeacherEntity teacher = teacherRepository.findByUserId(userID);
			LOGGER.info("LOGGER: Current logged-in: username = {}; userID = {}, role = {}", username, userID, role);
			
			LOGGER.info("LOGGER: Checking if role = ROLE_TEACHER");
			if (!role.equals(ERole.ROLE_TEACHER)) {
				LOGGER.info("LOGGER: Access denied");
				return new ResponseEntity<RESTError>(new RESTError(1, "Access denied. Wrong role"), HttpStatus.FORBIDDEN);
			}
			
			LOGGER.info("LOGGER: ROLE_TEACHER confirmed. Checking if grading exists");
			if (!gradingRepository.findById(id).isPresent()) {
				LOGGER.info("LOGGER: Grading ID {} not found", id);
				return new ResponseEntity<RESTError>(new RESTError(2, "Grading ID not found"), HttpStatus.NOT_FOUND);				
			}	
			
			LOGGER.info("LOGGER: Grading exists. Checking if teacher can delete grading");
			GradingEntity grading = gradingRepository.findById(id).get();
			if (!grading.getTeacher().equals(teacher)) {
				LOGGER.info("LOGGER: No authorities for deleting this grading");
				return new ResponseEntity<RESTError>(new RESTError(3, "No authorities for deleting this gradingg"), HttpStatus.FORBIDDEN);				
			}
			
			gradingRepository.delete(grading);
			
			LOGGER.info("LOGGER: Grading deleted successfully");
			return new ResponseEntity<RESTError>(new RESTError(4, "Grading deleted successfully"), HttpStatus.OK);
		}
		catch (Exception e) { 
			LOGGER.error("LOGGER: Error in executing the request: Communication Error", e);
			return new ResponseEntity<RESTError>(new RESTError(5, "Exception occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Rest api za teacher profil
		@Secured({"ROLE_TEACHER"})
		@RequestMapping(method = RequestMethod.GET, value="/teacher/profil") 
		public ResponseEntity<?> getStudentProfil() {
			
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((User)principal).getUsername();
			Integer userID = userRepository.findUserByUsername(username).getId();
			ERole role = userRepository.findUserByUsername(username).getRole();

			if (role.equals(ERole.ROLE_TEACHER)) {
				List<TeacherEntity> teacher = teacherRepository.findListByUserId(userID);
				return new ResponseEntity<List<TeacherEntity>>(teacher, HttpStatus.OK);
			}
			return new ResponseEntity<RESTError>(new RESTError(1, "Something doesnt work" ), HttpStatus.BAD_REQUEST);
	}
	}
