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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "version"})
@JsonPropertyOrder({"id", "firstName", "lastName", "schoolClass"})
@Table (name = "STUDENT")
public class StudentEntity extends PersonEntity {

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "user_id")
	private UserEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "parent_id")
	private ParentEntity parent;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "class_id")
	private ClassEntity schoolClass;
	
	@JsonIgnore
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private List<GradingEntity> grading = new ArrayList<GradingEntity>();

	public StudentEntity() {}
	public StudentEntity(UserEntity user, ParentEntity parent, ClassEntity schoolClass, List<GradingEntity> grading) {
		super();
		this.user = user;
		this.parent = parent;
		this.schoolClass = schoolClass;
		this.grading = grading;
	}

	@Override
	public String toString() {
		return "StudentEntity [user=" + user + ", parent=" + parent + ", schoolClass=" + schoolClass + ", grading="
				+ grading + "]";
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public ParentEntity getParent() {
		return parent;
	}

	public void setParent(ParentEntity parent) {
		this.parent = parent;
	}

	public ClassEntity getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(ClassEntity schoolClass) {
		this.schoolClass = schoolClass;
	}
	public List<GradingEntity> getGrading() {
		return grading;
	}

	public void setGrading(List<GradingEntity> grading) {
		this.grading = grading;
	}	
	

}
