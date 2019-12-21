package BackEndProject.BackEndProject.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.ERole;
import BackEndProject.BackEndProject.entites.StudentEntity;
import BackEndProject.BackEndProject.entites.UserEntity;


public interface UserRepository extends CrudRepository<UserEntity, Integer>  {
	
	UserEntity findUserByUsername(String username);
	
	List<UserEntity> findByRole(ERole role);

	List<UserEntity> findById(StudentEntity student);	

	

}


