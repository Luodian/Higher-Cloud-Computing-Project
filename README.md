# Higher-Cloud-Computing-Project

### 简述

We are committed to building a cloud computing platform that leverages idle resources based on mobile or local networks

我们致力于构造一个基于外网及局域内网平台搭建的利用闲置资源进行科学计算的云计算服务平台。

### 第14周-第15周周二下午：

**任务分配计划：**

- [ ] 完成基于Java FX构建的GUI应用程序。（武德浩，王烨臻）
      - 完成程序框架。
      - 动画的效果。
      - 能够使用Java获取Ubuntu下的系统权限，进行spark配置文件，ssh配置文件等的读写与修改。
- [ ] 服务器与界面接口开发，服务器自身页面的开发。（钟宇宏，刘丹丹）
- [ ] Docker与开发方面的对接。（李博）
- [ ] Spark，Hadoop，ZooKeeper与开发方面的对接，深入理解这些工具在Ubuntu组网下的运行机理。（王烨臻，刘丹丹，李博）
      - 本周内完成组网间的配置，写一份配置的接口文档！

（我们要深入理解其运行机制，才能够明白在出错时该怎么办~目前Spark配置完成，但是始终不能够获取到datanode结点，很让人觉得沮丧。）

**应用界面功能简述：**

> 推荐按照SS那种风格来做，系统性应用，整个窗口尽量居中时占 800*600 大小较好。

- main_page 
  - 有一个大的 chart 用来做网络中结点的展示。
  - 有显示用户的id，昵称的区域。
  - 有『上线』，『下线』，『提交任务』，『系统配置』，『算力估测』等按钮。
  - 有显示在线时间，积分等按钮
- system_preference_page
  - 可以修改昵称。
  - 有包括`host_name`,`inet_ip`,`hosts设置`等细化的配置。
  - 有`本机信息`按钮。
- login_page
  - 包含账号，密码输入框。
  - 包含注册新用户按钮。
- sign_up_page
  - 包含用户名，昵称，密码的填写。
  - 包含用户邮箱的填写。
  - 填写邮箱后等待120S的验证码，确认后激活注册用户。