package BackEndProject.BackEndProject.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.ClassEntity;


public interface ClassRepository extends CrudRepository <ClassEntity, Integer> {
	
	List <ClassEntity> findBySchoolYear(Integer schoolYear);
	
	ClassEntity findByClassNameIgnoreCase(String className);
	

}
