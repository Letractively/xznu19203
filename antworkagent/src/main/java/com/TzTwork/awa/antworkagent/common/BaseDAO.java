package com.TzTwork.awa.antworkagent.common;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class BaseDAO extends HibernateDaoSupport {

	protected static final Log log = LogFactory.getLog(BaseDAO.class);

	public void save(Object object) {
		log.debug("saving " + getObjectClass().getName() + " instance");
		try {
			getHibernateTemplate().save(object);
			log.debug(getObjectClass().getName() + "save successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName() + " save failed", re);
		}
	}

	public void saveAll(List entities) {
		log.debug("saveAll " + getObjectClass().getName() + " instance");
		try {
			getHibernateTemplate().saveOrUpdateAll(entities);
			log.debug(getObjectClass().getName() + " saveAll successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName() + " saveAll failed", re);
		}
	}

	public void batchSaveAll(Collection entities) {
		log.debug("saveAll " + getObjectClass().getName() + " instance");
		Session session = getHibernateTemplate().getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			if (entities.size() > 0) {
				for (Iterator it = entities.iterator(); it.hasNext();) {
					Object ob = (Object) it.next();
					session.save(ob);
				}
			}
			tx.commit();
			log.debug(getObjectClass().getName() + " saveAll successful");
		} catch (HibernateException he) {
			tx.rollback();
			log.error(getObjectClass().getName() + " saveAll failed", he);
		} finally {
			session.close();
		}
	}

	public void update(Object object) {
		log.debug("updating " + getObjectClass().getName() + " instance");
		try {
			getHibernateTemplate().update(object);
			log.debug(getObjectClass().getName() + "update successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName() + " update failed", re);
		}
	}
	
	public void saveOrUpdate(Object object) {
		log.debug("saving or updating " + getObjectClass().getName() + " instance");
		try {
			getHibernateTemplate().saveOrUpdate(object);
			log.debug(getObjectClass().getName() + "save or update successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName() + "save or update failed", re);
		}
	}

	public void delete(Object object) {
		log.debug("deleting " + getObjectClass().getName() + " instance");
		try {
			getHibernateTemplate().delete(object);
			log.debug(getObjectClass().getName() + "delete successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName() + "delete failed", re);
			throw re;
		}
	}

	public void deleteAll(Collection collection) {
		log.debug("deleting " + getObjectClass().getName()
				+ " collection instance");
		try {
			getHibernateTemplate().deleteAll(collection);
			log.debug(getObjectClass().getName()
					+ "delete collection successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName() + "delete collection failed",
					re);
		}
	}

	public void deleteById(Serializable id) {
		log.debug("deleting " + getObjectClass().getName()
				+ " collection instance");
		try {
			Object entity = getHibernateTemplate().get(this.getObjectClass(),
					id);
			if (entity != null) {
				getHibernateTemplate().delete(entity);
			}
			log.debug(getObjectClass().getName() + "delete "
					+ getObjectClass().getName() + " successful");
		} catch (RuntimeException re) {
			log.error("delete " + getObjectClass().getName() + " failed", re);
		}
	}

	public Object findById(Serializable id) {
		log.debug("getting " + getObjectClass().getName()
				+ " instance with id: " + id);
		Object result = null;
		try {
			result = getHibernateTemplate().get(this.getObjectClass(), id);
			log.debug(getObjectClass().getName() + "get successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName() + " get failed", re);
		}
		return result;
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding " + getObjectClass().getName()
				+ " instance with property: " + propertyName + ", value: "
				+ value);
		List result = new ArrayList();
		try {
			String queryString = "from " + getObjectClass().getName()
					+ " as model where model." + propertyName + "= ?";
			result = getHibernateTemplate().find(queryString, value);
			log.debug(getObjectClass().getName()
					+ "find by property name successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName()
					+ "find by property name failed", re);
		}
		return result;
	}

	public List findAll() {
		log.debug("finding all " + getObjectClass().getName());
		List result = new ArrayList();
		try {
			result = this.getHibernateTemplate().loadAll(this.getObjectClass());
			log.debug(getObjectClass().getName() + "find all successful");
		} catch (RuntimeException re) {
			log.error(getObjectClass().getName() + "find all failed", re);
		}
		return result;
	}

	protected List queryByHQL(final String hql) {
		List result = new ArrayList();
		result = this.getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(hql);
						return query.list();
					}
				});
		if (result == null) {
			result = new ArrayList();
		}
		return result;
	}

	public void deleteByHQL(final String hql) {
		this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.createQuery(hql).executeUpdate();
				return null;
			}
		});
	}

	protected abstract Class getObjectClass();

}