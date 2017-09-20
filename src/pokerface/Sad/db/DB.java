package pokerface.Sad.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;


import pokerface.Sad.get.ZhihuUser;
import pokerface.Sad.util.DBUtil;

public class DB {
	
	static Logger logger = null;
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(DB.class);
	}
	
	/**
	 * 将用户的简单信息写入数据库
	 * @param zhihuUser
	 * @return
	 */
	public static boolean writeZhihuUserIntoDB(ZhihuUser zhihuUser){
		Connection conn = null;
		PreparedStatement ps = null;
		boolean result = false;
		try {
			conn = DBUtil.getConn();
			ps = conn.prepareStatement(DBUtil.getDBProperties().getProperty("sql_insert_user"));
			ps.setString(1, zhihuUser.url_token);
			ps.setInt(2, zhihuUser.distance);
			
			DBUtil.beginTransaction(conn);
			try {
				ps.executeUpdate();
				logger.info("url_token:" + zhihuUser.url_token + " distance:"+ zhihuUser.distance +" 写入成功");
			} catch (MySQLIntegrityConstraintViolationException e) { // 若已有重复则跳过
				logger.info("url_token:" + zhihuUser.url_token + " distance:"+ zhihuUser.distance +" 已存在");
			}
			DBUtil.commitTransaction(conn);
			result = true;
		} catch (IOException | SQLException e) {
			logger.error("数据库操作异常", e);
			DBUtil.rollback(conn);
		} finally {
			DBUtil.close(ps, conn, null);
			logger.debug("关闭资源");
		}
		
		return result;
	}
	public static boolean writeFolloweeListIntoDB(ZhihuUser zhihuUser){
		boolean result = true;
		if(zhihuUser.followeeList != null)
		{
			for (ZhihuUser followee : zhihuUser.followeeList) {
				if(!DB.writeZhihuUserIntoDB(followee))
				{
					logger.error("写入关注者失败 followee:"+followee.url_token);
					result = false;
					continue; //若关注者写入失败则跳过写入关注关系
				}
				if(!DB.writeFollowRelationIntoDB(zhihuUser.url_token, followee.url_token))
				{
					logger.error("写入关注关系失败 follower:"+zhihuUser.url_token+"followee:"+followee.url_token);
					result = false;
				}
			}
		} else {
			logger.error("followeeList对象为null");
			return false;
		}
		return result;
	}
	/**
	 * 完善用户在数据库中的信息
	 * @param zhihuUser
	 * @return
	 */
	public static boolean fillZhihuUserInfoIntoDB(ZhihuUser zhihuUser){
		
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean result = false;
		try {
			conn = DBUtil.getConn();
			ps = conn.prepareStatement(DBUtil.getDBProperties().getProperty("sql_fill_user"));
			ps.setString(1, zhihuUser.username);
			ps.setString(2, zhihuUser.sex);
			ps.setString(3, zhihuUser.educations != null ? zhihuUser.educations.toString() : null);
			ps.setString(4, zhihuUser.employments != null ? zhihuUser.employments.toString() : null);
			ps.setString(5, zhihuUser.answer_count);
			ps.setString(6, zhihuUser.question_count);
			ps.setString(7, zhihuUser.follower_count);
			ps.setString(8, zhihuUser.followee_count);
			ps.setString(9, zhihuUser.articles_count);
			ps.setString(10, zhihuUser.columns_count);
			ps.setString(11, zhihuUser.agreement_count);
			ps.setString(12, zhihuUser.thanks_count);
			ps.setString(13, zhihuUser.collected_count);
			ps.setString(14, zhihuUser.url_token);
			DBUtil.beginTransaction(conn);
			try {
				ps.executeUpdate();
				logger.info("username:" + zhihuUser.username + " 信息写入成功");
			} catch (MySQLIntegrityConstraintViolationException e) { // 若已有重复则跳过
				logger.info("username:" + zhihuUser.username + " 已存在");
			}
			DBUtil.commitTransaction(conn);
			result = true;
		} catch (IOException | SQLException e) {
			logger.error("数据库操作异常",e);
			DBUtil.rollback(conn);
		} finally {
			DBUtil.close(ps, conn, null);
			logger.debug("关闭资源");
		}
		return result;
	}
	
	/**
	 * 将关注关系写入数据库
	 * @param follower_url_token
	 * @param followee_url_token
	 * @return
	 */
	public static boolean writeFollowRelationIntoDB(String follower_url_token,String followee_url_token){
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean result = false;
		try {
			conn = DBUtil.getConn();
			ps = conn.prepareStatement(DBUtil.getDBProperties().getProperty("sql_insert_relation"));
			ps.setString(1, follower_url_token);
			ps.setString(2, followee_url_token);
			DBUtil.beginTransaction(conn);
			try {
				ps.executeUpdate();
				logger.info("relation:" + follower_url_token + "->"+followee_url_token+" 写入成功");
			} catch (MySQLIntegrityConstraintViolationException e) { // 若已有重复则跳过
				logger.info("relation:" + follower_url_token + "->"+followee_url_token+ " 已存在");
			}
			DBUtil.commitTransaction(conn);
			result = true;
		} catch (IOException | SQLException e) {
			logger.error("数据库操作异常",e);
			DBUtil.rollback(conn);
		} finally {
			DBUtil.close(ps, conn, null);
			logger.debug("关闭资源");
		}
		
		return result;
	}
	/**
	 * 获取距离源节点一定距离的用户列表
	 * @param distance
	 * @return
	 */
	public static List<ZhihuUser> getUnfillZhihuUserByDistance(int distance){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ZhihuUser> unfillZhihuUserList = null;
		try {
			conn = DBUtil.getConn();
			ps = conn.prepareStatement(DBUtil.getDBProperties().getProperty("sql_select"));
			ps.setInt(1, distance);
			DBUtil.beginTransaction(conn);
			rs = ps.executeQuery();
			DBUtil.commitTransaction(conn);
			unfillZhihuUserList = new LinkedList<ZhihuUser>();
			ZhihuUser zhihuUser = null;
			while(rs.next()){
				zhihuUser = new ZhihuUser();
				zhihuUser.url_token = rs.getString("url_token");
				zhihuUser.distance = new Integer(rs.getString("distance"));
				unfillZhihuUserList.add(zhihuUser);
			}
		} catch (IOException | SQLException e) {
			logger.error("数据库操作异常",e);
			DBUtil.rollback(conn);
		} finally {
			DBUtil.close(ps, conn, rs);
			logger.debug("关闭资源");
		}
		return unfillZhihuUserList;
	}
	/**
	 * 从数据库中取出指定距离的抓取失败的用户列表
	 */
	public static List<ZhihuUser> getFailed(int distance){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ZhihuUser> zhihuUserList = new ArrayList<ZhihuUser>();
		try {
			conn = DBUtil.getConn();
			ps = conn.prepareStatement(DBUtil.getDBProperties().getProperty("sql_select_fail"));
			ps.setInt(1, distance);
			rs = ps.executeQuery();
			ZhihuUser zhihuUser = null;
			while(rs.next())
			{
				zhihuUser = new ZhihuUser();
				zhihuUser.url_token = rs.getString("url_token");
				zhihuUser.distance = rs.getInt("distance");
				zhihuUserList.add(zhihuUser);
			}
		} catch (IOException | SQLException e) {
			logger.error("连接数据库失败",e);
		} finally {
			DBUtil.close(ps, conn, rs);
		}
		return zhihuUserList;
	}
	
	/**
	 * 取出数据库中所有未完善信息的用户 
	 */
	public static List<ZhihuUser> getUnfillUserList(){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ZhihuUser> unfillUserList = new ArrayList<ZhihuUser>();
		try {
			conn = DBUtil.getConn();
			ps = conn.prepareStatement(DBUtil.getDBProperties().getProperty("sql_select_unfill"));
			rs = ps.executeQuery();
			ZhihuUser zhihuUser = null;
			
			while(rs.next()){
				zhihuUser = new ZhihuUser();
				zhihuUser.url_token = rs.getString("url_token");
				zhihuUser.distance = rs.getInt("distance");
				unfillUserList.add(zhihuUser);
			}
		} catch (IOException | SQLException e) {
			logger.error("连接数据库失败",e);
		} finally {
			DBUtil.close(ps, conn, rs);
		}
		
		
		return unfillUserList;
	}
}
