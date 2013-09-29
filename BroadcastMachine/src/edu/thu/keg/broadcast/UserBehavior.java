package edu.thu.keg.broadcast;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.thu.keg.provider.IDatabaseProvider;
import edu.thu.keg.provider.impl.OracleProviderImpl;

public class UserBehavior {
	public static void main(String age[]) {
		System.out.println("链接oracle数据路：... ");
		String sql = "select imei from B9_IMEI_BRANDING ";
		IDatabaseProvider ssp = null;
		ssp = OracleProviderImpl.getInstance("bj_broadcastdpt", "root");
		PreparedStatement pstmt = null;
		FileWriter fw = null;
		try {
			System.out.println(sql);
			pstmt = ssp.getConnection().prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = pstmt.executeQuery();

			Calendar CT = Calendar.getInstance();
			String ConnectionTime = "none";
			ConnectionTime = rs.getString("ConnectTime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = sdf.parse(ConnectionTime);
			CT.setTime(dt);
			long timeseries = CT.getTimeInMillis();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fw.close();
				pstmt.getConnection().close();
			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
