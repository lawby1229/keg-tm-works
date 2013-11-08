package edu.thu.keg.GB.http0702.brand.features;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import edu.thu.keg.GB.http0702.brand.iimmfilter.BrandChangeFilter;

public class FeatureExtract {

	HashMap<String, Integer> FeatureMap;
	List<Lis> Features;

	public FeatureExtract() {
		FeatureMap = new HashMap<>();
		String keys[] = { "查地图", "查消息", "查信息", "管理手机", "逛空间", "看视频", "看新闻",
				"聊天", "买东西", "拍照", "上人人", "上网", "上微博", "收发邮件", "听音乐", "通信",
				"玩游戏", "阅读", "照明", "做记录" };
		for (int i = 0; i < keys.length; i++) {
			FeatureMap.put(keys[i], i + 1);
		}

	}

	public static void main(String arg[]) {
		FeatureExtract fe = new FeatureExtract();
		BrandChangeFilter bcf = new BrandChangeFilter();
		ResultSet rs = bcf
				.runsql("select imsi,behavior from B161_IIMM_BEHAVIOR_TAG_G50 order by imsi");
		try {
			while (rs.next()) {
				String imsi = rs.getString("IMSI");
				String behavior = rs.getString("BEHAVIOR");

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
