package com.ai.rgsh.util.tag;

import java.io.Serializable;
import java.util.List;
/**
 * 权限（按钮权限+角色权限）
 */
public class Privilege implements Serializable{
	private static final long serialVersionUID = 625621418985564489L;

	public List<String> getMenus() {
		return menus;
	} 

	public void setMenus(List<String> menus) {
		this.menus = menus;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	private List<String> menus; 
	private List<String> roles;

	public Privilege() {
	}

}
