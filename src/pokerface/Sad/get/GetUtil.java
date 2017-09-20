package pokerface.Sad.get;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import pokerface.Sad.db.DB;
import pokerface.Sad.util.FileUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class GetUtil {

	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(GetUtil.class);
	}
	public static void main(String[] args) throws IOException {
		ZhihuUser user = new ZhihuUser();
		user.url_token = "maple";
		getUserFolloweeList(user);
		System.out.println(user);
	}
	/**
	 * 爬取目标用户集合
	 * 在主线程中调用
	 */
	public static void crawlAimList(int threadNum,
			List<ZhihuUser> aimZhihuUserList) {
		//分发任务
		Thread crawler = null;
		List<Thread> crawlerList = new ArrayList<Thread>();
		List<List<ZhihuUser>> taskQueneList = new ArrayList<List<ZhihuUser>>(threadNum);
		int queneSize = (int) Math.floor((double)aimZhihuUserList.size()/(double)threadNum);
		int index = 0;
		List<ZhihuUser> taskQuene = null;
		for (int i=0; i<threadNum; i++) {
			//若是最后一个元素则跳出
			if(index == aimZhihuUserList.size()) break; 
			//为每一个集合添加元素
			taskQuene = new ArrayList<ZhihuUser>();
			if(i!=(threadNum-1))
			{
				for(int j = 0 ;j<queneSize;j++)
				{
					if(index == aimZhihuUserList.size()) break; 
					taskQuene.add(aimZhihuUserList.get(index++));
					 
				}
			} else {
				//最后一组加入剩下所有元素
				for(int j = index ;j<aimZhihuUserList.size();j++)
				{
					taskQuene.add(aimZhihuUserList.get(index++));
				}
			}
			taskQueneList.add(taskQuene);
		}
		
		//创建线程
		for(int i=0;i<taskQueneList.size();i++)
		{
			crawler = new Crawler(taskQueneList.get(i),i+1);
			crawlerList.add(crawler);
			crawler.start();
		}
		
		//主线程等待抓取线程结束
		logger.info("主线程阻塞");
		while(crawlerList.get(0).isAlive() || crawlerList.get(1).isAlive() || crawlerList.get(2).isAlive() ||
				crawlerList.get(3).isAlive() || crawlerList.get(4).isAlive() || crawlerList.get(5).isAlive());
		logger.info("所有线程抓取完成 ");
	}
	
	
	
	public static void getUserFolloweeList(ZhihuUser user)
			throws IOException {
		try {
			
		
			List<ZhihuUser> followeeList = new LinkedList<ZhihuUser>();
			int offset = -20;
			String url = url = "https://www.zhihu.com/api/v4/members/"+user.url_token+"/followees?include=data%5B*%5D.answer_count%2Carticles_count%2Cgender%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics&offset="+offset +"&limit=20"; 
			logger.info("开始获取 username:"+user.username+"的关注列表");
			int index = 0;
			while(url != null)
			{
				offset = offset + 20;
				url = "https://www.zhihu.com/api/v4/members/"+user.url_token+"/followees?include=data%5B*%5D.answer_count%2Carticles_count%2Cgender%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics&offset="+offset +"&limit=20";
				String resp = get(url, null, null);
				logger.info("正在获取第"+(++index)+"页");
				logger.debug("第"+index+"页响应:"+resp);
				JSONObject json = JSONObject.parseObject(resp);
				JSONObject pageJO = json.getJSONObject("paging");
				String nextUrl = null;
				if(pageJO.getString("is_end").equals("false"))
				{
					nextUrl = pageJO.getString("next");
				}
				JSONArray followeesJA = json.getJSONArray("data");
				if(!followeesJA.isEmpty())
				{
					ZhihuUser followee = null;
					for(int i=0;i < followeesJA.size();i++)
					{
						followee = new ZhihuUser();
						JSONObject followeeJO = followeesJA.getJSONObject(i);
						followee.url_token = followeeJO.getString("url_token");
						followee.distance = user.distance + 1;
						logger.debug("解析到关注着 url_token:"+followee.url_token);
						followeeList.add(followee);
					}
				}
				url = nextUrl;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger.error("中断异常",e);
				}
			}
			logger.info("获取 username:"+user.username+"的关注列表结束");
			user.followeeList = followeeList;
		} catch (NullPointerException | com.alibaba.fastjson.JSONException e) {
			logger.error("抓取"+user.url_token+"关注者列表异常");
		}
	}
	
	public static ZhihuUser getUserInfoByUrlToken(ZhihuUser user)
			throws IOException {
		try {
			
		
			String url_token = user.url_token;
			//从异步请求抓取
			logger.info("开始获取 url_token:"+url_token+"的个人信息");
			String url = "https://www.zhihu.com/api/v4/members/"+url_token+"?include=allow_message%2Cis_followed%2Cis_following%2Cis_org%2Cis_blocking%2Cemployments%2Canswer_count%2Cfollower_count%2Carticles_count%2Cgender%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics";
			logger.debug("异步请求:"+url);
			String resp = get(url, null, null);
			logger.debug("异步请求响应"+resp);
			JSONObject json = JSONObject.parseObject(resp);
			//解析出用户名
			String username = json.getString("name");
			//解析出性别
			String sex = json.getString("gender");
			if(sex.equals("1")) sex = "男";
			else if(sex.equals("0")) sex = "女";
			else sex = "未知";
			//解析职业经历
			JSONArray employmentsArray = json.getJSONArray("employments");
			List<Entry<String, String>> employments = new LinkedList<Entry<String, String>>();
			if(!employmentsArray.isEmpty())
			{
				JSONObject employmentJO = null;
				for(int i=0;i<employmentsArray.size();i++)
				{
					employmentJO = employmentsArray.getJSONObject(i);
					String companyName = null;
					String jobName = null;
					//解析公司名
					JSONObject companyJO = employmentJO.getJSONObject("company");
					if(companyJO != null)	companyName = companyJO.getString("name");
					//解析职位名
					JSONObject jobJO = employmentJO.getJSONObject("job");
					if(jobJO != null)	jobName = jobJO.getString("name");
					
					employments.add(new AbstractMap.SimpleEntry(companyName, jobName));
				}
			}
	
			//从页面抓取
			url = "https://www.zhihu.com/people/"+url_token+"/activities";
			logger.debug("页面请求:"+url);
			String answer_html = get(url,null,null);
			Document doc = Jsoup.parse(answer_html);
			logger.debug("页面响应"+doc.toString());
			//Jsoup解析
			String answer_count = doc.select("#ProfileMain > div.ProfileMain-header > ul > li:nth-child(2) > a > span").text();
			String question_count = doc.select("#ProfileMain > div.ProfileMain-header > ul > li:nth-child(3) > a > span").text();
			String articles_count = doc.select("#ProfileMain > div.ProfileMain-header > ul > li:nth-child(4) > a > span").text();
			String columns_count = doc.select("#ProfileMain > div.ProfileMain-header > ul > li:nth-child(5) > a > span").text();
			String fan_count = doc.select("#root > div > main > div > div > div.Profile-main > div.Profile-sideColumn > div.Card.FollowshipCard > div > a:nth-child(3) > div.NumberBoard-value").text();
			String follower_count = doc.select("#root > div > main > div > div > div.Profile-main > div.Profile-sideColumn > div.Card.FollowshipCard > div > a:nth-child(1) > div.NumberBoard-value").text();
			//正则解析
			String agreement_count = null;
			Matcher agreement_count_matcher = Pattern.compile("获得 ([0-9]+?) 次赞同").matcher(answer_html);
			if(agreement_count_matcher.find()) agreement_count = agreement_count_matcher.group(1);
			else agreement_count = "0";
			String thanks_count = null;
			String collect_count = null;
			Matcher thanks_count_matcher = Pattern.compile("获得 ([0-9]+?) 次感谢").matcher(answer_html);
			Matcher collect_count_matcher = Pattern.compile("([0-9]+?) 次收藏").matcher(answer_html);
			if(thanks_count_matcher.find()){
				thanks_count = thanks_count_matcher.group(1); 
			} else {
				thanks_count = "0";
			}
			if(collect_count_matcher.find()){
				collect_count = collect_count_matcher.group(1); 
			} else {
				collect_count = "0";
			}
			user.username = username;
			user.url_token = url_token;
			user.sex = sex;
			user.employments = employments;
			user.answer_count = answer_count;
			user.question_count = question_count;
			user.articles_count = articles_count;
			user.columns_count = columns_count;
			user.follower_count = fan_count;
			user.followee_count = follower_count;
			user.agreement_count = agreement_count;
			user.thanks_count = thanks_count;
			user.collected_count = collect_count;
			logger.info("获取 url_token:"+url_token+"的个人信息结束");
		} catch (NullPointerException | com.alibaba.fastjson.JSONException e) {
			logger.error("抓取"+user.url_token+"个人信息异常");
		}
		return user;
	}
	
	public static String get(String url,String proxyHost,String proxyPort) throws IOException {  
		CloseableHttpClient closeableHttpClient = null;
		//根据协议类型创建相应client
		if(isHttps(url))
			closeableHttpClient = createSSLClientDefault();
			
		else {
			//创建HttpClientBuilder  
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create(); 
			httpClientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build());
			//HttpClient  
			closeableHttpClient = httpClientBuilder.build();  
		}
		
		HttpGet httpGet = new HttpGet(url);  
		RequestConfig requestConfig = null;
		
		//设置请求头
        httpGet.setHeader("Accept", "application/json, text/plain, */*");  
