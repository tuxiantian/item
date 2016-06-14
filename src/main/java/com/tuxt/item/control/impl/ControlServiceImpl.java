package com.tuxt.item.control.impl;

import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.ai.common.xml.util.ControlConstants.RETURN_CODE;
import com.ai.frame.bean.InputObject;
import com.ai.frame.bean.OutputObject;
import com.ai.frame.logger.Logger;
import com.ai.frame.logger.LoggerFactory;
import com.ai.frame.util.JsonUtil;
import com.tuxt.item.bean.User;
import com.tuxt.item.control.IControlService;
import com.tuxt.item.util.Constants;

/**
 * @author shilei6@asiainfo.com
 * @since 2015-04-20
 */
public class ControlServiceImpl implements IControlService {
	private com.ai.frame.IControlService controlService;
	private static final Logger logger = LoggerFactory
			.getApplicationLog(ControlServiceImpl.class);

	public OutputObject execute(InputObject inputObject) {
		OutputObject outputObject = null;
		List<String> roles = null;
		try{
			User user=	(User) ServletActionContext.getRequest().getSession().getAttribute(Constants.SESSION_USER);

			if (user!=null&&user.getPrivilege()!=null) {
				roles = user.getPrivilege().getRoles();
				inputObject.getParams().put("roles", roles);
				inputObject.getParams().put("opId", user.getUserName());
				inputObject.getParams().put("userId", user.getUserId());
			}
		}catch(Exception e){
			logger.error("Error:", "获取用户信息异常", e);
		}
		try {
			long start = System.currentTimeMillis();
			logger.info("START INVOKE WEBAPP...", "");
			String json = JsonUtil.inputObject2Json(inputObject);
			String result = controlService.execute(json);
			outputObject = JsonUtil.json2OutputObject(result);
			long end = System.currentTimeMillis();
			logger.info("END INVOKE WEBAPP...", String.valueOf(end - start) + "ms");
		} catch (Exception e) {
			logger.error("Error:", "调用后台服务异常", e);
			outputObject = new OutputObject();
			outputObject.setReturnCode(RETURN_CODE.SYSTEM_ERROR);
			outputObject.setReturnMessage(e.getMessage() == null ? e.getCause().getMessage() : e.getMessage());
		}
		if (outputObject.getBean()!=null) {
			outputObject.getBean().put("roles", roles);
		}
		return outputObject;
	}

	public com.ai.frame.IControlService getControlService() {
		return controlService;
	}

	public void setControlService(com.ai.frame.IControlService controlService) {
		this.controlService = controlService;
	}

}
