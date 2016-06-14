package com.tuxt.item.bean;

import java.io.Serializable;

import com.ai.rgsh.util.tag.Privilege;

public class User implements Serializable {
	private static final long serialVersionUID = -557421552520594061L;
	
	/**
	 * 人员  登录信息
	 */
	private String userId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	private String userName;//登录帐号 操作ID
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	private String passWord;//登录密码
	/**
	 * 人员个人信息
	 */
	private String loginUserName;//登录人姓名
	private String tel;//登录人手机号码
	/**
	 * 组织信息
	 */
	private String orgName;//组织名称
	private String orgId;//组织ID
	private String note;//备注
	
	/**
	 * 岗位类型  多对多  岗位对应权限   
	 */
	
	/**
	 * 权限信息 1对多   1个权限对应多个菜单ID
	 */
	private Privilege privilege;
	/**
	 * 短信随机码信息
	 */
	
	private String randomNumber;//随机码
	private int randomSendNum=0;//下发次数
	private int randomCheckErrorNum=0;//验证错误次数
	private String randomSendTime;//下发时间
	private boolean isCheck=false;
	
	/**
	 * 日志相关信息
	 */
	private String loginId;//登录主ID
	private String loginTime;//登录时间

	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getLoginUserName() {
		return loginUserName;
	}
	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}
	public String getLoginUserPhone() {
		return tel;
	}
	public void setLoginUserPhone(String loginUserPhone) {
		this.tel = loginUserPhone;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}
	public String getRandomNumber() {
		return randomNumber;
	}
	public void setRandomNumber(String randomNumber) {
		this.randomNumber = randomNumber;
	}
	public int getRandomSendNum() {
		return randomSendNum;
	}
	public void setRandomSendNum(int randomSendNum) {
		this.randomSendNum = randomSendNum;
	}
	public int getRandomCheckErrorNum() {
		return randomCheckErrorNum;
	}
	public void setRandomCheckErrorNum(int randomCheckErrorNum) {
		this.randomCheckErrorNum = randomCheckErrorNum;
	}
	public String getRandomSendTime() {
		return randomSendTime;
	}
	public void setRandomSendTime(String randomSendTime) {
		this.randomSendTime = randomSendTime;
	}
	public Privilege getPrivilege() {
		return privilege;
	}
	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	
}
