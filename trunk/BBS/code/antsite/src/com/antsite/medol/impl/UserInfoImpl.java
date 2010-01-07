package com.antsite.medol.impl;

import com.antsite.medol.bo.UserInfoBO;
import com.antsite.medol.dao.UserInfoDAO;
import com.antsite.medol.po.UserInfo;

public class UserInfoImpl implements UserInfoBO {
	private UserInfoDAO userInfoDAO;

	public UserInfoDAO getUserInfoDAO() {
		return userInfoDAO;
	}

	public void setUserInfoDAO(UserInfoDAO userInfoDAO) {
		this.userInfoDAO = userInfoDAO;
	}

	public boolean validate(UserInfo userinfo) {
		// TODO Auto-generated method stub
		return userInfoDAO.validate(userinfo);
	}
	
}
