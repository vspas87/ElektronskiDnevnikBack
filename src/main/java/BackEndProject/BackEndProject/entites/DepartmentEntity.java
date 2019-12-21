package BackEndProject.BackEndProject.entites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "version"})
@JsonPropertyOrder({"id", "name", "classroom" })
@Table (name = "department")
public class DepartmentEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("DepartmentID")
	@Column(name = "departmentID")
	private Integer id;
	
	@NotNull(message = "Name is required.")
	@Column(name = "Name")
	private String name;
	

	@Column(name = "classroom")
	private Integer classroom;
	
	@Version
	private Integer version;
	
	public DepartmentEntity() {}
	public DepartmentEntity(Integer id, @NotNull(message = "Name is required.") String name, Integer classroom,
			Integer version) {
		super();
		this.id = id;
		this.name = name;
		this.classroom = classroom;
		this.version = version;
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
	public Integer getClassroom() {
		return classroom;
	}
	public void setClassroom(Integer classroom) {
		this.classroom = classroom;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
