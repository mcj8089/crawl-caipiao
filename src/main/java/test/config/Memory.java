package test.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import test.util.IpThread;

public class Memory {

	public static IpThread ipThread = null;
	
	/**
	 * 是否使用代理IP
	 */
	public static boolean useProxyIp = true;
	
	/**
	 * 代理IP的API接口，需要从 https://www.data5u.com 获取
	 */
	public static String apiUrl = "http://api.ip.data5u.com/dynamic/get.html?order=5bcbcsdf31120b39dc42decbaa663DF&random=2&sep=3";
	
	/**
	 * 图片存储路径
	 */
	public static String imgSavePath = "D://imgPath//";
	
	/**
	 * 默认超时时间60秒
	 */
	public static int DEFAULT_TIMEOUT = 60000;
	
	/**
	 * 默认线程池大小
	 */
	public static ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	

}
