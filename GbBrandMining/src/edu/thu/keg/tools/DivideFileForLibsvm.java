package edu.thu.keg.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * 
 * @author ybz 将一个libsvm的文件分成若干个不同比例的文件
 */
public class DivideFileForLibsvm {
	public DivideFileForLibsvm() {

	}

	public static void divideFile(String file, double trainRatio) {
		int divideNum = (int) (1 / trainRatio);
		LineNumberReader lnr = null;
		FileWriter fwsTrains[] = null;
		FileWriter fwsTests[] = null;
		try {
			File sourceFile = new File(file);
			lnr = new LineNumberReader(new FileReader(sourceFile));
			fwsTrains = new FileWriter[divideNum];
			fwsTests = new FileWriter[divideNum];

			for (int i = 0; i < fwsTrains.length; i++) {
				fwsTrains[i] = new FileWriter(sourceFile.getParent() + "/"
						+ "trainFile" + i + ".libsvm");
				fwsTests[i] = new FileWriter(sourceFile.getParent() + "/"
						+ "testFile" + i + ".libsvm");
			}
			String line = lnr.readLine();
			int lineNum = 0;
			while (line != null) {
				for (int i = 0; i < fwsTrains.length; i++) {
					if ((lineNum + i) % divideNum == 0) {
						fwsTrains[i].write(line + "\n");
						fwsTrains[i].flush();
					} else {
						fwsTests[i].write(line + "\n");
						fwsTests[i].flush();
					}
				}
				line = lnr.readLine();
				lineNum++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				lnr.close();
				for (int i = 0; i < fwsTrains.length; i++) {
					fwsTrains[i].close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String arg[]) {
		DivideFileForLibsvm.divideFile("./a.txt", 0.2);
	}
}
