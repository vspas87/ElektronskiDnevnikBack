package BackEndProject.BackEndProject.entites;

import java.time.LocalDate;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "version"})
@JsonPropertyOrder({"id", "examType", "examDate", "examGrade"})
@Table (name = "GRADING")
public class GradingEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("GRADING_ID")
	@Column(name = "grading_id")
	private Integer id;
	
	@Column(name = "exam_type")
	private String examType;
	
	@Column(name = "exam_date")
	@JsonFormat(
			shape = JsonFormat.Shape.STRING,
			pattern = "dd-MM-yyyy")
	private LocalDate examDate;
	
	@NotNull(message = "Grade is required.")
	@Min(value = 1, message = "Grade must be between 1 and 5" )
	@Max(value = 5, message = "Grade must be between 1 and 5" )
	@Column(name = "exam_grade")
	private Integer examGrade;
	
	@Version
	private Integer version;

	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id")
	private TeacherEntity teacher;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "subject_id")
	private SubjectEntity subject;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id")
	private StudentEntity student;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "parent_id")
	private ParentEntity parent;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "class_id")
	private ClassEntity classes;
		

	public GradingEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GradingEntity(Integer id, String examType,
			LocalDate examDate,
			@NotNull(message = "Grade is required.") @Min(value = 1, message = "Grade must be between 1 and 5") @Max(value = 5, message = "Grade must be between 1 and 5") Integer examGrade,
			TeacherEntity teacher, SubjectEntity subject, StudentEntity student, ParentEntity parent) {
		super();
		this.id = id;
		this.examType = examType;
		this.examDate = examDate;
		this.examGrade = examGrade;
		this.teacher = teacher;
		this.subject = subject;
		this.student = student;
		this.parent = parent;
	}

	public GradingEntity(ClassEntity classes) {
		super();
		this.classes = classes;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

	public LocalDate getExamDate() {
		return examDate;
	}

	public void setExamDate(LocalDate examDate) {
		this.examDate = examDate;
	}

	public Integer getExamGrade() {
		return examGrade;
	}

	public void setExamGrade(Integer examGrade) {
		this.examGrade = examGrade;
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

	public StudentEntity getStudent() {
		return student;
	}

	public void setStudent(StudentEntity student) {
		this.student = student;
	}
	
	public ClassEntity getClasses() {
		return classes;
	}

	public void setClasses(ClassEntity classes) {
		this.classes = classes;
	}

	public ParentEntity getParent() {
		return parent;
	}

	public void setParent(ParentEntity parent) {
		this.parent = parent;
	}


	@Override
	public String toString() {
		return "GradingEntity [id=" + id + ", examType=" + examType + ", examDate=" + examDate + ", examGrade="
				+ examGrade + ", teacher=" + teacher + ", subject=" + subject + ", student=" + student + "]";
	}
	

}
