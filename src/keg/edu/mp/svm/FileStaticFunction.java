package keg.edu.mp.svm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class FileStaticFunction {
	/**
	 * 帮助方法,通过f_b写入Content内容
	 * 
	 * @param f_b
	 * @param Content
	 */
	public static void writeString(BufferedOutputStream f_b, String Content) {
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
	public static LineNumberReader getLNR(String Filename) {
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
	public static BufferedOutputStream getBOS(String Filename) {
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

	public static void closeBOS(BufferedOutputStream f_b) {

		try {
			f_b.flush();
			f_b.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
