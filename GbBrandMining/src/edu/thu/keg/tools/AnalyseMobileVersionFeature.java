package edu.thu.keg.tools;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AnalyseMobileVersionFeature {

	HashMap<String, Integer> Dimensions;
	List<HashMap<Integer, String>> FeaturesAllLine;
	List<String> Labels;

	AnalyseMobileVersionFeature() {
		Dimensions = new HashMap<String, Integer>();
		FeaturesAllLine = new ArrayList<>();
		Labels = new ArrayList<>();
	}

	public void loadFile(String fileName) {

		try {
			FileReader fr = new FileReader(fileName);
			LineNumberReader lnr = new LineNumberReader(fr);
			String line = lnr.readLine();
			int lineNum = 0;
			while (line != null) {
				String[] features = line.split(",");
				lineNum++;
				if (features.length > 1) {
					HashMap<Integer, String> lineFeature = new HashMap<>();
					Labels.add(features[0]);
					for (int i = 3; i < features.length; i++) {
						String feature[] = features[i].split("：");
						System.out.println(features.length + " " + feature[0]
								+ ":" + feature[1]);
						if (!Dimensions.containsKey(feature[0].trim())) {
							Dimensions.put(feature[0].trim(),
									Dimensions.size() + 1);
						}
						lineFeature.put(Dimensions.get(feature[0].trim()),
								feature[1].trim());
					}
					FeaturesAllLine.add(lineFeature);
				}
				line = lnr.readLine();
				System.out.println(lineNum);

			}
			lnr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void outputFile(String fileName) {
		try {
			FileWriter fw1 = new FileWriter(fileName + "_Dimensions");
			FileWriter fw = new FileWriter(fileName);
			Iterator itKey = Dimensions.keySet().iterator();
			while (itKey.hasNext()) {
				String key = (String) itKey.next();
				fw1.write(Dimensions.get(key) + ":" + key + "\n");
			}
			fw1.flush();
			for (int i = 0; i < FeaturesAllLine.size(); i++) {
				fw.write(Labels.get(i) + " ");
				HashMap<Integer, String> lineFeature = FeaturesAllLine.get(i);
				itKey = lineFeature.keySet().iterator();
				while (itKey.hasNext()) {
					int key = (int) itKey.next();
					fw.write(key + ":" + lineFeature.get(key) + ",");
				}
				fw.write("\b\n");
				fw.flush();
			}
			fw.close();
			fw1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String arg[]) {
		AnalyseMobileVersionFeature app = new AnalyseMobileVersionFeature();

		app.loadFile("手机型号/型号参数-1对1.csv");
		app.outputFile("手机型号/聚类wc");
	}
}
