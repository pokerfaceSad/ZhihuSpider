package pokerface.Sad.main;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import pokerface.Sad.db.DB;
import pokerface.Sad.get.GetUtil;
import pokerface.Sad.get.ZhihuUser;
import pokerface.Sad.util.DBUtil;
import pokerface.Sad.util.HttpUtil;

public class Init {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		/*
		 *初始化数据库
		 */
		ZhihuUser user = new ZhihuUser();
		user.url_token = "liu-xing-yu-78-17";
		DB.writeZhihuUserIntoDB(user);
		GetUtil.getUserInfoByUrlToken(user);
		DB.fillZhihuUserInfoIntoDB(user);
		GetUtil.getUserFolloweeList(user);
		DB.writeFolloweeListIntoDB(user);
		
	}
}
