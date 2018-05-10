# 一、文件传输

Guacamole可以通过SFTP提供文件传输，即使不是通过SSH访问远程桌面。

| 参数 | 说明 | RDP | VNC | SSH |
| -------- | -------- | -------- | -------- | -------- |
| enable-sftp | 是否应启用文件传输。如果设置为“true”，将允许用户使用SFTP从指定的服务器上传或下载文件。如果省略，SFTP将被禁用。 | 支持 | 支持 | 支持 |
| sftp-directory | 上传文件的目录，如果它们被简单地拖放，否则缺少特定的上传位置。此参数是可选的。如果省略，将使用提供SFTP的SSH服务器的默认上传位置。 | 支持 | 支持 |  |
| sftp-hostname | 托管SFTP的服务器的主机名或IP地址。此参数是可选的。如果省略，将使用该hostname参数指定的VNC/RDP服务器的主机名 。 | 支持 | 支持 |  |
| sftp-passphrase | 用于解密私钥以用于公钥认证的密码。如果私钥不需要密码，则不需要此参数。 | 支持 | 支持 |  |
| sftp-password | 使用指定SSH服务器进行SFTP认证时使用的密码。 | 支持 | 支持 |  |
| sftp-port | 提供SFTP的SSH服务器正在监听的端口，通常为22.此参数是可选的。如果省略，将使用标准端口22。 | 支持 | 支持 |  |
| sftp-private-key | 用于公钥认证的私钥的全部内容。如果未指定此参数，则不会使用公钥认证。私钥必须采用OpenSSH格式，由OpenSSH ssh-keygen实用程序生成。 | 支持 | 支持 |  |
| sftp-username | 用于连接SFTP的指定SSH服务器时进行身份验证的用户名。对于VNC，此参数是必需的。如果为RDP连接指定了用户名，则此参数是可选的。如果省略，username 将使用为参数提供的值。 | 支持 | 支持 |  |

#### Notice:

Guacamole包括 guacctl实用程序，用于通过SSH连接在用户端运行SSH服务器时控制文件下载和上传。

# 二、 会话记录

#### 1.图形化会话记录

VNC、RDP、SSH、Telnet会话可以图形化记录。这些记录采取Guacamole协议转储的形式，并自动记录到指定的目录。使用guacamole-server提供的guacenc程序可以将记录转换成普通的视频流。

例如，NAME要从录音“ NAME” 生成一个名为“ .m4v” 的视频，您可以运行：

 

$ guacenc /path/to/recording/NAME

| 参数 | 说明 |
| -------- | -------- |
| recording-path | 应创建屏幕录制文件的目录。如果需要创建图形记录，则需要此参数。指定此参数可以进行图形屏幕录制。如果省略此参数，则不会创建图形录制。 |
| create-recording-path | 如果设置为“true”，则该recording-path参数指定的目录 如果尚不存在，将自动创建。将只创建路径中的最终目录 - 如果路径之前的其他目录不存在，则自动创建将失败，并会记录错误。此参数是可选的。默认情况下，该recording-path参数指定的目录 将不会自动创建，并且尝试在不存在的目录中创建录像将被记录为错误。如果启用了图形记录功能，此参数才有效果。如果recording-path未指定，图形会话记录将被禁用，此参数将被忽略 |
| recording-name | 用于任何创建录音的文件名。 此参数是可选的。如果省略，将使用值“录制”。如果启用了图形记录功能，此参数才有效果。如果recording-path未指定，图形会话记录将被禁用，此参数将被忽略。 |


#### 2.文本会话记录（打字搞）

SSH、Telnet会话的完整原始文本内容（包括定时信息）可以自动记录到指定的目录。该记录也称为typescript，将被写入由typescript-path以下 目录指定的目录中的两个文件NAME，其中包含原始文本数据NAME.timing，其中包含定时信息，NAME该typescript-name参数提供的值在哪里。

此格式与标准UNIX 脚本命令使用的格式兼容 ，并且可以使用scriptreplay重播 （如果已安装）。例如，要重播名为“ NAME” 的打字机，您可以运行：

$ scriptreplay NAME.timing NAME

| 参数 | 说明 |
| -------- | -------- |
| typescript-path | 应该创建typescript文件的目录。如果需要记录打字稿，则需要此参数。指定此参数可以进行录音。如果省略此参数，则不会记录任何打字稿。 |
| create-typescript-path | 如果设置为“true”，则该typescript-path参数指定的目录 如果尚不存在，将自动创建。将只创建路径中的最终目录 - 如果路径之前的其他目录不存在，则自动创建将失败，并会记录错误。此参数是可选的。默认情况下，该typescript-path参数指定的目录将不会自动创建，并且将尝试将不存在目录中的typescript记录为错误。如果启用了录音录音，此参数才有效果。如果typescript-path没有指定，则会禁用录音，并忽略此参数。 |
| typescript-name | 确定文件名的数据和时间文件时使用的基本文件名。此参数是可选的。如果省略，将使用值“typescript”。每个打字稿包括其通过指定的目录中创建的两个文件typescript-path：NAME，其中包含的原始文本数据，并且NAME.timing，其中包含定时信息，其中NAME是提供了一种用于所述的值typescript-name 的参数。如果启用了录音录音，此参数才有效果。如果typescript-path没有指定，则会禁用录音，并忽略此参数。 |

