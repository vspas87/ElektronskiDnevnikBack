package BackEndProject.BackEndProject.service;

import java.util.List;

import BackEndProject.BackEndProject.entites.StudentEntity;


public interface StudentDao {
	public List<StudentEntity> findStudentsByClasses(Integer classes);
	public List<StudentEntity> findStudentsByParentId(Integer parentId);
	public List<StudentEntity> findStudentsByTeacherId(Integer teacherId);
	public List<StudentEntity> findAllStudents();

}
