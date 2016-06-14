package com.tuxt.item.control.impl;

import java.util.List;
import java.util.Map;

import com.ai.common.xml.bean.Parameter;
import com.ai.common.xml.bean.ValidateResult;
import com.ai.common.xml.filter.IControlRequest;
import com.ai.common.xml.util.ControlRequestUtil;
import com.ai.common.xml.util.ControlConstants.RETURN_CODE;
import com.ai.frame.logger.Logger;
import com.ai.frame.logger.LoggerFactory;

public class ControlRequestImpl extends IControlRequest {
	private static Logger logger = LoggerFactory.getServiceLog(ControlRequestImpl.class);

	/**
	 * 参数验证
	 */
	public ValidateResult validate(String path, List<Parameter> sources,
			Map<String, String[]> params) {
		if (sources == null) {
			return new ValidateResult(RETURN_CODE.IS_OK, null);
		}

		ValidateResult result = ControlRequestUtil.checkParams(sources, params);
		if (!RETURN_CODE.IS_OK.equals(result.getReturnCode())) {
			System.err.println("参数验证有问题" + path);
			logger.error("参数验证有问题?", "path=" + path);
		}
		return result;
	}
}
