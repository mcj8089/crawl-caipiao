package test.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.Jsoup;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * 采集基础类
 * @author Administrator
 *
 */
public class CrawlerUtil {
	
	static int DEFAULT_RETRY_TIME = 6;
	
	public static List<String> ipportList = new ArrayList<String>();

	public static String getAnProxyIp( ) {
		if( ipportList.size() > 0 ) {
			return ipportList.get((int) (ipportList.size() * Math.random())) ;
		}
		return null;
	}

	public synchronized static void writeToFile(String filename, String dir, String content) {
		try {
			System.err.println("写出到文件:" + dir + "/" + filename);
			
			File dirFile = new File(dir);
			if( !dirFile.exists() ) {
				dirFile.mkdirs();
			}
			
			File targetFile = new File(dir, filename);
			if( !targetFile.exists() ) {
				targetFile.createNewFile();
			}
			
			FileWriter writer = new FileWriter(targetFile, true);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("写出到文件出错:" + e.getMessage());
		}
	}
	
    public static String downloadFile(String fileUrl,String saveUrl, Map<String, String> headerMap, boolean useProxy) {
        HttpURLConnection httpUrl = null;
        byte[] buf = new byte[1024];
        int size = 0;
        try {
        	LogUtil.print("下载图片：" + fileUrl);
            //下载的地址
            URL url = new URL(fileUrl);
            
            if( headerMap != null ) {
            	if( useProxy ) {
            		String ipport = getAnProxyIp();
        			java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress((ipport.split(":"))[0], Integer.parseInt((ipport.split(":"))[1])));
        			httpUrl = (HttpURLConnection)url.openConnection(proxy);
            	} else {
            		//支持http特定功能
                    httpUrl = (HttpURLConnection) url.openConnection();
            	}
            	
            	for( String key : headerMap.keySet() ) {
            		httpUrl.addRequestProperty(key, headerMap.get(key));
            	}
            } else {
            	//支持http特定功能
                httpUrl = (HttpURLConnection) url.openConnection();
            }
            
            httpUrl.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36 SE 2.X MetaSr 1.0");
            httpUrl.connect();
            //缓存输入流,提供了一个缓存数组,每次调用read的时候会先尝试从缓存区读取数据
            BufferedInputStream bis = new BufferedInputStream(httpUrl.getInputStream());
            //输出流,输出到新的地址上面
            FileOutputStream fos = new FileOutputStream(saveUrl);
            while ((size = bis.read(buf)) != -1){
                fos.write(buf, 0, size);
            }
            //记得及时释放资源
            fos.close();
            bis.close();
            
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	if( httpUrl != null ) {
        		httpUrl.disconnect();
        	}
        }
        
