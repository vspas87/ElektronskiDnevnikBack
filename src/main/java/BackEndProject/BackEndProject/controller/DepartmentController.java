package BackEndProject.BackEndProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import BackEndProject.BackEndProject.entites.AdministratorEntity;
import BackEndProject.BackEndProject.entites.DepartmentEntity;
import BackEndProject.BackEndProject.entites.SubjectEntity;
import BackEndProject.BackEndProject.repositories.DepartmentRepository;
import BackEndProject.BackEndProject.repositories.TeacherRepository;
import BackEndProject.BackEndProject.util.RESTError;

@RestController
@RequestMapping(path = "/dnevnik/department")
public class DepartmentController {
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	//get All department
	
		@Secured("ROLE_ADMIN")
		@RequestMapping(method = RequestMethod.GET) 
		public ResponseEntity<?> getAllDepart() {
			List<DepartmentEntity> depart = (List<DepartmentEntity>) departmentRepository.findAll();
			return new ResponseEntity<List<DepartmentEntity>>(depart, HttpStatus.OK);
		}
		
	// update department
		
		@Secured("ROLE_ADMIN")
		@RequestMapping(method = RequestMethod.POST, value="/update/{id}")
		public ResponseEntity<?> updateDepart(@PathVariable Integer id, 
				@RequestParam String name, 
				@RequestParam Integer classroom) 
		{
				for (DepartmentEntity department : departmentRepository.findAll()) {
					if (department.getId().equals(id)) {
						department.setName(name);
						department.setClassroom(classroom);
						departmentRepository.save(department);
						return new ResponseEntity<RESTError>(new RESTError(1, "Depart ID updated successsfully"), HttpStatus.OK);
						}
					}
					return new ResponseEntity<RESTError>(new RESTError(2, "Depart ID not found"), HttpStatus.NOT_FOUND);
			}
	
		// add department
		
		@Secured("ROLE_ADMIN")
		@RequestMapping(method = RequestMethod.POST, value="/add")
		public ResponseEntity<?> addNewDepart(@RequestParam String name,  @RequestParam Integer classroom) {
			
					DepartmentEntity depart = new DepartmentEntity();
					depart.setName(name); 
					depart.setClassroom(classroom);
					departmentRepository.save(depart);
					return new ResponseEntity<RESTError>(new RESTError(2, "Department added successsfully"), HttpStatus.OK);
				}
		}

