package com.antsite.system.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.ActionSupport;

import com.antsite.publics.DateConvert;

public abstract class BaseAction extends ActionSupport {

	protected static final Logger log = Logger.getLogger(BaseAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setHeader("Cache-Control","no-store");
		response.setHeader("Pragrma","no-cache");
		response.setDateHeader("Expires",0); 
		
		ConvertUtils.register(new DateConvert(),java.util.Date.class);

		log.info("#" + this.getClass() + " start");
		ActionForward forward;
		try {
			forward = doAction(mapping, form, request, response);
			log.info(this.getActionName());
		} catch (Exception e) {
			log.error(e);
			forward = mapping.findForward("error");
		}
		log.info("#" + this.getClass() + " end");
		return forward;
	}

	protected abstract ActionForward doAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	protected abstract String getActionName();

	protected Object getBean(String str) {
		return getWebApplicationContext().getBean(str);
	}

	protected void setSession(HttpServletRequest request, String key,
			Object object) {
		HttpSession session = request.getSession();
		session.setAttribute(key, object);
	}

	protected Object getSession(HttpServletRequest request, String key) {
		HttpSession session = request.getSession();
		return session.getAttribute(key);
	}
}
