package com.tuxt.item.action;

import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.common.xml.util.ControlConstants;
import com.ai.frame.bean.OutputObject;
import com.ai.frame.logger.Logger;
import com.ai.frame.logger.LoggerFactory;
import com.tuxt.item.util.ExcelPOI07Util;
import com.tuxt.item.util.StringUtil;

public class ItemAction extends BaseAction{
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
	public void exportWebExcel() {
        HttpServletRequest req = getRequest();
        String userAgent = req.getHeader("User-Agent");
        OutputObject obj = super.getOutputObject();

        if (ControlConstants.RETURN_CODE.SYSTEM_ERROR.equals(obj.getReturnCode())) {
            super.sendJson(super.convertOutputObject2Json(obj));
            return;
        }

        HttpServletResponse resp = getResponse();
        OutputStream outStream = null;
        try {
            outStream = resp.getOutputStream();
            resp.reset();
            boolean isIE = false;
            if (userAgent != null
                    && (userAgent.toLowerCase().contains("msie") || userAgent.toLowerCase().contains("rv"))) {
                isIE = true;
            }
            String fileName = (String) obj.getBean().get("fileName");
            if(StringUtil.isEmpty(fileName)){
                fileName = "导出订单_" + System.currentTimeMillis() + ".xlsx";
            }
            resp.setContentType("application/octet-stream");
            setResponseFileName(isIE, resp, fileName);
            ExcelPOI07Util.createExcel2007(outStream, obj);
            obj.getBeans().clear();
            outStream.close();
            resp.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setResponseFileName(boolean isIE, HttpServletResponse response, String displayName) throws Exception {
        if (isIE) {
            displayName = URLEncoder.encode(displayName, "UTF-8");
            displayName = displayName.replaceAll("\\+", "%20");// 修正URLEncoder将空格转换成+号的BUG
            response.setHeader("Content-Disposition", "attachment;filename=" + displayName);
        } else {
            displayName = new String(displayName.getBytes("UTF-8"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + displayName + "\"");// firefox空格截断
        }
    }
}
