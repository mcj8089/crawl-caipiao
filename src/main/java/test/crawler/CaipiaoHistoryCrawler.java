package test.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import test.bean.CaiPiaoIssue;
import test.bean.CaiPiaoWinner;
import test.config.Memory;
import test.util.CrawlerUtil;
import test.util.LogUtil;
import test.util.StrUtil;

/**
 * 彩票历史
 */
public class CaipiaoHistoryCrawler extends Crawler {
	
	String TAG = "CaipiaoHistoryCrawler";
	
	Map<String, String> headerMap = new HashMap<String, String>();

	int retryTime = 3;
	
	AtomicInteger atoInt = new AtomicInteger(1);
	
	Set<String> uniqSet = new HashSet<String>();

	public void crawl() {
		
		headerMap.put("Accept", "*/*");
		headerMap.put("Accept-Encoding", "gzip, deflate, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.9");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Cookie", "BAIDU_SSP_lcr=https://www.baidu.com/link?url=riNXkDsMHCOiaKifIQRKh0P3RuASJjDVfIvNZy0PFwS&wd=&eqid=8a03215500000b570000000360dbeecd; _ga=GA1.2.1911959757.1625027094; _gid=GA1.2.724130032.1625027094; PHPSESSID=45a4gkalmomcnbjabcvkmij3p3; Hm_lvt_12e4883fd1649d006e3ae22a39f97330=1625027094; Hm_lvt_692bd5f9c07d3ebd0063062fb0d7622f=1625027095; _gat_UA-66069030-3=1; Hm_lpvt_692bd5f9c07d3ebd0063062fb0d7622f=1625027400; Hm_lpvt_12e4883fd1649d006e3ae22a39f97330=1625027400; KLBRSID=13ce4968858adba085afff577d78760d|1625027411|1625027093");
		headerMap.put("Host", "jc.zhcw.com");
		headerMap.put("Referer", "https://www.zhcw.com/kjxx/pl3/kjxq/");
		headerMap.put("Sec-Fetch-Dest", "script");
		headerMap.put("Sec-Fetch-Mode", "no-cors");
		headerMap.put("Sec-Fetch-Site", "same-site");
		
		crawlZhongCai(1);
		crawlZhongCai(2);
		crawlZhongCai(3);
		crawlZhongCai(4);
		crawlZhongCai(5);
		crawlZhongCai(6);
		crawlZhongCai(7);
		crawlZhongCai(8);
		
		LogUtil.logInfo(TAG, "采集任务已完成");
		
	}

