package BackEndProject.BackEndProject.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.GradingEntity;
import BackEndProject.BackEndProject.entites.ParentEntity;
import BackEndProject.BackEndProject.entites.SubjectEntity;
import BackEndProject.BackEndProject.entites.TeacherEntity;

public interface SubjectRepository extends CrudRepository <SubjectEntity, Integer> {
	
	List <SubjectEntity> findByWeeklyFund(Integer weeklyFund);
	
	SubjectEntity findByNameIgnoreCase(String name);
	
	List <SubjectEntity> findByTeacher(TeacherEntity teacherID);

}
