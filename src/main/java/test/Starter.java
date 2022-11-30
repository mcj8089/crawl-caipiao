package test;

import test.config.Memory;
import test.crawler.CaipiaoHistoryCrawler;
import test.util.IpThread;

public class Starter {

	public static void main(String[] args) {
		
		if( Memory.useProxyIp ) { // 是否开启代理IP
			Memory.ipThread = new IpThread();
			Memory.ipThread.start();
		}
		
		new CaipiaoHistoryCrawler().crawl();
	}
}
