package test.crawler;

public class Crawler {

	protected String key = "";
	
	/**
	 * 把数据存储入数据库
	 */
	protected void crawlToDB(Object bean) {
	}

	/**
	 * 把数据存储入ES
	 */
	protected void crawlToEs(Object bean) {
		
	}

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}
	
}
