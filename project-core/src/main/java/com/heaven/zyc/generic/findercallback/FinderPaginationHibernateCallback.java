 
package com.heaven.zyc.generic.findercallback;

import com.heaven.zyc.generic.HibernateUtils;
import com.heaven.zyc.generic.finder.Finder;
import com.heaven.zyc.generic.finder.ParametersFinder;
import com.heaven.zyc.generic.finder.QueryStringFinder;
import com.heaven.zyc.pagination.Pagination;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.Map;

/**
 * 分页查询Callback
 * @author  jianguo.xu
 * @version 1.0,2010-9-15
 */
public class FinderPaginationHibernateCallback extends
		FinderListHibernateCallback {

	private Pagination pagination;

	public FinderPaginationHibernateCallback(Finder finder,
			Pagination pagination) {
		super(finder);
		this.pagination = pagination;
	}

 
	public Object doInHibernate(Session session) throws HibernateException,
			SQLException {
		setPaginationCount(session);
		return super.doInHibernate(session);
	}
	/**
	 * 设置分页对象的记录数
	 * @author jianguo.xu
	 */
	private void setPaginationCount(Session session) {
		String queryString = HibernateUtils
		.countHql(genQueryString());
		Query countQuery = session.createQuery(queryString);
		countQuery.setCacheable(getFinder().isCacheable());
		Map<String, Object> paramMap = null;
		if (getFinder() instanceof ParametersFinder)
			paramMap = ((ParametersFinder) getFinder()).getParameters();
		if (paramMap != null) {
			HibernateUtils.paramQuery(countQuery, paramMap);
		}
		int count = getCount(countQuery);
		pagination.setCount(count);
	}
	
	private int getCount(Query countQuery) {
		if(HibernateUtils.isExistHavingHql(((QueryStringFinder) getFinder())
				.genQueryString()))
		return countQuery.list().size();
	else return ((Long) countQuery.iterate().next()).intValue();
	}
	
	protected Object processQuery(Query query) {
		return query.setFirstResult(
				(pagination.getCurrentPage() - 1) * pagination.getPageSize())
				.setMaxResults(pagination.getPageSize()).list();
	}

}
