package edu.thu.keg.provider.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import edu.thu.keg.provider.DatabaseProviderException;
import edu.thu.keg.provider.IDatabaseProvider;
import edu.thu.keg.provider.IllegalQueryException;

public class OracleProviderImpl implements IDatabaseProvider {
	private static OracleProviderImpl instance;

	public static OracleProviderImpl getInstance(String userName,
			String password) {
		// TODO multi-thread
		if (instance == null)
			instance = new OracleProviderImpl(userName, password);
		System.out.println(instance.connString);
		return instance;
	}

	private Connection conn;
	private String connString;

	private OracleProviderImpl(String userName, String password) {

		this.setDefaultSQLProvider(userName, password);
	}

	private void setDefaultSQLProvider(String userName, String password) {
		String address = ResourceBundle.getBundle("database").getString(
				"OracleAddress");
		String dbName = ResourceBundle.getBundle("database").getString(
				"databaseName");
		// String connString = "jdbc:" + address + ";databaseName=" +
		// databaseName
		// + ";integratedSecurity=true;";
		//
		String connString = "jdbc:oracle:thin:" + userName + "/" + password
				+ "@" + address + ":" + dbName;
		this.connString = connString;
	}

	@Override
	public String getConnectionString() {
		return connString;
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (this.conn == null || this.conn.isClosed())
			this.conn = DriverManager.getConnection(connString);
		return this.conn;
	}

	@Override
	public ResultSet executeQuery(String query) throws IllegalQueryException {
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return rs;
		} catch (Exception ex) {
			throw new IllegalQueryException(ex.getMessage());
		}
	}

	@Override
	public void execute(String text) throws DatabaseProviderException {
		Statement stmt;
		try {
			stmt = this.getConnection().createStatement();
			stmt.executeUpdate(text);
			System.out.println("Excute:" + text);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void closeConnection() throws SQLException {
		this.conn.close();

	}

}
