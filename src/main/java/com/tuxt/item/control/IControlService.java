package com.tuxt.item.control;

import com.ai.frame.bean.InputObject;
import com.ai.frame.bean.OutputObject;

/**
 * @author shilei6@asiainfo.com
 * @since 2015-04-20
 */
public interface IControlService {
	/**
	 * Call WebService Unified Method
	 * 
	 * @param inputObject
	 * @return OutputObject
	 */
	OutputObject execute(InputObject inputObject);
}
