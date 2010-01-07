package com.antsite.system.util;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

public class BaseForm extends ActionForm {

	protected static Logger log = Logger.getLogger(BaseForm.class.getName());
	
	protected String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
