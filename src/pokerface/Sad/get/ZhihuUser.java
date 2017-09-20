package pokerface.Sad.get;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class ZhihuUser {
	
	public String username; //用户名
	public String sex; //性别
	public String url_token; //域名
	public List<Entry<String, String>> educations; //教育经历
	public List<Entry<String, String>> employments; //职业经历
	public String answer_count; //回答总数
	public String question_count; //问题总数
	public String follower_count; //粉丝总数
	public List<ZhihuUser> followerList; //粉丝列表
	public String followee_count; //关注者总数
	public List<ZhihuUser> followeeList; //关注者列表
	public String articles_count; //文章总数
	public String columns_count; //专栏总数
	public String agreement_count; //获得赞同总数
	public String thanks_count; //获得感谢总数
	public String collected_count; //被收藏总数
	
	public int distance; //与关系网络中源节点的距离
	public ZhihuUser() {
		super();
	}
	@Override
	public String toString() {
		return "ZhihuUser [username=" + username + ", sex=" + sex
				+ ", url_token=" + url_token + ", educations=" + educations
				+ ", employments=" + employments + ", answer_count="
				+ answer_count + ", question_count=" + question_count
				+ ", fan_count=" + follower_count + ", follower_count="
				+ followee_count + ", articles_count=" + articles_count
				+ ", columns_count=" + columns_count + ", agreement_count="
				+ agreement_count + ", thanks_count=" + thanks_count
				+ ", collect_count=" + collected_count + "]";
	}
	
}	
