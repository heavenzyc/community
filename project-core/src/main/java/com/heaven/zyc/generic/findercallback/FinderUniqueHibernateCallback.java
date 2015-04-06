 
package com.heaven.zyc.generic.findercallback;

import com.heaven.zyc.generic.finder.Finder;
import com.heaven.zyc.generic.finder.QueryStringFinder;
import org.hibernate.Query;

/**
 * 唯一记录查询callback
 * @author  jianguo.xu
 * @version 1.0,2010-9-15
 */
public class FinderUniqueHibernateCallback extends FinderQueryStringHibernateCallback{
	
	public FinderUniqueHibernateCallback(Finder finder){
		super(finder);
	}
	protected String genQueryString() {
		if (getFinder() instanceof QueryStringFinder)
			return ((QueryStringFinder) getFinder()).genQueryString();
		return null;
	}

	protected Object processQuery(Query query) {
		return query.uniqueResult();
	}

}


