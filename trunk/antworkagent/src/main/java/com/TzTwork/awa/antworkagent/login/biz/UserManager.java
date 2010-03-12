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
		String hql = "select u from Userinfo as u where u.username="+"'"+username+"'" + " and u.password=" + "'"+password+"'";
		List result = queryByHQL(hql);
		if(result.size()>0){
			userinfo = (Userinfo) result.get(0);
		}
		return userinfo;
	}

}
