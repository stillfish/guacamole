# 一、Guacamole介绍

Guacamole是一个提供了基于HTML5 web应用程序的远程桌面代理服务器。通过使用Guacamole服务器，我们很轻松的在浏览器上远程访问Guacamole代理的主机。

![guacamole架构](https://github.com/TelDragon/guacamole/blob/master/docs/_static/img/guac-arch.png?raw=true)

我们可以在浏览器访问Guacamole页面的时候，此时，浏览器会通过HTTP使用Guacamole协议与Guacamole 服务器中的Web服务器进行连接。Guacamole Web应用会从用户的请求中读取Guacamole协议，并将其转发给guacd（本地Guacamole代理）。Guacd根据web 应用转发过来的Guacamole协议来代替用户连接到远程桌面服务器。在Guacamole Web应用与guacd进行通信的时候，两者均不需要知道实际使用的远程桌面协议是什么，即协议不可知性。

在Guacamole的组成中主要包含如下三部分：

#### Guacamole协议是用于远程显示和事件传输的协议，不实现特定的桌面环境支持，实现了现有远程桌面的超集。
#### guacd是Guacamole的核心，guacd也不了解任何具体的远程桌面协议，而是实现了通过web应用转发的Guacamole协议来确定哪些协议需要加载，哪些参数必须传递给它。
#### web应用程序是Guacamole与用户进行交互的部分。Apache提供了基于Java的编写的Web应用程序，但是这并不代表Guacamole 只支持Java。Guacamole是一个API。

# 测试界面

在浏览器中打开Guacamole Web应用，地址为http://Guacamole_Server_IP:8080/guacamole

Guacamole登录界面
![guacamole登录界面](https://github.com/TelDragon/guacamole/blob/master/docs/_static/img/Guacamole00.png?raw=true)

下图为guacamole登录后显示的页面。
![guacamole登陆后的界面](https://github.com/TelDragon/guacamole/blob/master/docs/_static/img/Guacamole01.png?raw=true)

下图是“CentOS 7 TigerVNC”连接结果，主要测试TigerVNC,采用VNC协议。
![guacamole_VNC](https://github.com/TelDragon/guacamole/blob/master/docs/_static/img/Guacamole02.png?raw=true)

下图是“Windows 10(Test)”连接的结果展示，测试RDP。
![guacamole_windows](https://github.com/TelDragon/guacamole/blob/master/docs/_static/img/Guacamole03.png?raw=true)

下图是"CentOS SSH"连接结果，测试SSH。
![guacamole_ssh](https://github.com/TelDragon/guacamole/blob/master/docs/_static/img/Guacamole04.png?raw=true)

下图是“Ubuntu x11vnc”，测试x11vnc。
![guacamloe_ubuntu](https://github.com/TelDragon/guacamole/blob/master/docs/_static/img/Guacamole05.png?raw=true)

