package mytomcat.server;

import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class WebApp {

    private static ServletContext context;

    static{
        try {
            //获取解析工厂
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //获取解析器
            SAXParser sax = factory.newSAXParser();
            //指定xml+处理器
            WebHandler web = new WebHandler();
            sax.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("WEB-INF/web.xml"), web);

            //将list 转成Map
            context = new ServletContext();
            Map<String,String> servlet =context.getServlet();
            //servlet-name  servlet-class
            for(Entity entity:web.getEntityList()){
                servlet.put(entity.getName(), entity.getClz());
            }

            //url-pattern servlet-name
            Map<String,String> mapping =context.getMapping();
            for(Mapping map:web.getMappingList()){
                List<String> urls =map.getUrlPattern();
                for(String url:urls ){
                    mapping.put(url, map.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Servlet getServlet(String url) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        Servlet servlet = null;
        if((null == url)||(url.trim()).equals("")){
            return null;
        }
        // 根据字符串(完整路径)创建对象
        String name=context.getServlet().get(context.getMapping().get(url));
        if((null != name) && !(name.trim()).equals("")){
            // 确保空构造存在
            servlet = (Servlet)Class.forName(name).newInstance();
        }
        return servlet;
    }

}