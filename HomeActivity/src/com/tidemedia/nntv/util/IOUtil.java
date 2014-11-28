package com.tidemedia.nntv.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
	public static final String UTF8 = "UTF-8";

	public static void save(String filePath, String content) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			fos.write(content.getBytes(UTF8));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fos);
		}
	}
	
	public static String readContent(String filePath) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
			return toString(fis, UTF8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			close(fis);
		}
	}
	
	public static String toString(InputStream is, String charset) throws IOException {
		if (is == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = is.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		return new String(baos.toByteArray(), charset);
	}
	
	public static void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
