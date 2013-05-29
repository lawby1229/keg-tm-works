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

	public Future(String filename, int column) {
		GlobolMap.put("CELL_ID", CELL_ID);
		GlobolMap.put("TOWN_ID", TOWN_ID);
		GlobolMap.put("BRAND", BRAND);
		GlobolMap.put("PRODUCT_ID", PRODUCT_ID);

		LineNumberReader lnr = null;
		BufferedOutputStream bos_neg = null;
		BufferedOutputStream both_test = null;

		BufferedOutputStream bos_pos[] = new BufferedOutputStream[6];

		BufferedOutputStream bos_neg_test = null;
		BufferedOutputStream bos_pos_test = null;
		try {
			lnr = getLNR(filename);
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

			String[] output = new String[] { "train_file1", "train_file2",
					"train_file3", "train_file4", "train_file5", "train_file6" };
			bos_neg = getBOS("only_neg");
			both_test = getBOS("both_test_file");

			for (int i = 0; i < bos_pos.length; i++)
				bos_pos[i] = getBOS(output[i]);
			int test_pos_line = 0, test_neg_line = 0;
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
					writeString(bos_neg, "-1 " + line_out + "\n");
					if (test_neg_line < 79941) {
						for (int j = 0; j < bos_pos.length - 1; j++)
							// 前七万多个负例都会去每个整理的文件，除了最后一个
							writeString(bos_pos[j], "-1 " + line_out + "\n");
						test_neg_line++;
					} else
						// 后1万作为测试用例
						writeString(both_test, "-1 " + line_out + "\n");
				} else {
					int j = (int) (Math.random() * (output.length + 1));

					if (j == output.length) {
						if (test_pos_line > 10000) {
							System.out.println("没写入" + k);
							continue;
						}
						writeString(both_test, "+1 " + line_out + "\n");
						test_pos_line++;
					} else
						writeString(bos_pos[j], "+1 " + line_out + "\n");
				}
				System.out.println("写入" + k);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				lnr.close();
				both_test.close();
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

	/**
	 * 帮助方法,通过f_b写入Content内容
	 * 
	 * @param f_b
	 * @param Content
	 */
	public void writeString(BufferedOutputStream f_b, String Content) {
		try {

			byte[] b;
			b = Content.getBytes();
			f_b.write(b);
			f_b.flush();
		} catch (IOException ex) {
			System.out.println(ex);
		}// TODO add your handling code here:
	}

	/**
	 * 帮助方法,读取文件流的封装
	 * 
	 * @param Filename
	 * @return
	 */
	public LineNumberReader getLNR(String Filename) {
		File infile = new File(Filename);
		FileInputStream f;
		LineNumberReader f_b = null;
		try {
			f = new FileInputStream(infile);
			f_b = new LineNumberReader(new InputStreamReader(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return f_b;
	}

	/**
	 * 帮助方法,输出数据流的封装
	 * 
	 * @param Filename
	 * @return
	 */
	public BufferedOutputStream getBOS(String Filename) {
		File outfile = new File(Filename);
		FileOutputStream f;
		BufferedOutputStream f_b = null;
		try {
			f = new FileOutputStream(outfile, false);
			f_b = new BufferedOutputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return f_b;
	}

	public void closeBOS(BufferedOutputStream f_b) {

		try {
			f_b.flush();
			f_b.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String law[]) {
		Future f = new Future("D:\\移动原始数据\\移动\\校园用户用户整体清单_no.csv", 1);
	}

}
