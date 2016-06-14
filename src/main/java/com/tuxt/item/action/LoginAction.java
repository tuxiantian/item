package com.tuxt.item.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.ai.common.xml.util.ControlConstants;
import com.ai.frame.bean.InputObject;
import com.ai.frame.bean.OutputObject;
import com.ai.rgsh.util.tag.Privilege;
import com.tuxt.item.bean.User;
import com.tuxt.item.util.Constants;

public class LoginAction extends BaseAction{
	public void login(){
		OutputObject out = super.getOutputObject();
		if(null != out && "0".equals(out.getReturnCode())){
			User user=new User();
			user.setUserId(out.getValue("userid"));
			user.setUserName(out.getValue("username"));
			user.setLoginUserPhone(out.getValue("tel"));
			user.setPassWord(out.getValue("password"));
			HttpSession session=getSession();
			session.setAttribute(Constants.SESSION_USER, user);
		}
		super.sendJson(super.convertOutputObject2Json(out));
	}
	public void getPersonalInfo(){
		User user=(User)getSession().getAttribute(Constants.SESSION_USER);
		String name=user.getLoginUserName();
		OutputObject outputObject=new OutputObject(ControlConstants.RETURN_CODE.IS_OK, "取得个人信息");
		outputObject.getBean().put("name", name);
		sendJson(outputObject);
	}
	public void index() {
		/************************* 权限查询 **************************/
		getMenuByUser();//获取用户权限
		/************************* @TODO 查询用户组织信息 **************************/		
	}
	public void getMenuByUser(){
		User user=(User)getSession().getAttribute(Constants.SESSION_USER);
		//根据User ID查询角色及其对应的菜单
		InputObject in=new InputObject();
		in.setMethod("queryMenu");
		in.setService("loginService");
		in.getParams().put("userId", user.getUserId());
		OutputObject menuList=getOutputObject(in);
		Privilege privilege=new Privilege();
		Set<String> set=new HashSet<String>();
		List <String>menus=new ArrayList<String>();
		for(Map<String,Object> map:menuList.getBeans()){
			set.add((String)map.get("ROLE_ID"));
			menus.add((String)map.get("MENU_ID"));
		}
		List<String> roles=new ArrayList<String>(set);
		privilege.setMenus(menus);
		privilege.setRoles(roles);
		user.setPrivilege(privilege);
		sendJson(menuList);
	}
	public void quit(){
		HttpSession session=getSession();
		session.setAttribute(Constants.SESSION_USER, null);
		session.invalidate();
		sendJson(new OutputObject("0","成功退出"));
	}
}
