package pokerface.Sad.main;

import java.util.List;

import pokerface.Sad.db.DB;
import pokerface.Sad.get.GetUtil;
import pokerface.Sad.get.ZhihuUser;


/**
 * 完善用户信息
 * @author XinYuan
 *
 */
public class FillInfo {
	public static void main(String[] args) {
		List<ZhihuUser> unfillUserList = DB.getUnfillUserList();
		System.out.println(unfillUserList.size());
		if(unfillUserList != null){
			GetUtil.crawlAimList(6, unfillUserList);
		}
	}
}
