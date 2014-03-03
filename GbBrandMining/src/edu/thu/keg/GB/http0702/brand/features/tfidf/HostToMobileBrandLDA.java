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
 * LDA的手机型号和手机上网host的记录
 * 
 * @author ybz
 * 
 */
public class HostToMobileBrandLDA implements IBrandTools {
	// HashMap<String, Integer> FeatureMap2Int = new HashMap<>();//
	// 其中值是从1开始的，其中值是从1开始的,记录每个标签对应的唯一编号
	// HashMap<String, Integer> VersionMap = new HashMap<>();// 其中值是从1开始的

	// HashMap<Integer, Integer> FeatureSumMap = new HashMap<>();//
	// 键值代表维度和全文出现总数总数
	List<Integer> lineSumList = new ArrayList<Integer>();// 每行是单词总数
	int DimensionOfClass = 16;
	String trainTable = "";
	String testTable = "";
	int[] trainDis;
	int[] testDis;
	String tag;
	List<List<String>> trainFeatures;
	List<List<String>> testFeatures;
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

		List<List<String>> Features;
		int[] Dis;
		ResultSet rs;
		if (isTrainFile) {
			trainFeatures = new ArrayList<List<String>>();
			trainDis = new int[DimensionOfClass];
			Features = trainFeatures;
			Dis = trainDis;
			// rs = getRs(trainTable);
			rs = getRs(trainTable, "");
		} else {
			testFeatures = new ArrayList<List<String>>();
			Features = testFeatures;
			testDis = new int[DimensionOfClass];
			Dis = testDis;
			// rs = getRs(testTable);
			rs = getRs(testTable, "");
		}
		// Features = new ArrayList<int[]>();
		String versionRow = "";
		ArrayList<String> row = null;
		try {
			while (rs.next()) {
				String host = rs.getString("host");
				String version = rs.getString("version");
				String behavior = rs.getString("behavior");
				// 遇到一个新用户建立一个新的row
				if (!versionRow.equals(version)) {
					versionRow = version;
					row = new ArrayList<String>();
					row.add(host);
					Features.add(row);
				}
				row.add(host);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeFeatureToFileLDA(boolean isTrainFile) {
		String tableName = "";
		List<List<String>> list;
		if (isTrainFile) {
			tableName = trainTable;
			list = trainFeatures;
		} else {
			tableName = testTable;
			list = testFeatures;
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(tableName + "_MobileSet_Base_Host_LDA" + ".txt");
			fw.write(list.size() + "\n");
			for (int i = 0; i < list.size(); i++) {
				String rowStr = "";
				List<String> row = list.get(i);
				rowStr = row.get(0) + "";
				for (int j = 1; j < row.size(); j++) {
					rowStr = rowStr + " " + row.get(j);
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

	}

	public static void main(String arg[]) {
		HostToMobileBrandLDA app = null;
		app = new HostToMobileBrandLDA("X8_ONE_G500_ADDFUNC_NBQ_G5_T3K",
				"//X71_TEST_BASE_BEHAVIOR_nbq", "");
		app.getFile(true);
		app.writeFeatureToFileLDA(true);
	}
}