	// 彩票类型：1快乐8，2双色球，3福彩3D，4七乐彩，5大乐透，6排列3，7排列5，8七星彩
	private void crawlZhongCai(Integer type) {

		Set<String> issueSet = new HashSet<String>();
		
		String prefix = "";
		String surfix = ".html";
		
		if( type == 1 ) {
			issueSet = getIssueSet("https://www.ydniu.com/open/kl8.aspx");
			prefix = "https://www.ydniu.com/open/kl8/";
		} else if( type == 2 ) {
			issueSet = getIssueSet("https://www.ydniu.com/open/ssq.aspx");
			prefix = "https://www.ydniu.com/open/ssq/";
		} else if( type == 3 ) {
			issueSet = getIssueSet("https://www.ydniu.com/open/sd.aspx");
			prefix = "https://www.ydniu.com/open/sd/";
		} else if( type == 4 ) {
			issueSet = getIssueSet("https://www.ydniu.com/open/qlc.aspx");
			prefix = "https://www.ydniu.com/open/qlc/";
		} else if( type == 5 ) {
			issueSet = getIssueSet("https://www.ydniu.com/open/dlt.aspx");
			prefix = "https://www.ydniu.com/open/dlt/";
		} else if( type == 6 ) {
			issueSet = getIssueSet("https://www.ydniu.com/open/pl3.aspx");
			prefix = "https://www.ydniu.com/open/pl3/";
		} else if( type == 7 ) {
			issueSet = getIssueSet("https://www.ydniu.com/open/pl5.aspx");
			prefix = "https://www.ydniu.com/open/pl5/";
		} else if( type == 8 ) {
			issueSet = getIssueSet("https://www.ydniu.com/open/qxc.aspx");
			prefix = "https://www.ydniu.com/open/qxc/";
		}
		
		for( String issue : issueSet ) {
			
			final String fPrefix = prefix;
			Memory.threadPool.execute(new Runnable() {
				
				@Override
				public void run() {

					if( !uniqSet.add(issue) ) {
						return;
					}
					
					try {
						// START
						String url = fPrefix + issue + surfix;

						String html = null;
						for( int i = 1; i <= retryTime; i ++ ) {
							try {
								if( i == retryTime && Memory.useProxyIp ) {
									html = CrawlerUtil.getHtml(url, false, false, Memory.DEFAULT_TIMEOUT, headerMap);
								} else {
									html = CrawlerUtil.getHtml(url, Memory.useProxyIp, false, Memory.DEFAULT_TIMEOUT, headerMap);
								}
								if( StrUtil.isNotEmpty(html) && html.contains("Bad Gateway: www.ydniu.com:443") || html.contains("白名单校验失败") ) {
									i = i - 1;
									continue;
								}
								if( StrUtil.isNotEmpty(html) ) {
									break;
								}
							} catch ( Exception e ) {
								LogUtil.logInfo(TAG, "采集分期报错", e);
							}
						}
						
						if( StrUtil.isNotEmpty(html) ) {
							
							try {
								
								Document startDoc = Jsoup.parse(html);
								
								CaiPiaoIssue caiPiaoIssue = new CaiPiaoIssue();
								caiPiaoIssue.setIssue(issue);

								Elements openNumberRedEl = startDoc.select("#openNumber i");
								Elements openNumberBlueEl = startDoc.select("#openNumber em");
								
								StringBuilder redBallSB = new StringBuilder();
								for( Element el : openNumberRedEl ) {
									redBallSB.append(el.text()).append(",");
								}
								
								StringBuilder blueBallSB = new StringBuilder();
								for( Element el : openNumberBlueEl ) {
									blueBallSB.append(el.text()).append(",");
								}
								
								String temp = startDoc.select("#openDate").text();
								
								String openTime = temp.split("，")[0].replace("开奖日期：", "");
								String deadlineAwardDate = temp.split("，")[1].replace("兑奖截止日期：", "");
								String frontWinningNum = redBallSB.toString();
								String backWinningNum = blueBallSB.toString();
								Float saleMoney = Float.valueOf(startDoc.select("#sumSales").text().replace(",", ""));
								Float prizePoolMoney = Float.valueOf(startDoc.select("#prizePool").text().replace(",", ""));
								
								frontWinningNum = frontWinningNum.substring(0, frontWinningNum.length() - 1);
								backWinningNum = backWinningNum.substring(0, backWinningNum.length() - 1);
								
								caiPiaoIssue.setBackWinningNum(backWinningNum);
								caiPiaoIssue.setDeadlineAwardDate(deadlineAwardDate);
								caiPiaoIssue.setFrontWinningNum(frontWinningNum);
								caiPiaoIssue.setOpenTime(openTime);
								caiPiaoIssue.setPrizePoolMoney(prizePoolMoney);
								caiPiaoIssue.setSaleMoney(saleMoney);
								caiPiaoIssue.setType(type);
								caiPiaoIssue.setCpId(type + issue);
								
							    List<CaiPiaoWinner> winnerList = new ArrayList<CaiPiaoWinner>();
								
								Elements trs = startDoc.select("#t_WinType tr");
			 					for( Element tr : trs ) {
			 						String name = tr.select("td").get(0).text();
			 						String num = tr.select("td").get(1).text();
			 						String money = tr.select("td").get(2).text();
			 						
			 						CaiPiaoWinner winner = new CaiPiaoWinner();
			 						winner.setBaseAwardMoney( Float.valueOf(money) );
			 						winner.setBaseAwardNum(Integer.valueOf(num));
			 						winner.setRemark(name);
			 						winner.setCpId(caiPiaoIssue.getCpId());
			 						winner.setIdx(type + issue + name);
			 						
			 						winnerList.add(winner);
			 					}
								
			 					crawlToDB(winnerList);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						
						}	
						// END
					} catch (Exception e) {
						e.printStackTrace();
					}
									
				}
			});
			
		}
	
	}

	private Set<String> getIssueSet(String url) {
		headerMap.put("Host", "www.ydniu.com");
		headerMap.put("Referer", "https://www.ydniu.com/open/ssq.aspx");
		
		String html = null;
		for( int i = 1; i <= retryTime; i ++ ) {
			try {
				if( i == retryTime && Memory.useProxyIp ) {
					html = CrawlerUtil.getHtml(url, false, false, Memory.DEFAULT_TIMEOUT, headerMap);
				} else {
					html = CrawlerUtil.getHtml(url, Memory.useProxyIp, false, Memory.DEFAULT_TIMEOUT, headerMap);
				}
				if( StrUtil.isNotEmpty(html) ) {
					break;
				}
			} catch ( Exception e ) {
				LogUtil.logInfo(TAG, "采集分期报错", e);
			}
		}
		
		Set<String> reSet = new HashSet<>();
		
		if( StrUtil.isNotEmpty(html) ) {
			
			if( html.contains("Bad Gateway: www.ydniu.com:443") || html.contains("白名单校验失败") ) {
				return getIssueSet(url);
			}
			
	 		Document document = Jsoup.parse(html);
	 		Elements as = document.select(".iSelectBox .iSelectList.listOverFlow a");
	 		
	 		for( Element el : as ) {
	 			reSet.add(el.text());
	 		}
		}
		
		return reSet;
	}

}
