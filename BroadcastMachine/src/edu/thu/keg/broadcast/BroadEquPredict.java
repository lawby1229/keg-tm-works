package edu.thu.keg.broadcast;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.thu.keg.provider.IDatabaseProvider;
import edu.thu.keg.provider.impl.OracleProviderImpl;

public class BroadEquPredict {
	List<Long> alarmTimeSeriel;

	public BroadEquPredict() {

	}

	private void getAlarmTime() {
		alarmTimeSeriel = new ArrayList<>();
		String sql_alarm = "select alarm_datetime from r4_alr_id10041610cB57D02_nnull";
		IDatabaseProvider ssp = null;
		ssp = OracleProviderImpl.getInstance("broadcastdpt", "root");
		PreparedStatement pstmt = null;
		long timeseries;
		Calendar CT = Calendar.getInstance();
		try {
			System.out.println(sql_alarm);
			pstmt = ssp.getConnection().prepareStatement(sql_alarm,
					Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Date dt = rs.getDate("alarm_datetime");
				CT.setTime(dt);
				timeseries = CT.getTimeInMillis();
				alarmTimeSeriel.add(timeseries);
				System.out.println(timeseries);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				pstmt.getConnection().close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String age[]) {
		System.out.println("链接oracle数据路：... ");
		String sql_equ = "select * from R4_EQU_ID10041610CB57D02_NNULL order by result_id  ";
		IDatabaseProvider ssp = null;
		ssp = OracleProviderImpl.getInstance("broadcastdpt", "root");
		PreparedStatement pstmt = null;
		FileWriter fw = null;

		int resultId = 0;
		String datatime = null;
		String type = null;
		double value = 0;
		long timeseries;
		Calendar CT = Calendar.getInstance();
		try {
			pstmt = ssp.getConnection().prepareStatement(sql_equ,
					Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				resultId = (int) rs.getDouble("RESULT_ID");
				Date dt = rs.getDate("CHECK_DATETIME");
				CT.setTime(dt);
				timeseries = CT.getTimeInMillis();
				type = rs.getString("TYPE");
				value = rs.getDouble("VALUE1");
				System.out.println(resultId + ", " + timeseries + ", " + type
						+ ", " + value);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				pstmt.getConnection().close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
