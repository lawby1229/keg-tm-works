package edu.thu.keg.GB.http0702.brand.features.lda;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import weka.core.Version;

import edu.thu.keg.GB.http0702.brand.features.tfidf.IBrandTools;
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
	List<String> VersionList = new ArrayList<String>();
	List<String> PriceList = new ArrayList<String>();
	List<String> EolList = new ArrayList<String>();
	int DimensionOfClass = 16;
	String trainTable = "";
	int[] trainDis;
	// List<List<String>> trainFeatures;
	// List<List<String>> testFeatures;
	boolean isVersionAsTag;
	String folder;

	public HostToMobileBrandLDA(String trainTable) {
		this.trainTable = trainTable;
		this.isVersionAsTag = false;
	}

	@Override
	public ResultSet getRs(String tableName, String tag) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select 统一名称, host, 价位, 是否停产,behavior  from "
				+ tableName + " order by 统一名称, host, behavior");
		return rs;
	}

	@Override
	public void getFile(boolean isTrainFile) {

		List<List<String>> Features;
		int[] Dis;
		ResultSet rs;
		FileWriter fw = null;
		rs = getRs(trainTable, "");

		String versionRow = "";
		ArrayList<String> row = null;
		int i = 0;
		try {
			fw = new FileWriter("temp.dat", false);
			while (rs.next()) {
				String host = rs.getString("host");
				String behavior = rs.getString("behavior");
				String hostBehavior = host.replaceAll(" ", "") + "_" + behavior;
				String samaName = rs.getString("统一名称");
				String price = rs.getString("价位");
				String eol = rs.getString("是否停产");
				if (behavior.equals("上网"))
					continue;
				if (!versionRow.equals(samaName)) {
					if (VersionList.size() == 0)
						fw.write(hostBehavior);
					else
						fw.write("\n" + hostBehavior);
					fw.flush();
					VersionList.add(samaName);
					PriceList.add(price);
					EolList.add(eol);
					versionRow = samaName;
					row = new ArrayList<String>();
				} else
					fw.write(" " + hostBehavior);
				fw.flush();

				if (i++ % 100000 == 0)
					System.out.println(i);
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeFeatureToFileLDA(String fileName) {
		String tableName = trainTable;
		if (fileName.equals(""))
			fileName = tableName + "_LDA" + ".txt";
		List<List<String>> list;
		FileWriter fw = null;
		LineNumberReader lnr = null;
		try {
			lnr = new LineNumberReader(new FileReader("temp.dat"));
			fw = new FileWriter(fileName);
			fw.write(VersionList.size() + "\n");
			String line = lnr.readLine();
			while (line != null) {
				fw.write(line + "\n");
				line = lnr.readLine();
				fw.flush();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				lnr.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void writeVersionToFileLDA(String fileName) {
		FileWriter fw = null;
		String tableName = trainTable;
		if (fileName.equals(""))
			fileName = tableName + "_LDA_Version" + ".txt";
		try {
			fw = new FileWriter(fileName);
			for (int i = 0; i < VersionList.size(); i++) {
				fw.write(VersionList.get(i) + "," + PriceList.get(i) + ","
						+ EolList.get(i) + "\n");

				fw.flush();
				System.out.println(i);
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

		app = new HostToMobileBrandLDA("z152_g500_t2k_z151_pri_up10");
		System.out.println("read oracle...");
		app.getFile(true);
		System.out.println("output lda file...");
		app.writeFeatureToFileLDA("LdaInputFile_noInternet.txt");
		System.out.println("output versiong...");
		app.writeVersionToFileLDA("LdaUsersVersion_noInternet.txt");

	}
}
