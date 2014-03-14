package edu.thu.keg.GB.http0702.brand.features.lda;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.thu.keg.GB.http0702.brand.features.tfidf.IBrandTools;
import edu.thu.keg.GB.http0702.brand.iimmfilter.BrandChangeFilter;

/**
 * 获取从数据库中获取数据，让每个人作为一行，上网记录为属性
 * 
 * @author ybz
 * 
 */
public class ReadUserHostBehaviorLDA implements IBrandTools {
	String trainTable = "";
	HashMap<String, Integer> Host2Id = null;// 存放lda中的host_behavior到维度数的哈希

	public ReadUserHostBehaviorLDA(String trainTable) {
		this.trainTable = trainTable;
	}

	public void readHostbehavior2Id(String fileName) {
		try {
			Host2Id = new HashMap<String, Integer>();
			LineNumberReader lnr = new LineNumberReader(
					new FileReader(fileName));
			String line = lnr.readLine();
			line = lnr.readLine();
			while (line != null) {
				// System.out.println(line);
				String[] dimen = line.split(" ");
				Host2Id.put(dimen[0], Integer.valueOf(dimen[1]));
				line = lnr.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ResultSet getRs(String tableName, String tag) {
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf.runsql("select imei,统一名称,host,behavior from "
				+ tableName + " order by imei, host, behavior");
		return rs;
	}

	@Override
	public void getFile(boolean isTrain) {
		FileWriter fw = null;
		ResultSet rs;
		rs = getRs(trainTable, "");
		String imeiRow = "";
		String hostBehavior = "";
		try {
			int i = 0;
			fw = new FileWriter("SameName_HostBehavior.txt", false);
			HashMap<Integer, Integer> row = new HashMap<>();
			while (rs.next()) {
				String imei = rs.getString("imei");
				String samaName = rs.getString("统一名称");
				String host = rs.getString("host").replaceAll(" ", "");
				String behavior = rs.getString("behavior");
				hostBehavior = host + "_" + behavior;
				if (behavior.equals("上网"))
					continue;
				if (!imei.equals(imeiRow)) {
					System.out.println(++i);
					if (row.size() > 0) {
						fw.write(samaName);
						// 所有维度的值
						Iterator<Integer> it_key = row.keySet().iterator();
						while (it_key.hasNext()) {
							int key = it_key.next();
							fw.write("," + key + ":" + row.get(key));
						}
						fw.write("\n");
					}
					fw.flush();
					row = new HashMap<>();
					imeiRow = imei;
				}
				// System.out.println(hostBehavior);
				if (!Host2Id.containsKey(hostBehavior))
					continue;
				int key = Host2Id.get(hostBehavior);// 得到host对应的维度值
				if (!row.containsKey(key)) {
					row.put(key, 0);
				}
				row.put(key, row.get(key) + 1);
				// if (i++ % 1000 == 0)
				// System.out.println(i);
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

	public static void main(String arg[]) {
		ReadUserHostBehaviorLDA rubl = new ReadUserHostBehaviorLDA(
				"z152_g500_t2k_z151_pri_up10");
		rubl.readHostbehavior2Id("C:\\Users\\ybz\\GitHub\\Law-LDA-jGibbLDA\\JGibbLDA-v.1.0\\wordmap.txt");
		rubl.getFile(true);
	}
}
