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

/**
 * 对多分类的7种手机数据进行分类和预测的特征抽取 其中 特征值：用户行为 标签：手机属于的集合
 * 
 * @author Law
 * 
 */
public class MultiBehavirToMobileSetFeatExtra {
	final static HashMap<String, Integer> FeatureMap = new HashMap<>();
	final static String keys[] = { "查地图", "查消息", "查信息", "管理手机", "逛空间", "看视频",
			"看新闻", "聊天", "买东西", "拍照", "上人人", "上网", "上微博", "收发邮件", "听音乐", "通信",
			"玩游戏", "阅读", "照明", "做记录" };
	final static int DimensionOfClass = 16;
	final static int Dimension = keys.length;
	String trainTable = "";
	String testTable = "";
	String tag = "";
	List<int[]> trainFeatures;
	List<int[]> testFeatures;
	int[] trainDis;
	int[] testDis;

	public MultiBehavirToMobileSetFeatExtra(String trainTable,
			String testTable, String tag) {
		this.trainTable = trainTable;
		this.testTable = testTable;
		this.tag = tag;
		trainFeatures = new ArrayList<int[]>();
		testFeatures = new ArrayList<int[]>();
		for (int i = 0; i < keys.length; i++) {
			FeatureMap.put(keys[i], i + 1);
		}
	}

	public void getFile(boolean isTrainFile) {
		List<int[]> Features;
		int[] Dis;
		ResultSet rs;
		if (isTrainFile) {
			trainFeatures = new ArrayList<int[]>();
			Features = trainFeatures;
			trainDis = new int[DimensionOfClass];
			Dis = trainDis;
			rs = getRs(trainTable, tag);
		} else {
			testFeatures = new ArrayList<int[]>();
			Features = testFeatures;
			testDis = new int[DimensionOfClass];
			Dis = testDis;
			rs = getRs(testTable, tag);
		}
		// Features = new ArrayList<int[]>();
		String imsiRow = "";
		int i = -1;
		int[] row = null;
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String behavior = rs.getString("BEHAVIOR");
				if (!imsiRow.equals(imsi)) {
					int brandtype = rs.getInt(tag);
					Dis[brandtype]++;
					imsiRow = imsi;
					i++;
					row = new int[Dimension + 1];
					row[0] = brandtype;
					Features.add(row);
					System.out.println(i);
				}
				row[FeatureMap.get(behavior)]++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ResultSet getRs(String tableName, String tag) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imsi,behavior," + tag + " from "
				+ tableName + " order by imsi");
		return rs;
	}

	public void writeFeatureToFile(boolean isTrainFile) {
		String tableName = "";
		int[] Dis;
		Iterator<int[]> it;
		if (isTrainFile) {
			tableName = trainTable;
			Dis = trainDis;
			it = trainFeatures.iterator();
		} else {
			tableName = testTable;
			Dis = testDis;
			it = testFeatures.iterator();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(tableName + "_MobileSet_Base_Behavior_" + tag
					+ ".txt");
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
			fw.close();
			// 写分布文件
			fw = new FileWriter("Dis_" + tableName
					+ "_MobileSet_Base_Behavior_" + tag + ".txt");
			for (int i = 0; i < Dis.length; i++) {
				fw.write(i + " " + Dis[i] + "\n");
			}
			fw.flush();
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
		// 对多分类的7种手机数据进行分类和预测的特征抽取
		// MultiBehaviorToBrandFeatExtra app = new
		// MultiBehaviorToBrandFeatExtra(
		// "Z0_TRAIN_ONE_G500_TOP1000", "Z0_TEST_CHANGED_EACH_BRAND");
		MultiBehavirToMobileSetFeatExtra app;
//		app = new MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
//				"X41_TEST_BASE_BEHAVIOR", "C6");
//		app.getFile(false);
//		app.writeFeatureToFile(false);
//		app.getFile(true);
//		app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C7");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C8");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C9");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C10");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C11");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C12");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C13");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C14");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
		 app = new
		 MultiBehavirToMobileSetFeatExtra("X4_TRAIN_ONE_G500_ADDFUNC",
		 "X41_TEST_BASE_BEHAVIOR", "C15");
		 app.getFile(false);
		 app.writeFeatureToFile(false);
		 app.getFile(true);
		 app.writeFeatureToFile(true);
	}
}
