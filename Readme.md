
# 基于Java的知乎爬虫


爬虫基于Java实现，数据采用MySQL存储，可视化使用Echarts。数据获取逻辑是，从源节点开始抓取关注列表构成数据集。
下面是自己抓取时采取的步骤，可以按照具体需要调整

    Step0: 用BuildDB.sql搭建数据库
    
    Step1: 在ZhihuSpider.properties中配置好账号信息，抓取深度和线程数量
    
    Step2: 运行pokerface.Sad.login.ZhihuLogin的main方法，会将所需cookie填入cookie.properties(也可手动登录，直接填入)
    
    Step3: 运行pokerface.Sad.main.Init类的main方法初始化数据库中的数据集
    
    Step4: 运行pokerface.Sad.main.Main类的main方法开始抓取用户关注关系
    
    Step5: 等待抓取完成
    
    Step6: 运行pokerface.Sad.main.Fix类的main方法会将抓取失败的用户信息重新抓取
    
    Step7: 运行pokerface.Sad.main.fillInfo类的main方法抓取所有用户的个人信息


## 1. 男女比例
![男女比例][1]

还是汉子占多数
## 2. 用户获得赞同数量分布


![赞同数分布][2]
仰望赞同数大于100万的12位大佬
## 3.获得赞同数Top10
![赞同数Top10][3]

@张佳玮  3881887
@Seasee Youl  1577849
@马前卒  1490940
@vczh  1470443
@唐缺  1468457
@鬼木知  1371725
@肥肥猫  1368270
@朱炫  1295927
@ze ran  1269743
@豆子  1225418
## 4.粉丝数Top10

![粉丝数Top10][4]
@张佳玮  1426823
@李开复 1013888
@黄继新  809900
@周源  777401
@yolfilm  768063
@丁香医生  755301
@张亮  720349
@张小北  666883
@李淼  662630
@朱炫  652258
## 5.回答Top10

![回答数Top10][5]

@Phil  17909
@vczh  16373
@王若枫  13111
@浪琴  12764
@李东  11808
@柴健翌  11281
@zhen-liang 11164(已被知乎停用，看了下回答，可能是某个搞机器学习的大佬弄出来的机器人)
@赵钢  11137
@另一只袜子  10146
@luvian zhang  9833
## 6.提问Top10
![提问数Top10][6]

@阿混  3154
@David Chang  2685
@玉箫沙  2535(已被封号，据说是被续了)
@Howard Dominic  2528
@图灵Don  2178
@歆盐  2060
@程瀚  1756
@张亮  1755
@张醒  1656
@fudanboy  1530


还有一些没有可视化的数据
## 7.文章数Top10

@扑克投资家-林辉  1639
@王佳伦  1620
@马力  1507
@张十三  1390
@军旗猎猎  1364
@陈希  1181
@不鳥萬如一  1170
@耿怀民  1155
@嘶吼RoarTalk  1096
@周永   1082
## 8.被收藏数Top10

@寺主人  1805631
@下厨房  1625157
@张佳玮  1194602
@曾少贤  177151
@肥肥猫  1026440
@恶膜的奶爸  992186
@warfalcon  804395
@Jennyyy  747445
@白诗诗  736685
@朱炫  678335


  [1]: https://raw.githubusercontent.com/pokerfaceSad/ZhihuSpider/master/Pic/%E7%94%B7%E5%A5%B3%E6%AF%94%E4%BE%8B.png
  [2]: https://raw.githubusercontent.com/pokerfaceSad/ZhihuSpider/master/Pic/%E8%8E%B7%E5%BE%97%E8%B5%9E%E5%90%8C%E6%95%B0%E9%87%8F%E5%88%86%E5%B8%83.png
  [3]: https://raw.githubusercontent.com/pokerfaceSad/ZhihuSpider/master/Pic/%E8%B5%9E%E5%90%8C%E6%95%B0Top10.png
  [4]: https://raw.githubusercontent.com/pokerfaceSad/ZhihuSpider/master/Pic/%E7%B2%89%E4%B8%9D%E6%95%B0Top10.png
  [5]: https://raw.githubusercontent.com/pokerfaceSad/ZhihuSpider/master/Pic/%E5%9B%9E%E7%AD%94%E6%95%B0Top10.png
  [6]: https://raw.githubusercontent.com/pokerfaceSad/ZhihuSpider/master/Pic/%E6%8F%90%E9%97%AE%E6%95%B0Top10.png
