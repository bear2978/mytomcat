package mytomcat.server;

import mytomcat.http.Dispatcher;
import mytomcat.util.CloseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    private ServerSocket serverSocket;

    // 最大等待时间,默认10秒
    private long timeout = 10000;

    // 服务器是否正常运行的标志
    private static boolean isNormal=true;

    /**
     * 启动服务器方法,默认8080端口
     */
    public WebServer() {
        this(8080);
    }

    public WebServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("connected to server,port："+port);
        } catch (IOException e) {
            isNormal = false;
            e.printStackTrace();
        }
    }

    /**
     * 启动服务器
     */
    public void start() {
        try {
            while (isNormal){
                Socket client = serverSocket.accept();
                long now = System.currentTimeMillis();
                // 一个请求一个线程
                new Thread(new Dispatcher(client)).start();
/*                while (true){
                    long end= System.currentTimeMillis();
                    if(end-now >= timeout){
                        // 超时响应500
                        Response resp=new Response(client.getOutputStream());
                        resp.sendRedirect("500.html");
                        resp.pushToClient(500);
                    }
                }
*/
            }
        } catch (IOException e) {
            isNormal = false;
            stop();
            e.printStackTrace();
        }
    }

    /**
     * 停止服务器方法
     */
    public void stop() {
        System.out.println("Disconnected from server");
        CloseUtil.close(serverSocket);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
