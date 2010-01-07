package com.antsite.medol.login.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.antsite.medol.login.form.LoginForm;
import com.antsite.system.util.BaseAction;


public class LoginAction extends BaseAction {

	protected ActionForward doAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		LoginForm loginForm = (LoginForm)form;
		String username = loginForm.getUsername();
		System.out.println(username);
		return mapping.findForward("succ");
	}

	protected String getActionName() {
		// TODO Auto-generated method stub
		return null;
	}

}
