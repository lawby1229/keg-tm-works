package edu.thu.keg.parse.html.imei;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.parserapplications.filterbuilder.Filter;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

/**
 * <br>
 * 标题: <br>
 * 功能概要: <br>
 * 版权: cityyouth.cn (c) 2005 <br>
 * 公司:上海城市青年网 <br>
 * 创建时间:2005-12-21 <br>
 * 修改时间: <br>
 * 修改原因：
 * 
 * @author 张伟
 * @version 1.0
 */
public class ImeiParse {
	public static void testHtml() {
		try {
			String sCurrentLine;
			String sTotalString;
			sCurrentLine = "";
			sTotalString = "";
			java.io.InputStream l_urlStream;
			java.net.URL l_url = new java.net.URL(
					"http://www.imeidb.com/?imei=113176002530049");
			java.net.HttpURLConnection l_connection = (java.net.HttpURLConnection) l_url
					.openConnection();
			l_connection.connect();
			l_urlStream = l_connection.getInputStream();
			java.io.BufferedReader l_reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(l_urlStream));
			while ((sCurrentLine = l_reader.readLine()) != null) {
				sTotalString += sCurrentLine;
			}
			System.out.println(sTotalString);

			System.out.println("====================");
			String testText = extractText(sTotalString);
			System.out.println(testText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 抽取纯文本信息
	 * 
	 * @param inputHtml
	 * @return
	 */
	public static String extractText(String inputHtml) throws Exception {
		StringBuffer text = new StringBuffer();
		Parser parser = Parser.createParser(new String(inputHtml.getBytes(),
				"8859_1"), "8859-1");
		// 遍历所有的节点
		NodeList nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
			public boolean accept(Node node) {
				return true;
			}
		});
		Node node = nodes.elementAt(0);
		text.append(new String(node.toPlainTextString().getBytes("8859_1")));
		return text.toString();
	}

	/**
	 * 读取文件的方式来分析内容. filePath也可以是一个Url.
	 * 
	 * @param resource
	 *            文件/Url
	 */
	public static List<String> ImeiPraser(String imei) throws Exception {
		String resource = "http://www.imei.info/?imei=" + imei;
//		System.out.println(resource);
		AgentHttp agent = new AgentHttp();
		resource = agent.getHtml(resource);
		if (resource == null)
			return null;
		Parser myParser = new Parser(resource);

		// 设置编码
		myParser.setEncoding("utf-8");
		NodeFilter nf = getImeiInfoFilter();
		NodeList nodeList = myParser.extractAllNodesThatMatch(nf);
		List<String> result = new ArrayList();
		for (int i = 0; i < 2 && i < nodeList.size(); i++) {

			// TableTag tabletag = (TableTag) nodeList.elementAt(i);
			Node node = nodeList.elementAt(i);
			// System.out.println(tabletag.toHtml());
			// System.out.println(node.getText());
			// System.out.println(node.toHtml());
			// System.out.println(node.toPlainTextString());
			String brand = node.getLastChild().getText();
			if (brand.startsWith("we're sorry"))
				return result;
			result.add(brand);
			// System.out.println(node.getFirstChild().toPlainTextString());
			// System.out.println(node.getLastChild().getText());
			// //
			// System.out.println("==============");

		}
		return result;
	}

	private static NodeFilter getImeiDBFilter() {
		String filterStr = "table";
		String filterStr2 = "td";
		NodeFilter filterWidth = new HasAttributeFilter("width");
		NodeFilter filter = new TagNameFilter(filterStr);
		NodeFilter filter2 = new TagNameFilter(filterStr2);
		// NodeFilter filter3 = new
		AndFilter andfilter = new AndFilter(filter2, new NotFilter(filterWidth));
		return andfilter;
	}

	private static NodeFilter getImeiInfoFilter() {
		String filterStr = "p";
		String filterStr2 = "td";
		NodeFilter filterid = new HasAttributeFilter("id", "dane");
		NodeFilter filter = new TagNameFilter(filterStr);
		// NodeFilter filter2 = new TagNameFilter(filterStr2);
		// NodeFilter filter3 = new
		// AndFilter andfilter = new AndFilter(filter, new NotFilter(filterid));
		return filter;
	}

	/*
	 * public static void main(String[] args) { TestYahoo testYahoo = new
	 * TestYahoo(); testYahoo.testHtml(); }
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(ImeiPraser("869567010923108"));

	}
}
