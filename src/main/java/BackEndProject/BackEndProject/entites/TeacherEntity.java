package BackEndProject.BackEndProject.entites;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({"id", "firstName", "lastName"})
@Table (name = "TEACHER")
public class TeacherEntity extends PersonEntity {

	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "user_id")
	private UserEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "subject_id")
	private SubjectEntity subject;
	
	@JsonIgnore
	@OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private List<TeachingEntity> teaching = new ArrayList<TeachingEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private List<GradingEntity> grading = new ArrayList<GradingEntity>();


	public TeacherEntity() {
		super();
	}
	public TeacherEntity(Integer id,
			@NotNull(message = "First name is required.") @Size(min = 1, max = 20, message = "First name must have between {min} and {max} characters.") String firstName,
			@NotNull(message = "Last name is required.") @Size(min = 1, max = 20, message = "Last name must have between {min} and {max} characters.") String lastName) {
		super(id, firstName, lastName);
	}

	public TeacherEntity(UserEntity user, SubjectEntity subject, List<TeachingEntity> teaching,
			List<GradingEntity> grading) {
		super();
		this.user = user;
		this.subject = subject;
		this.teaching = teaching;
		this.grading = grading;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
	}

	public List<TeachingEntity> getTeaching() {
		return teaching;
	}

	public void setTeaching(List<TeachingEntity> teaching) {
		this.teaching = teaching;
	}

	public List<GradingEntity> getGrading() {
		return grading;
	}

	public void setGrading(List<GradingEntity> grading) {
		this.grading = grading;
	}
	

	@Override
	public String toString() {
		return "TeacherEntity [user=" + user + ", subject=" + subject + ", teaching=" + teaching + ", grading="
				+ grading + "]";
	}

	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
