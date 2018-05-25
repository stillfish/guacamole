package org.apache.guacamole.net.example;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleResourceNotFoundException;
import org.apache.guacamole.GuacamoleSecurityException;
import org.apache.guacamole.net.GuacamoleSocket;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.apache.guacamole.servlet.GuacamoleHTTPTunnelServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
QQ:1067623902
2018/05/25
 */
public class TutorialGuacamoleTunnelServlet extends GuacamoleHTTPTunnelServlet {
    private final Logger logger = LoggerFactory.getLogger(TutorialGuacamoleTunnelServlet.class);

    @Override
    protected GuacamoleTunnel doConnect(HttpServletRequest request)
            throws GuacamoleException {
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        String time= (String) dateFormat.format(date);
        Map<String,String> map=new HashMap<String,String>();
        try {
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
            servletFileUpload.setHeaderEncoding("UTF-8");
            List<FileItem> list = servletFileUpload.parseRequest(request);
            for (FileItem fileItem : list) {
                if(fileItem.isFormField()){
                    String fieldName = fileItem.getFieldName();
                    String fieldValue = fileItem.getString("UTF-8");
                    map.put(fieldName,fieldValue);
                }
            }
        } catch (Exception e) {
            throw new GuacamoleSecurityException(e);
        }
        String protocol= (String)map.get("protocol");
        if (!protocol.equalsIgnoreCase("rdp") && !protocol.equalsIgnoreCase("vnc") &&
                !protocol.equalsIgnoreCase("ssh") && !protocol.equalsIgnoreCase("telnet")){
            this.logger.debug("error:method");
            throw new GuacamoleSecurityException("error:method");
        }
        String typescript_name= (String)map.get("typescript-name");
        if(typescript_name == null || typescript_name.length() == 0) {
            this.logger.debug("error:typescript_name is null");
            throw new GuacamoleResourceNotFoundException("error:typescript_name is null");
        }
        String recording_name= (String)map.get("recording-name");
        if(recording_name == null || recording_name.length() == 0) {
            this.logger.debug("error:recording_name is null");
            throw new GuacamoleResourceNotFoundException("error:recording_name is null");
        }
        String hostname= (String)map.get("hostname");
        String port= (String)map.get("port");
        String username= (String)map.get("username");
        String password= (String)map.get("password");


        // Create our configuration
        //详细参数设置及说明  http://guacamole.apache.org/doc/gug/configuring-guacamole.html
        GuacamoleConfiguration config = new GuacamoleConfiguration();

        config.setProtocol(protocol);
        config.setParameter("hostname", hostname);
        config.setParameter("port", port);
        config.setParameter("username", username);
        config.setParameter("password", password);
        config.setParameter("typescript-path", "/usr/share/nginx/html/demo/media/"+time);
        config.setParameter("create-typescript-path", "true");
        config.setParameter("typescript-name", typescript_name);

        config.setParameter("recording-path", "/usr/share/nginx/html/demo/media/"+time);
        config.setParameter("create-recording-path", "true");
        config.setParameter("recording-name", recording_name);

        String font_name= (String)map.get("font-name");
        if(font_name != null && font_name.length() != 0) {
            config.setParameter("font-name", font_name);
        }
        switch (protocol){
            case "rdp":
                String ignore= (String)map.get("ignore");
                if(ignore == null || ignore.length() == 0) {
                    ignore="true";
                }
                config.setParameter("ignore-cert", ignore);
                String security= (String)map.get("security");
                if(security == null || security.length() == 0) {
                    security="nla";
                }
                config.setParameter("security", security);
                break;
            case "vnc":
                break;
            case "ssh":
                break;
            case "telnet":
                break;
        }
        // Connect to guacd - everything is hard-coded here.
        //创建guac进程  hostname:目标ip  port:目标端口
        GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
                new InetGuacamoleSocket("localhost", 4822),
                config
        );
        return new SimpleGuacamoleTunnel(socket);
    }
}