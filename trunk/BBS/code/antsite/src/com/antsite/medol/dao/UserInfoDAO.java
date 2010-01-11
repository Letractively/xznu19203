package com.antsite.medol.dao;

import com.antsite.medol.po.UserInfo;
import com.antsite.system.util.BaseDAO;

public class UserInfoDAO extends BaseDAO{

	public boolean validate(UserInfo userinfo) {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(userinfo.getUsername().equals("kkk")){
			flag = true;
		}
		return flag;
	}

	protected Class getObjectClass() {
		// TODO Auto-generated method stub
		return UserInfo.class;
	}

}
