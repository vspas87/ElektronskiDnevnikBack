package BackEndProject.BackEndProject.entites;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@Entity
@JsonPropertyOrder({"id", "firstName", "lastName"})
@Table (name = "ADMINISTRATOR")
public class AdministratorEntity extends PersonEntity {
	
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "user_id")
	private UserEntity user;
	

	public AdministratorEntity() {
		super();
	}

	public AdministratorEntity(Integer id,
			@NotNull(message = "First name is required.") @Size(min = 1, max = 20, message = "First name must have between {min} and {max} characters.") String firstName,
			@NotNull(message = "Last name is required.") @Size(min = 1, max = 20, message = "Last name must have between {min} and {max} characters.") String lastName) {
		super(id, firstName, lastName);

	}

	public AdministratorEntity(UserEntity user) {
		super();
		this.user = user;
	}
	
	

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return "AdministratorEntity [user=" + user + "]";
	}

}