        return null;
    }

	public static String getByJsoup(String url, boolean userProxyIp, Map<String, String> params, Map<String, String> headers) {
		
		String ipport = null;
    	if (userProxyIp) {
        	ipport = getAnProxyIp();
		}

		System.out.println("请求网址-" + url + ( ipport != null ? "  代理IP 【" + ipport + "】" : "" ));
		
    	if( params == null ) {
    		params = new HashMap<String, String>();
    	}
    	
    	org.jsoup.nodes.Document document = null;
    	if( StrUtil.isNotEmpty(ipport) ) {
    		java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress((ipport.split(":"))[0], Integer.parseInt((ipport.split(":"))[1])));
    		try {
				document = Jsoup.connect(url).timeout(50000).headers(headers).data(params).proxy(proxy).ignoreContentType(true).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} else {
    		try {
				document = Jsoup.connect(url).timeout(50000).headers(headers).data(params).ignoreContentType(true).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
        
        return document.html();
		
    }
	
	public static String doPost(String url, boolean userProxyIp, Map<String, String> params, Map<String, String> headers) throws Exception {
    	
    	HttpURLConnection connection = null;
    	URL link = new URL(url);
    	
    	if (userProxyIp) {
        	
        	String ipport = getAnProxyIp();
        	if (ipport != null) {
        		java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress((ipport.split(":"))[0], Integer.parseInt((ipport.split(":"))[1])));
            	connection = (HttpURLConnection)link.openConnection(proxy);
			}else {
				connection = (HttpURLConnection)link.openConnection();
			}
		} else {
			connection = (HttpURLConnection)link.openConnection();
		}

    	if( headers != null && headers.size() > 0  ) {
    		for( String key : headers.keySet() ) {
    			connection.addRequestProperty(key, headers.get(key));
    		}
    	}
     	connection.setUseCaches(false);
    	connection.setReadTimeout(10000);
    	connection.setDoOutput(true);
    	connection.setDoInput(true);
    	connection.setRequestMethod("POST");
    	connection.connect();
    	
        //POST请求
    	if( params != null && params.size() > 0 ) {
    		StringBuilder paramBuilder = new StringBuilder();
    		for( String key : params.keySet() ) {
    			paramBuilder.append(key).append("=").append(params.get(key)).append("&");
    		}
    		
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
            out.write(paramBuilder.toString());
            out.flush();
            out.close();   
    	}
    	
        String line = null;
        StringBuilder html = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        while((line = reader.readLine()) != null){
        	html.append(line);
        }
        
        return html.toString();
		
    }

	public static String getHtml(String url, boolean userProxyIp, boolean useJs, int timeout) {
		return getHtml(url, userProxyIp, useJs, timeout, null);
	}
	
	public static String getHtml(String url, boolean userProxyIp, boolean useJs, int timeout, boolean fllowRedirect) {
		return getHtml(url, userProxyIp, useJs, timeout, null);
	}
	
	public static String getHtml(String url, boolean userProxyIp, boolean useJs, int timeout, Map<String, String> headerMap, String ...ignoreJs) {
		return getHtmlAndCookie(url, userProxyIp, useJs, timeout, headerMap, true, DEFAULT_RETRY_TIME, "UTF-8")[0];
	}
	
	public static String getHtml(String url, boolean userProxyIp, boolean useJs, int timeout, Map<String, String> headerMap, String charset) {
		return getHtmlAndCookie(url, userProxyIp, useJs, timeout, headerMap, true, DEFAULT_RETRY_TIME, charset)[0];
	}
	
	public static String getHtml(String url, boolean userProxyIp, boolean useJs, int timeout, Map<String, String> headerMap, String charset, String ...ignoreJs) {
		return getHtmlAndCookie(url, userProxyIp, useJs, timeout, headerMap, true, DEFAULT_RETRY_TIME, charset)[0];
	}
	
	public static HtmlPage getHtmlPage(String url, boolean userProxyIp, boolean useJs, int timeout, Map<String, String> headerMap, String ...ignoreJs) {

		System.out.println("请求网址-" + url);
		System.out.println("请求参数-" + headerMap);
		
		HtmlPage htmlPage = null;
		
		BrowserVersion[] versions = {BrowserVersion.BEST_SUPPORTED, BrowserVersion.CHROME, BrowserVersion.FIREFOX, BrowserVersion.FIREFOX_78, BrowserVersion.INTERNET_EXPLORER, BrowserVersion.EDGE};
		WebClient client = new WebClient(versions[(int)(versions.length * Math.random())]);
		try {
			client.getOptions().setThrowExceptionOnFailingStatusCode(false);
			client.getOptions().setJavaScriptEnabled(useJs);
			client.getOptions().setCssEnabled(false);
			
			client.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
			client.getOptions().setThrowExceptionOnScriptError(false);
			client.getOptions().setTimeout(timeout);
			client.getOptions().setAppletEnabled(true);
			client.getOptions().setGeolocationEnabled(true);
			client.getOptions().setRedirectEnabled(true);
			client.getOptions().setUseInsecureSSL(true);

			client.getOptions().setDownloadImages(false);
			client.getOptions().setWebSocketEnabled(false);
			
			client.waitForBackgroundJavaScript(15000);
			client.setAjaxController(new NicelyResynchronizingAjaxController());//设置支持AJAX
			
			if( headerMap != null && headerMap.size() > 0 ) {
				for( String key : headerMap.keySet() ) {
					if( "Cookie".equals(key) ) {
						String value = headerMap.get(key);
						String[] cookies = value.split(";");
						for( String cookie : cookies ) {
							String[] kv = cookie.trim().split("=");
							if( kv.length == 2 ) {
								client.getCookieManager().addCookie(new Cookie("/", kv[0], kv[1]));
							} else {
								client.getCookieManager().addCookie(new Cookie("/", kv[0], ""));
							}
						}
					} else {
						client.addRequestHeader(key, headerMap.get(key));
					}
				}
			}
			
			String ipport = null;
			if (userProxyIp) {
				ipport = getAnProxyIp();
				if( ipport != null ) {
					System.out.println("使用代理-" + ipport);
					ProxyConfig proxyConfig = new ProxyConfig((ipport.split(",")[0]).split(":")[0], Integer.parseInt((ipport.split(",")[0]).split(":")[1]), "http");
					client.getOptions().setProxyConfig(proxyConfig); // 此处设置代理IP
				}
			}
			
			long startMs = System.currentTimeMillis();
			
			Page page = client.getPage(url);
			
			htmlPage = (HtmlPage)page;
			htmlPage = (HtmlPage) htmlPage.getBody().mouseMove();
			
			long endMs = System.currentTimeMillis();
			
			System.out.println( (ipport != null ? "\t使用代理" + ipport : "\t") + "用时 " + (endMs - startMs) + "毫秒 ：" + url);
			
		} catch (Exception e) {
			if( e instanceof SSLHandshakeException ) {
				
			} else {
				e.printStackTrace();
			}
		} finally {
			client.close();
		}
		
		return htmlPage;
	
	}
	
	public static String[] getHtmlAndCookie(String url, boolean userProxyIp, boolean useJs, int timeout, Map<String, String> headerMap, boolean followRedirect, int retryTime, String charset, String ...ignoreJs) {

		System.out.println("请求网址-" + url);
		System.out.println("请求参数-" + headerMap);
		
		String html = "";
		String resCookie = "";
		
		BrowserVersion[] versions = {BrowserVersion.BEST_SUPPORTED, BrowserVersion.CHROME, BrowserVersion.FIREFOX, BrowserVersion.FIREFOX_78, BrowserVersion.INTERNET_EXPLORER, BrowserVersion.EDGE};
		WebClient client = new WebClient(versions[(int)(versions.length * Math.random())]);
		try {
			client.getOptions().setThrowExceptionOnFailingStatusCode(false);
			client.getOptions().setJavaScriptEnabled(useJs);
			client.getOptions().setCssEnabled(false);
			
			client.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
			client.getOptions().setThrowExceptionOnScriptError(false);
			client.getOptions().setTimeout(timeout);
			client.getOptions().setAppletEnabled(true);
			client.getOptions().setGeolocationEnabled(true);
			client.getOptions().setRedirectEnabled(followRedirect);
			client.getOptions().setUseInsecureSSL(true);

			client.getOptions().setDownloadImages(false);
			client.getOptions().setWebSocketEnabled(false);
			
			client.waitForBackgroundJavaScript(15000);
			client.setAjaxController(new NicelyResynchronizingAjaxController());//设置支持AJAX
			
			WebRequest request = new WebRequest(new URL( url ));
			request.setCharset( Charset.forName( charset ) );
			
			if( headerMap != null && headerMap.size() > 0 ) {
				for( String key : headerMap.keySet() ) {
					if( "Cookie".equalsIgnoreCase(key) ) {
						String value = headerMap.get(key);
						String[] cookies = value.split(";");
						for( String cookie : cookies ) {
							String[] kv = cookie.trim().split("=");
							if( kv.length == 2 ) {
								client.getCookieManager().addCookie(new Cookie("/", kv[0], kv[1]));
							} else {
								client.getCookieManager().addCookie(new Cookie("/", kv[0], ""));
							}
						}
					} else {
						client.addRequestHeader(key, headerMap.get(key));
					}
				}
			}
			
			String ipport = null;
			if (userProxyIp) {
				ipport = getAnProxyIp();
				if( ipport != null ) {
					System.out.println("使用代理-" + ipport);
					ProxyConfig proxyConfig = new ProxyConfig((ipport.split(",")[0]).split(":")[0], Integer.parseInt((ipport.split(",")[0]).split(":")[1]), "http");
					client.getOptions().setProxyConfig(proxyConfig); // 此处设置代理IP
				}
			}
			
			long startMs = System.currentTimeMillis();
			
			Page page = client.getPage(request);
			WebResponse response = page.getWebResponse();
			
			for( NameValuePair pair : response.getResponseHeaders()) {
				if( pair.getName().equals("Set-Cookie") ) {
					resCookie = pair.getValue();
					break;
				}
			}
			
			if (response.getContentType().indexOf("application/json") != -1 || response.getContentType().equals("text/plain")) {
				html = response.getContentAsString();
			} else {
				HtmlPage htmlPage = (HtmlPage)page;
				
				html = htmlPage.asXml();
			} 
			
			long endMs = System.currentTimeMillis();
			
			System.out.println( (ipport != null ? "\t使用代理" + ipport : "\t") + "用时 " + (endMs - startMs) + "毫秒 ：" + url);
			
		} catch (Exception e) {
			if( e instanceof SocketTimeoutException  && retryTime <= DEFAULT_RETRY_TIME ) {
				return getHtmlAndCookie(url, userProxyIp, useJs, timeout, headerMap, followRedirect, retryTime + 1, charset);
			} else {
				e.printStackTrace();
				System.err.println(url + "请求出错：" + e.getMessage());
			}
		} finally {
			client.close();
		}
		
		return new String[] {html, resCookie};
	
	}

	//////////////////////////JXBrowser////////////////////////////
	
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}

	public static String getUserAgent() {
		String[] uas = {
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3314.0 Safari/537.36 SE 2.X MetaSr 1.0",
					"Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0; SE 2.X MetaSr 1.0) like Gecko",
					"Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36",
					"Mozilla/5.0 &#40;Windows NT 6.1; WOW64; rv:43.0&#41; Gecko/20100101 Firefox/43.0",
					"Mozilla/5.0 &#40;Windows NT 10.0; WOW64&#41; AppleWebKit/537.36 &#40;KHTML, like Gecko&#41; Chrome/53.0.2785.104 Safari/537.36 Core/1.53.1708.400 QQBrowser/9.5.9635.400",
					"Mozilla/5.0 &#40;Windows NT 6.1; WOW64&#41; AppleWebKit/537.36 &#40;KHTML, like Gecko&#41; Chrome/59.0.3071.104 Safari/537.36",
					"Mozilla/5.0 &#40;Windows NT 10.0; WOW64&#41; AppleWebKit/537.36 &#40;KHTML, like Gecko&#41; Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3742.400 QQBrowser/10.5.3864.400",
					"Mozilla/5.0 &#40;Windows NT 10.0; WOW64&#41; AppleWebKit/537.36 &#40;KHTML, like Gecko&#41; Chrome/57.0.2987.98 Safari/537.36 LBBROWSER",
					"Mozilla/5.0 &#40;Windows NT 6.1; WOW64&#41; AppleWebKit/537.36 &#40;KHTML, like Gecko&#41; Chrome/54.0.2840.59 Safari/537.36 115Browser/8.0.0"
				};
		return uas[(int)(Math.random() * uas.length)];
	}

}
