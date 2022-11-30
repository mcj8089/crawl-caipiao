package test.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 将图片进行base64处理
 */
public class ImageBase64Util {

	/**
	 * @Description: 将base64编码字符串转换为图片
	 * @Author:
	 * @CreateTime:
	 * @param imgStr base64编码字符串
	 * @param path   图片路径-具体到文件
	 * @return
	 */
	public static boolean generateImage(String imgStr, String path) {
		if (imgStr == null) {
			return false;
		}
		
		File file = new File(path);
		if( !file.getParentFile().exists() ) {
			file.getParentFile().mkdirs();
		}
		
		BASE64Decoder decoder = new BASE64Decoder();
		OutputStream out = null;
		try {
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e2) {
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {
		System.out.println(getLocalImageStr("C:\\Users\\Administrator\\Desktop\\执照.png"));
	}

	/**
	 * @Description: 根据图片地址转换为base64编码字符串
	 * @Author:
	 * @CreateTime:
	 * @return
	 */
	public static String getLocalImageStr(String imgFile) {
		InputStream inputStream = null;
		byte[] data = null;
		try {
			inputStream = new FileInputStream(imgFile);
			data = new byte[inputStream.available()];
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return getLocalImageStr(data);
	}

	/**
	 * @Description: 根据图片地址转换为base64编码字符串
	 * @Author:
	 * @CreateTime:
	 * @return
	 */
	public static String getLocalImageStr(byte[] data) {
		// 加密
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data).replaceAll("\n", "").replaceAll("\r", "");
	}

	public static String getRemoteImageStr(String urlLink, String saveUrl) {
		try {
			downloadFile(urlLink, saveUrl, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			return getLocalImageStr(saveUrl);
		} finally {
			File savedFile = new File(saveUrl);
			if (savedFile.exists()) {
				savedFile.deleteOnExit();
			}
		}
	}

	/**
	 * 计算base64图片的大小
	 * 
	 * @param base64
	 * @return 文件大小 kb
	 */
	public static Integer imageSize(String base64) {
		// 找到等号，把等号去掉
		if (base64.indexOf("=") > 0) {
			base64 = base64.substring(0, base64.indexOf("="));
		}
		Integer strLength = base64.length(); // 原来的字符流大小，单位为字节
		Integer size = strLength - (strLength / 8) * 2; // 计算后得到的文件流大小，单位为字节
		return size / 1024;
	}

	public static void downloadFile(String fileUrl, String saveUrl, Map<String, String> headerMap) throws Exception {
		HttpURLConnection httpUrl = null;
		byte[] buf = new byte[1024];
		int size = 0;

		LogUtil.print("下载图片：" + fileUrl);
		// 下载的地址
		URL url = new URL(fileUrl);

		if (headerMap != null) {
			if (headerMap.containsKey("proxyip")) {
				String ipport = headerMap.get("proxyip");
				java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
						new InetSocketAddress((ipport.split(":"))[0], Integer.parseInt((ipport.split(":"))[1])));
				httpUrl = (HttpURLConnection) url.openConnection(proxy);
			} else {
				// 支持http特定功能
				httpUrl = (HttpURLConnection) url.openConnection();
			}

			for (String key : headerMap.keySet()) {
				httpUrl.addRequestProperty(key, headerMap.get(key));
			}
		} else {
			// 支持http特定功能
			httpUrl = (HttpURLConnection) url.openConnection();
		}

		httpUrl.setConnectTimeout(10 * 1000);
		httpUrl.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36 SE 2.X MetaSr 1.0");
		httpUrl.connect();

		// 缓存输入流,提供了一个缓存数组,每次调用read的时候会先尝试从缓存区读取数据
		BufferedInputStream bis = new BufferedInputStream(httpUrl.getInputStream());
		// 输出流,输出到新的地址上面
		FileOutputStream fos = new FileOutputStream(saveUrl);
		while ((size = bis.read(buf)) != -1) {
			fos.write(buf, 0, size);
		}
		// 记得及时释放资源
		fos.flush();
		fos.close();
		bis.close();

		httpUrl.disconnect();
	}

}
