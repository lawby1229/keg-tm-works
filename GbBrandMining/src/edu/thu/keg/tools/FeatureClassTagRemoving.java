package edu.thu.keg.tools;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Set;

/**
 * 删除训练文件或者测试文件一些类的行数
 * 
 * @author ybz
 * 
 */
public class FeatureClassTagRemoving {

	public static void fileterLibsvmFeature(String fileName, String tag) {
		LineNumberReader lnr = null;
		FileWriter fw = null;
		try {
			lnr = new LineNumberReader(new FileReader(fileName));
			fw = new FileWriter(fileName.replace(".libsvm", "_CUT_" + tag
					+ ".libsvm"));
			String line = lnr.readLine();
			while (line != null) {
				if (line.split(" ")[0].equals(tag)) {
					line = lnr.readLine();
					continue;
				}
				fw.write(line + "\n");
				fw.flush();
				line = lnr.readLine();
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

	public static void fileterLibsvmFeatures(String fileName, Set<String> tags) {
		LineNumberReader lnr = null;
		FileWriter fw = null;
		try {
			lnr = new LineNumberReader(new FileReader(fileName));
			fw = new FileWriter(fileName.replace(".libsvm",
					"_CUT_Set" + tags.size() + ".libsvm"));
			String line = lnr.readLine();
			while (line != null) {
				if (tags.contains(line.split(" ")[0])) {
					line = lnr.readLine();
					continue;
				}
				fw.write(line + "\n");
				fw.flush();
				line = lnr.readLine();
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

	public static void main(String arg[]) {
		String folder = "140115_X5_X51_C5_C15_用户行为_TFIDF_weka/";
		for (int i = 6; i <= 15; i++) {
			String filename = "X5_TRAIN_ONE_G500_ADDFUNC_MobileSet_Base_Behavoir_TFIDF_WEKA_C"
					+ i + ".libsvm";
			FeatureClassTagRemoving
					.fileterLibsvmFeature(folder + filename, "1");
		}
	}
}
