package edu.thu.keg.parse.html.imei;

import java.io.BufferedInputStream;

import java.net.URL;

import java.net.URLConnection;

public class AgentHttp {

	public static void main(String args[]) {

		// System.getProperties().setProperty("proxySet", "true");
		// //如果不设置，只要代理IP和代理端口正确,此项不设置也可以

		System.out.println(getHtml("http://www.imeidb.com/?imei=013176002530049")); // 判断代理是否设置成功

	}

	public static String getHtml(String address) {
		 System.getProperties().setProperty("http.proxyHost", "127.0.0.1");
		
		 System.getProperties().setProperty("http.proxyPort", "8087");

		StringBuffer html = new StringBuffer();

		String result = null;

		try {

			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");

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
