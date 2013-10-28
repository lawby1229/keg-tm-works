package edu.thu.keg.GB.http0702;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.sql.TIMESTAMP;

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
		HashMap<String, ImsiImseiTimeSet> iitsHash = new HashMap<>();
		ImsiImseiTimeSet iitSet = null;
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String imei = rs.getString("IMEI");
				String minDate = rs.getString("MINREQ");
				String maxDate = rs.getString("MAXREQ");
				ImsiImseiTime iit = new ImsiImseiTime(imsi, imei, minDate,
						maxDate);// 生成一个iit四元组的实例
				if (!iitsHash.containsKey(imsi)) {
					iitsHash.put(imsi, new ImsiImseiTimeSet());
				}
				iitSet = iitsHash.get(imsi);
				iitSet.add(iit);
				if (i % 10000 == 0)
					System.out.println(i);
				i++;
			}

			iitSet = null;
			ImsiImseiTime iit = null;
			String print = "";
			Iterator<ImsiImseiTimeSet> it_iits = iitsHash.values().iterator();
			FileWriter fw = new FileWriter("用户持有手机排数异己.csv");
			while (it_iits.hasNext()) {
				iitSet = it_iits.next();
				for (int j = 0; j < iitSet.iits.size(); j++) {
					iit = iitSet.iits.get(j);

					print = iit.Imsi + "," + iit.Imei + ","
							+ new TIMESTAMP(iit.minDate).stringValue() + ","
							+ new TIMESTAMP(iit.maxDate).timestampValue() + "\n";
					fw.write(print);
				}
				fw.flush();
			}
			fw.close();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bcf.closeCon();
		}
	}
}

/**
 * 存数imsi-imei-min-max四元组的集合
 * 
 * @author ybz
 * 
 */
class ImsiImseiTimeSet {
	List<ImsiImseiTime> iits;

	public ImsiImseiTimeSet() {
		iits = new ArrayList<ImsiImseiTime>();
	}

	/**
	 * 添加一个四元组到同一个imsi的集合中区 如果有包含关系的进行取舍，保留大范围的
	 * 
	 * @param iit
	 */
	public void add(ImsiImseiTime iit) {
		ImsiImseiTime iitVal = null;
		for (int i = 0; i < iits.size(); i++) {
			iitVal = iits.get(i);
			if (iitVal.contains(iit) > 0) {
				iits.set(i, iit);
				return;
			}
		}
		iits.add(iit);
	}

}

/**
 * 存数imsi-imei-min-max四元组
 * 
 * @author ybz
 * 
 */
class ImsiImseiTime {

	String Imsi;
	String Imei;
	String minDate;
	String maxDate;
	Calendar minCT = Calendar.getInstance();
	Calendar maxCT = Calendar.getInstance();
	long minReq, maxReq;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	ImsiImseiTime(String imsi, String imei, String minDate, String maxDate) {
		this.Imsi = imsi;
		this.Imei = imei;
		this.minDate = minDate;
		this.maxDate = maxDate;

		try {

			minCT.setTime(sdf.parse(minDate));
			maxCT.setTime(sdf.parse(maxDate));
			minReq = minCT.getTimeInMillis();
			maxReq = maxCT.getTimeInMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int contains(ImsiImseiTime iit) {
		if (minReq < iit.minReq && maxReq > iit.maxReq) {
			return 1;
		} else if (minReq > iit.minReq && maxReq < iit.maxReq) {
			return -1;
		} else
			return 0;
	}
}