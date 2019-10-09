## Centos7 部署 guacamole1.0.0

* 关闭防火墙

```
systemctl stop firewalld.service
systemctl disable firewalld.service
vi /etc/selinux/config
SELINUX=disabled
```

* 安装系统开发环境

```
yum -y groupinstall "Development Tools" 
```

* 安装guacamole 依赖
```
yum install -y cairo-devel libjpeg-turbo-devel libpng-devel uuid-devel ffmpeg-devel freerdp-devel pango-devel libssh2-devel libtelnet-devel libvncserver-devel pulseaudio-libs-devel openssl-devel libvorbis-devel libwebp-devel 
```

* ffmpeg centos7 默认源没有改用其他源
```
yum install -y epel-release 
rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro
rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm
yum -y install ffmpeg-devel 
```

* freerdp-devel 开发库默认更新至2.0，但guacamole1.0.0 仍使用 1.2 。
[https://koji.fedoraproject.org/koji/buildinfo?buildID=1383747](https://koji.fedoraproject.org/koji/buildinfo?buildID=1383747)
```
rpm -ivh https://kojipkgs.fedoraproject.org//packages/freerdp1.2/1.2.0/13.el7/x86_64/freerdp1.2-1.2.0-13.el7.x86_64.rpm
rpm -ivh https://kojipkgs.fedoraproject.org//packages/freerdp1.2/1.2.0/13.el7/x86_64/freerdp1.2-devel-1.2.0-13.el7.x86_64.rpm
rpm -ivh https://kojipkgs.fedoraproject.org//packages/freerdp1.2/1.2.0/13.el7/x86_64/freerdp1.2-debuginfo-1.2.0-13.el7.x86_64.rpm
```

* 编译安装guacamole-server
[http://guacamole.apache.org/doc/gug/installing-guacamole.html](http://guacamole.apache.org/doc/gug/installing-guacamole.html)

```
wget http://archive.apache.org/dist/guacamole/1.0.0/source/guacamole-server-1.0.0.tar.gz
tar -zxvf guacamole-server-1.0.0.tar.gz
cd guacamole-server-1.0.0
./configure --with-init-dir=/etc/init.d
make && make install
ldconfig

/sbin/chkconfig guacd on  #设置开机自启动
```

* 配置环境变量

```
cd ~ 
vim /etc/bashrc

export GUACAMOLE_HOME=/etc/guacamole

source /etc/bashrc

echo $GUACAMOLE_HOME
```

* 配置guacamole

```
mkdir -p /etc/guacamole/
mkdir -p /etc/guacamole/extensions
mkdir -p /etc/guacamole/lib
mkdir -p /etc/guacamole/sqlauth/

vi /etc/guacamole/guacamole.properties

####guacamole.properties####
guacd-hostname: localhost
guacd-port:     4822
# user-mapping: /etc/guacamole/user-mapping.xml

# MySQL properties
mysql-hostname: localhost
mysql-port: 3306
mysql-database: guacamole_db
mysql-username: guacamole
mysql-password: guacamole
####guacamole.properties####
```

* 开启guacamole debug 日志
```
vi /etc/guacamole/logback.xml

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
```

* guacmole安装数据库扩展驱动

[http://guacamole.apache.org/doc/gug/jdbc-auth.html](http://guacamole.apache.org/doc/gug/jdbc-auth.html)

[http://ftp.ntu.edu.tw/MySQL/Downloads/Connector-J/](http://ftp.ntu.edu.tw/MySQL/Downloads/Connector-J/)

```
yum -y install java-1.8.0-openjdk*

cd /etc/guacamole/sqlauth
wget http://apache.org/dyn/closer.cgi?action=download&filename=guacamole/1.0.0/binary/guacamole-auth-jdbc-1.0.0.tar.gz
tar -zxvf guacamole-auth-jdbc-1.0.0.tar.gz
cp guacamole-auth-jdbc-1.0.0/mysql/guacamole-auth-jdbc-mysql-1.0.0.jar /etc/guacamole/extensions/


wget http://ftp.ntu.edu.tw/MySQL/Downloads/Connector-J/mysql-connector-java-5.1.46.tar.gz
tar -xzvf mysql-connector-java-5.1.46.tar.gz
cp mysql-connector-java-5.1.46/mysql-connector-java-5.1.46-bin.jar /etc/guacamole/lib/
```

* java 环境变量设置
```
vi /etc/profile (尾部)

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.212.b04-0.el7_6.x86_64
export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$PATH:$JAVA_HOME/bin

source /etc/profile
```

* 创建数据库

```
mysql -uroot -p

mysql> CREATE DATABASE guacamole_db;
mysql> CREATE USER 'guacamole'@'localhost' IDENTIFIED BY 'guacamole';
mysql> CREATE USER 'guacamole'@'127.0.0.1' IDENTIFIED BY 'guacamole';
mysql> GRANT SELECT,INSERT,UPDATE,DELETE ON guacamole_db.* TO 'guacamole'@'localhost';
mysql> GRANT SELECT,INSERT,UPDATE,DELETE ON guacamole_db.* TO 'guacamole'@'127.0.0.1';
mysql> FLUSH PRIVILEGES;
mysql> quit

cat /etc/guacamole/sqlauth/guacamole-auth-jdbc-1.0.0/mysql/schema/*.sql | mysql -uroot -p{密码} guacamole_db
```

* 安装tomcat8

```
wget http://mirror.bit.edu.cn/apache/tomcat/tomcat-8/v8.5.34/bin/apache-tomcat-8.5.34.tar.gz
tar -zxvf apache-tomcat-8.5.34.tar.gz -C /usr/local/
cd /usr/local/
mv apache-tomcat-8.5.34 tomcat
```

* 配置 systemctl 文件

```
vi /lib/systemd/system/tomcat.service

[Unit]
Description=tomcat
After=network.target
 
[Service]
Type=oneshot
ExecStart=/usr/local/tomcat/bin/startup.sh
ExecStop=/usr/local/tomcat/bin/shutdown.sh
ExecReload=/bin/kill -s HUP $MAINPID
RemainAfterExit=yes
 
[Install]
WantedBy=multi-user.target


chmod 754 /lib/systemd/system/tomcat.service 
```

* 参数设置

```
#启动服务 
systemctl start tomcat.service   
#关闭服务   
systemctl stop tomcat.service   
#开机启动   
systemctl enable tomcat.service
```


* 直接使用官方编译完的war包
```
wget https://mirrors.tuna.tsinghua.edu.cn/apache/guacamole/1.0.0/binary/guacamole-1.0.0.war
cp guacamole-1.0.0.war /usr/local/tomcat/webapps/guacamole.war
```

* 重启 tomcat 访问 默认账号"guacadmin"密码 "guacadmin"

[http://IP:8080/guacamole/](http://IP:8080/guacamole/)


* nginx 反代 guacamole

```
    location /guacamole/ {
    proxy_pass http://localhost:8080/guacamole/;
    proxy_buffering off;
    proxy_http_version 1.1;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection $http_connection;
    access_log off;
   }

```


* 字体设置

```
mkdir -p /usr/share/fonts/chinese
# 将 simhei.ttf(黑体) 与 simsun.ttc(宋体) 拷贝至此文件夹
ttmkfdir -e /usr/share/X11/fonts/encodings/encodings.dir

vi /etc/fonts/fonts.conf
<dir>/usr/share/fonts/chinese</dir>
fc-cache
fc-list
```
