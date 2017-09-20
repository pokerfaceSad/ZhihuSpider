CREATE DATABASE `zhihu_user_net` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE DATABASE `zhihu_user_net`;`
CREATE TABLE `follow_relation` (
  `follower_url_token` varchar(100) NOT NULL DEFAULT '' COMMENT '粉丝',
  `followee_url_token` varchar(100) NOT NULL DEFAULT '' COMMENT '被关注者',
  PRIMARY KEY (`follower_url_token`,`followee_url_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_info` (
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `sex` varchar(4) DEFAULT NULL COMMENT '性别',
  `education` text COMMENT '教育经历',
  `employments` text COMMENT '职业经历',
  `answer_count` varchar(20) DEFAULT NULL COMMENT '回答总数',
  `question_count` varchar(20) DEFAULT NULL COMMENT '问题总数',
  `follower_count` varchar(20) DEFAULT NULL COMMENT '粉丝总数',
  `followee_count` varchar(20) DEFAULT NULL COMMENT '关注者总数',
  `articles_count` varchar(20) DEFAULT NULL COMMENT '文章总数',
  `columns_count` varchar(20) DEFAULT NULL COMMENT '专栏总数',
  `agreement_count` varchar(20) DEFAULT NULL COMMENT '获得赞同总数',
  `thanks_count` varchar(20) DEFAULT NULL COMMENT '获得感谢总数',
  `collected_count` varchar(20) DEFAULT NULL COMMENT '被收藏总数',
  `url_token` varchar(100) NOT NULL DEFAULT '' COMMENT '域名',
  `distance` int(5) DEFAULT NULL COMMENT '与关系网络中源节点的距离',
  PRIMARY KEY (`url_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
