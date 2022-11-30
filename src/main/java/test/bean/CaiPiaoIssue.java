package test.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 中奖情况
 */
public class CaiPiaoIssue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cpId; // 彩票ID : 彩票类型+期

	private Integer type; // 彩票类型：1快乐8，2双色球，3福彩3D，4七乐彩，5大乐透，6排列3，7排列5，8七星彩

	private String issue; // 期数

	private String openTime; // 开奖时间

	private Float saleMoney; // 销售金额

	private Float prizePoolMoney; // 奖池金额

	private String deadlineAwardDate; // 截止兑奖日期

	private String frontWinningNum; // 开奖号码，前排

	private String backWinningNum; // 开奖号码，后排
	
	private List<CaiPiaoWinner> winnerList; // 中奖情况

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public Float getSaleMoney() {
		return saleMoney;
	}

	public void setSaleMoney(Float saleMoney) {
		this.saleMoney = saleMoney;
	}

	public Float getPrizePoolMoney() {
		return prizePoolMoney;
	}

	public void setPrizePoolMoney(Float prizePoolMoney) {
		this.prizePoolMoney = prizePoolMoney;
	}

	public String getDeadlineAwardDate() {
		return deadlineAwardDate;
	}

	public void setDeadlineAwardDate(String deadlineAwardDate) {
		this.deadlineAwardDate = deadlineAwardDate;
	}

	public String getFrontWinningNum() {
		return frontWinningNum;
	}

	public void setFrontWinningNum(String frontWinningNum) {
		this.frontWinningNum = frontWinningNum;
	}

	public String getBackWinningNum() {
		return backWinningNum;
	}

	public void setBackWinningNum(String backWinningNum) {
		this.backWinningNum = backWinningNum;
	}

	public List<CaiPiaoWinner> getWinnerList() {
		return winnerList;
	}

	public void setWinnerList(List<CaiPiaoWinner> winnerList) {
		this.winnerList = winnerList;
	}
	
}
