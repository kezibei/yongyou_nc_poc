package test;


public class Test {
	public static String url;
    public static void main(String[] args) throws Exception {
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
    		String servleturl = url+Expliot.getServleturl(servletname);
    		new Expliot(url, servletname, dnslog);
    		
    	
    	} else {
    		System.out.println("请输入 java -jar ncpoc.jar http://target.com\n"
    				+ "或者java -jar ncpoc.jar http://target.com Servlet xxx.dnslog.cn\n"
    				+ "目前支持的Servlet如下\n"
    				+ "MonitorServlet\n"
    				+ "MxServlet\n"
    				+ "XbrlPersistenceServlet\n"
    				+ "FileReceiveServlet\n"
    				+ "DownloadServlet\n"
    				+ "UploadServlet\n"
    				+ "DeleteServlet\n"
    				+ "ActionHandlerServlet\n"
    				+ "ServiceDispatcherServlet\n");
		}

    }
}
