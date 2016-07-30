package com.tuxt.item.interceptor;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.ai.common.xml.bean.Action;
import com.ai.common.xml.bean.Input;
import com.ai.common.xml.bean.Parameter;
import com.ai.common.xml.util.ControlConstants;
import com.ai.common.xml.util.ControlRequestUtil;
import com.ai.frame.bean.InputObject;
import com.ai.frame.bean.OutputObject;
import com.ai.frame.logger.Logger;
import com.ai.frame.logger.LoggerFactory;
import com.ai.frame.util.ConvertUtil;
import com.ai.frame.util.JsonUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.tuxt.item.common.UserManage;
import com.tuxt.item.util.Constants;
import com.tuxt.item.util.PropertiesUtil;
import com.tuxt.item.util.StringUtil;
import com.tuxt.item.util.Constants.CONFIG_NAME;

/**
 * @author shilei6@asiainfo.com
 * @since 2015-04-20
 */
public class AuthorityInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = 8259007565097878289L;
	private static final Logger logger = LoggerFactory
			.getUtilLog(AuthorityInterceptor.class);
	protected UserManage userManage;

	public void setUserManage(UserManage userManage) {
		this.userManage = userManage;
	}
	public String intercept(ActionInvocation invocation) throws Exception {
		// 创建InputObject对象
		createInputObject();
		ActionContext ctx = invocation.getInvocationContext();
		Map<String, Object> session = ctx.getSession();
		HttpServletRequest request = ServletActionContext.getRequest();
//		Object loginUser = session.get(Constants.SESSION_USER);
		Object loginUser =userManage.getOnLinuUser(request.getSession().getId(), null);
		
		StringBuffer urlBuffer= request.getRequestURL();
		String url = urlBuffer.toString();

		String method ;
		int index=url.lastIndexOf('?');
		if (index!=-1) {
			method=url.substring(url.lastIndexOf('!')+1, index);
		}else {
			method=url.substring(url.lastIndexOf('!')+1);
		}
		if (loginUser == null && !"login".equals(method) ) {
			logger.info("not login url is..",url);
			OutputObject outputObject=new OutputObject("1", "未登陆");
			HttpServletResponse response=(HttpServletResponse) ctx.getContext().get(org.apache.struts2.StrutsStatics.HTTP_RESPONSE);
			String jsonString=	JsonUtil.outputObject2Json(outputObject);
			response.getWriter().print(jsonString);
			return null;
		}else{
			return invocation.invoke();
		}

	}

	private void createInputObject() throws Exception {
		String method = "createInputObject";
		HttpServletRequest request = ServletActionContext.getRequest();
		String path = request.getRequestURI();

		// 处理请求的URI
		if (path.endsWith(".action")) {
			path = path.substring(0, path.indexOf(".action"));
		}

		if (path.startsWith(request.getContextPath())) {
			path = path.substring(request.getContextPath().length(),
					path.length());
		}

		// 根据请求的路径取得配置的Action
		Action action = ControlRequestUtil.getActionByPath(path);

		// 获取特定的请求参数UID
		String uid = request.getParameter(ControlConstants.UID);
		if (StringUtil.isEmpty(uid)) {
			uid = null;
		}


		// 根据请求组装InputObject对象
		InputObject inputObject = new InputObject();
		try {
			String ip = InetAddress.getLocalHost().getHostAddress().toString(); // 获取服务器IP地址
			inputObject.setServerIp(ip);
		} catch (Exception e) {
			logger.error(method, "Exception Occured When Try To Get IP Address !", e);
		}

		if (action != null) {
			Input input = ControlRequestUtil.getInputByUID(uid, action);
			processRequestParamters(request, inputObject, input);
			if (input != null) {
				if (StringUtil.isNotEmpty(input.getCode())) {
					inputObject.getParams().put("busiCode", input.getCode());
				}


				if (StringUtil.isNotEmpty(input.getService())) {
					inputObject.setService(input.getService());
				} else {
					inputObject.setService(action.getService());
				}

				if (StringUtil.isNotEmpty(input.getMethod())) {
					inputObject.setMethod(input.getMethod());
				} else {
					inputObject.setMethod(action.getMethod());
				}
			}
			ServletActionContext.getContext().put(ControlConstants.OUTPUT, ControlRequestUtil.getOutputByUID(uid, action));
		}
		ServletActionContext.getContext().put(ControlConstants.INPUTOBJECT, inputObject);
	}

	/**
	 * 处理参数
	 */
	private void processRequestParamters(
			HttpServletRequest request, InputObject inputObject, Input input) {
		List<Parameter> parameters = this.getParameters(request, input);
		StringBuffer cacheKey = new StringBuffer();
		for (Parameter p : parameters) {
			Object object = null;
			String key = p.getKey();
			String toKey = p.getToKey();
			String scope = p.getScope();
			if (StringUtil.isEmpty(key)) {
				continue;
			}
			try {
				String[] values = request.getParameterValues(key);
				if (values == null || values.length == 1) {
					if (scope != null && ControlConstants.PARAM_SCOPE.SESSION.equals(scope)) {// 从Session读取参数
						String[] keys = key.split("[.]");
						object = request.getSession().getAttribute(keys[0]);
						for (int i = 1; i < keys.length; i++) {
							if (object == null) {
								break;
							}
							Method method = object.getClass().getMethod(ConvertUtil.getMethodByField(keys[i]));
							object = method.invoke(object);
						}
					} else if (scope != null
							&& ControlConstants.PARAM_SCOPE.CONSTANTS.equals(scope)) {// 从常量类中读
						Field field = Class.forName("com.ai.rgsh.util.Constants").getDeclaredField(key);
						object = field.get(field.getName());
					} else if (scope != null && ControlConstants.PARAM_SCOPE.PROPERTIES.equals(scope)) {// 从配置文件中读取
						object = PropertiesUtil.getString(CONFIG_NAME.SYSTEM,key);
					} else if (scope != null && ControlConstants.PARAM_SCOPE.FILE.equals(scope)) {// 上传文件
						Map<String, Object> fileMap = ServletActionContext.getContext().getParameters();
						File[] files = (File[]) fileMap.get(key);
						if (files == null || files.length == 0) {
							continue;
						}

						String[] contentTypes = (String[]) fileMap.get(key + ControlConstants.FILE.CONTENTTYPE);
						String[] fileNames = (String[]) fileMap.get(key + ControlConstants.FILE.FILENAME);
						if (files.length == 1) {// 单个文件
							inputObject.addParams(key, toKey, ConvertUtil.file2String(files[0]));
							inputObject.addParams(key + ControlConstants.FILE.CONTENTTYPE, 
									toKey == null ? null : toKey + ControlConstants.FILE.CONTENTTYPE, contentTypes[0]);
							inputObject.addParams(key + ControlConstants.FILE.FILENAME, 
									toKey == null ? null : toKey + ControlConstants.FILE.FILENAME, fileNames[0]);
						} else {// 多个文件
							for (int i = 0; i < files.length; i++) {
								object = ConvertUtil.file2String(files[i]);
								inputObject.addBeans(key, toKey, i, String.valueOf(object));
								inputObject.addBeans(key + ControlConstants.FILE.CONTENTTYPE,
										toKey == null ? null : toKey + ControlConstants.FILE.CONTENTTYPE,
												i, contentTypes[i]);
								inputObject.addBeans(key + ControlConstants.FILE.FILENAME,
										toKey == null ? null : toKey + ControlConstants.FILE.FILENAME,
												i, fileNames[i]);
							}
						}
						continue;
					} else {// 从request获取数据
						object = request.getParameter(key);
					}
					inputObject.addParams(key, toKey, String.valueOf(object));//单行数据封装到Map<String,String> param
					cacheKey.append(key).append(String.valueOf(object));
					inputObject.addBeans(key, toKey, 0, String.valueOf(object));
				} else {// 多行数据提交
					for (int i = 0; i < values.length; i++) {
						inputObject.addBeans(key, toKey, i, values[i]);
					}
				}
				if (input.isCache()){
					inputObject.addParams(ControlConstants.CACHE_KEY, null, cacheKey.toString());
				}
			} catch (Exception e) {
				logger.error("processRequestParamters", "", e);
			}
		}
	}
	@SuppressWarnings("unchecked")
	private List<Parameter> getParameters(HttpServletRequest request, Input input) {
		List<Parameter> params = new ArrayList<Parameter>();
		if (ControlConstants.INPUT_SCOPE.REQUEST.equals(input.getScope())) {// 从request中取key
			Enumeration<String> enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				//				if (!ControlConstants.UID.equals(key))
				params.add(new Parameter(key, input.getToKeyByKey(key)));
			}
			for (Parameter param : input.getParameters()) {
				if (param.getScope() != null && !ControlConstants.PARAM_SCOPE.REQUEST.equals(param.getScope())){
					params.add(param);
				}
			}
		} else if (input.getScope() == null || ControlConstants.INPUT_SCOPE.CONTROL.equals(input.getScope())) {
			params.add(new Parameter(ControlConstants.UID, null));
			params.addAll(input.getParameters());
		}
		return params;
	}
}