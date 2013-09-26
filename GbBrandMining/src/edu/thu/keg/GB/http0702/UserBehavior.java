package edu.thu.keg.GB.http0702;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import edu.thu.keg.provider.IDatabaseProvider;
import edu.thu.keg.provider.impl.OracleProviderImpl;

public class UserBehavior {
	public static void main(String age[]) {
		System.out.println("链接oracle数据路：... ");
		String sql = "select * from t_gb_cdr_http_0702     where rownum <  5000";
		IDatabaseProvider ssp = null;
		ssp = OracleProviderImpl.getInstance("bj_gb", "root");
		PreparedStatement pstmt;
		try {
			System.out.println(sql);
			pstmt = ssp.getConnection().prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = pstmt.executeQuery();
			int i = 0;
			while (rs.next() && i++ < 10) {
				System.out.println(rs.getString(1));
			}
			pstmt.getConnection().close();
		} catch (SQLException e) {
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
