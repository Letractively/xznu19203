package com.TzTwork.awa.antworkagent.manager;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.TzTwork.awa.antworkagent.login.biz.UserManager;
import com.TzTwork.awa.antworkagent.po.Userinfo;


public class ManagerAction extends DispatchAction{
	
	private UserManager userManager;
	

	public UserManager getUserManager() {
		return userManager;
	}


	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	public ActionForward managerLogin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		String uname = request.getParameter("username");
		String pword = request.getParameter("password");
		Properties properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("admin.properties"); 
		properties.load(in);
		String username = properties.getProperty("username");
		String password = properties.getProperty("password");
		
		if(uname.equalsIgnoreCase(username) && pword.equalsIgnoreCase(password)){
			return mapping.findForward("userList");
		}else{
			return null;
		}
	}
	public ActionForward showUserinfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Userinfo> userinfo_list = userManager.findAll();
		System.out.println(userinfo_list.size());
		request.setAttribute("userinfo_list", userinfo_list);
		return mapping.findForward("userinfo_list");
	}
	
	public ActionForward saveUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		Userinfo userinfo = new Userinfo(username,password,1);
		userManager.save(userinfo);
		List<Userinfo> list = userManager.findAll();
		System.out.println(list.size());
		return mapping.findForward("userList");
	}
	
}