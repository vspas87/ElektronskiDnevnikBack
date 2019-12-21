package BackEndProject.BackEndProject.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.ClassEntity;
import BackEndProject.BackEndProject.entites.SubjectEntity;
import BackEndProject.BackEndProject.entites.TeacherEntity;
import BackEndProject.BackEndProject.entites.TeachingEntity;
import org.springframework.data.repository.CrudRepository;


public interface TeachingRepository extends CrudRepository <TeachingEntity, Integer> {
		
		List <TeachingEntity> findBySubject(SubjectEntity subjectID);
		
		List <TeachingEntity> findByTeacher(TeacherEntity teacherID);
		
		List <TeachingEntity> findBySchoolClass(ClassEntity classID);
		
		TeachingEntity findBySubjectAndTeacherAndSchoolClass(SubjectEntity subjectID, TeacherEntity teacherID, ClassEntity classID);



}
