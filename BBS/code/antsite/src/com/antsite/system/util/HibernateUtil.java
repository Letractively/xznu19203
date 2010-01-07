package com.antsite.system.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
	private static SessionFactory factory;

	private HibernateUtil() {
	}

	static {
		Configuration cfg = new Configuration();
		cfg.configure();
		factory = cfg.buildSessionFactory();
	}

	public static SessionFactory getSessionFactory() {
		return factory;
	}
	
	public static Session getSession(){
		return factory.openSession();
	}
}
