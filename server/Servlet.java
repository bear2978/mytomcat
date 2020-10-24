package mytomcat.server;

import mytomcat.http.HttpRequest;
import mytomcat.http.HttpResponse;

/**
 * 抽象为一个父类
 * @author Administrator
 */
public abstract class Servlet {
    // 方法分发
    public void service(HttpRequest req, HttpResponse resp) throws Exception{
        if("get".equalsIgnoreCase(req.getMethod())){
            this.doGet(req,resp);
        }else if("post".equalsIgnoreCase(req.getMethod())){
            this.doPost(req,resp);
        }
    }

    public abstract void doGet(HttpRequest req, HttpResponse resp) throws Exception;

    public abstract void doPost(HttpRequest req, HttpResponse resp) throws Exception;
}