package edu.thu.keg.fetch.internet;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import edu.thu.keg.adsl.ConnectNetwork;
import edu.thu.keg.parse.html.imei.AgentHttp;

public class FetchMobileFeatureZol extends Thread {
	final String UrlZolHost = "http://detail.zol.com.cn/index.php";
	final String UrlZolParams = "?c=SearchList&subcateId=57&keyword=";
	List<String> Version = null;
	AgentHttp agentHttp = new AgentHttp();
	FileWriter fw = null;
	boolean reDial = true;

	public FetchMobileFeatureZol() {
		Version = new ArrayList<String>();
		try {
			fw = new FileWriter("型号参数.csv", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		try {

			for (int i = 1; i <= Version.size(); i++) {

				MobileVersionPraser(Version.get(i).replaceAll(" ", "+"), "");
				fw.flush();
				// if (reDial) {
				// redial();
				// i--;
				// } else
				// reDial = true;
				// sleep(200);
				System.out.print(i + " ");
			}
			System.out.println();
			fw.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void MobileVersionPraser(String searl, String page) throws Exception {
		String resource = UrlZolHost + UrlZolParams + searl;
		System.out.println(resource);
		resource = agentHttp.getHtml(resource);
		System.out.println(" 得到返回资源");
		String result = searl;
		if (resource == null)
			return;
		Parser myParser = new Parser(resource);

		// 设置编码
		// myParser.setEncoding("gb2312");
		myParser.setEncoding("UTF-8");
		NodeFilter nf = getZolFilter();
		NodeList nodeList = myParser.extractAllNodesThatMatch(nf);
		if (nodeList == null || nodeList.size() == 0) {
			System.out.println("没有list");
			fw.write(result + "\n");
			return;

		}
		nodeList = nodeList.extractAllNodesThatMatch(new HasAttributeFilter(
				"class", "intro"), true);
		String title = "", param = "";

		for (int i = 0; i < nodeList.size(); i++) {
			result = searl;
			Node node = nodeList.elementAt(i);
			NodeList nodeListSub = node.getChildren();
			NodeList nodeList_Class_Title = nodeListSub
					.extractAllNodesThatMatch(new HasAttributeFilter("class",
							"title"), true);
			Node nodeSubTitle = nodeList_Class_Title.elementAt(0);
			System.out.println(nodeSubTitle.toPlainTextString());
			result = result + "," + nodeSubTitle.toPlainTextString();
			NodeList nodeList_Tag_li = nodeListSub.extractAllNodesThatMatch(
					new TagNameFilter("li"), true);
			for (int j = 0; j < nodeList_Tag_li.size(); j++) {
				Node nodeSubLi = nodeList_Tag_li.elementAt(j);
				// System.out.println(nodeSubLi.toPlainTextString());
				result = result + "," + nodeSubLi.toPlainTextString();
			}
			// System.out.println(nodeListSub.elementAt(1).toPlainTextString());
			// System.out.println(nodeListSub.elementAt(2).toPlainTextString());
			// System.out.println(nodeListSub.elementAt(3).toPlainTextString());
			fw.write(result + "\n");
			System.out.println(result);
			System.out.println("============================================");
		}
	}

	private NodeFilter getZolFilter() {
		String filterStr = "table";

		NodeFilter filterid = new HasAttributeFilter("id", "result_list");
		// NodeFilter filterbordercolor = new HasAttributeFilter("bordercolor",
		// "#111111");
		// NodeFilter filter = new TagNameFilter(filterStr);
		// AndFilter andfilter = new AndFilter(filter, new NotFilter(filterid));
		// AndFilter andfilter2 = new AndFilter(andfilter, filterbordercolor);

		return filterid;

	}

	public String[] loadMobileVersion() {
		try {
			FileReader fr = new FileReader("手机型号/手机型号.csv");
			LineNumberReader lnr = new LineNumberReader(fr);
			String line = lnr.readLine();
			line = lnr.readLine();
			int i = 0;
			while (line != null) {
				Version.add(line.split(",")[0]);
				i++;
				line = lnr.readLine();
			}
			System.out.println("一共有" + i + "行记录");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
			}
			Thread.sleep(2000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String arg[]) {
		FetchMobileFeatureZol fmfz = new FetchMobileFeatureZol();
		fmfz.loadMobileVersion();
		fmfz.start();
		// try {
		// fmfz.MobileVersionPraser(
		// "philips xenium 9@9++".replaceAll(" ", "+"), "");
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}
}
