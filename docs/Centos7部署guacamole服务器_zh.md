## Centos7 部署 guacamole0.9.14

#### 关闭防火墙

```
# setenforce 0  # 可以设置配置文件永久关闭
# systemctl stop iptables.service
# systemctl stop firewalld.service
```

* 添加依赖库

```
yum -y groupinstall "Development Tools" 

yum install cairo-devel libjpeg-devel libpng-devel uuid-devel ffmpeg-devel freerdp-devel freerdp-plugins pango-devel libssh2-devel libtelnet-devel    libvncserver-devel pulseaudio-libs-devel openssl-devel libvorbis-devel libwebp-devel wget gedit  java-1.8.0-openjdk*
```

* ffmpeg centos7 默认源没有改用其他源

```
yum install -y epel-release 
rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro
rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm
yum -y install ffmpeg-devel libtelnet-devel
```

* 编译安装guacamole-server
[http://guacamole.apache.org/doc/gug/installing-guacamole.html](http://guacamole.apache.org/doc/gug/installing-guacamole.html)

```
wget https://mirrors.tuna.tsinghua.edu.cn/apache/guacamole/0.9.14/source/guacamole-server-0.9.14.tar.gz

yum -y install cairo-devel libjpeg-turbo-devel libjpeg-devel libpng-devel uuid-devel ffmpeg-devel freerdp-devel pango-devel libssh2-devel libtelnet-devel libvncserver-devel pulseaudio-libs-devel openssl-devel libvorbis-devel libwebp-devel

tar -zxvf guacamole-server-0.9.14.tar.gz
cd guacamole-server-0.9.14
./configure --with-init-dir=/etc/init.d
make && make install
ldconfig

/sbin/chkconfig guacd on  #设置开机自启动，根据需要
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

* guacmole安装数据库扩展驱动

[http://guacamole.apache.org/doc/gug/jdbc-auth.html](http://guacamole.apache.org/doc/gug/jdbc-auth.html)

[http://ftp.ntu.edu.tw/MySQL/Downloads/Connector-J/](http://ftp.ntu.edu.tw/MySQL/Downloads/Connector-J/)

```
yum -y install java-1.8.0-openjdk*

cd /etc/guacamole/sqlauth/
wget http://apache.org/dyn/closer.cgi?action=download&filename=guacamole/0.9.14/binary/guacamole-auth-jdbc-0.9.14.tar.gz
tar -zxvf guacamole-auth-jdbc-0.9.14.tar.gz
cp /etc/guacamole/sqlauth/guacamole-auth-jdbc-0.9.14/mysql/guacamole-auth-jdbc-mysql-0.9.14.jar /etc/guacamole/extensions/


wget http://ftp.ntu.edu.tw/MySQL/Downloads/Connector-J/mysql-connector-java-5.1.46.tar.gz
tar -zxvf mysql-connector-java-5.1.46.tar.gz
cp /etc/guacamole/sqlauth/mysql-connector-java-5.1.46/mysql-connector-java-5.1.46-bin.jar /etc/guacamole/lib
```

* 创建数据库

```
mysql -u root -p

mysql> CREATE DATABASE guacamole_db;
mysql> CREATE USER 'guacamole'@'localhost' IDENTIFIED BY 'guacamole';
mysql> CREATE USER 'guacamole'@'127.0.0.1' IDENTIFIED BY 'guacamole';
mysql> GRANT SELECT,INSERT,UPDATE,DELETE ON guacamole_db.* TO 'guacamole'@'localhost';
mysql> GRANT SELECT,INSERT,UPDATE,DELETE ON guacamole_db.* TO 'guacamole'@'127.0.0.1';
mysql> FLUSH PRIVILEGES;
mysql> quit

cd /etc/guacamole/sqlauth/guacamole-auth-jdbc-0.9.14/mysql
cat schema/*.sql | mysql -u root -p guacamole_db
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


* 直接使用编译包安装guacamole-client

```
wget https://mirrors.tuna.tsinghua.edu.cn/apache/guacamole/0.9.14/binary/guacamole-0.9.14.war
cp guacamole-0.9.14.war /usr/local/tomcat/webapps/guacamole.war
ln -s /etc/guacamole/guacamole.properties /usr/local/tomcat/.guacamole/    #软连接可以忽略
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
