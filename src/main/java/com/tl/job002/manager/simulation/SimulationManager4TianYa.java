package com.tl.job002.manager.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.tl.job002.utils.IOUtil;
import com.tl.job002.utils.RegexUtil;

public class SimulationManager4TianYa {
	public static CloseableHttpClient httpClient = HttpClients.createDefault();

	// 1.loginurl获取
	public static String getLoginSuccessUrl(String loginUrl, String userName,
			String password) throws IOException {
		HttpPost loginPost = new HttpPost(loginUrl);
		loginPost
				.addHeader(
						"Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		loginPost.addHeader("Connection", "keep-alive");
		loginPost
				.addHeader("Content-Type", "application/x-www-form-urlencoded");
		loginPost.addHeader("Host", "passport.tianya.cn");
		loginPost.addHeader("Origin", "http://www.tianya.cn");
		loginPost.addHeader("Pragma", "no-cache");
		loginPost.addHeader("Referer", "http://www.tianya.cn/");
		loginPost.addHeader("Upgrade-Insecure-Requests", "1");
		loginPost
				.addHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3664.3 Safari/537.36");
		// 添加form表单
		List<NameValuePair> formFieldList = new ArrayList<NameValuePair>();
		// 构造用户名字段
		formFieldList.add(new BasicNameValuePair("vwriter", userName));
		formFieldList.add(new BasicNameValuePair("vpassword", password));
		formFieldList.add(new BasicNameValuePair("rmflag", "1"));
		formFieldList.add(new BasicNameValuePair("fowardURL", ""));
		formFieldList.add(new BasicNameValuePair("returnURL", ""));
		// 将表单数据设置到请求对象中
		loginPost.setEntity(new UrlEncodedFormEntity(formFieldList, "utf-8"));
		CloseableHttpResponse httpResponse = httpClient.execute(loginPost);
		byte[] byteArray = IOUtil.convertInputStreamToByteArray(httpResponse
				.getEntity().getContent());
		// 将对应的URL拿到
		String htmlSource = new String(byteArray);
		String hrefRegex = "href=\"([\\s\\S]*?)\"";
		String href = RegexUtil.getMatchText(htmlSource, hrefRegex, 1);
		return href;
	}

	// 2.通过loginSuccess获取cookies值
	public static String getCookiesByLoginSuccessUrl(String href)
			throws UnsupportedOperationException, IOException {
		HttpGet httpGet = new HttpGet(href);
		httpGet.addHeader(
				"Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		httpGet.addHeader("Accept-Encoding", "gzip");
		httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpGet.addHeader("Cache-Control", "no-cache");
		httpGet.addHeader("Connection", "keep-alive");
		httpGet.addHeader("Host", "passport.tianya.cn");
		httpGet.addHeader("Pragma", "no-cache");
		httpGet.addHeader("Upgrade-Insecure-Requests", "1");
		httpGet.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3664.3 Safari/537.36");
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		Header[] headerArray = httpResponse.getAllHeaders();
		byte[] byteArray = IOUtil.convertInputStreamToByteArray(httpResponse
				.getEntity().getContent());
		String htmlSource = new String(byteArray);

		// 拿到response cookie值的url
		String cookieUrlRegex = "src=\"([\\s\\S]*?)\"";
		List<String> urlList = RegexUtil.getMatchTextList(htmlSource,
				cookieUrlRegex, 1);
		List<String> urlListUpdate = new ArrayList<String>();
		for (String url : urlList) {
			url = "http:" + url;
			urlListUpdate.add(url);
		}
		StringBuilder cookieStringBuilder = new StringBuilder();
		for (String url : urlListUpdate) {
			httpGet = new HttpGet(url);
			httpGet.addHeader(
					"Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			httpGet.addHeader("Accept-Encoding", "gzip");
			httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
			httpGet.addHeader("Cache-Control", "no-cache");
			httpGet.addHeader("Connection", "keep-alive");
			httpGet.addHeader("Host", "passport.tianya.cn");
			httpGet.addHeader("Pragma", "no-cache");
			httpGet.addHeader("Upgrade-Insecure-Requests", "1");
			httpGet.addHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3664.3 Safari/537.36");
			httpGet.addHeader("Referer", href);
			httpResponse = httpClient.execute(httpGet);
			headerArray = httpResponse.getAllHeaders();
			for (Header header : headerArray) {
				if ("Set-Cookie".equals(header.getName())) {
					cookieStringBuilder.append(header.getValue());
					cookieStringBuilder.append(";");
				}
			}
			break;
		}

		return cookieStringBuilder.toString();
	}

	//
	public static boolean isLogin(String myIndex, String cookies)
			throws UnsupportedOperationException, IOException {
		HttpGet httpGet = new HttpGet(myIndex);
		httpGet.addHeader(
				"Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		httpGet.addHeader("Accept-Encoding", "gzip");
		httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpGet.addHeader("Cache-Control", "no-cache");
		httpGet.addHeader("Connection", "keep-alive");
		httpGet.addHeader("Host", "www.tianya.cn");
		httpGet.addHeader("Pragma", "no-cache");
		httpGet.addHeader("Upgrade-Insecure-Requests", "1");
		httpGet.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3664.3 Safari/537.36");
		// httpGet.addHeader("Referer", href);
		httpGet.setHeader("Cookie", cookies);
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		byte[] byteArray = IOUtil.convertInputStreamToByteArray(httpResponse
				.getEntity().getContent());
		String htmlSource = new String(byteArray, "utf-8");
		IOUtil.writeStringToFile(htmlSource,
				"C:\\Users\\admin\\Desktop\\spider.html");
		if (htmlSource.contains("我的帖子")) {
			return true;
		}
		return false;

	}

	public static String getCookies(String userName, String password) throws IOException {
		String loginUrl = "https://passport.tianya.cn/login";
		String href = getLoginSuccessUrl(loginUrl, userName, password);
		String cookies = getCookiesByLoginSuccessUrl(href);
		return cookies;
	}

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		String myIndex = "http://www.tianya.cn/138716530";
		String userName = "15803289100";
		String password = "haojiayou@12345";
		String cookies = getCookies(userName, password);
		boolean flag = isLogin(myIndex, cookies);
		System.out.println(flag);
	}
}
