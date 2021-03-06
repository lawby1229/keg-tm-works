package edu.thu.keg.GB.http0702.brand.features.tfidf;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.thu.keg.GB.http0702.brand.iimmfilter.BrandChangeFilter;

/**
 * 对多分类的7种手机数据进行分类和预测的特征抽取 其中 特征值：用户上网所经历的HOST网址 标签：手机的品牌
 * 
 * @author Law
 * 
 */
public class MultiHostToFeatureExtraTI {
	final static HashMap<String, Integer> FeatureMap2Int = new HashMap<>();// 其中值是从1开始的
	// 其中值是从1开始的

	final static HashMap<String, Integer> VersionMap = new HashMap<>();// 其中值是从1开始的
	HashMap<Integer, Integer> FeatureSumMap = new HashMap<>();// 键值代表维度和全文出现总数总数
	List<Integer> lineSumList = new ArrayList<Integer>();// 每行是单词总数
	int DimensionOfClass = 16;
	String trainTable = "";
	String testTable = "";
	int[] trainDis;
	int[] testDis;
	String tag;
	List<HashMap<Integer, Integer>> trainFeatures;
	List<HashMap<Integer, Integer>> testFeatures;
	boolean isVersionAsTag;
	String folder;
	int partitionNum = 200;

	/**
	 * @return the partitionNum
	 */
	public int getPartitionNum() {
		return partitionNum;
	}

	/**
	 * @param partitionNum
	 *            the partitionNum to set
	 */
	public void setPartitionNum(int partitionNum) {
		this.partitionNum = partitionNum;
	}

	public MultiHostToFeatureExtraTI(String trainTable, String testTable,
			String tag) {
		this.trainTable = trainTable;
		this.testTable = testTable;
		this.tag = tag;
		this.isVersionAsTag = false;
	}

