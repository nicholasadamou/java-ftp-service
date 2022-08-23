package com.nicholasadamou.ftp.service.model;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.jwt.JwtHelper;

import java.util.Map;

public class User {

	private Integer userIdentity;
	private String role;
	private String emailAddress;

	public Integer getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(Integer userIdentity) {
		this.userIdentity = userIdentity;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public static User getUserFrom(String token) {
		JsonParser parser = JsonParserFactory.getJsonParser();
		Map<String, ?> tokenData = parser.parseMap(JwtHelper.decode(token).getClaims());

		User user = new User();
		user.setUserIdentity(Integer.parseInt(tokenData.get("usr_identity").toString()));
		user.setRole(tokenData.get("role").toString());
		user.setEmailAddress(tokenData.get("email_address").toString());

		return user;
	}

	public boolean isNotValidUser() {
		return userIdentity == null || userIdentity == 0;
	}

	@Override
	public String toString() {
		return "User{" +
				"userIdentity=" + userIdentity +
				", role='" + role + '\'' +
				", emailAddress='" + emailAddress + '\'' +
				'}';
	}
}
