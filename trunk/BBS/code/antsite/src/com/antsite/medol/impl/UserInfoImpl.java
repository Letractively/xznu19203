package com.antsite.medol.impl;

import com.ansite.medol.po.UserInfo;
import com.antsite.medol.dao.UserInfoDAO;

public class UserInfoImpl {
	private UserInfoDAO userInfoDAO;

	public UserInfoDAO getUserInfoDAO() {
		return userInfoDAO;
	}

	public void setUserInfoDAO(UserInfoDAO userInfoDAO) {
		this.userInfoDAO = userInfoDAO;
	}
	public boolean validate(UserInfo userinfo){
		return userInfoDAO.validate(userinfo);
	}
}
