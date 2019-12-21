package BackEndProject.BackEndProject.dto;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import BackEndProject.BackEndProject.entites.ERole;

public class UserDto {
	
	@NotNull(message = "Username is required.")
	@Size(min = 5, max = 20, message = "Username must have between {min} and {max} characters.")
	@Column(nullable = false, unique = true, updatable = false)
	private String username;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@NotNull(message = "Password is required.")
	@Size(min = 5, message = "Password must have minimum {min} characters.")
	@Column(nullable = false)
	private String password;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@NotNull(message = "Password is required.")
	@Size(min = 5, message = "Password must have minimum {min} characters.")
	@Column(nullable = false)
	private String confirmedPassword;
	
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Role is required.")
	@Column(nullable = false)
	private ERole role;
	
	@Version
	private Integer version;
	


	public UserDto() {
		super();
	}

	public UserDto(
			@NotNull(message = "Username is required.") @Size(min = 5, max = 20, message = "Username must have between {min} and {max} characters.") String username,
			@NotNull(message = "Password is required.") @Size(min = 5, message = "Password must have minimum {min} characters.") String password,
			@NotNull(message = "Password is required.") @Size(min = 5, message = "Password must have minimum {min} characters.") String confirmedPassword,
			@NotNull(message = "Role is required.") ERole role) {
		super();
		this.username = username;
		this.password = password;
		this.confirmedPassword = confirmedPassword;
		this.role = role;
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

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}

	public ERole getRole() {
		return role;
	}

	public void setRole(ERole role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserDto [username=" + username + ", password=" + password + ", confirmedPassword=" + confirmedPassword
				+ ", role="+ "]"; 
	}
	

}
