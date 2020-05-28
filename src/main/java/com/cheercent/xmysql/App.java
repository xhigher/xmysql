package com.cheercent.xmysql;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.xmysql.model.ProductInfoModel;
import com.cheercent.xmysql.model.UserInfoModel;

/**
 * Hello world!
 *
 */
public class App {
	
	private static Logger logger = LoggerFactory.getLogger(App.class);
	
	private static String configFile = "/application.properties";
	
    public static void main( String[] args ){
        
    	init();
    	
    	demo1();
    	
    }
    
    private static void demo1() {
    	UserInfoModel userModel = new UserInfoModel();
    	String username = "13715666888";
    	String nickname = "xhigher";
    	String avatar = "https://avatars2.githubusercontent.com/u/65011074?s=60&v=4";
    	String userid = userModel.addInfo(username, nickname, avatar);
    	if(userid == null) {
    		logger.error("UserInfoModel.addInfo:error!");
    	}
    	
    	JSONObject userInfo = userModel.getInfo(userid);
    	if(userInfo == null) {
    		logger.error("UserInfoModel.getInfo:error!");
    	}
    	if(userInfo.isEmpty()) {
    		logger.error("UserInfoModel.getInfo:null!");
    	}
    	logger.info("user = {}", userInfo.toString());
    	
    	nickname = "xhigher2";
    	if(!userModel.updateInfo(userid, nickname, null)) {
    		logger.error("UserInfoModel.updateInfo:error!");
    	}
    	
    	if(!userModel.deleteInfo(userid)) {
    		logger.error("UserInfoModel.deleteInfo:error!");
    	}
    	
    	userInfo = userModel.findBySQL("SELECT * FROM `user_info` WHERE `userid`=?", userid);
    	
    	JSONArray userList = userModel.selectBySQL("SELECT * FROM `user_info` WHERE `status`=?", 1);
    	
    	boolean result = userModel.executeBySQL("UPDATE `user_info` SET nickname=? WHERE `userid`=?", "xhigher3", userid);
    	
    	ProductInfoModel productModel = new ProductInfoModel();
    	JSONObject pageData = productModel.getPageData(1, 1, 1, 20);
    	if(pageData == null) {
    		logger.error("ProductInfoModel.getPageData:error!");
    	}
    	logger.info("product.page = {}", pageData.toString());
    }
    
    
    private static void init() {
    	try{
			Properties properties = new Properties();
			InputStream is = Object.class.getResourceAsStream(configFile);
			properties.load(is);
			if (is != null) {
				is.close();
			}
			
			XMySQL.init(properties);
			
		}catch(Exception e){
			logger.error("XLauncher.Exception:", e);
		}
    }
}
