package com.TzTwork.awa.antworkagent.login.action;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
		String msg = "";
		HttpSession session = request.getSession();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println(username);
		Userinfo userinfo = userManager.validateUser(username, password);
		if(userinfo!=null){
			session.setAttribute("user", userinfo);
			msg = "1";
		}else{
			msg = "0";		
		}
		out.println(msg);
		return null;
	}
	
	public ActionForward login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		//登陆成功
		HttpSession session = request.getSession();
		Userinfo userinfo = (Userinfo) session.getAttribute("user");
		request.setAttribute("user", userinfo);
		return mapping.findForward("succ");
	}
	
	public ActionForward visitor_login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		session.invalidate();//清空session
		return mapping.findForward("visitor");
	}
	
}