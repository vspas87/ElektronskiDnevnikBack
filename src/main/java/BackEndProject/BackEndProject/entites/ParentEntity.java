package BackEndProject.BackEndProject.entites;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({"id", "firstName", "lastName", "email"})
@Table (name = "PARENT")
public class ParentEntity extends PersonEntity {

	@Email(message = "Email is not valid.")
	private String email;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@JsonIgnore
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	List<StudentEntity> students = new ArrayList<StudentEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "parent", fetch= FetchType.LAZY, cascade= CascadeType.REFRESH)
	List<GradingEntity> grading= new ArrayList<GradingEntity>();

	public ParentEntity() {
		super();
	}

	public ParentEntity(Integer parentid,
			@NotNull(message = "First name is required.") @Size(min = 1, max = 20, message = "First name must have between {min} and {max} characters.") String firstName,
			@NotNull(message = "Last name is required.") @Size(min = 1, max = 20, message = "Last name must have between {min} and {max} characters.") String lastName) {
		super(parentid, firstName, lastName);
		// TODO Auto-generated constructor stub
	}

	public ParentEntity(@Email(message = "Email is not valid.") String email, UserEntity user,
			List<StudentEntity> students) {
		super();
		this.email = email;
		this.user = user;
		this.students = students;
	}
	
	public ParentEntity(List<GradingEntity> grading) {
		super();
		this.grading = grading;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}
	
	public List<GradingEntity> getGrading() {
		return grading;
	}

	public void setGrading(List<GradingEntity> grading) {
		this.grading = grading;
	}


	@Override
	public String toString() {
		return "ParentEntity [email=" + email + ", user=" + user + ", students=" + students + "]";
	}
	
}

