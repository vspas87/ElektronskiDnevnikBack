package BackEndProject.BackEndProject.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import BackEndProject.BackEndProject.entites.AdministratorEntity;


public interface AdministratorRepository extends CrudRepository <AdministratorEntity, Integer> {
	
	AdministratorEntity findByUserId(Integer userID);
	
	List<AdministratorEntity> findByLastNameIgnoreCase(String lastName);
		
	List<AdministratorEntity> findByFirstNameAndLastNameAllIgnoreCase(String firstName, String lastName);
	
	List<AdministratorEntity> findByFirstNameOrLastNameAllIgnoreCase(String firstName, String lastName);


}
