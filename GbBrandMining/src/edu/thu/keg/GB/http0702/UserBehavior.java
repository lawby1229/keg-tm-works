package edu.thu.keg.GB.http0702;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.SliderUI;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import edu.thu.keg.parse.html.imei.ImeiParse;
import edu.thu.keg.parse.html.imei.ImeiPraseImeiInfo;
import edu.thu.keg.provider.IDatabaseProvider;
import edu.thu.keg.provider.impl.OracleProviderImpl;

public class UserBehavior {
	public static void main(String age[]) {
		System.out.println("链接oracle数据路：... ");
		String sql = "select distinct(imei) from t_gb_cdr_http_0702     where imei is not null and rownum <  10";
		IDatabaseProvider ssp = null;
		ssp = OracleProviderImpl.getInstance("bj_gb", "root");
		PreparedStatement pstmt = null;
		String url = "http://www.imei.info/api/checkimei/";
		Map<String, String> param = new HashMap<String, String>();
		param.put("login", "lawby1229");
		param.put("password", "248929250law");
		// param.put("imei", "");
		FileWriter fw = null;
		try {
			System.out.println(sql);
			pstmt = ssp.getConnection().prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = pstmt.executeQuery();
			int i = 0;
			fw = new FileWriter("Imei-brand", true);
			String imei = null;
			String type = null;
			String brand = null;
			while (rs.next() && i++ < 10) {
				imei = rs.getString(1);
				List<String> doublekill = ImeiParse.ImeiPraser(imei);
				while (doublekill.size() > 0
						&& doublekill.get(0).startsWith(" if you are not")) {
					System.out.println(i + "正在重试");
					doublekill = ImeiParse.ImeiPraser(imei);
				}
				if (doublekill.size() == 0)
					System.out.println(i + ":没有");
				else {
					System.out.println(i + ":" + doublekill);
					fw.write(imei + "," + doublekill.get(0) + ","
							+ doublekill.get(1) + "\n");
					fw.flush();
				}
				// param.put("imei", rs.getString(1));
				// String reInfo = ImeiPraseImeiInfo.http(url, param);
				// System.out.println(reInfo);
				// JSONObject job = JSONObject.fromObject(reInfo);
				// if (!job.has("error"))
				// System.out.println(job.getString("model"));

			}

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
		// pstmt.setString(1, userid);
		// pstmt.setString(2, favstr);

	}

	int change() {
		return 0;
	}
}

class Child extends UserBehavior {

	public int change() {
		return 0;
	}
}
