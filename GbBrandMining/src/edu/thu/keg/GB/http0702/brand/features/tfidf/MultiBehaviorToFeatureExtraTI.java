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

import org.apache.commons.lang.ArrayUtils;

import edu.thu.keg.GB.http0702.brand.iimmfilter.BrandChangeFilter;

/**
 * 对多分类的7种手机数据进行分类和预测的特征抽取 其中 特征值：用户上网所经历的HOST网址 标签：手机的品牌
 * 
 * @author Law
 * 
 */
public class MultiBehaviorToFeatureExtraTI implements IBrandTools {
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

	HashMap<String, Integer> FeatureMap2Int = new HashMap<>();// 其中值是从1开始的，其中值是从1开始的,记录每个标签对应的唯一编号
	HashMap<String, Integer> VersionMap = new HashMap<>();// 其中值是从1开始的

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

	public MultiBehaviorToFeatureExtraTI(String trainTable, String testTable,
			String tag) {
		this.trainTable = trainTable;
		this.testTable = testTable;
		this.tag = tag;
		this.isVersionAsTag = false;
	}

	/**
	 * 通过读去数据库表格数据，将testTable和trainTable保存到相应的testFeature和trainFeature中
	 */
	public void getTrainTestFiles() {
		// 得到训练数据集
		System.out.println("get train file...");
		getFile(true);
		// 加载测试数据集
		System.out.println("get test file...");
		getFile(false);
		// 加载所有标签出现的文章数
		setFeatureSumMap();
	}

	public void getSingleFiles(boolean isTrainFile) {
		// 得到训练数据集
		System.out.println("get " + ((isTrainFile) ? "train" : "test")
				+ " file...");
		getFile(isTrainFile);
		// 加载所有标签出现的文章数
		setFeatureSumMap();
	}

