package pokerface.Sad.login;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.CharsetUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pokerface.Sad.main.Main;
import pokerface.Sad.util.CharSetUtil;


public class ZhihuLogin {
	static Logger logger = null;
	static{
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(ZhihuLogin.class);
	}
	public static void main(String[] args) {
		
		Properties pro = new Properties();
		try {
			pro.load(new FileInputStream("ZhihuSpider.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String account = null;
		String password = null;
		String cookie = null;
		
		Properties pro_cookie = new Properties();
		
		for(int i=1;i<=6;i++)
		{
			account = pro.getProperty("account"+i);
			password = pro.getProperty("password"+i);
			cookie = zhihuLogin(account, password);
			if(cookie != null){
				pro_cookie.setProperty("cookie"+i, cookie);
				System.out.println(cookie);
			} else {
				System.out.println("登录失败");
			}
		}
		try {
			pro_cookie.store(new FileOutputStream("cookie.properties"), "Cookie");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String zhihuLogin(String account,String password){
		
		boolean isEmailLogin = false;
		logger.debug("账号:"+account+"  密码:"+password+" 尝试模拟登录");
		Pattern p_email = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
		//判断是否为邮箱登录
		if(p_email.matcher(account).find()) isEmailLogin = true;
		List<BasicNameValuePair> payload = new LinkedList<BasicNameValuePair>();
		if(isEmailLogin) payload.add(new BasicNameValuePair("email", account));
		else payload.add(new BasicNameValuePair("phone_num", account));
		payload.add(new BasicNameValuePair("captcha_type", "cn"));
		payload.add(new BasicNameValuePair("password", password));
		CloseableHttpClient closeableHttpClient = createSSLClientDefault();
		HttpResponse response = null;
		String loginUrl = null;
		if(isEmailLogin) loginUrl = "https://www.zhihu.com/login/email";
		else loginUrl = "https://www.zhihu.com/login/phone_num";
		HttpPost httpPost = new HttpPost(loginUrl);

		// 设置请求头
		httpPost.setHeader("Accept",
				"Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPost.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");
		httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		httpPost.setHeader(
				"cookie",
				"d_c0=\"ABDAOtrqpwqPTiCJEESZQfP_3kIJ5_Oetgk=|1475847437\"; _za=6d4a30dd-9ad9-4708-a63c-6db1fa31c092; _zap=89545320-2b02-4227-93ba-7d3b47b35c2f; q_c1=5d8c0952449f4edb8888217c99d08800|1500169228000|1475847436000; capsion_ticket=\"2|1:0|10:1501398602|14:capsion_ticket|44:MjEyZmM5OTIwNDQ4NDFlMWFlNzhhZGI4OWIxZGQzNzA=|3d2c6529269b2ac4445fd8382af344826b21fea755c09522eac07198611bc6bb\"; aliyungf_tc=AQAAAI7nX3CigwwAZncrJA0ZoJvhI5Mn; _xsrf=ff1f9f02-cd28-42f3-8aca-618d6890b247; l_cap_id=\"ZGQwNGNjODM3YmUzNDgxY2JiMDU4NDIwMDI1OGY4NGQ=|1501477532|7b91137bff10d86c34266fa415c263ec86f8ebd6\"; r_cap_id=\"MzAxNGY2MGQxMWNlNDEzZmJkZmYzMTZhMTBlZjI4ZjQ=|1501477532|38b62c9849dd881a8ddd72d0efb5e7dcfac146ce\"; cap_id=\"NGFlZDhkMDhjMmMyNDkyZDk5NjM2ZDQ5MDRmMzIyYmI=|1501477532|38ac5cf87560d2ec6e536cd4bd22c9e94842fd81\"; __utma=51854390.1699328739.1495879145.1501473042.1501476215.26; __utmc=51854390; __utmz=51854390.1501476215.26.8.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmv=51854390.000--|2=registration_date=20160512=1^3=entry_date=20161007=1");

		RequestConfig requestConfig = null;
		requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(1000).setConnectTimeout(1000)
					.setSocketTimeout(1000).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
		httpPost.setConfig(requestConfig);

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		if (payload != null) {
			for (BasicNameValuePair basicNameValuePair : payload)
				formparams.add(basicNameValuePair);
		}
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(entity);
			
			response = closeableHttpClient.execute(httpPost);
			Header[] allHeaders = response.getAllHeaders();
			Pattern p_cookie = Pattern.compile("z_c0=(.+?);");
			String cookie = null;
			for (Header header : allHeaders) {
				Matcher m_cookie = p_cookie.matcher(header.getValue());
				if(m_cookie.find()) cookie=m_cookie.group();  
			}
			HttpEntity httpEntity = response.getEntity();
			String responseHtml = null;
			if (httpEntity == null){
				return null;
			}
			else {
				byte[] bytes = new byte[1024 * 1024]; // 最大1M
				InputStream is = httpEntity.getContent();
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length
						&& (numRead = is.read(bytes, offset, bytes.length - offset)) != -1)
					offset += numRead;
				responseHtml = new String(bytes, 0, offset, "utf-8");
				String charSet = getCharSet(responseHtml);
				if (charSet == null || charSet.equals("utf8") || charSet.equals("utf-8")){
					// 若是未解析到编码方式或编码方式为UTF8则不处理
				}
				else
					responseHtml = new String(bytes, 0, offset, charSet);
				
				Pattern p_msg = Pattern.compile("\"msg\": \"(.+?)\"");
				Matcher m_msg = p_msg.matcher(responseHtml);
				if(m_msg.find()) {
					String msg = CharSetUtil.unicode2String(m_msg.group(1));
					if(msg.equals("登录成功"))
						return cookie;
					else
					{
						logger.error("登录失败，原因:"+msg);
						return null;
					}
				} else {
					logger.error("登录失败，原因:响应解析失败");
					return null;
				}
			}
		} catch (IOException e) {
			logger.error("登录失败",e);
		}
		return null;

	}


	/**
	 * 返回一个SSL连接的CloseableHttpClient实例
	 * 
	 * @param cookieStore
	 * @return
	 */
	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext);

			return HttpClients
					.custom()
					.setSSLSocketFactory(sslsf)
					.setDefaultSocketConfig(
							SocketConfig.custom().setSoTimeout(5000).build())
					.build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	public static boolean isHttps(String url) {
		boolean result = false;

		Pattern p = Pattern.compile("https://");
		Matcher m = p.matcher(url);
		if (m.find())
			result = true;

		return result;
	}

	/**
	 * 正则获取字符编码
	 * 
	 * @param content
	 * @return
	 */
	private static String getCharSet(String content) {
		String regex = "charset=['\\\"]*(.+?)[ '\\\">]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find())
			return matcher.group(1);
		else
			return null;
	}
}
