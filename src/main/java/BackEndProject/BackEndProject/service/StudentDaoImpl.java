package BackEndProject.BackEndProject.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import BackEndProject.BackEndProject.entites.StudentEntity;

	
	@Service
	public class StudentDaoImpl implements StudentDao {
		
		@PersistenceContext
		private EntityManager em;
		
		@Override
		public List<StudentEntity> findStudentsByClasses(Integer classes) {
			String sql= "select s.classes, Concat(u.firstname,' ', u.lastname) from UserEntity u inner join StudentEntity s "
					+ "on u.id=s.userId where classes=:classes";
			
			Query query = em.createQuery(sql);
			query.setParameter("classes", classes);
			
			List<StudentEntity> result= new ArrayList<>();
			result=query.getResultList();
			return result;
		}
		
		//Query za parents KIDS
		@Override
		public List<StudentEntity> findStudentsByParentId(Integer parentId) {
			String msql="SELECT s.first_name, s.last_name, g.exam_date, g.exam_grade,g.exam_type "
					+ "from student s inner join grading g on s.id=g.student_id where parent_id=:parentId";
			Query query=em.createQuery(msql);
			query.setParameter("parentId", parentId);
			
			List<StudentEntity> result= new ArrayList<>();
			result=query.getResultList();
			return result;
		}
		
		//Query za teachers - kojim studentima predaje
		@Override
		public List<StudentEntity> findStudentsByTeacherId(Integer teacherId) {
			String tsql="select CONCAT(u.firstname,' ', u.lastname), s.classes from TeachingEntity t inner join StudentEntity s "
					+ "on t.departmentId=s.department inner join UserEntity u on u.id=s.idStudent where teacher_id=:teacherId";
			Query query=em.createQuery(tsql);
			query.setParameter("teacherId", teacherId);
			List<StudentEntity> result= new ArrayList<>();
			result=query.getResultList();
			return result;
		}
		
		//Svi studenti
		@Override
		public List<StudentEntity> findAllStudents() {
			String sl="select u.firstname, u.lastname, u.email from UserEntity u inner join StudentEntity s on u.id=s.idStudent";
			Query query=em.createQuery(sl);
			List<StudentEntity> result= new ArrayList();
			result= query.getResultList();
			return result;
	}
	}

