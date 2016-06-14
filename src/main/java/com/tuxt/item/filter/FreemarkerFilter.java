package com.tuxt.item.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ai.frame.logger.Logger;
import com.ai.frame.logger.LoggerFactory;

public class FreemarkerFilter implements Filter {
	private FilterConfig filterConfig;
	private static final Logger logger = LoggerFactory.getServiceLog(FreemarkerFilter.class);
	public void destroy() {
		
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String filePath = StringUtils.substringAfter(request.getRequestURI().toString(), request.getContextPath());
		//String htmlPath = filterConfig.getServletContext().getRealPath("/");
		long start = System.currentTimeMillis();
		logger.info("Freemarker Engine process start", "FilePath="+filePath);
		FreemarkerEngine  freemarkerEngine = FreemarkerEngine.getInstance(filterConfig.getServletContext());
		try {
			Map<String,String> param = getParameter(request);
			freemarkerEngine.process(param,filePath, response);
			long end = System.currentTimeMillis();
			logger.info("Freemarker Engine end", "cost "+(end-start)+" ms");
			return;
		} catch (Exception e) {
			logger.error("Freemarker Engine end", e.getMessage());
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,String> getParameter(HttpServletRequest request){
		Enumeration<String> enumeration = request.getParameterNames();
		Map<String,String> param = new HashMap<String,String>();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			String value = request.getParameter(key);
			param.put(key, value);
		}
		return param;
	}
}
