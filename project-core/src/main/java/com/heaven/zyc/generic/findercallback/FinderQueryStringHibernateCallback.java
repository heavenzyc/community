 
package com.heaven.zyc.generic.findercallback;

import com.heaven.zyc.generic.HibernateUtils;
import com.heaven.zyc.generic.finder.Finder;
import com.heaven.zyc.generic.finder.ParametersFinder;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.Map;

/**
 * Finder查询器的实现类
 * 
 * @author  jianguo.xu
 * @version 1.0,2010-9-15
 */
public abstract class FinderQueryStringHibernateCallback extends
		FinderHibernateCallback {

	public FinderQueryStringHibernateCallback(Finder finder) {
		super(finder);
	}
	/**
	 * 得到查询语句
	 * 该模板方法是根据Finder的HQL生成正式查询语句
	 * @author jianguo.xu
	 * @return
	 */
	protected abstract String genQueryString();
	/**
	 * 改模板方法为实际对query的执行方法
	 * 返回执行结果
	 * @author jianguo.xu
	 * @param query
	 * @return
	 */
	protected abstract Object processQuery(Query query);

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
	 */
	public Object doInHibernate(Session session) throws HibernateException,
			SQLException {
		Query query = genQuery(session);
		query.setCacheable(getFinder().isCacheable());
		return getFinder().convert(processQuery(query));
	}
	/**
	 * 根据Finder生成Query
	 * 如果该Finder是ParametersFinder则需要把查询参数设置到query对象中
	 * 否则直接返回该query对象
	 * @author jianguo.xu
	 * @param session
	 * @return
	 */
	protected Query genQuery(Session session) {
		Query query = session.createQuery(genQueryString());
		Map<String, Object> paramMap = null;
		Object[] properties = null;
		if (getFinder() instanceof ParametersFinder) {
			ParametersFinder parametersFinder = (ParametersFinder) getFinder();
			paramMap = parametersFinder.getParameters();
			properties = parametersFinder.getProperties();
		}	
		if (paramMap != null)  
			HibernateUtils.paramQuery(query, paramMap);
		else if (properties != null)  
			HibernateUtils.paramQuery(query, properties);
		return query;
	}
}
