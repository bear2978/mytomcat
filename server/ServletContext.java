package mytomcat.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文
 */
public class ServletContext {
    //为每一个servlet取个别名
    private Map<String,String> servlet ;

    private Map<String,String> mapping;

    public ServletContext(){
        servlet = new HashMap<>();
        mapping = new HashMap<>();
    }

    public Map<String, String> getServlet() {
        return servlet;
    }
    public void setServlet(Map<String, String> servlet) {
        this.servlet = servlet;
    }
    public Map<String, String> getMapping() {
        return mapping;
    }
    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

}