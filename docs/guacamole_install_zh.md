## Centos7 部署 guacamole0.9.14

#### 关闭防火墙

```
# setenforce 0  # 可以设置配置文件永久关闭
# systemctl stop iptables.service
# systemctl stop firewalld.service
```

#### 添加依赖库

```
yum -y groupinstall "Development Tools" 

yum install cairo-devel libjpeg-devel libpng-devel uuid-devel ffmpeg-devel freerdp-devel pango-devel libssh2-devel libtelnet-devel    libvncserver-devel pulseaudio-libs-devel openssl-devel libvorbis-devel libwebp-devel wget gedit  java-1.8.0-openjdk*
```

* ffmpeg centos7 默认源没有改用其他源

```
yum install -y epel-release 
rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro
rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm
yum install ffmpeg-devel libtelnet-devel
```

#### 安装tomcat8

```
cd /opt/
wget http://mirror.bit.edu.cn/apache/tomcat/tomcat-8/v8.5.30/bin/apache-tomcat-8.5.30.tar.gz
tar -zxvf apache-tomcat-8.5.30.tar.gz -C /usr/local/
cd /usr/local/
mv apache-tomcat-8.5.30 tomcat
cd tomcat/bin
./startup.sh

systemctl stop firewalld
systemctl disable firewalld
```
##### tomcat开机自启

* 新建服务文件
```
vi /lib/systemd/system/tomcat.service
######以下是内容########
[Unit]
Description=tomcat
After=network.target
 
[Service]
Type=oneshot
ExecStart=/usr/local/tomcat/bin/startup.sh   //自已的tomcat目录
ExecStop=/usr/local/tomcat/bin/shutdown.sh
ExecReload=/bin/kill -s HUP $MAINPID
RemainAfterExit=yes
 
[Install]
WantedBy=multi-user.target
```

* 设置脚本权限
```
chmod 754 /lib/systemd/system/tomcat.service 
```

* 启动参数
```
#启动服务 
systemctl start tomcat.service   
#关闭服务   
systemctl stop tomcat.service   
#开机启动   
systemctl enable tomcat.service
```

#### 下载guacamole安装包

```
cd /opt/
wget https://mirrors.tuna.tsinghua.edu.cn/apache/guacamole/0.9.14/source/guacamole-server-0.9.14.tar.gz
wget https://mirrors.tuna.tsinghua.edu.cn/apache/guacamole/0.9.14/source/guacamole-client-0.9.14.tar.gz
wget https://mirrors.tuna.tsinghua.edu.cn/apache/guacamole/0.9.14/binary/guacamole-0.9.14.war

```

#### 编译安装guacamole-server

```
tar -zxvf guacamole-server-0.9.14.tar.gz
cd guacamole-server-0.9.14
./configure --with-init-dir=/etc/init.d
make && make install
ldconfig

/etc/init.d/guacd start
/sbin/chkconfig guacd on  #设置开机自启动，根据需要
```

##### 配置环境变量

```
cd ~ 
vim .bashrc

export GUACAMOLE_HOME=/etc/guacamole

source .bashrc
```

#### 安装guacamole-client

* 直接使用编译包

```
cd /opt/
cp guacamole-0.9.14.war /usr/local/tomcat/webapps/guacamole.war
ln -s /etc/guacamole/guacamole.properties /usr/local/tomcat/.guacamole/    #软连接可以忽略
```

#### 配置guacamole

```
mkdir -p /etc/guacamole/
mkdir -p /etc/guacamole/extensions
mkdir -p /etc/guacamole/lib
vi /etc/guacamole/guacamole.properties

####guacamole.properties####
guacd-hostname: localhost
guacd-port:     4822
user-mapping: /etc/guacamole/user-mapping.xml

# MySQL properties
# mysql-hostname: localhost
# mysql-port: 3306
# mysql-database: guacamole_db
# mysql-username: guacamole
# mysql-password: guacamole
####guacamole.properties####
```

* logback.xml 服务日志开启

```
vi /etc/guacamole/logback.xml

####logback.xml####
<configuration>

    <!-- Appender for debugging -->
    <appender name="GUAC-DEBUG" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Log at DEBUG level -->
    <root level="debug">
        <appender-ref ref="GUAC-DEBUG"/>
    </root>

</configuration>
####logback.xml####
```

* user-mapping.xml     #配置用户映射文件（当使用mysql链接时此配置文件可忽略！）