	/**
	 * 根据isTrainFile 更新trainFeature或者testFesture 如果加載過version就得到的是version当标签的
	 * 
	 * @param isTrainFile
	 */
	public void getFile(boolean isTrainFile) {
		List<HashMap<Integer, Integer>> Features;
		int[] Dis;
		ResultSet rs;
		if (isTrainFile) {
			trainFeatures = new ArrayList<HashMap<Integer, Integer>>();
			trainDis = new int[DimensionOfClass];
			Features = trainFeatures;
			Dis = trainDis;
			// rs = getRs(trainTable);
			rs = getRsPartion(trainTable, partitionNum);
		} else {
			testFeatures = new ArrayList<HashMap<Integer, Integer>>();
			Features = testFeatures;
			testDis = new int[DimensionOfClass];
			Dis = testDis;
			// rs = getRs(testTable);
			rs = getRsPartion(testTable, partitionNum);
		}
		// Features = new ArrayList<int[]>();
		String imsiRow = "";
		int i = -1;
		HashMap<Integer, Integer> row = null;
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String behavior = rs.getString("BEHAVIOR");
				// 遇到一个新用户建立一个新的row
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
				}
				if (!FeatureMap2Int.containsKey(behavior))
					continue;
				int iKey = (int) FeatureMap2Int.get(behavior);
				int iValue = 1;
				if (row.containsKey(iKey)) {
					iValue = row.get(iKey).intValue() + 1;
				}
				row.put(iKey, iValue);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultSet getRs(String tableName, String tag) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imsi, behavior, " + tag + " from "
				+ tableName + " order by imsi");
		return rs;
	}

	private ResultSet getRsPartion(String tableName, int partitionNum) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imsi,behavior, " + tag + " from "
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
			fw = new FileWriter(tableName + "_MobileSet_Base_Behavior_" + tag
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
			fw = new FileWriter(tableName + "_MobileSet_Base_Behavoir_TFIDF_"
					+ tag + "_top" + partitionNum + ".libsvm");
			while (it.hasNext()) {
				String rowStr = "";
				HashMap<Integer, Double> row = it.next();
				Integer[] keys = row.keySet().toArray(new Integer[0]);
				Arrays.sort(keys);
				rowStr = row.get(0).intValue() + "";
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

	public void writeTfIdfFeatureToFileForWeka() {
		String tableName = "";
		Iterator<HashMap<Integer, Double>> it;
		tableName = trainTable + "";
		List<HashMap<Integer, Integer>> allFeatures = new ArrayList<>();
		allFeatures.addAll(trainFeatures);
		allFeatures.addAll(testFeatures);
		// 得到所有的allFeature的特征TFIDF的向量空间
		it = getTfIdfFeature(allFeatures).iterator();
		FileWriter fw = null;
		try {
			fw = new FileWriter(tableName
					+ "_MobileSet_Base_Behavoir_TFIDF_WEKA_" + tag + "_top"
					+ partitionNum + ".libsvm");
			while (it.hasNext()) {
				String rowStr = "";
				HashMap<Integer, Double> row = it.next();
				Integer[] keys = row.keySet().toArray(new Integer[0]);
				Arrays.sort(keys);
				// 删除某一列
				// if (row.get(0).intValue() == 1)
				// continue;
				rowStr = row.get(0).intValue() + "";
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
		writeDisFile();
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

	private void writeDisFile() {
		int[] Dis = new int[trainDis.length];
		String tableName = "";

		tableName = trainTable;
		for (int i = 0; i < Dis.length; i++) {
			Dis[i] = trainDis[i] + testDis[i];
		}

		FileWriter fw;
		try {
			fw = new FileWriter("Dis_" + tableName
					+ "_MobileSet_Base_Host_WEKA_" + tag + ".txt");
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

	public void loadBehaviorDimension() {
		String keys[] = { "查地图", "查消息", "查信息", "管理手机", "逛空间", "看视频", "看新闻",
				"聊天", "买东西", "拍照", "上人人", "上网", "上微博", "收发邮件", "听音乐", "通信",
				"玩游戏", "阅读", "照明", "做记录" };

		for (int i = 0; i < keys.length; i++) {
			FeatureMap2Int.put(keys[i], i + 1);
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

	private void setFeatureSumMap() {
		ArrayList<HashMap<Integer, Integer>> feature = new ArrayList<>();
		if (trainFeatures != null)
			feature.addAll(trainFeatures);
		if (testFeatures != null)
			feature.addAll(testFeatures);
		for (int i = 0; i < feature.size(); i++) {// 每一行的feature
			HashMap<Integer, Integer> row = feature.get(i);
			Iterator<Integer> itHost = row.keySet().iterator();
			while (itHost.hasNext()) {// 一行中的每一个
				int hostId = itHost.next();
				if (!FeatureSumMap.containsKey(hostId))
					FeatureSumMap.put(hostId, 0);
				FeatureSumMap.put(hostId, FeatureSumMap.get(hostId) + 1);
			}
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
		lineSumList = new ArrayList<>();
		for (int i = 0; i < feature.size(); i++) {// 每一行的feature
			HashMap<Integer, Integer> row = feature.get(i);
			Iterator<Integer> itHost = row.keySet().iterator();
			int sumLine = 0;
			while (itHost.hasNext()) {// 一行中的每一个
				int hostId = itHost.next();
				sumLine += row.get(hostId);
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
				// System.out.print("tf:" + tf);
				// System.out.print("idf:" + idf + "\n");
				rowNew.put(hostId, tf * idf);
			}
			result.add(rowNew);
		}
		return result;
	}

	public static void main(String arg[]) {
		MultiBehaviorToFeatureExtraTI app = null;

		for (int i = 5; i <= 15; i++) {
			// train和test分开的2k
			// app = new MultiBehaviorToFeatureExtraTI(
			// "X7_TRAIN_ONE_G500_ADDFUNC_nbq",
			// "X71_TEST_BASE_BEHAVIOR_nbq", "C" + i);
			app = new MultiBehaviorToFeatureExtraTI(
					"X8_ONE_G500_ADDFUNC_NBQ_G5_T3K",
					"//X71_TEST_BASE_BEHAVIOR_nbq", "C" + i);
			System.out.println(i);
			app.setPartitionNum(150 - (i - 4) * 10);
			app.loadBehaviorDimension();
			// 每次都得调用，读取训练和测试表中的数据
			// app.getTrainTestFiles();
			app.getSingleFiles(true);
			app.writeTfIdfFeatureToFile(true);
		}

	}
}
