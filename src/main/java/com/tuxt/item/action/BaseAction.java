package com.tuxt.item.action;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.ai.common.xml.bean.Output;
import com.ai.common.xml.util.ControlConstants;
import com.ai.frame.bean.InputObject;
import com.ai.frame.bean.OutputObject;
import com.ai.frame.logger.Logger;
import com.ai.frame.logger.LoggerFactory;
import com.ai.frame.util.JsonUtil;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.tuxt.item.control.IControlService;
import com.tuxt.item.util.StringUtil;

/**
 * @author shilei6@asiainfo.com
 * @since 2015-04-20
 */
public abstract class BaseAction extends ActionSupport{
	private static final long serialVersionUID = 1581119741116374826L;
	private static final Logger logger = LoggerFactory.getActionLog(BaseAction.class);
	private IControlService controlService; // 前后工程调用服务
	private InputObject inputObject;

	/** Get the request Object **/
	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	/** Get the response Object **/
	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	/** Get the current Session **/
	public HttpSession getSession() {
		return getRequest().getSession();
	}
	
	/** Get the current Session **/
	public HttpSession getSession(boolean arg0) {
		return getRequest().getSession(arg0);
	}

	/** Get the Servlet Context **/
	public ServletContext getServletContext() {
		return ServletActionContext.getServletContext();
	}

	/** Get the Value Stack of Struts2 **/
	public ValueStack getValueStack() {
		return ServletActionContext.getValueStack(getRequest());
	}
	

	/** Get the IControlService Object **/
	public IControlService getControlService() {
		return controlService;
	}

	public void setControlService(IControlService controlService) {
		this.controlService = controlService;
	}


	/** Print OutputStream to the Browser **/
	public void sendJson(String json) {
		try {
			getRequest().setCharacterEncoding("UTF-8");
			getResponse().setContentType("text/html;charset=UTF-8");
			getResponse().getWriter().print(json);
			logger.info("sendJson", json);
		} catch (IOException e) {
			logger.error("sendJson", "Exception Occured When Send Json to Client !", e);
		}
	}
	
	/** Print OutputStream to the Browser **/
	public void sendJson(OutputObject out) {
		try {
			String json=convertOutputObject2Json(out);
			getRequest().setCharacterEncoding("UTF-8");
			getResponse().setContentType("text/html;charset=UTF-8");
			getResponse().getWriter().print(json);
			logger.info("sendJson", json);
		} catch (IOException e) {
			logger.error("sendJson", "Exception Occured When Send Json to Client !", e);
		}
	}

	/** Get InputObject Object Encapsulated **/
	public InputObject getInputObject() {
		inputObject = (InputObject) ServletActionContext.getContext().get(ControlConstants.INPUTOBJECT);
		return inputObject;
	}

	/** Call Services and Get OutputObject Object **/
	public OutputObject getOutputObject() {
		return getOutputObject(getInputObject());
	}

	/** Call Services and Get OutputObject Object **/
	public OutputObject getOutputObject(InputObject inputObject) {
		OutputObject object= this.execute(inputObject);
		return object;
	}
	
	private OutputObject execute(InputObject inputObject) {
		OutputObject outputObject = null;
		try {
				outputObject = getControlService().execute(inputObject);
		} catch (Exception e) {
			logger.error("", "Invoke Service Error.", inputObject.getService() + "." + inputObject.getMethod(), e);
		}
		return outputObject;
	}
	
	/**
	 * Json String Unified Conversion Method
	 * @param outputObject
	 * @return Json
	 */
	public String convertOutputObject2Json(OutputObject outputObject) {
		return convertOutputObject2Json(getInputObject(), outputObject);
	}

	/**
	 * Json String Unified Conversion Method
	 * @param outputObject
	 * @return Json
	 */
	public String convertOutputObject2Json(InputObject inputObject,OutputObject outputObject) {
		String json = "";
		if (outputObject == null || inputObject == null){
			return json;
		}

		Output output = (Output) ServletActionContext.getContext().get(ControlConstants.OUTPUT);
		try {
			// 如果配置了相应的IConvertor则执行，否则执行默认的Json转换功能
			if (output != null && StringUtil.isNotEmpty(output.getConvertor())) {
				json = JsonUtil.outputObject2Json(output.getConvertor(),
						output.getMethod(), inputObject, outputObject);
			}else{
				json = JsonUtil.outputObject2Json(outputObject);
			}
		} catch (Exception e) {
			logger.error("convertOutputObject", "Convert Output Error.", "", e);
		}
		return json;
	}
}
