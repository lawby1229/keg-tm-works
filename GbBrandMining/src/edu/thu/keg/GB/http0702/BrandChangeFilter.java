package edu.thu.keg.GB.http0702;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.thu.keg.parse.html.imei.ImeiParse;
import edu.thu.keg.provider.IDatabaseProvider;
import edu.thu.keg.provider.impl.OracleProviderImpl;

/**
 * 将数据库中的b11_b4_imsi_imei_minmax_req表中的 imsi用户的imei交集去除，形成前max不小于后min的数据
 * 
 * @author Law
 * 
 */
public class BrandChangeFilter {
	Connection conn = null;

	public BrandChangeFilter() {

	}

	public ResultSet runsql(String sql) {
		System.out.println("链接oracle数据路：... ");
		// sql =
		// "select * from b11_b4_imsi_imei_minmax_req order by imsi,minreq";
		IDatabaseProvider ssp = null;
		ssp = OracleProviderImpl.getInstance("bj_brand", "root");
		PreparedStatement pstmt = null;
		FileWriter fw = null;
		ResultSet rs = null;
		try {
			System.out.println(sql);
			conn = ssp.getConnection();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			rs = pstmt.executeQuery();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
		// pstmt.setString(1, userid);
		// pstmt.setString(2, favstr);
	}

	public void closeCon() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String arg[]) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf
				.runsql("select * from b11_b4_imsi_imei_minmax_req where rownum<10 order by imsi,minreq");

		int i = 0;
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String imei = rs.getString("IMEI");
				Calendar CT = Calendar.getInstance();
				String date = rs.getString("MINREQ");
				System.out.println(imsi+" "+date);
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:SS");

				Date dt = sdf.parse(date);
				CT.setTime(dt);
				System.out.println(imsi + " " + " 日子：" + CT.DAY_OF_YEAR + " 小时： "
						+ CT.HOUR + " 分钟：" +CT.MINUTE );
				i++;
			}
		} catch (SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bcf.closeCon();
		}
	}
}
