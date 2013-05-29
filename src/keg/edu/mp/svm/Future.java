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
		BufferedOutputStream bos_pos[] = new BufferedOutputStream[7];
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
					// for (int j = 0; j < row.length; j++) {
					// System.out.print(row[j] + " ");
					// }
					// System.out.print("\n");
					futures.add(row);
				}
				line = lnr.readLine();
				System.out.println(lineNum++);

			}
			String[] output = new String[] { "output_future1",
					"output_future2", "output_future3", "output_future4",
					"output_future5", "output_future6", "output_future7" };
			bos_neg = getBOS("output_future0_neg");
			for (int i = 0; i < bos_pos.length; i++)
				bos_pos[i] = getBOS(output[i]);
			int k = 0;
			for (Double[] future : futures) {
				String line_out = "";
				for (int i = 0; i < future.length - 1; i++) {
					if (future[i] > 1000000)
						continue;
					line_out = line_out + i + ":" + future[i] + " ";
				}
				if (future[future.length - 1] == 1) {
					writeString(bos_neg, "-1 " + line_out + "\n");
				} else {
					writeString(bos_pos[(int) (Math.random() * output.length)],
							"+1 " + line_out + "\n");
				}
				System.out.println(k++);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				lnr.close();
				bos_neg.close();
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
		Future f = new Future("F:\\大数据\\移动\\校园用户用户整体清单\\校园用户用户整体清单_no.csv", 1);
	}

}
