package payloads;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class Gzipbs {
	
	public static byte[] tobytes(Object object) throws Exception {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bs);
        oos.writeObject(object);
        byte[] buf = bs.toByteArray();
        oos.flush();
		return buf;
		
	}
}
