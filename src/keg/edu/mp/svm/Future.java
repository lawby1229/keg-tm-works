package keg.edu.mp.svm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Future {
	HashMap<String, HashMap<String, Double>> GlobolMap = new HashMap<String, HashMap<String, Double>>();
	HashMap<String, Double> CELL_ID = new HashMap<>();
	HashMap<String, Double> TOWN_ID = new HashMap<>();
	HashMap<String, Double> BRAND = new HashMap<>();
	HashMap<String, Double> PRODUCT_ID = new HashMap<>();
	List<String> Labels = new ArrayList<>();
	List<Double[]> futures = new ArrayList<Double[]>();

	public Future(String filename, int testFileNum) {
		GlobolMap.put("CELL_ID", CELL_ID);
		GlobolMap.put("TOWN_ID", TOWN_ID);
		GlobolMap.put("BRAND", BRAND);
		GlobolMap.put("PRODUCT_ID", PRODUCT_ID);

		LineNumberReader lnr = null;
		// BufferedOutputStream bos_neg = null;
		// BufferedOutputStream both_test = null;

		BufferedOutputStream bos_test[] = new BufferedOutputStream[testFileNum];

		// BufferedOutputStream bos_neg_test = null;
		// BufferedOutputStream bos_pos_test = null;
		try {
			lnr = FileStaticFunction.getLNR(filename);
			String line;
			line = lnr.readLine();
			int lineNum = 0;
			while (line != null) {
				Double[] row = null;
				if (lineNum == 0) {
					String[] elems = line.split(",");
					for (String elem : elems) {
						Labels.add(elem);
					}
				} else {
					String[] elems = line.split(",");
					row = new Double[elems.length - 1];
					for (int i = 1; i < elems.length; i++) {
						if (i != 1 && i != 2 && i != 4 && i != 5) {
							if (elems[i].equals(""))
								row[i - 1] = Double.MAX_VALUE;
							else
								row[i - 1] = Double.parseDouble(elems[i]);
						} else {
							HashMap<String, Double> map = GlobolMap.get(Labels
									.get(i));
							if (map.containsKey(elems[i]))
								row[i - 1] = map.get(elems[i]);
							else {
								row[i - 1] = new Double(map.size());
								map.put(elems[i], row[i - 1]);
							}
						}
					}
					futures.add(row);
				}
				line = lnr.readLine();
				System.out.println("读取" + lineNum++);

			}
			System.out.println("总数据量:" + futures.size());

			for (int i = 0; i < bos_test.length; i++) {
				bos_test[i] = FileStaticFunction.getBOS("train_file" + i);

			}
			int test_pos_line = 0, test_pos_line_in = 0, test_neg_line = 0;

			for (int k = 0; k < futures.size(); k++) {
				Double[] future = futures.get(k);

				String line_out = "";
				for (int i = 0; i < future.length - 1; i++) {
					if (future[i] > 1000000 || future[i] == 0)
						continue;
					line_out = line_out + (int) (i + 1) + ":" + future[i] + " ";
				}

				// 到此为止保存了所有这一行的特征到line_out中
				if (future[future.length - 1] == 1) {// 负例前7w行进入每个的训练数据集

					int filenum = test_neg_line % testFileNum;
					FileStaticFunction.writeString(bos_test[filenum], "-1 "
							+ line_out + "\n");
					test_neg_line++;
				} else {
					if (test_pos_line % 6 == 0) {
						int filenum = test_pos_line_in % testFileNum;
						FileStaticFunction.writeString(bos_test[filenum], "+1 "
								+ line_out + "\n");
						test_pos_line_in++;
					}
					test_pos_line++;
				}
				System.out.println("写入" + k);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				lnr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static boolean isNumberic(String num) {
		try {
			Double.parseDouble(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void main(String law[]) {
		Future f = new Future("D:\\移动原始数据\\移动\\校园用户用户整体清单_no.csv", 3);
	}

}
