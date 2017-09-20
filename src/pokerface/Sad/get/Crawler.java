package pokerface.Sad.get;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pokerface.Sad.db.DB;
//抓取线程
public class Crawler extends Thread{
	
	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(Crawler.class);
	}
	
	List<ZhihuUser> unfillZhihuUser = null;
	int no; //线程编号 一按照此编号获取cookie
	String cookie = null;
	public Crawler(List<ZhihuUser> unfillZhihuUser,int no) {
		this.unfillZhihuUser = unfillZhihuUser;
		this.no = no;
		this.setName("crawer"+no);
		try {
			Properties pro = new Properties();
			pro.load(new FileInputStream("cookie.properties"));
			String cookie = pro.getProperty("cookie"+this.no);
			if(cookie != null) {
				this.cookie = cookie;
				logger.debug(this.getName()+"获取cookie:"+this.cookie);
			}
		} catch (IOException e) {
			logger.error("获取cookie失败",e);
		}
		
	}
	@Override
	public void run() {
		logger.info(this.getName()+"线程启动，任务列表长度:"+unfillZhihuUser.size());
		int index = 0;
		for (ZhihuUser zhihuUser : unfillZhihuUser) {
			logger.info("开始抓取第"+(++index)+"个用户信息");
			try {
				GetUtil.getUserInfoByUrlToken(zhihuUser);
				DB.fillZhihuUserInfoIntoDB(zhihuUser);
//				GetUtil.getUserFolloweeList(zhihuUser);
//				DB.writeFolloweeListIntoDB(zhihuUser);
			} catch (IOException e) {
				logger.error("抓取"+zhihuUser.url_token+"信息异常",e);
			}
		}
		logger.info(this.getName()+"线程结束");
	}
	
}