var ioc = {
  	configs : {
        type : 'org.nutz.ioc.impl.PropertiesProxy',
        fields : {
            paths : ['config.properties']
        }
    },
	imagePath: {
		type: 'java.lang.String',
		args: [{java: "$configs.get('img.path')"}]
	},
	imagePath2: {
		type: 'java.lang.String',
		args: [{java: "$configs.get('img.path2')"}]
	},
	dbStrTemp: {
		type: 'java.lang.String',
		args: [{java: '$configs.get("db.str")'}]
	},
	dbStr: {
		type: 'java.lang.String',
		args: [{java: 'com.lpsedu.helper.des3.Des3.decode($dbStrTemp'}]
	},
	jdbcConfig: {
		type: 'com.lpsedu.helper.des3.JdbcConfig',
		args: [{java: 'com.lpsedu.helper.des3.ParseJDBC.parseMysql($dbStr'}]
	},
	dbConfig: {
		type: 'com.lps.tqms.pojo.DbConfig',
		args: [{java: "$jdbcConfig.getConnString()"},
		       {java: "$jdbcConfig.getConnUser()"},
		       {java: "$jdbcConfig.getConnPass()"},
		       {java: "$configs.get('db.dbDriver')"}]
	},
	cacheConfig: {
		type: 'com.lps.tqms.pojo.CacheConfig',
		args: [{java: "$configs.get('cache.clearInterval')"},
		       {java: "$configs.get('cache.clearTime')"}]
	},
	dao: {
        type : "org.nutz.dao.impl.NutDao",
        args : [{refer:"dataSource"}]
    },
	dataSource: {
        type: "com.alibaba.druid.pool.DruidDataSource",
        events: {
            depose: "close"
        },
        fields : {
            driverClassName: {java: "$configs.get('db.dbDriver')"},
            url: {java: "$jdbcConfig.getConnString()"},
            username: {java: "$jdbcConfig.getConnUser()"},
            password: {java: "$jdbcConfig.getConnPass()"}
        }
    }
}