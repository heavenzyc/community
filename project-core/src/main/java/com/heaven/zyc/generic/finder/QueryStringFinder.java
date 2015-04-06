 
package com.heaven.zyc.generic.finder;

/**
 * description
 * @author  jianguo.xu
 * @version 1.0,2010-9-15
 */
public abstract class QueryStringFinder implements Finder {
	/**
	 * 缓存
	 */
	private boolean cacheable;
	/**
	 * @return the cacheable
	 */
	public boolean isCacheable() {
		return cacheable;
	}

	/**
	 * @param cacheable
	 *            the cacheable to set
	 */
	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	/**
	 * 生成查询语句
	 * 
	 * @return
	 */
	public abstract String genQueryString();
	
	public Object convert(Object obj){
		return obj;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((genQueryString() == null) ? 0 : genQueryString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof QueryStringFinder))
			return false;
		QueryStringFinder otherFinder = (QueryStringFinder) other;
		String queryString = genQueryString();
		String otherQueryString = otherFinder.genQueryString();
		return isCacheable() == otherFinder.isCacheable()
				&& queryString != null && queryString.equals(otherQueryString);
	}
 
}

