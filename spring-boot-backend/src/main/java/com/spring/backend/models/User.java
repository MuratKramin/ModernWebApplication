package com.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

@Table(	name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(	name = "likes",
//			joinColumns = @JoinColumn(name = "id_user", nullable = false, foreignKey = @ForeignKey(name = "likes_users_id_fk")),
//			inverseJoinColumns = @JoinColumn(name = "id_hotel", nullable = false, foreignKey = @ForeignKey(name = "likes_hotels_id_fk")))
//	private List<Likes> hotels = new ArrayList<>();
//
//	public List<Likes> getLikedHotels() {
//		return hotels;
//	}
//
//	public void setLikedHotels(List<Likes> hotels) {
//		this.hotels = hotels;
//	}



	//@JsonIgnore
	//@JsonManagedReference
	@OneToMany(mappedBy = "user_rev")
	@JsonManagedReference("user-back-ref")
	private List<ResidenceHistory> residenceHistoryList;

	//@JsonIgnore
	@OneToMany(mappedBy = "user")
	@JsonManagedReference("user-back-ref-likes")
	private List<Likes> likesList;

	public List<Likes> getLikesList() {
		return likesList;
	}

	public void setLikesList(List<Likes> likesList) {
		this.likesList = likesList;
	}


	public User(Long id, String username, String email, String password, Set<Role> roles, List<ResidenceHistory> residenceHistoryList, List<Likes> likesList) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.residenceHistoryList = residenceHistoryList;
		this.likesList = likesList;
	}

	public List<ResidenceHistory> getResidenceHistoryList() {
		return residenceHistoryList;
	}

	public void setResidenceHistoryList(List<ResidenceHistory> residenceHistoryList) {
		this.residenceHistoryList = residenceHistoryList;
	}



	public User() {
	}

	public User(Long id, String username, String email, String password, Set<Role> roles, List<ResidenceHistory> residenceHistoryList) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.residenceHistoryList = residenceHistoryList;
	}

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

//	public List<ResidenceHistory> getResidenceHistoryList() {
//		return residenceHistoryList;
//	}
}
