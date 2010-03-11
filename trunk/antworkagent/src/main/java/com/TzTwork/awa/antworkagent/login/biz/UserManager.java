package com.TzTwork.awa.antworkagent.login.biz;

import java.util.List;

import com.TzTwork.awa.antworkagent.common.BaseDAO;
import com.TzTwork.awa.antworkagent.po.Userinfo;

public class UserManager extends BaseDAO{

	protected Class getObjectClass() {
		// TODO Auto-generated method stub
		return Userinfo.class;
	}
	
	public Userinfo validateUser(String username,String password){
		Userinfo userinfo = null;
		String hql = "select userinfo from userinfo where username="+username + "and password=" + password;
		List result = queryByHQL(hql);
		
		return userinfo;
	}

}
