package mytomcat.util;

/**
 * 关闭资源工具
 */
public class CloseUtil {

    public synchronized static void close(Object... obj) {
        for (Object temp : obj) {
            if (temp != null) {
                try {
                    ((AutoCloseable) temp).close();
                } catch (Exception e) {
                    System.err.println("资源关闭异常");
                }
            }
        }
    }
}
