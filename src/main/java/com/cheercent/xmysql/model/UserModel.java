package com.cheercent.xmysql.model;

import com.cheercent.xmysql.XModel;


public abstract class UserModel extends XModel {

	public static final String dataSourceName = "user";
	
	@Override
	protected String getDataSourceName() {
		return dataSourceName;
	}
	
}
