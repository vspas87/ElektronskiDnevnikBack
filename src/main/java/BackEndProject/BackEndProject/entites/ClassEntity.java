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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "version"})
@JsonPropertyOrder({"id", "className", "schoolYear" })
@Table (name = "CLASS")
public class ClassEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("CLASS_ID")
	@Column(name = "class_id")
	private Integer id;
	
	@NotNull(message = "Class name is required.")
	@Column(name = "class_name")
	private String className;
	

	@Column(name = "school_year")
	private Integer schoolYear;
	
	@Version
	private Integer version;
	
	
	@JsonIgnore
	@OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private List<StudentEntity> students = new ArrayList<StudentEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	List<TeachingEntity> teachings = new ArrayList<TeachingEntity>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "classes", fetch= FetchType.LAZY, cascade= CascadeType.REFRESH)
	List<GradingEntity> grading= new ArrayList<GradingEntity>();
	

	public ClassEntity() {
		super();
	}
	
	public ClassEntity(Integer id,
			@NotNull(message = "Class name is required.") 
			String className,
			Integer schoolYear,
			List<StudentEntity> students, List<TeachingEntity> teachings) {
		super();
		this.id = id;
		this.className = className;
		this.schoolYear = schoolYear;
		this.students = students;
		this.teachings = teachings;
	}
	
	
	public ClassEntity(List<GradingEntity> grading) {
		super();
		this.grading = grading;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Integer getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(Integer schoolYear) {
		this.schoolYear = schoolYear;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}

	public List<TeachingEntity> getTeachings() {
		return teachings;
	}

	public void setTeachings(List<TeachingEntity> teachings) {
		this.teachings = teachings;
	}
	

	public List<GradingEntity> getGrading() {
		return grading;
	}

	public void setGrading(List<GradingEntity> grading) {
		this.grading = grading;
	}

	
	@Override
	public String toString() {
		return "ClassEntity [id=" + id + ", className=" + className + ", schoolYear=" + schoolYear + ", students="
				+ students + ", teachings=" + teachings + "]";
	}	

}

