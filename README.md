# xmysql
mysql超级易用库，支持多数据源，简化事务处理，json交互

简介
---
  mysql超级易用库，支持多数据源，简化事务处理，json交互。
  采用数据库连接池管理资源，业务层model不需要关心库表所在的数据库，也不需管理连接的获取与释放，只需要专注于业务操作即可。
  数据结果集根据设定可以转换需要的json格式。 开始事务通过事务上下文开启即可。




初始化服务

```properties
mysql.status=1
mysql.dataSource.size=2

mysql.dataSource1.name=user
mysql.dataSource1.driverClassName=com.mysql.cj.jdbc.Driver
mysql.dataSource1.url=jdbc:mysql://127.0.0.1:3306/cc_user?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull&useOldAliasMetadataBehavior=true
mysql.dataSource1.user=root
mysql.dataSource1.password=cheercent
mysql.dataSource1.cachePrepStmts=true
mysql.dataSource1.prepStmtCacheSize=250
mysql.dataSource1.prepStmtCacheSqlLimit=2048
mysql.dataSource1.maximumPoolSize=2

mysql.dataSource2.name=product
mysql.dataSource2.driverClassName=com.mysql.cj.jdbc.Driver
mysql.dataSource2.url=jdbc:mysql://127.0.0.1:3306/cc_product?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull&useOldAliasMetadataBehavior=true
mysql.dataSource2.user=root
mysql.dataSource2.password=cheercent
mysql.dataSource2.cachePrepStmts=true
mysql.dataSource2.prepStmtCacheSize=250
mysql.dataSource2.prepStmtCacheSqlLimit=2048
mysql.dataSource2.maximumPoolSize=2
```

```java
	Properties properties = new Properties();
	InputStream is = Object.class.getResourceAsStream(configFile);
	properties.load(is);
	if (is != null) {
		is.close();
	}
	
	XMySQL.init(properties);
```
 
业务层
定义model，定义一个继承与XModel的抽象model作为datasource层，业务库表model继承该moldel即可
```java

    //datasource model
    public abstract class UserModel extends XModel {

        public static final String dataSourceName = "user";
        
        @Override
        protected String getDataSourceName() {
            return dataSourceName;
        }
        
    }

    //table model
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
```

业务逻辑
简单的增删改查
链式调用方式
```java

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
```

sql调用方式
```java

        userInfo = userModel.findBySQL("SELECT * FROM `user_info` WHERE `userid`=?", userid);
        
        JSONArray userList = userModel.selectBySQL("SELECT * FROM `user_info` WHERE `status`=?", 1);
        
        boolean result = userModel.executeBySQL("UPDATE `user_info` SET nickname=? WHERE `userid`=?", "xhigher3", userid);
        
```

分页

```java

        ProductInfoModel productModel = new ProductInfoModel();
        JSONObject pageData = productModel.getPageData(1, 1, 1, 20);
        if(pageData == null) {
            logger.error("ProductInfoModel.getPageData:error!");
        }
        logger.info("product.page = {}", pageData.toString());
        
```

事务

```java

        XContext context = new XContext();
        userModel.setTransaction(context);
        nickname = "xhigher2";
        if(!userModel.updateInfo(userid, nickname, null)) {
            logger.error("UserInfoModel.updateInfo:error!");
        }
        
        UserInfoModel userModel2 = new UserInfoModel();
        
        userModel2.setTransaction(context);
        String username2 = "13715666999";
        String nickname2 = "xhigher999";
        String avatar2 = "https://avatars2.githubusercontent.com/u/65011074?s=60&v=4";
        String userid2 = userModel2.addInfo(username2, nickname2, avatar2);
        if(userid2 == null) {
            logger.error("UserInfoModel.addInfo:error!");
            context.endTransaction(false);
        }
        context.submitTransaction();
        
```



