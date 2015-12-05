package com.lps.tqms.dao;

import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.lps.tqms.pojo.DbConfig;

@IocBean
public class BaseDao {
	@Inject("refer:dbConfig")
	protected DbConfig config;
	@Inject("refer:dao")
	protected Dao dao;
	@Inject("refer:imagePath")
	public String imagePath;
	@Inject("refer:imagePath2")
	public String imagePath2;
	@Inject("refer:dataSource")
	public DataSource dataSource;
}
