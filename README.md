# 电子科技大学每日健康填报自动打卡

电子科技大学每日自动健康填报

# 新版方式(采用Github的Actions功能)

1. [获取sessionId](#sessionId获取方法)
2. Fork本仓库
3. 点击Settings→Secrets→New repository secret
4. 在Name中填入session，在Value中填入刚刚获取到的XXX-XXX
5. 进入Actions界面手动运行一次后既可，之后每天8点会自动运行

# 旧版方式(采用QQ机器人)

## 简介

使用了mirai作为QQ机器人，通过QQ机器人进行交互，可以自动对微信进行抓包获取sessionId。

每天定时进行一次健康打卡，打卡的结果通过QQ机器人返回。

## 依赖组件

python3（3.9.6可以使用，其余未测试），openjdk 11，mitmproxy(可选)

```
pip3 install mitmproxy
```

## 使用方法

1. 修改Robot.java，输入管理QQ和机器人QQ的信息

2. 修改daka.java，输入打卡地址，打卡地址可以在微信小程序中查看（可选：修改data.txt位置，此文件存放sessionId）

3. 在根目录（或者第2步的位置）新建daka.txt，写入sessionId和日期。日期代表要从哪天开始打卡。[sessionId获取方法](#sessionId获取方法)
   （当sessionId过期后，QQ机器人会发来登录失败的提示，修改后向QQ机器人发送`打卡`既可）（目前还没遇到失效的情况）

   ```
   00000000-0000-0000-0000-000000000000
   2021-08-01
   ```

4. 运行Gradle中shadow的shadowJar，生成dakaRobot.jar

   ![](https://i.loli.net/2021/08/04/WMUkaTmYe3fB86H.png)（idea界面)

5. 将daka.py，daka.txt，dakaRobot.jar放到同一个目录中，运行py和jar（第一次运行mirai会跳出设备锁，按照上面的步骤验证既可）

6. 机器人默认在每天的8点打卡，可以在源文件中修改

   ## 完整版使用方法（实际上sessionId存活时间极长，用不到）

   如果有空闲的电脑或者虚拟机，推荐一直运行程序，以centos7为例

   ##### 安装python和mitmproxy

   ```
   yum -y groupinstall "Development tools"
   yum install -y zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel libffi-devel wget
   wget https://www.python.org/ftp/python/3.9.6/Python-3.9.6.tgz
   mkdir /usr/local/python3
   mv Python-3.9.6.tgz /usr/local/python3/
   cd /usr/local/python3/
   tar xf Python-3.9.6.tgz 
   cd Python-3.9.6
   ./configure --prefix=/usr/local/python3
   make && make install
   ln -s /usr/local/python3/bin/python3 /usr/bin/python3
   ln -s /usr/local/python3/bin/pip3 /usr/bin/pip3
   cd /etc/profile.d
   echo 'export PATH=$PATH:/usr/local/python3/bin/' > python3.sh
   pip3 install mitmproxy
   ```

   ##### 关闭防火墙

   ```
   systemctl disable firewalld
   ```

   ##### 安装openjdk11

   ```
   yum install -y java-11-openjdk java-11-openjdk-devel.x86_64
   ls -lrt /etc/alternatives/java
   输出中有一段java-11-openjdk-11.0.12.0.7-0.el7_9.x86_64（根据自己电脑显示的来），下面的那部分要修改成一样的
   vi /etc/profile
   ```

   底部加入

   ```
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.12.0.7-0.el7_9.x86_64
   export JRE_HOME=$JAVA_HOME/jre
   export CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib:$CLASSPATH
   export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH
   ```

   刷新配置

   ```
   source /etc/profile
   ```

   ##### 运行

   将daka.py，daka.txt，dakaRobot.jar，daka.sh，device.json（第一次成功登录mirai后生成的）放入同一目录下

   ```
   chmod 777 daka.sh
   ./daka.sh
   ```

   ##### sessionId的自动获取

    1. 设置手机的代理服务器，端口为8080，ip为运行py文件的电脑ip。手机打开mitm.it网站，出现内容则说明连接上了代理服务器。
    2. 根据所用手机选择对应证书下载并且安装证书。安卓手机参考
       https://blog.csdn.net/djzhao627/article/details/102812783
       https://blog.51cto.com/abool/1429700
    3. 手机先完全关闭微信，再打开微信小程序，进入健康打卡界面，此时data.txt会被更新，如果打不开健康打卡界面可以关开代理再试
    4. 获取或者更新sessionId后向机器人发送`打卡`即可开始打卡

   ## sessionId获取方法

    1. 安装Burp Suite Community Edition
    2. 安装Burp Suite Community Edition的CA证书(在Proxy的Options选项中)
    3. 在Proxy的Intercept中确认Intercept is off，然后打开微信小程序直到体温填报的界面
    4. 设置电脑代理服务器为127.0.0.1:8080
    5. 在每日填报和假期出行之间多点几下，可以看到HTTP history中有很多条目
    6. 找到`/wxvacation/api/epidemic/checkRegisterNew`或`/wxvacation/api/epidemic/getBackSchoolInfo`
    7. 查看Response选项卡中的SESSION=XXX-XXX-XXX-XXX-XXX即为需要的

