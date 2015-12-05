package com.lps.tqms.pojo;

public class DbConfig {
	private String connString;
	private String connUser;
	private String connPass;
	private String dbDriver;
	
	public DbConfig() {
	}
	
	public DbConfig(String connString, String connUser, String connPass,
			String dbDriver) {
		super();
		this.connString = connString;
		this.connUser = connUser;
		this.connPass = connPass;
		this.dbDriver = dbDriver;
	}

	public String getConnString() {
		return connString;
	}

	public String getConnUser() {
		return connUser;
	}

	public String getConnPass() {
		return connPass;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setConnString(String connString) {
		this.connString = connString;
	}

	public void setConnUser(String connUser) {
		this.connUser = connUser;
	}

	public void setConnPass(String connPass) {
		this.connPass = connPass;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	
}
