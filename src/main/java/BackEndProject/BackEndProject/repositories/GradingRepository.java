package BackEndProject.BackEndProject.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.ClassEntity;
import BackEndProject.BackEndProject.entites.GradingEntity;
import BackEndProject.BackEndProject.entites.ParentEntity;
import BackEndProject.BackEndProject.entites.StudentEntity;
import BackEndProject.BackEndProject.entites.SubjectEntity;
import BackEndProject.BackEndProject.entites.TeacherEntity;
import BackEndProject.BackEndProject.entites.TeachingEntity;


public interface GradingRepository extends CrudRepository <GradingEntity, Integer> {
	
	List <GradingEntity> findByStudent(StudentEntity studentID);
	
	List <GradingEntity> findBySubject(SubjectEntity subjectID);
	

	List<GradingEntity> findByTeacher(TeacherEntity teacherID);
	List<GradingEntity> findByClasses(ClassEntity classID);
	
	List <GradingEntity> findByTeacherAndClasses(TeacherEntity teacherID, ClassEntity classID);
	
	List <GradingEntity> findByStudentAndSubjectAndTeacher (StudentEntity studentID, SubjectEntity subjectID, TeacherEntity teacherID);
	
	List<GradingEntity> findByParent(ParentEntity parentID);


	

		
}
