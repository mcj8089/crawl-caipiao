package test.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;

import test.config.Memory;

public class IpThread extends Thread {
	
	// 代理IP的API接口，需要从 https://www.data5u.com 获取
	String apiUrl = Memory.apiUrl;
	long sleepMs = 5000;
	
	public IpThread() {}
	
	public IpThread(long sleepMs) {
		this.sleepMs = sleepMs;
	}

	public void run() {
		while( Memory.useProxyIp ) {
			try {
				java.net.URL url = new java.net.URL(apiUrl);
				
				// 	LogUtil.print( apiUrl );
				
		    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		    	connection.setConnectTimeout(3000);
		    	connection = (HttpURLConnection)url.openConnection();
		    	
		        InputStream raw = connection.getInputStream();  
		        InputStream in = new BufferedInputStream(raw);  
		        byte[] data = new byte[in.available()];
		        int bytesRead = 0;  
		        int offset = 0;  
		        while(offset < data.length) {  
		            bytesRead = in.read(data, offset, data.length - offset);  
		            if(bytesRead == -1) {  
		                break;  
		            }  
		            offset += bytesRead;  
		        }  
		        in.close();  
		        raw.close();
				String[] res = new String(data, "UTF-8").split("\n");
				
				if(new String(data, "UTF-8").indexOf("服务已经到期") != -1) {

					throw new Exception(" 请到http://www.data5u.com获取最新的代理IP-API接口，或者修改Memory.useProxyIp=false ");
					
				}
				
				CrawlerUtil.ipportList = Arrays.asList(res);
				
			} catch (Exception e) {
				System.err.println("获取代理IP出错：" + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
			
			try {
				Thread.sleep(sleepMs);
			} catch (InterruptedException eX) {
				eX.printStackTrace();
			}
		}
	
	}
}