package edu.thu.keg.GB.http0702.brand.features.tfidf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.thu.keg.GB.http0702.brand.iimmfilter.BrandChangeFilter;

/**
 * LDA的手机型号和手机上网host的记录
 * 
 * @author ybz
 * 
 */
public class HostToMobileBrandLDA implements IBrandTools {
	HashMap<String, Integer> FeatureMap2Int = new HashMap<>();// 其中值是从1开始的，其中值是从1开始的,记录每个标签对应的唯一编号
	HashMap<String, Integer> VersionMap = new HashMap<>();// 其中值是从1开始的

	// HashMap<Integer, Integer> FeatureSumMap = new HashMap<>();//
	// 键值代表维度和全文出现总数总数
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

	public HostToMobileBrandLDA(String trainTable, String testTable, String tag) {
		this.trainTable = trainTable;
		this.testTable = testTable;
		this.tag = tag;
		this.isVersionAsTag = false;
	}

	@Override
	public ResultSet getRs(String tableName, String tag) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select version, host, behavior  from "
				+ tableName + " order by version");
		return rs;
	}

	@Override
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
			rs = getRs(trainTable, tag);
		} else {
			testFeatures = new ArrayList<HashMap<Integer, Integer>>();
			Features = testFeatures;
			testDis = new int[DimensionOfClass];
			Dis = testDis;
			// rs = getRs(testTable);
			rs = getRs(testTable, tag);
		}
		// Features = new ArrayList<int[]>();
		String versionRow = "";
		int i = -1;
		HashMap<Integer, Integer> row = null;
		try {
			while (rs.next()) {
				String host = rs.getString("host");
				String version = rs.getString("version");
				String behavior = rs.getString("behavior");
				// 遇到一个新用户建立一个新的row
				if (!versionRow.equals(version)) {
					int hostKey;
					hostKey = FeatureMap2Int.get(host);
					versionRow = version;
					i++;
					row = new HashMap<Integer, Integer>();
					row.put(hostKey, 1);
					Features.add(row);
				}
				if (!FeatureMap2Int.containsKey(behavior))
					continue;
				int iKey = (int) FeatureMap2Int.get(host);
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

	public static void main(String arg[]) {
		HostToMobileBrandLDA app = null;
		app = new HostToMobileBrandLDA("X8_ONE_G500_ADDFUNC_NBQ_G5_T3K",
				"//X71_TEST_BASE_BEHAVIOR_nbq", "");
		app.loadHostDimension("host", "X8_ONE_G500_ADDFUNC_NBQ_G5_T3K");
	}
}
