package mytomcat.http;

import java.io.IOException;
import java.net.Socket;

import mytomcat.server.Servlet;
import mytomcat.server.WebApp;
import mytomcat.util.CloseUtil;
import mytomcat.util.FindFileUtil;

/**
 * 一个请求与响应,就一个此对象
 * @author 86185
 */
public class Dispatcher implements Runnable {
    // 通信Socket
    private Socket client;
    // 请求对象
    private HttpRequest req;
    // 响应对象
    private HttpResponse resp;
    // 响应状态码
    private int code = 200;

    public Dispatcher(Socket client) {
        this.client = client;
        try {
            req = new HttpRequest(client.getInputStream());
            resp = new HttpResponse(client.getOutputStream());
        } catch (IOException e) {
            this.code = 500;
        }
    }

    @Override
    public void run() {
        // 如果请求Url末端包含.,则表示访问的是资源 // 截取文件名
        String filename = req.getUrl() == null ? null : req.getUrl().substring(req.getUrl().lastIndexOf("/") + 1);
        String content;
        try {
            if (filename!=null&&filename.contains(".")) {
                try {
                    content = FindFileUtil.findFile(filename);
                }catch (IOException e){
                    this.code = 404;
                    content = FindFileUtil.findFile("404.html");
                }
                resp.println(content);
            } else {
                // 使用多态创建Servlet
                Servlet servlet = WebApp.getServlet(req.getUrl());
                if (null == servlet) {
                    //找不到处理的Servlet
                    this.code = 404;
                    content = FindFileUtil.findFile("404.html");
                    resp.println(content);
                } else {
                    // 访问Servlet则调用服务
                    servlet.service(req, resp);
                }
            }
            // 响应客户端请求
            resp.pushToClient(code);
        } catch (Exception e) {
            try {
                this.code = 500;
                content = FindFileUtil.findFile("500.html");
                resp.println(content + e.getMessage());
                resp.pushToClient(code);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        CloseUtil.close(client);
    }

}