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

public class FeatureExtract {

	final static HashMap<String, Integer> FeatureMap = new HashMap<>();
	final static String keys[] = { "查地图", "查消息", "查信息", "管理手机", "逛空间", "看视频",
			"看新闻", "聊天", "买东西", "拍照", "上人人", "上网", "上微博", "收发邮件", "听音乐", "通信",
			"玩游戏", "阅读", "照明", "做记录" };

	final static int DIMENSION = keys.length;

	List<int[]> Features;
	String tableName = "";

	public FeatureExtract(String tableName) {
		Features = new ArrayList<int[]>();
		this.tableName = tableName;
		for (int i = 0; i < keys.length; i++) {
			FeatureMap.put(keys[i], i + 1);
		}

	}

	/**
	 * 抽取特征值主函数
	 */
	public void getFeatures() {
		String imsiRow = "";
		int i = -1;
		int[] row = null;
		ResultSet rs = getRs();
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String behavior = rs.getString("BEHAVIOR");
				// System.out.println(imsi + "," + behavior + " " + imsiRow);
				if (!imsiRow.equals(imsi)) {
					imsiRow = imsi;
					i++;
					row = new int[FeatureExtract.DIMENSION + 1];
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

	/**
	 * 得到sql运行结果
	 * 
	 * @return
	 */
	public ResultSet getRs() {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imsi,behavior from " + tableName
				+ " order by imsi");
		return rs;
	}

	/**
	 * 將最後的特征集合写入文件
	 * 
	 * @param isPos
	 */
	public void writeFeatureToFile(int isPos) {
		String rowStr = String.valueOf(isPos);
		FileWriter fw = null;
		try {
			fw = new FileWriter(tableName + "_Feature.txt");
			Iterator<int[]> it = Features.iterator();
			while (it.hasNext()) {
				rowStr = String.valueOf(isPos);
				int row[] = it.next();
				for (int i = 0; i < row.length; i++) {
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
		FeatureExtract fe = new FeatureExtract("D171_iIMM_behavior_tag_top800");
		fe.getFeatures();
		fe.writeFeatureToFile(1);
		fe = new FeatureExtract("C165_iIMM_BT_WITH_SBRAND_G50");
		fe.getFeatures();
		fe.writeFeatureToFile(-1);

	}
}