[编写用户映射配置文件，具体参数配置文档](http://guacamole.apache.org/doc/gug/configuring-guacamole.html)

[中文文档参数参考](https://www.realks.com/category/technology/remote-desktop/)
```
<user-mapping>
        <authorize username="admin" password="admin">
                 <connection name="Centos7">
                         <protocol>ssh</protocol>
                         <param name="hostname">192.168.1.199</param>
                         <param name="port">22</param>
                         <param name="username">root</param>
                         <param name="password">1qaz2wsx</param>
                         <param name="typescript-path">/usr/local/tomact/webapps/guacamole/media/${GUAC_DATE}</param> 
                         <param name="create-typescript-path">true</param>
                         <param name="typescript-name">7b40a4ff-552c-4891-a62f-4cef1e73c0dc</param>
                 </connection>
                 <connection name="Telnet">
                         <protocol>telnet</protocol>
                         <param name="hostname">192.168.1.250</param>
                         <param name="port">23</param>
                         <param name="typescript-path">/usr/local/tomact/webapps/guacamole/media/${GUAC_DATE}</param> 
                         <param name="create-typescript-path">true</param>
                         <param name="typescript-name">c0601d1e-a5a6-42d4-9fca-9d310817f95b</param>

                 </connection>
                 <connection name="Windows">
                         <protocol>rdp</protocol>
                         <param name="hostname">192.168.1.249</param>
                         <param name="port">3389</param>
                         <param name="username">Administrator</param>
                         <param name="password">Topmay2011</param>
                         <param name="security">nla</param>
                         <param name="ignore-cert">true</param>

                         <param name="client-name">Guacamole1</param>
                         <param name="console">true</param>
                         <param name="server-layout">en-us-qwerty</param>

                         <param name="recording-path">/usr/local/tomact/webapps/guacamole/media/${GUAC_DATE}</param>
                         <param name="create-recording-path">true</param>
                         <param name="recording-name">c0601d1e-a5a6-42d4-9fca-9d310817f95b</param>
                  </connection>

       </authorize>

<!-- 第二个登录用户可开启md5 认证 -->
       <authorize 
            username="administrator"
            password="6df0461533321ec04e4ff38b86b6a986"
            encoding="md5">

                         <protocol>rdp</protocol>
                         <param name="hostname">192.168.1.249</param>
                         <param name="port">3389</param>
                         <!--如远端服务器开启了网络认证需要加上这四个参数，账号密码必须在前！ -->
                         <param name="username">Administrator</param>
                         <param name="password">Topmay2011</param>
                         <param name="security">nla</param>
                         <param name="ignore-cert">true</param>
                         
                         <param name="client-name">Guacamole1</param>
                         <param name="console">true</param>
                         <param name="server-layout">en-us-qwerty</param>

                         <!-- 实时录像 前提要有这个目录！-->
                         <param name="recording-path">/usr/local/tomact/webapps/guacamole/video/</param>
                         <param name="create-recording-path">true</param>
                         <param name="recording-name">Windows</param>
       </authorize>

</user-mapping>

```

#### 浏览器访问 http://{您的服务器IP地址}:8080/guacamole/

#### 使用mysql 扩展认证用户

```
vi /etc/guacamole/guacamole.properties

####guacamole.properties####
guacd-hostname: localhost
guacd-port:     4822
# user-mapping: /etc/guacamole/user-mapping.xml

# MySQL properties
mysql-hostname: localhost
mysql-port: 3306
mysql-database: guacamole_db
mysql-username: guacamole
mysql-password: guacamole
####guacamole.properties####
```

##### 安装mysql5.5服务 [mysql安装向导](https://blog.csdn.net/petrel2015/article/details/78822466)

```
wget http://dev.mysql.com/get/mysql57-community-release-el7-10.noarch.rpm
yum -y install mysql57-community-release-el7-10.noarch.rpm
vi /etc/yum.repos.d/mysql-community.repo   # 修改内容启用5.5
yum -y install mysql-server
systemctl enable mysqld
systemctl start mysqld
systemctl status mysqld
```

*  下载guacamole-auth-jdbc 扩展驱动

```
mkdir -p /etc/guacamole/sqlauth/
cd /etc/guacamole/sqlauth/
wget http://apache.org/dyn/closer.cgi?action=download&filename=guacamole/0.9.14/binary/guacamole-auth-jdbc-0.9.14.tar.gz
tar -zxvf guacamole-auth-jdbc-0.9.14.tar.gz
cp guacamole-auth-jdbc-0.9.14/mysql/guacamole-auth-jdbc-mysql-0.9.14.jar /etc/guacamole/extensions/

```

* 下载mysql jdbc 扩展驱动

[mysql-jdbc](https://dev.mysql.com/downloads/connector/j/)
```
cd /etc/guacamole/sqlauth/
wget http://ftp.ntu.edu.tw/MySQL/Downloads/Connector-J/mysql-connector-java-5.1.46.tar.gz
tar -zxvf mysql-connector-java-5.1.46.tar.gz
cp mysql-connector-java-5.1.46/mysql-connector-java-5.1.46-bin.jar /etc/guacamole/lib

```

* 创建数据库

```
mysql

mysql> CREATE DATABASE guacamole_db;
mysql> CREATE USER 'guacamole'@'localhost' IDENTIFIED BY 'guacamole';
mysql> GRANT SELECT,INSERT,UPDATE,DELETE ON guacamole_db.* TO 'guacamole'@'localhost';
mysql> FLUSH PRIVILEGES;
mysql> quit
```

* 导入sql文件到数据库

```
cat /etc/guacamole/sqlauth/guacamole-auth-jdbc-0.9.14/mysql/schema/*.sql | mysql guacamole_db
```

* 重启tomcat 默认账号"guacadmin"密码 "guacadmin"

* 浏览器访问 http://{您的服务器IP地址}:8080/guacamole/

* 日志查询

```
cat /usr/local/tomcat/logs/catalina.out
```


###Nginx反代  看你自己需求

```
yum install nginx -y

systemctl start nginx.service  #启动
systemctl enable nginx.service #开机自启
```

* 代理配置文件

```
vi /etc/nginx/nginx.conf

location /guacamole/ {
    proxy_pass http://HOSTNAME:8080/guacamole/;
    proxy_buffering off;
    proxy_http_version 1.1;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection $http_connection;
    access_log off;
}
```
