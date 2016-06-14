package com.tuxt.item.action;

import com.ai.frame.bean.OutputObject;
import com.ai.frame.logger.Logger;
import com.ai.frame.logger.LoggerFactory;

public class ItemCategoryAction extends BaseAction{
private static final Logger logger = LoggerFactory.getActionLog(ItemAction.class);
	
	public String execute() {
		long start = System.currentTimeMillis();
		logger.info("execute", "Start");
		OutputObject object = super.getOutputObject();
		super.sendJson(super.convertOutputObject2Json(object));
		logger.info("execute", "End");
		logger.info("********************************execute****************************", String.valueOf(System.currentTimeMillis()-start));
		return null;
	}
}
