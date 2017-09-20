package pokerface.Sad.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.jna.platform.FileUtils;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;

import pokerface.Sad.db.DB;
import pokerface.Sad.get.Crawler;
import pokerface.Sad.get.GetUtil;
import pokerface.Sad.get.ZhihuUser;
import pokerface.Sad.util.FileUtil;

public class Main {
	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(Main.class);
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		
		Properties pro = null;
		try {
			pro = FileUtil.getProperties("ZhihuSpider.properties");
		} catch (IOException e) {
			logger.error("配置文件读取异常",e);
			throw new FileNotFoundException();
		}
		
		
		
		int beginDistance = new Integer(pro.getProperty("beginDistance")); //初始距离
		int currentDistance = beginDistance; //当前距离
		int endDistance = new Integer(pro.getProperty("endDistance"));; //结束距离
		int threadNum = new Integer(pro.getProperty("threadNum"));; //线程数量
		
		logger.info("ZhihuSpider启动	beginDistance="+beginDistance+" endDistance="+endDistance+" threadNum="+threadNum);
		
		List<ZhihuUser> aimZhihuUserList = null;
		while(currentDistance != endDistance)
		{
			
			aimZhihuUserList = DB.getUnfillZhihuUserByDistance(currentDistance);
			if(aimZhihuUserList != null){
				GetUtil.crawlAimList(threadNum, aimZhihuUserList);
				currentDistance++;
			}
		}

	}

	
}
