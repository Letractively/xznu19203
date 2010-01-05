package com.antsite.medol.dao;

import com.antsite.medol.bo.UserInfoBO;
import com.antsite.medol.po.UserInfo;

public class UserInfoDAO implements UserInfoBO {

	public boolean validate(UserInfo userinfo) {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(userinfo.getUsername().equals("kkk")){
			flag = true;
		}
		return flag;
	}

}
