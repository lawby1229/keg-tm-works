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

import edu.thu.keg.parse.html.imei.AgentHttp;

public class ScanNumPlan extends Thread {
	final String UrlNumPlan = "http://www.numberingplans.com/?page=plans&sub=imeinr&alpha_2_input="
			+ "01" + "&current_page=";
	AgentHttp agentHttp = new AgentHttp();
	FileWriter fw = null;
	boolean reDial = true;

	public ScanNumPlan() {
		try {
			fw = new FileWriter("Imei-brand", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		try {

			for (int i = 152; i <= 155; i++) {
				ImeiPraser(String.valueOf(i));
				System.out.println(i);
				fw.flush();
				if (reDial) {
					redial();
					i--;
				} else
					reDial = true;
				sleep(500);

			}
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void ImeiPraser(String page) throws Exception {
		String resource = UrlNumPlan + page;
		System.out.println(resource);
		resource = agentHttp.getHtml(resource);
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
//		System.out.println("过滤出" + nodeList.size());
		int i = 0;
		for (Node node : nodeList.elementAt(0).getChildren().toNodeArray()) {
			// TableTag tabletag = (TableTag) nodeList.elementAt(i);
//			System.out.println("第一个人table&&");
			if (!node.getText().equals("tr"))
				continue;

			if (i >= 2 && i <= 11) {
				reDial = false;
//				System.out.println("tr个数" + i);
				int j = 0;
				String result = "";
				for (Node tdNode : node.getChildren().toNodeArray()) {
					if (!tdNode.getText().startsWith("td"))
						continue;
//					System.out.println(j + " "
//							+ tdNode.toPlainTextString().trim());
					result = result + tdNode.toPlainTextString().trim() + ",";
					j++;

				}
				result = result.substring(0, result.length() - 2) + "\n";
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

	private void write(String s) {

	}

	private void redial() {
		System.out.println("拨号中、、、");
	}

	public static void main(String arg[]) {
		ScanNumPlan snp = new ScanNumPlan();
		snp.start();

	}
}
