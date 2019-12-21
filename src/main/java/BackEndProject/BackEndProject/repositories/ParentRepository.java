package BackEndProject.BackEndProject.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.ParentEntity;


public interface ParentRepository extends CrudRepository <ParentEntity, Integer> {
	
	@Query("DELETE ParentEntity p WHERE p.user.id = :userID")
        void deleteByUserId(Integer userID);
	
	ParentEntity findByUserId(Integer userID);
	
	List<ParentEntity> findListByUserId(Integer userID);
	
	ParentEntity findParentByEmail(String email);
	

}
