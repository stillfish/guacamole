# API文档
Guacamole提供了几种API用于在现有基础设施和应用中扩展和嵌入Guacamole 。Guacamole代码库的大部分实际上构成了Guacamole核心; 名为“Guacamole”的Web应用程序仅仅利用这个核心，将其包装在一个不错的用户界面和简单的认证方案中。

您可以轻松添加其他协议支持，支持其他身份验证方法，或使用Guacamole核心API和相关堆栈创建其他HTML5远程桌面应用程序。

## C（libguac）
用于扩展和开发Guacamole的C API是libguac。Guacamole项目生成的所有本地组件都与该库链接，并且该库为扩展这些本地组件的本地功能（通过实现客户端插件）提供了通用基础。

libguac主要用于开发客户端插件，如libguac-client-vnc或libguac-client-rdp，或者用于开发支持Guacamole协议的代理，如guacd。本章旨在概述如何使用libguac，以及如何将它用于与Guacamole协议的一般通信。

[查看libguac文档](http://guacamole.apache.org/doc/libguac)

## Java(guacamole-common)
Guacamole项目提供的Java API称为guacamole-common。它提供了在由guacamole-common-js提供的JavaScript客户端与本地代理守护进程guacd之间隧道化数据的基本方法。还提供了其他类，它们处理Guacamole协议并从guacamole.properties中读取更容易，但通常，此库的目的是为了便于在JavaScript客户端和guacd之间创建自定义隧道。

[查看guacamole-common文档](http://guacamole.apache.org/doc/guacamole-common)

## JavaScript（guacamole-common-js）
Guacamole项目提供了一个JavaScript API，用于连接符合Guacamole设计的其他组件，例如使用libguac或guacamole-common的项目。这个API被称为guacamole-common-js。

guacamole-common-js提供了一个Guacamole客户端的JavaScript实现，以及用于从JavaScript获取协议数据的隧道机制以及Web应用程序的guacd或服务器端。

为方便起见，它还提供鼠标和键盘抽象对象，可将JavaScript鼠标，触摸和键盘事件转换为鳄梨酱可更容易地消化的一致数据。还包括为Guacamole网络应用程序开发的可扩展屏幕键盘。

[查看guacamole-common-js文档](http://guacamole.apache.org/doc/guacamole-common-js)

## 延伸 (guacamole-ext)
Guacamole-ext不是Guacamole项目提供的Java API的一部分，它是Guacamole Web应用程序使用的API的一个子集，在单独的项目中提供，可以编写扩展，特别是身份验证提供程序，以调整Guacamole很好地适应现有的部署。

[查看guacamole-ext文档](http://guacamole.apache.org/doc/guacamole-ext)
