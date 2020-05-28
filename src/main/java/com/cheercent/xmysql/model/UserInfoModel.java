package com.cheercent.xmysql.model;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.xmysql.CommonUtils;


public class UserInfoModel extends UserModel {

	@Override
	protected String tableName() {
		return "user_info";
	}

	private String createUserid(long millis) {
		return CommonUtils.randomString(1, true)+Long.toString(millis, 36) + CommonUtils.randomString(1, true);
	}
	
	public String addInfo(String username, String nickname, String avatar){
		long millis = System.currentTimeMillis();
		String userid = createUserid(millis);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("userid", userid);
		values.put("username", username);
		values.put("nickname", nickname);
		values.put("avatar", avatar == null ? "" : avatar);
		values.put("level", 0);
		values.put("profile", "");
		values.put("updatetime", CommonUtils.getCurrentYMDHMS(millis));
		if(this.prepare().set(values).insert()) {
			return userid;
		}
		return null;
	}

	public boolean updateInfo(String userid, String nickname, String avatar){
		Map<String, Object> values = new HashMap<String, Object>();
		if(nickname != null) {
			values.put("nickname", nickname);
		}
		if(avatar != null) {
			values.put("avatar", avatar);
		}
		values.put("updatetime", CommonUtils.getCurrentYMDHMS());

		this.prepare();
		if(nickname != null){
			this.addWhere("nickname_num", 0);
		}
		return this.set(values).addWhere("userid", userid).update();
	}


	public JSONObject getInfo(String userid){
		return this.prepare().addWhere("userid",userid).find();
	}
	
	public JSONArray getList(JSONArray useridList){
		return this.prepare().addWhere("userid",useridList, WhereType.IN).select();
	}
	
	public boolean deleteInfo(String userid){
		return this.prepare().addWhere("userid",userid).delete();
	}
}
