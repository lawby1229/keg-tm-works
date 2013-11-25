package edu.thu.keg.GB.http0702.brand.features;

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

public class MultiFeatureHostExtraction {
	final static HashMap<String, Integer> FeatureMap = new HashMap<>();
	String trainTable = "";
	String testTable = "";
	List<HashMap<Integer, Integer>> trainFeatures;
	List<HashMap<Integer, Integer>> testFeatures;

	public MultiFeatureHostExtraction(String trainTable, String testTable) {
		this.trainTable = trainTable;
		this.testTable = testTable;
		trainFeatures = new ArrayList<HashMap<Integer, Integer>>();
		testFeatures = new ArrayList<HashMap<Integer, Integer>>();

	}

	public void getFile(boolean isTrainFile) {
		List<HashMap<Integer, Integer>> Features;
		ResultSet rs;
		if (isTrainFile) {
			trainFeatures = new ArrayList<HashMap<Integer, Integer>>();
			Features = trainFeatures;
			rs = getRs(trainTable);
		} else {
			testFeatures = new ArrayList<HashMap<Integer, Integer>>();
			Features = testFeatures;
			rs = getRs(testTable);
		}
		// Features = new ArrayList<int[]>();
		String imsiRow = "";
		int i = -1;
		HashMap<Integer, Integer> row = null;
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String host = rs.getString("HOST");
				int brandtype = rs.getInt("BRANDTYPE");
				if (!imsiRow.equals(imsi)) {
					imsiRow = imsi;
					i++;
					row = new HashMap<Integer, Integer>();
					row.put(0, brandtype);
					Features.add(row);
					System.out.println(i);
				}
				int iKey = (int) FeatureMap.get(host);
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

	private ResultSet getRs(String tableName) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imsi,host,brandtype from "
				+ tableName + " where behavior order by imsi");
		return rs;
	}

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
			fw = new FileWriter(tableName + "_Feature.txt");
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
	}

	public void loadHostDimension(String field, String tableName) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select distinct(" + field + ")" + " from "
				+ tableName);
		try {
			int i = 1;
			while (rs.next()) {
				FeatureMap.put(rs.getString(1), i++);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String arg[]) {
		// 对多分类的7种手机数据进行分类和预测的特征抽取
		MultiFeatureHostExtraction app = new MultiFeatureHostExtraction(
				"Z0_TRAIN_ONE_G500_TOP1000", "Z0_TEST_CHANGED_EACH_BRAND");
		app.loadHostDimension("", "");
		app.getFile(false);
		app.writeFeatureToFile(false);
		app.getFile(true);
		app.writeFeatureToFile(true);

	}
}
