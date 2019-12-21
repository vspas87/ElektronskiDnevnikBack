package BackEndProject.BackEndProject.entites;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "version"})
@JsonPropertyOrder({"id", "username", "role"})
@Table (name = "USER")
public class UserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("USER_ID")
	@Column(name = "user_id")
	private Integer id;
	
	@NotNull(message = "Username is required.")
	@Size(min = 5, max = 20, message = "Username must have between {min} and {max} characters.")
	private String username;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@NotNull(message = "Password is required.")

	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Role is required.")
	@Column(nullable = false)
	private ERole role;
	
	@Version
	private Integer version;

	
	@JsonIgnore
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private AdministratorEntity administrator;
	
	@JsonIgnore
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private TeacherEntity teacher;
	
	@JsonIgnore
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private StudentEntity student;
	
	@JsonIgnore
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private ParentEntity parent;


	public UserEntity() {
		super();
	}
	
	public UserEntity(Integer id,
			@NotNull(message = "Username is required.") @Size(min = 5, max = 20, message = "Username must have between {min} and {max} characters.") String username,
			@NotNull(message = "Password is required.") @Size(min = 5, message = "Password must have minimum {min} characters.") String password,
			@NotNull(message = "Role is required.") ERole role, AdministratorEntity administrator,
			TeacherEntity teacher, StudentEntity student, ParentEntity parent) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.administrator = administrator;
		this.teacher = teacher;
		this.student = student;
		this.parent = parent;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ERole getRole() {
		return role;
	}

	public void setRole(ERole role) {
		this.role = role;
	}

	public AdministratorEntity getAdministrator() {
		return administrator;
	}

	public void setAdministrator(AdministratorEntity administrator) {
		this.administrator = administrator;
	}

	public TeacherEntity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherEntity teacher) {
		this.teacher = teacher;
	}

	public StudentEntity getStudent() {
		return student;
	}

	public void setStudent(StudentEntity student) {
		this.student = student;
	}

	public ParentEntity getParent() {
		return parent;
	}

	public void setParent(ParentEntity parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", username=" + username + ", password=" + password + ", role=" + role
				+ ", administrator=" + administrator + ", teacher=" + teacher + ", student=" + student + ", parent="
				+ parent + "]";
	}
	

}
