package com.cheercent.xmysql.model;

import com.cheercent.xmysql.XModel;


public abstract class ProductModel extends XModel {

	public static final String dataSourceName = "product";
	
	@Override
	protected String getDataSourceName() {
		return dataSourceName;
	}
	
}
