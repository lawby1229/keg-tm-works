package edu.thu.keg.GB.http0702.brand.features;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.thu.keg.GB.http0702.brand.iimmfilter.BrandChangeFilter;

public class MultiFeatureExtraction {
	final static HashMap<String, Integer> FeatureMap = new HashMap<>();
	final static String keys[] = { "查地图", "查消息", "查信息", "管理手机", "逛空间", "看视频",
			"看新闻", "聊天", "买东西", "拍照", "上人人", "上网", "上微博", "收发邮件", "听音乐", "通信",
			"玩游戏", "阅读", "照明", "做记录" };

	String trainTable = "";
	String testTable = "";
	List<int[]> trainFeatures;
	List<int[]> testFeatures;

	public MultiFeatureExtraction(String trainTable, String testTable) {
		this.trainTable = trainTable;
		this.testTable = testTable;
		trainFeatures = new ArrayList<int[]>();
		for (int i = 0; i < keys.length; i++) {
			FeatureMap.put(keys[i], i + 1);
		}
	}

	public void getFile(boolean isTrainFile) {
		List<int[]> Features;
		ResultSet rs;
		if (isTrainFile) {
			Features = trainFeatures;
			rs = getRs(trainTable);
		} else {
			Features = testFeatures;
			rs = getRs(testTable);
		}
		Features = new ArrayList<int[]>();
		String imsiRow = "";
		int i = -1;
		int[] row = null;
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String behavior = rs.getString("BEHAVIOR");
				int brandtype = rs.getInt("BRANDTYPE");
				if (!imsiRow.equals(imsi)) {
					imsiRow = imsi;
					i++;
					row = new int[FeatureExtract.DIMENSION + 1];
					row[0] = brandtype;
					Features.add(row);
					System.out.println(i);
				}
				row[FeatureExtract.FeatureMap.get(behavior)]++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ResultSet getRs(String tableName) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imsi,behavior,brandtype from "
				+ tableName + " order by imsi");
		return rs;
	}

	public void writeFeatureToFile(boolean isTrainFile) {
		String tableName = "";
		Iterator<int[]> it;
		if (isTrainFile) {
			tableName = trainTable;
			it = trainFeatures.iterator();
		} else {
			tableName = testTable;
			it = testFeatures.iterator();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(tableName + "_Feature.txt");
			while (it.hasNext()) {
				String rowStr = "";
				int row[] = it.next();
				rowStr = row[0] + "";
				for (int i = 1; i < row.length; i++) {
					if (row[i] != 0)
						rowStr = rowStr + " " + i + ":" + row[i];
				}
				System.out.println(rowStr);
				fw.write(rowStr + "\n");
				fw.flush();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String arg[]) {
		MultiFeatureExtraction app = new MultiFeatureExtraction("", "");
		app.getFile(true);
		app.writeFeatureToFile(true);

	}
}
