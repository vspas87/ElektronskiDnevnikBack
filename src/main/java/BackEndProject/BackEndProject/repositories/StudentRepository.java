package BackEndProject.BackEndProject.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.StudentEntity;


public interface StudentRepository extends CrudRepository <StudentEntity, Integer> {
	
	StudentEntity findByUserId(Integer userID);
	
	List<StudentEntity> findListByUserId(Integer userID);
	
	List <StudentEntity> findByParentId(Integer userID);
	
	
}


