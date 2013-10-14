package edu.thu.keg.parse.html.imei;

import java.io.BufferedInputStream;

import java.net.URL;

import java.net.URLConnection;

public class AgentHttp {
	int useragentNum = 0;

	/**
	 * 获取address网址的内容字符串
	 * 
	 * @param address
	 * @return
	 */
	public String getHtml(String address) {
		// System.getProperties().setProperty("proxySet", "true");
		// System.getProperties().setProperty("http.proxyHost", "127.0.0.1");
		// System.getProperties().setProperty("http.proxyPort", "8087");
//		System.getProperties()
//				.setProperty(
//						"Cookie",
//						"__qca=P0-45434415-1381594809918; PHPSESSID=4ade2046ec0c40b799653a84f0bd0257; __utma=94995971.1891526951.1381594810.1381681198.1381684353.3; __utmb=94995971.3.10.1381684353; __utmc=94995971; __utmz=94995971.1381594810.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		StringBuffer html = new StringBuffer();

		String result = null;

		try {

			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			switch (useragentNum++ % 4) {
			case 0:
				conn.setRequestProperty(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; GTB5; .NET CLR 2.0.50727; CIBA)");
				break;
			case 1:
				conn.setRequestProperty(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; GTB5; .NET CLR 2.0.50727; CIBA)");
				break;
			case 2:
				conn.setRequestProperty(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");

			case 3:
				conn.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.69 Safari/537.36");
			}

			BufferedInputStream in = new BufferedInputStream(
					conn.getInputStream());

			try {

				String inputLine;

				byte[] buf = new byte[4096];

				int bytesRead = 0;

				while (bytesRead >= 0) {

					inputLine = new String(buf, 0, bytesRead, "utf-8");

					html.append(inputLine);

					bytesRead = in.read(buf);

					inputLine = null;

				}

				buf = null;

			} finally {

				in.close();

				conn = null;

				url = null;

			}

			result = new String(html.toString().trim().getBytes("utf-8"))
					.toLowerCase();

		} catch (Exception e) {

			e.printStackTrace();

			return null;

		}

		html = null;

		return result;

	}

}