	/**
	 * 根据isTrainFile 更新trainFeature或者testFesture 如果加載過version就得到的是version当标签的
	 * 
	 * @param isTrainFile
	 */
	public void getFile(boolean isTrainFile, boolean isUsePartition) {
		List<HashMap<Integer, Integer>> Features;
		int[] Dis;
		ResultSet rs;
		if (isTrainFile) {
			trainFeatures = new ArrayList<HashMap<Integer, Integer>>();
			trainDis = new int[DimensionOfClass];
			Features = trainFeatures;
			Dis = trainDis;
			if (!isUsePartition)
				rs = getRs(trainTable);
			else
				rs = getRsPartion(trainTable, partitionNum);
		} else {
			testFeatures = new ArrayList<HashMap<Integer, Integer>>();
			Features = testFeatures;
			testDis = new int[DimensionOfClass];
			Dis = testDis;
			if (!isUsePartition)
				rs = getRs(testTable);
			else
				rs = getRsPartion(testTable, partitionNum);
		}
		// Features = new ArrayList<int[]>();
		String imsiRow = "";
		int i = -1;
		HashMap<Integer, Integer> row = null;
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String host = rs.getString("HOST");

				if (!imsiRow.equals(imsi)) {
					int brandtype;
					if (!isVersionAsTag)
						brandtype = rs.getInt(tag);
					else {
						String version = rs.getString(tag);
						if (!VersionMap.containsKey(version))
							continue;
						brandtype = VersionMap.get(version);
					}
					Dis[brandtype]++;
					imsiRow = imsi;
					i++;
					row = new HashMap<Integer, Integer>();
					row.put(0, brandtype);
					Features.add(row);
					// System.out.print(i + ",");
				}
				if (!FeatureMap2Int.containsKey(host))
					continue;
				int iKey = (int) FeatureMap2Int.get(host);
				int iValue = 1;
				if (row.containsKey(iKey)) {
					iValue = row.get(iKey).intValue() + 1;
				}
				row.put(iKey, iValue);
			}
			System.out.print("\n");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ResultSet getRs(String tableName) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imsi, host, " + tag + " from "
				+ tableName + " order by imsi");
		return rs;
	}

	private ResultSet getRsPartion(String tableName, int partitionNum) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imsi,host, " + tag + " from "
				+ tableName + " where imsi in" + " (select imsi from "
				+ "(select imsi," + tag + ",row_number() over (partition by "
				+ tag + " order by count(*) desc) row_number from " + tableName
				+ " group by imsi," + tag + ") where row_number<"
				+ partitionNum + ")" + " order by " + tag + ",imsi");
		// select * from X61_TEST_BASE_BEHAVIOR_nb where imsi in
		// (select imsi from (select imsi ,c5,row_number() over (partition by c5
		// order by count(*) desc) row_number --根据brandtype分组,logcnt排名
		// from X61_TEST_BASE_BEHAVIOR_nb group by imsi,c5) where row_number<10)
		// order by c5, imsi;

		return rs;
	}

	/**
	 * 根据isTrainFile 往文件里写trainFile或者testFile 并且写分部文件
	 * 
	 * @param isTrainFile
	 */
	public void writeFeatureToFile(boolean isTrainFile) {
		String tableName = "";
		Iterator<HashMap<Integer, Integer>> it;
		if (isTrainFile) {
			tableName = trainTable;
			it = trainFeatures.iterator();
		} else {
			tableName = testTable;
			it = testFeatures.iterator();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(tableName + "_MobileSet_Base_Host_" + tag
					+ ".txt");
			while (it.hasNext()) {
				String rowStr = "";
				HashMap<Integer, Integer> row = it.next();
				Integer[] keys = row.keySet().toArray(new Integer[0]);
				Arrays.sort(keys);
				rowStr = row.get(keys[0]) + "";
				for (int i = 1; i < keys.length; i++) {
					rowStr = rowStr + " " + keys[i] + ":" + row.get(keys[i]);
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
		writeDisFile(isTrainFile);
	}

	/**
	 * 根据isTrainFile 往文件里写trainFile或者testFile的TFIDF特征提取后的文件 并且写分部文件
	 * 
	 * @param isTrainFile
	 */
	public void writeTfIdfFeatureToFile(boolean isTrainFile) {
		String tableName = "";
		Iterator<HashMap<Integer, Double>> it;
		if (isTrainFile) {
			tableName = trainTable;
			it = getTfIdfFeature(trainFeatures).iterator();
		} else {
			tableName = testTable;
			it = getTfIdfFeature(testFeatures).iterator();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(tableName + "_MobileSet_Base_Host_TFIDF_" + tag
					+ ".libsvm");
			while (it.hasNext()) {
				String rowStr = "";
				HashMap<Integer, Double> row = it.next();
				Integer[] keys = row.keySet().toArray(new Integer[0]);
				Arrays.sort(keys);
				rowStr = row.get(0) + "";
				for (int i = 0; i < keys.length; i++) {
					if (keys[i] == 0)
						continue;
					rowStr = rowStr + " " + keys[i] + ":" + row.get(keys[i]);
				}
				// System.out.println(rowStr);
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
		writeDisFile(isTrainFile);
	}

	/**
	 * 写分部文件，每次写文件后会调用
	 * 
	 * @param isTrainFile
	 */
	private void writeDisFile(boolean isTrainFile) {
		int[] Dis = null;
		String tableName = "";
		if (isTrainFile) {
			tableName = trainTable;
			Dis = trainDis;
		} else {
			tableName = testTable;
			Dis = testDis;
		}
		FileWriter fw;
		try {
			fw = new FileWriter("Dis_" + tableName + "_MobileSet_Base_Host_"
					+ tag + ".txt");
			for (int i = 0; i < Dis.length; i++) {
				fw.write(i + " " + Dis[i] + "\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void loadHostDimension(String field, String tableName) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select distinct(" + field + ")" + " from "
				+ tableName);
		try {
			int i = 1;
			while (rs.next()) {
				FeatureMap2Int.put(rs.getString(1), i);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void loadVersionDimension(String field, String tableName) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		isVersionAsTag = true;
		ResultSet rs = bcf.runsql("select distinct(" + field + ")" + " from "
				+ tableName);
		try {
			FileWriter fw = new FileWriter("Dimension_" + tableName + "_"
					+ field + "_map.txt");

			int i = 1;
			String str = null;
			while (rs.next()) {
				str = rs.getString(1);
				fw.write(str + " " + i + "\n");
				VersionMap.put(str, i++);
			}
			DimensionOfClass = VersionMap.size() + 1;
			fw.flush();
			fw.close();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 讲feature转换成tfidf的feature
	 * 
	 * @param feature
	 * @return
	 */
	private List<HashMap<Integer, Double>> getTfIdfFeature(
			List<HashMap<Integer, Integer>> feature) {
		for (int i = 0; i < feature.size(); i++) {// 每一行的feature
			HashMap<Integer, Integer> row = feature.get(i);
			Iterator<Integer> itHost = row.keySet().iterator();
			int sumLine = 0;
			while (itHost.hasNext()) {// 一行中的每一个
				int hostId = itHost.next();
				sumLine += row.get(hostId);
				if (!FeatureSumMap.containsKey(hostId))
					FeatureSumMap.put(hostId, 0);
				FeatureSumMap.put(hostId, FeatureSumMap.get(hostId) + 1);
			}
			lineSumList.add(sumLine);
		}
		List<HashMap<Integer, Double>> result = new ArrayList<HashMap<Integer, Double>>();
		for (int i = 0; i < feature.size(); i++) {
			HashMap<Integer, Integer> row = feature.get(i);
			Iterator<Integer> itHost = row.keySet().iterator();
			HashMap<Integer, Double> rowNew = new HashMap<>();
			while (itHost.hasNext()) {
				double tf = 0, idf = 0;
				int hostId = itHost.next();
				if (hostId == 0) {
					rowNew.put(0, row.get(0) + 0.0);
					continue;
				}
				tf = (double) row.get(hostId) / lineSumList.get(i);
				if (FeatureSumMap.get(hostId) == 0)
					idf = 1;
				else
					idf = Math.log((double) lineSumList.size()
							/ FeatureSumMap.get(hostId));
//				System.out.print("tf:" + tf);
//				System.out.print("idf:" + idf + "\n");
				rowNew.put(hostId, tf * idf);
			}
			result.add(rowNew);
		}
		return result;
	}

	public static void main(String arg[]) {
		// 对多分类的7种手机数据进行分类和预测的特征抽取
		// MultiHostToBrandFeatExtra app = new MultiHostToBrandFeatExtra(
		// "z1_train_one_g500_top1000", "Z21_TEST_HOST_IN_TRAIN");
		// app.loadHostDimension("host", "Z21_TEST_HOST_IN_TRAIN");
		// app.getFile(false);
		// app.writeFeatureToFile(false);
		//
		// app.getFile(true);
		// app.writeFeatureToFile(true);
		for (int i = 4; i <= 15; i++) {
			MultiHostToFeatureExtraTI app = new MultiHostToFeatureExtraTI(
					"X8_ONE_G500_ADDFUNC_NBQ_G5_T3K",
					"//X51_TEST_BASE_BEHAVIOR", "C" + i);
			System.out.println("1");
			app.loadHostDimension("host", "X8_ONE_G500_ADDFUNC_NBQ_G5_T3K");
			// System.out.println("2");
			// app.loadVersionDimension("version", "X4_TRAIN_ONE_G500_ADDFUNC");
			// System.out.println("3");
			// app.getFile(false);
			// System.out.println("4");
			// app.writeTfIdfFeatureToFile(false);
			app.setPartitionNum(150 - (i - 4) * 10);
			app.getFile(true, true);
			app.writeTfIdfFeatureToFile(true);
		}
		// app = new MultiHostToFeatureExtra("Z3_TRAIN_ONE_G500T1K_ADDFUNC",
		// "Z32_TEST_BASE_HOST", "C4");
		// app.loadHostDimension("host", "Z3_TRAIN_ONE_G500T1K_ADDFUNC");
		// app.getFile(false);
		// app.writeFeatureToFile(false);
		// app.getFile(true);
		// app.writeFeatureToFile(true);
		// app = new MultiHostToFeatureExtra("Z3_TRAIN_ONE_G500T1K_ADDFUNC",
		// "Z32_TEST_BASE_HOST", "C5");
		// app.loadHostDimension("host", "Z3_TRAIN_ONE_G500T1K_ADDFUNC");
		// app.getFile(false);
		// app.writeFeatureToFile(false);
		// app.getFile(true);
		// app.writeFeatureToFile(true);
		// app = new MultiHostToFeatureExtra("Z3_TRAIN_ONE_G500T1K_ADDFUNC",
		// "Z32_TEST_BASE_HOST", "C6");
		// app.loadHostDimension("host", "Z3_TRAIN_ONE_G500T1K_ADDFUNC");
		// app.getFile(false);
		// app.writeFeatureToFile(false);
		// app.getFile(true);
		// app.writeFeatureToFile(true);

	}
}
