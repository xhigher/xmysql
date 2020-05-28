package com.cheercent.xmysql.model;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.xmysql.CommonUtils;


public class ProductInfoModel extends ProductModel {
	
	@Override
	protected String tableName() {
		return "product_info";
	}
	
	public boolean addInfo(int type, int subtype, String title, String iconurl, JSONArray imgurls, String profiles){
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("type", type);
		values.put("subtype", subtype);
		values.put("title", title);
		values.put("iconurl", iconurl);
		values.put("imgurls", imgurls.toString());
		values.put("profiles", profiles);
		values.put("status", 1);
		values.put("orderno", System.currentTimeMillis());
		values.put("ymdhms0", "");
		values.put("ymdhms1", CommonUtils.getCurrentYMDHMS());
		return this.prepare().set(values).insert();
	}

	public JSONObject getInfo(int productid){
		return this.prepare().addWhere("productid", productid).find();
	}

	public JSONArray getList(JSONArray productids){
		return this.prepare().addWhere("productid", productids, WhereType.IN).select();
	}

	public boolean deleteInfo(int productid){
		return this.prepare().addWhere("productid", productid).delete();
	}
	
	public long getTotal(int type, int subtype){
		this.prepare();
		if (type > 0){
			this.addWhere("type", type);
		}else if(subtype > 0){
			this.addWhere("subtype", subtype);
		}
		return this.count();
	}
	
	public JSONObject getPageData(int type, int subtype, int pagenum, int pagesize){
		this.prepare().field("type,subtype,title,iconurl").addWhere("status", 1);
		if (type > 0){
			this.addWhere("type", type);
		}else if(subtype > 0){
			this.addWhere("subtype", subtype);
		}
		return this.order("orderno", true).page(pagenum, pagesize);
	}

}
