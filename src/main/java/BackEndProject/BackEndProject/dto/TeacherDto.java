package BackEndProject.BackEndProject.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class TeacherDto {
	
	@NotNull(message = "First name is required.")
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@NotNull(message = "Last name is required.")
	@Column(name = "last_name", nullable = false)
	private String lastName;

	public TeacherDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TeacherDto(@NotNull(message = "First name is required.") String firstName,
			@NotNull(message = "Last name is required.") String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
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
		return "TeacherDto [firstName=" + firstName + ", lastName=" + lastName + "]";
	}

}
