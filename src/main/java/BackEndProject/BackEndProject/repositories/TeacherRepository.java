package BackEndProject.BackEndProject.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.TeacherEntity;


public interface TeacherRepository extends CrudRepository <TeacherEntity, Integer> {
	
	TeacherEntity findByUserId(Integer userID);
	
	List<TeacherEntity> findListByUserId(Integer userID);



}
