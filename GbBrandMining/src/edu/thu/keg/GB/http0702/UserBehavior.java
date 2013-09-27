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

import edu.thu.keg.parse.html.imei.ImeiParse;
import edu.thu.keg.parse.html.imei.ImeiPraseImeiInfo;
import edu.thu.keg.provider.IDatabaseProvider;
import edu.thu.keg.provider.impl.OracleProviderImpl;

public class UserBehavior {
	public static void main(String age[]) {
		System.out.println("链接oracle数据路：... ");
		String sql = "select distinct(imei) from t_gb_cdr_http_0702     where imei is not null and rownum <  5000";
		IDatabaseProvider ssp = null;
		ssp = OracleProviderImpl.getInstance("bj_gb", "root");
		PreparedStatement pstmt;
		String url = "http://www.imei.info/api/checkimei/";
		Map<String, String> param = new HashMap<String, String>();
		param.put("login", "lawby1229");
		param.put("password", "248929250law");
		// param.put("imei", "");
		try {
			System.out.println(sql);
			pstmt = ssp.getConnection().prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = pstmt.executeQuery();
			int i = 0;
			FileWriter fw = new FileWriter("Imei-brand");
			while (rs.next() && i++ < 3) {
				System.out.println(i + " " + rs.getString(1));
				List<String> tribleRe = ImeiParse.ImeiPraser(rs.getString(1));
				System.out.println(tribleRe);
				param.put("imei", rs.getString(1));
				String reInfo = ImeiPraseImeiInfo.http(url, param);
				System.out.println(reInfo);
			}
			pstmt.getConnection().close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
