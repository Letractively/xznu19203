package com.TzTwork.awa.antworkagent.login.action;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.TzTwork.awa.antworkagent.login.biz.UserManager;
import com.TzTwork.awa.antworkagent.po.Userinfo;


public class LoginAction extends DispatchAction{
	
	private UserManager userManager;
	

	public UserManager getUserManager() {
		return userManager;
	}


	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	public ActionForward loginValidate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println(username);
		List<Userinfo> userinfo_list = userManager.findAll();
		for(Userinfo u:userinfo_list){
			if(u.getUsername().equals(username) && u.getPassword().equals(password)){
				return mapping.findForward("succ");
			}
		}
		String msg = "用户名或密码错误!";
		out.println(msg);
		return null;
	}
	
}