package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class Dirsearch {
	public String url;
	public Dirsearch(String url) throws Exception {
		this.url = url;
		System.out.println("检测开始");
		String[] servlet = {
				"MonitorServlet",
				"MxServlet",
				"XbrlPersistenceServlet",
				"FileReceiveServlet",
				"DownloadServlet",
				"UploadServlet",
				"DeleteServlet",
				"ActionHandlerServlet",
				"ServiceDispatcherServlet"
				};
		for (int i = 0; i < servlet.length; i++) {
			curltest(Expliot.getServleturl(servlet[i]), servlet[i]);
		}
		curltest("servlet/~ic/bsh.servlet.BshServlet","BshServlet");
		curltest("servlet/~ic/ShowAlertFileServlet","ShowAlertFileServlet");
		curltest("uapws/pages/error.jsp?msg=<img%20src=1>","errorXSS");
		System.out.println("检测完毕");
	}
	

	
	private void curltest(String Servlet,String Servletname){
		try {
			curlexec(Servlet, Servletname);
		} catch (Exception e) {
			System.out.println("访问"+Servletname+"失败，请检测网络，或者存在waf");
		}
		
	}
	private void curlexec(String Servlet,String Servletname) throws Exception  {
        URL url = new URL(this.url+Servlet);
        trustAllHttpsCertificates();
        HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (Servletname.equals("ServiceDispatcherServlet")){
        	connection.setRequestMethod("POST");
        } else {
        	connection.setRequestMethod("GET");
        }
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.connect();
        if (connection.getResponseCode() == 200 | connection.getResponseCode() == 302 ) {
            InputStream is = (InputStream) connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
            }
        	if (Servletname.equals("BshServlet")) {
        		System.out.println("存在 BshServlet 请访问 "+this.url+Servlet);
			} else if (Servletname.equals("ShowAlertFileServlet")) {
				System.out.println("存在 ShowAlertFileServlet 302跳转为  "+connection.getHeaderField("Location"));
			} else if (Servletname.equals("errorXSS")) {
				if (sbf.indexOf("<img src=1>")!=-1) {
					System.out.println("存在 errorXSS 请访问"+this.url+"uapws/pages/error.jsp?msg=<script>alert(1)</script>");
				} else {
					System.out.println("不存在 "+Servletname);
				}
			} else {
				System.out.println("存在 "+Servletname);
			}
        } else {
        	System.out.println("不存在 "+Servletname);
        }
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
