package test.bean;

import java.io.Serializable;

/**
 * 中奖情况
 */
public class CaiPiaoWinner implements Serializable {

	private static final long serialVersionUID = 1L;

	private String idx; // 彩票ID : 彩票类型+期+奖项名称
	
	private String cpId; // 彩票ID

	private String remark; // 奖项名称

	private Integer baseAwardNum; // 基本中奖注数（注）

	private Float baseAwardMoney; // 基本中奖金额（元）

	public String getIdx() {
		return idx;
	}
	public void setIdx(String idx) {
		this.idx = idx;
	}
	
	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getBaseAwardNum() {
		return baseAwardNum;
	}

	public void setBaseAwardNum(Integer baseAwardNum) {
		this.baseAwardNum = baseAwardNum;
	}

	public Float getBaseAwardMoney() {
		return baseAwardMoney;
	}

	public void setBaseAwardMoney(Float baseAwardMoney) {
		this.baseAwardMoney = baseAwardMoney;
	}

}
