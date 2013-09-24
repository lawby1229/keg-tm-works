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

import ls.*;
import ls.libsvm.*;

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
								row[i - 1] = new Double(0);
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
				bos_test[i] = FileStaticFunction.getBOS("train_file_csv" + i);

			}
			int test_pos_line = 0, test_pos_line_in = 0, test_neg_line = 0;

			for (int k = 0; k < futures.size(); k++) {
				Double[] future = futures.get(k);

				String line_out = "";
				for (int i = 0; i < future.length - 1; i++) {
//					if (future[i] == 0)
//						continue;
//					line_out = line_out + (int) (i + 1) + ":" + future[i] + " ";
					line_out = line_out +","+future[i];
				}
				
				// 到此为止保存了所有这一行的特征到line_out中
				if (future[future.length - 1] == 1) {// 负例前7w行进入每个的训练数据集

					int filenum = test_neg_line % testFileNum;
					FileStaticFunction.writeString(bos_test[filenum], "-1"
							+ line_out + "\n");
					test_neg_line++;
				} else {
					if (test_pos_line % 6 == 0) {
						int filenum = test_pos_line_in % testFileNum;
						FileStaticFunction.writeString(bos_test[filenum], "+1"
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
//		 Future f = new Future("D:\\移动原始数据\\移动\\校园用户用户整体清单_no.csv", 3);
		 String[] arg = { ".\\heart_scale", // 存放SVM训练模型用的数据的路径
		 ".\\mode" ,"-t","0"}; // 存放SVM通过训练数据训练出来的模型的路径 -t是核函数
//		String[] arg0 = { ".\\train_file0", ".\\mode0_L", "-t", "0" };
		// String[] arg1 = { ".\\train_file1", // 存放SVM训练模型用的数据的路径
		// ".\\mode1" }; // 存放SVM通过训练数据训练出来的模型的路径
		// String[] arg2 = { ".\\train_file2", // 存放SVM训练模型用的数据的路径
		// ".\\mode2" }; // 存放SVM通过训练数据训练出来的模型的路径

		// String[] arg01 = { ".\\train_file0", ".\\mode0_l" };
		// String[] arg12 = { ".\\train_file1", ".\\mode1_l" };
		// String[] arg02 = { ".\\train_file2", ".\\mode2_l" };
		// ---------------------------------------------------------------------------------------------------
		 String[] parg = { ".\\heart_scale", // 这个是存放测试数据
		 ".\\mode", // 调用的是训练以后的模型
		 ".\\predict" }; // 生成的结果的文件的路径

//		String[] parg0_1 = { ".\\train_file0", // 这个是存放测试数据
//				".\\mode0_L", // 调用的是训练以后的模型
//				".\\predict0_0_L" }; // 生成的结果的文件的路径
		// String[] parg0_2 = { ".\\train_file2", // 这个是存放测试数据
		// ".\\mode0", // 调用的是训练以后的模型
		// ".\\predict0_2" }; // 生成的结果的文件的路径
		//
		// String[] parg1_0 = { ".\\train_file0", // 这个是存放测试数据
		// ".\\mode1", // 调用的是训练以后的模型
		// ".\\predict1_0" }; // 生成的结果的文件的路径
		// String[] parg1_2 = { ".\\train_file2", // 这个是存放测试数据
		// ".\\mode1", // 调用的是训练以后的模型
		// ".\\predict1_2" }; // 生成的结果的文件的路径
		//
		// String[] parg2_0 = { ".\\train_file0", // 这个是存放测试数据
		// ".\\mode2", // 调用的是训练以后的模型
		// ".\\predict2_0" }; // 生成的结果的文件的路径
		// String[] parg2_1 = { ".\\train_file1", // 这个是存放测试数据
		// ".\\mode2", // 调用的是训练以后的模型
		// ".\\predict2_1" }; // 生成的结果的文件的路径

		// String[] parg01_2 = { ".\\train_file2", ".\\mode01", ".\\predict01_2"
		// };
		// String[] parg12_0 = { ".\\train_file0", ".\\mode12", ".\\predict12_0"
		// };
		// String[] parg02_1 = { ".\\train_file1", ".\\mode02", ".\\predict02_1"
		// };
		System.out.println("........SVM运行开始..........");

		try {
		svm_train.main(arg);
//			svm_train.main(arg0);
			// svm_train.main(arg1);
			// svm_train.main(arg2);
		svm_predict.main(parg);
//			svm_predict.main(parg0_1);
			// svm_predict.main(parg0_2);
			// svm_predict.main(parg1_0);
			// svm_predict.main(parg1_2);
			// svm_predict.main(parg2_0);
			// svm_predict.main(parg2_1);
			// svm_train.main(arg01);
			// svm_train.main(arg12);
			// svm_train.main(arg02);
			// svm_predict.main(parg01_2);
			// svm_predict.main(parg12_0);
			// svm_predict.main(parg02_1);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