//        httpGet.setHeader("authorization", "Bearer Mi4xT2E2UkJRQUFBQUFBRU1BNjJ1cW5DaGNBQUFCaEFsVk4yQVBhV1FEdXZDNDltT08wTTdveVBVbk5YQk5ZeV84RDlR|1504868056|d6415eb674b1884d4f311b12952b1ad63dfacd0f");  
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        //获取此线程的cookie
        String cookie = null;
        if((Thread.currentThread()) instanceof Crawler)
        {
        	cookie = ((Crawler)(Thread.currentThread())).cookie;
        	if(cookie != null)
        		httpGet.setHeader("cookie", cookie);  
        	else
        		logger.error("线程cookie异常");
        } else {
        	cookie = FileUtil.getProperties("cookie.properties").getProperty("cookie1");
        	httpGet.setHeader("cookie", cookie);
        }
        if(proxyHost != null && proxyPort != null)
        {
        	
        	HttpHost proxy = new HttpHost(proxyHost, new Integer(proxyPort).intValue());
        	
        	requestConfig = RequestConfig.custom()  
        			.setConnectionRequestTimeout(10000).setConnectTimeout(10000)  
        			.setSocketTimeout(10000).setCookieSpec(CookieSpecs.STANDARD_STRICT).setProxy(proxy).build();
        }else{
        	requestConfig = RequestConfig.custom()  
        			.setConnectionRequestTimeout(10000).setConnectTimeout(10000)  
        			.setSocketTimeout(10000).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        }
        httpGet.setConfig(requestConfig);    
		
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
		try {  
		    //执行get请求  
			httpResponse = closeableHttpClient.execute(httpGet);  
		    //获取响应消息实体  
		    entity = httpResponse.getEntity();
		    
		    String html = null;
		    //判断响应实体是否为空  
		    if (entity == null) return null; 
	    	else
		    {
	    		byte[] bytes = new byte[1024*1024]; //最大1M
	    		InputStream is = entity.getContent();
	    		int offset = 0;
	    		int numRead = 0;
	    		while(offset < bytes.length && 
	    				(numRead = is.read(bytes, offset, bytes.length-offset)) != -1) 
	    			offset+=numRead;
	    		html = new String(bytes,0,offset,"utf-8");
	    		String charSet = getCharSet(html);
	    		if(charSet == null || charSet.equals("utf8") || charSet.equals("utf-8"))
	    			//若是未解析到编码方式或编码方式为UTF8 
	    			return html;
	    		else
	    			html = new String(bytes,0,offset,charSet);
	    		return html;
		    }
		} finally {  
		    try {  
		    	//关闭流并释放资源
		    	if(entity!=null)
		    		EntityUtils.consume(entity);
		    	if(httpGet != null)
		    		httpGet.releaseConnection();
	            if(closeableHttpClient!= null)
	            	closeableHttpClient.close();
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }
	}
	
	/**
	 * 返回一个SSL连接的CloseableHttpClient实例
	 * @return
	 */
    public static CloseableHttpClient createSSLClientDefault(){
        try {
            SSLContext sslContext=new SSLContextBuilder().loadTrustMaterial(
                    null,new TrustStrategy() {
                        //信任所有
                        public boolean isTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslsf=new SSLConnectionSocketFactory(sslContext);
            
            return HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build()).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }
    /** 
     * 正则获取字符编码 
     * @param content 
     * @return 
     */  
    private static String getCharSet(String content){  
        String regex = "charset=['\\\"]*(.+?)[ '\\\">]";  
        Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(content);  
        if(matcher.find())  
            return matcher.group(1);  
        else  
            return null;  
    }  
    
    /**
     * 判断请求URL是否为https请求
     * @param url
     * @return
     */
    public static boolean isHttps(String url){
		boolean result =  false;
    	
		Pattern p = Pattern.compile("https://");
		Matcher m = p.matcher(url);
		if(m.find()) result = true;
		
    	return result;
    }
}
