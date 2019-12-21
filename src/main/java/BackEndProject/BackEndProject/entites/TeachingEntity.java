package BackEndProject.BackEndProject.entites;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "version"})
@JsonPropertyOrder({"id", "teacher", "subject", "class"})
@Table (name = "TEACHING")
public class TeachingEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("TEACHING_ID")
	@Column(name = "teaching_id")
	private Integer id;
	
	@Version
	private Integer version;
	
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id")
	private TeacherEntity teacher;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "subject_id")
	private SubjectEntity subject;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "class_id")
	private ClassEntity schoolClass;
	

	public TeachingEntity() {
		super();
	}
	
	public TeachingEntity(Integer id, TeacherEntity teacher, SubjectEntity subject, ClassEntity schoolClass) {
		super();
		this.id = id;
		this.teacher = teacher;
		this.subject = subject;
		this.schoolClass = schoolClass;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TeacherEntity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherEntity teacher) {
		this.teacher = teacher;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
	}

	public ClassEntity getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(ClassEntity schoolClass) {
		this.schoolClass = schoolClass;
	}

	@Override
	public String toString() {
		return "TeachingEntity [id=" + id + ", teacher=" + teacher + ", subject=" + subject + ", schoolClass="
				+ schoolClass + "]";
	}
	
}


