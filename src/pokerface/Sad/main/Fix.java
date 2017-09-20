package pokerface.Sad.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pokerface.Sad.db.DB;
import pokerface.Sad.get.GetUtil;
import pokerface.Sad.get.ZhihuUser;
import pokerface.Sad.util.DBUtil;

/**
 * 重新抓取失败的目标
 * @author XinYuan
 *
 */
public class Fix {
	public static void main(String[] args) {
		List<ZhihuUser> failedList = DB.getFailed(2);
		System.out.println(failedList.size());
		if(failedList != null){
			GetUtil.crawlAimList(6, failedList);
		}
		
	}
	

}
