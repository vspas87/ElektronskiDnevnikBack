package BackEndProject.BackEndProject.entites;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "version"})
@JsonPropertyOrder({"id", "name", "weeklyFund"})
@Table (name = "SUBJECT")
public class SubjectEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("SUBJECT_ID")
	@Column(name = "subject_id")
	private Integer id;
	
	@NotNull(message = "Name of subject is required.")
	private String name;
	
	@NotNull(message = "Weekly fund is required.")
	@Max(value = 40, message = "Weekly fund must be between 0 and 40")
	@Column(name = "weekly_fund")
	private Integer weeklyFund;
	
	@Version
	private Integer version;

	@JsonIgnore
	@OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private List<TeacherEntity> teacher = new ArrayList<TeacherEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private List<TeachingEntity> teaching = new ArrayList<TeachingEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private List<GradingEntity> grading = new ArrayList<GradingEntity>();


	public SubjectEntity() {
		super();
	}

	public SubjectEntity(Integer id, @NotNull(message = "Name of subject is required.") String name,
			@NotNull(message = "Weekly fund is required.") @Max(value = 40, message = "Weekly fund must be between 0 and 40") Integer weeklyFund,
			List<TeacherEntity> teacher, List<TeachingEntity> teaching, List<GradingEntity> grading) {
		super();
		this.id = id;
		this.name = name;
		this.weeklyFund = weeklyFund;
		this.teacher = teacher;
		this.teaching = teaching;
		this.grading = grading;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getWeeklyFund() {
		return weeklyFund;
	}

	public void setWeeklyFund(Integer weeklyFund) {
		this.weeklyFund = weeklyFund;
	}

	public List<TeacherEntity> getTeacher() {
		return teacher;
	}

	public void setTeacher(List<TeacherEntity> teacher) {
		this.teacher = teacher;
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
		return "SubjectEntity [id=" + id + ", name=" + name + ", weeklyFund=" + weeklyFund + ", teacher=" + teacher
				+ ", teaching=" + teaching + ", grading=" + grading + "]";
	}
		

}
