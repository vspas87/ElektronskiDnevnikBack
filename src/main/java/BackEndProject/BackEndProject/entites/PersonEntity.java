package BackEndProject.BackEndProject.entites;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

@MappedSuperclass
public class PersonEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("id")
	private Integer id;
	
	@NotNull(message = "First name is required.")
	@Size(min = 1, max = 20, message = "First name must have between {min} and {max} characters.")
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@NotNull(message = "Last name is required.")
	@Size(min = 1, max = 20, message = "Last name must have between {min} and {max} characters.")
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Version
	private Integer version;
	
	
	public PersonEntity() {
		super();
	}
	
	public PersonEntity(Integer id,
			@NotNull(message = "First name is required.") @Size(min = 1, max = 20, message = "First name must have between {min} and {max} characters.") String firstName,
			@NotNull(message = "Last name is required.") @Size(min = 1, max = 20, message = "Last name must have between {min} and {max} characters.") String lastName) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "PersonEntity [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
}
