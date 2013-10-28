package edu.thu.keg.numplans;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.SliderUI;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import edu.thu.keg.adsl.ConnectNetwork;
import edu.thu.keg.parse.html.imei.AgentHttp;
/**
 * 
 * @author ybz
 *
 */
public class ScanNumPlan extends Thread {
	final String UrlNumPlanPart1 = "http://www.numberingplans.com/?page=plans&sub=imeinr&alpha_2_input=";
	final String UrlNumPlanPart2 = "&current_page=";

	AgentHttp agentHttp = new AgentHttp();
	FileWriter fw = null;
	boolean reDial = true;
	int[][] range = new int[19][2];
	String[] type = new String[19];

	public ScanNumPlan() {
		type[0] = "35";
		range[0][0] = 35;
		range[0][1] = 1401;

		type[1] = "44";
		range[1][0] = 44;
		range[1][1] = 58;
		
		type[2] = "97";
		range[2][0] = 97;
		range[2][1] = 0;
		
		type[3] = "98";
		range[3][0] = 98;
		range[3][1] = 3;
		
		type[4] = "99";
		range[4][0] = 99;
		range[4][1] = 4;
		
		type[5] = "49";
		range[5][0] = 49;
		range[5][1] = 25;
		
		type[6] = "50";
		range[6][0] = 50;
		range[6][1] = 9;
		
		type[7] = "51";
		range[7][0] = 51;
		range[7][1] = 1;
		
		type[8] = "52";
		range[8][0] = 52;
		range[8][1] = 45;
		
		type[9] = "10";
		range[9][0] = 10;
		range[9][1] = 1;
		
		type[10] = "33";
		range[10][0] = 33;
		range[10][1] = 61;
		
		type[11] = "31";
		range[11][0] = 31;
		range[11][1] = 0;
		
		type[12] = "30";
		range[12][0] = 30;
		range[12][1] = 1;
		
		type[13] = "91";
		range[13][0] = 91;
		range[13][1] = 12;
		
		type[14] = "45";
		range[14][0] = 45;
		range[14][1] = 14;
		
		type[15] = "01";
		range[15][0] = 01;
		range[15][1] = 155;
		
		type[16] = "54";
		range[16][0] = 54;
		range[16][1] = 1;
		
		type[17] = "86";
		range[17][0] = 86;
		range[17][1] = 21;
		
		type[18] = "53";
		range[18][0] = 53;
		range[18][1] = 1;
	}

	@Override
	public void run() {

		try {
			for (int i = 0; i < range.length &&i<1; i++) {
				System.out.print(i);
				fw = new FileWriter(i + "_Imei-brand_" + range[i][0] + "-"
						+ range[i][1], true);
				for (int j = 1; j <= range[i][1]; j++) {

					ImeiPraser(type[i], String.valueOf(j));
					System.out.print(j + ",");
					fw.flush();
					if (reDial) {
						redial();
						j--;
					} else
						reDial = true;
					sleep(200);
				}
				System.out.println();
				fw.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void ImeiPraser(String searl, String page) throws Exception {
		String resource = UrlNumPlanPart1 + searl + UrlNumPlanPart2 + page;
		System.out.println(resource);
		resource = agentHttp.getHtml(resource);
		System.out.print(" 得到返回资源");
		if (resource == null)
			return;
		Parser myParser = new Parser(resource);

		// 设置编码
		myParser.setEncoding("utf-8");

		// NodeList nodeList = myParser.extractAllNodesThatMatch(new
		// NodeFilter() {
		// public boolean accept(Node node) {
		// return true;
		// }
		// });
		NodeFilter nf = getNumPlanFilter();
		NodeList nodeList = myParser.extractAllNodesThatMatch(nf);
		// System.out.println("过滤出" + nodeList.size());
		int i = 0;
		for (Node node : nodeList.elementAt(0).getChildren().toNodeArray()) {
			// TableTag tabletag = (TableTag) nodeList.elementAt(i);
			// System.out.println("第一个人table&&");
			if (!node.getText().equals("tr"))
				continue;

			if (i >= 2 && i <= 11) {
				reDial = false;
				// System.out.println("tr个数" + i);
				int j = 0;
				String result = "";
				for (Node tdNode : node.getChildren().toNodeArray()) {
					if (!tdNode.getText().startsWith("td"))
						continue;
					// System.out.println(j + " "
					// + tdNode.toPlainTextString().trim());
					result = result + tdNode.toPlainTextString().trim() + ",";
					j++;

				}
				result = result.substring(0, result.length() - 1) + "\n";
				System.out.println(result);
				fw.write(result);
			}
			i++;
		}
		// return result;
	}

	private NodeFilter getNumPlanFilter() {
		String filterStr = "table";

		NodeFilter filterid = new HasAttributeFilter("id", "AutoNumber1");
		NodeFilter filterbordercolor = new HasAttributeFilter("bordercolor",
				"#111111");
		NodeFilter filter = new TagNameFilter(filterStr);
		AndFilter andfilter = new AndFilter(filter, new NotFilter(filterid));
		AndFilter andfilter2 = new AndFilter(andfilter, filterbordercolor);
		return andfilter2;
	}

	public void redial() {
		try {
			// 断开连接
			ConnectNetwork.cutAdsl("宽带连接");
			Thread.sleep(5000);
			// 再连，分配一个新的IP
			while (!ConnectNetwork.connAdsl("宽带连接", "010010217422", "21423244")) {
				System.out.println("拨号失败，重播中、、、");
				Thread.sleep(1000);
			}Thread.sleep(2000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String arg[]) {

		ScanNumPlan snp = new ScanNumPlan();
		// snp.redial();
		snp.start();

	}
}
