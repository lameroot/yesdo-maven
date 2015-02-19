package ru.yesdo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.support.index.IndexType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lameroot on 13.12.14.
 * Пользователь системы. Это может быть как пользователь системы для мерчантов, так и пользователь который заходит на сайт.
 * Реализация как имплементация UserDetails of SpringSecurity
 * Также предусмотреть возможность авторизации через соц. сети (!!!на данном этапе делать не надо)
 * В оконсоли должна быть отдельная вкладка для управления пользователями, можно всё также как это сделано у нас в консоли
 */
@NodeEntity
@Entity
@Table(name = "users")
public class User {

    public static final String INDEX_FOR_LOGIN = "user_login";

    @GraphId
	@Id
	@SequenceGenerator(name = "user_id_gen", sequenceName = "user_seq")
	@GeneratedValue(generator = "user_id_gen", strategy = GenerationType.SEQUENCE)
	private Long id;

    @org.springframework.data.annotation.Transient
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<Permission> permissions = new HashSet<>();//пермиссии которыми обладает пользваотель

	@GraphProperty
    @Indexed(indexName = INDEX_FOR_LOGIN, indexType = IndexType.SIMPLE)
	private String login;

    @org.springframework.data.annotation.Transient
	@Column(name = "password_hash")
	private String passwordHash;

    @RelatedTo(type = "USER", direction = Direction.INCOMING)
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Merchant merchant;

    @Transient
    @RelatedToVia(type = "RATED")
    private Set<Rating> ratings = new HashSet<>();

	@Transient
	private Set<Product> whiteProducts;
	@Transient
	private Set<Product> blackProducts;
	@Transient
	private Set<Activity> whiteActivities;
	@Transient
	private Set<Activity> blackActivities;
	@Transient
	private Set<Merchant> whiteMerchants;
	@Transient
	private Set<Merchant> blackMerchants;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@JsonIgnore
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Set<Product> getWhiteProducts() {
		return whiteProducts;
	}

	public void setWhiteProducts(Set<Product> whiteProducts) {
		this.whiteProducts = whiteProducts;
	}

	public Set<Product> getBlackProducts() {
		return blackProducts;
	}

	public void setBlackProducts(Set<Product> blackProducts) {
		this.blackProducts = blackProducts;
	}

	public Set<Activity> getWhiteActivities() {
		return whiteActivities;
	}

	public void setWhiteActivities(Set<Activity> whiteActivities) {
		this.whiteActivities = whiteActivities;
	}

	public Set<Activity> getBlackActivities() {
		return blackActivities;
	}

	public void setBlackActivities(Set<Activity> blackActivities) {
		this.blackActivities = blackActivities;
	}

	public Set<Merchant> getWhiteMerchants() {
		return whiteMerchants;
	}

	public void setWhiteMerchants(Set<Merchant> whiteMerchants) {
		this.whiteMerchants = whiteMerchants;
	}

	public Set<Merchant> getBlackMerchants() {
		return blackMerchants;
	}

	public void setBlackMerchants(Set<Merchant> blackMerchants) {
		this.blackMerchants = blackMerchants;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
}
