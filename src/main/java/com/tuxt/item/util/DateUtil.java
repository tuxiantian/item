package com.tuxt.item.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import com.ai.frame.logger.Logger;
import com.ai.frame.logger.LoggerFactory;
import com.tuxt.item.util.Constants.CONFIG_NAME;

public final class DateUtil {
	public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYYMMDD_FORMAT = "yyyyMMdd";
	public static final String YYYYMM_FORMAT = "yyyyMM";
	public static final String YYYYNMMYDDR="yyyy年MM月dd日 HH分mm秒";
	private final static Logger logger = LoggerFactory
			.getUtilLog(DateUtil.class);
	private static TimeZone tz = null;
	static {
		tz = TimeZone.getTimeZone("GMT+8");
		TimeZone.setDefault(tz); // 设置时区
	}

	/**
	 * 校验是否是在登录时间范围内登录
	 * 
	 * @return
	 */
	public static boolean checkLoginTime() {
		String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
		String loginStartTime = currentDate + " "+ (PropertiesUtil.getString(CONFIG_NAME.SYSTEM,"loginStartTime")); // 登录开始时间
		String loginEndTime = currentDate + " "+ (PropertiesUtil.getString(CONFIG_NAME.SYSTEM,"loginEndTime")); // 登录结束时间
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
		if (DateUtil.formatDate(loginStartTime).getTime() < DateUtil
				.formatDate(currentTime).getTime()
				&& DateUtil.formatDate(currentTime).getTime() < DateUtil
						.formatDate(loginEndTime).getTime()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * 将字符串转换成日期
	 * 
	 * @param date
	 *            日期字符串
	 * @return
	 */
	public static Date formatDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			logger.error("将字符串转换成日期出错!>>", date, e);
		}
		return new Date();
	}

	public static Date getNowDate() {
		return new Date();
	}

	/**
	 * 将字符串转换成日期
	 * 
	 * @param date
	 *            日期字符串
	 * @param pattern
	 *            格式
	 * @return
	 */
	public static Date formatDate(String date, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			logger.error("将字符串转换成日期出错!>>", date, e);
		}
		return new Date();
	}

	/**
	 * 将日期按照特定格式转换成字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatString(Date date, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}

	/**
	 * 将日期按照特定格式转换成字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
		return dateFormat.format(date);
	}

	/**
	 * 将日期字符串dateStr从sourcePattern格式转换成pattern格式
	 * 
	 * @param dateStr
	 * @param sourcePattern
	 * @param pattern
	 * @return
	 */
	public static String formatString(String dateStr, String sourcePattern,
			String pattern) {
		Date date = formatDate(dateStr, sourcePattern);
		return formatString(date, pattern);
	}

	/**
	 * 获取去年今天
	 * 
	 * @return yyyyMMdd
	 */
	public static String getPreviousYear() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDD_FORMAT);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
		return dateFormat.format(calendar.getTime());
	}

	/**
	 * 获取date之前月份
	 * 
	 * @param date
	 *            时间
	 * @param count
	 *            月份个数
	 * @return [{04月=201204}, {03月=201203}]
	 */
	public static Map<String, String> getPreviousMonths(Date date, int count) {
		Map<String, String> monthMap = new TreeMap<String, String>(
				new Comparator<Object>() {
					public int compare(Object o1, Object o2) {
						if (o1 == null || o2 == null)
							return 0;
						return String.valueOf(o2).compareTo(String.valueOf(o1));
					}
				});
		Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < count; i++) {
			calendar.setTime(date);
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - i);
			monthMap.put(formatString(calendar.getTime(), "yyyy") + "年"
					+ formatString(calendar.getTime(), "MM") + "月",
					formatString(calendar.getTime(), "yyyyMM"));
		}
		return monthMap;
	}

	/**
	 * 获取date之前N个月的时间
	 * 
	 * @param date
	 *            时间
	 * @param count
	 *            月份个数
	 * @return [{04月=201204}, {03月=201203}]
	 */
	public static String getPreviousDates(Date date, int count) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - count);
		return formatString(calendar.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 在date基础上加addMonth个月
	 * 
	 * @param date
	 * @param addMonth
	 * @return
	 */
	public static Date addMonthes(Date date, int addMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + addMonth);
		return calendar.getTime();
	}
	
	/**
	 * 获取Date日期对应月的最后一天
	 * 
	 * @param date
	 *            Date对象
	 */
	public static Date getLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}
	/**
	 * 获取下个月的1号
	 * @param date
	 * @return
	 */
    public static String getNextOne(Date date){
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.MONTH,1);
    	c.set(Calendar.DAY_OF_MONTH,1);
    	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");  
    	  String dateString=formatter.format(c.getTime());
    	return dateString;
    }
    
    /**
     * 
    * @Title: getCurrentDate 
    * @Description: 得到当前日期 格式为 yyyy年MM月dd日 HH分mm秒
    * @param @return    设定文件 
    * @return String    返回类型 
    * @throws
     */
    public static String getCurrentDate(){
    	SimpleDateFormat sdf = new SimpleDateFormat(YYYYNMMYDDR);  
    	return sdf.format(System.currentTimeMillis());
    }
    
    /**
     * 
    * @Title: getCurrentDate 
    * @Description: 得到当前日期 格式为 yyyy年MM月dd日 HH分mm秒
    * @param @return    设定文件 
    * @return String    返回类型 
    * @throws
     */
    public static String getCurrentDateYYYYMM(){
    	SimpleDateFormat sdf = new SimpleDateFormat(YYYYMM_FORMAT);  
    	return sdf.format(System.currentTimeMillis());
    }
    
    public static int compareDate(String startDay,String endDay,int stype){    
         int n = 0;
         String[] u = {"天","月","年"};
         String formatStyle = stype==1?"yyyy-MM":"yyyy-MM-dd";
         
         endDay = endDay==null?getCurrentDate("yyyy-MM-dd"):endDay;
         
         DateFormat df = new SimpleDateFormat(formatStyle);
         Calendar c1 = Calendar.getInstance();
         Calendar c2 = Calendar.getInstance();
         try {
             c1.setTime(df.parse(startDay));
             c2.setTime(df.parse(endDay));
         } catch (Exception e3) {
             System.out.println("wrong occured");
         }
         //List list = new ArrayList();
         while (!c1.after(c2)) {// 循环对比，直到相等，n 就是所要的结果    
             //list.add(df.format(c1.getTime()));// 这里可以把间隔的日期存到数组中 打印出来    
             n++;
             if(stype==1){
                 c1.add(Calendar.MONTH, 1);// 比较月份，月份+1
             }
             else{
                 c1.add(Calendar.DATE, 1);// 比较天数，日期+1
             }
         }
         n = n-1;
         if(stype==2){
             n = (int)n/365;
         }
         System.out.println(startDay+" -- "+endDay+" 相差多少"+u[stype]+":"+n);    
         return n;
     }
    
	public static String getCurrentDate(String format){  
        Calendar day=Calendar.getInstance();   
        day.add(Calendar.DATE,0);   
        SimpleDateFormat sdf=new SimpleDateFormat(format);//"yyyy-MM-dd"  
        String date = sdf.format(day.getTime());  
        return date;  
    }

	//a 必须大于 b
	public static int getMonthNum(String a, String b){
		Date aa = formatDate(a, "yyyy-MM-dd");
		Date bb = formatDate(b, "yyyy-MM-dd");
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(aa);
		int month1 = calendar1.get(Calendar.MONTH);
		int year1 = calendar1.get(Calendar.YEAR);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(bb);
		int month2 = calendar2.get(Calendar.MONTH);
		int year2 = calendar2.get(Calendar.YEAR);
		int reNum = 0;
		if(year1 == year2){
			reNum = Math.abs(month1-month2) + 1;
		}else if(year1 > year2){
			//if(month1-month2>0){
				reNum = (year1-year2)*12 + (month1-month2) + 1 ;
//			}else{
//				reNum = (year1-year2)*12 + (month1-month2) + 1 ;
//			}
//			
		}else{
//			if(month2-month1>0){
//				reNum = (year2-year1)*12 + (month2-month1) + 1 ;
//			}else{
//				reNum = (year2-year1)*12 + (month2-month1) - 1 ;
//			}
		}
		
		return reNum;
	}
	
	/**
	 * 获取这个个月的1号
	 * @param date
	 * @return
	 */
	public static String getOne(Date date) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(c.getTime());
		return dateString;
	}
	    
	public static String getTime() {
		return formatString(new Date(), "yyyyMMddHHmmss");
	}
	
	 public static String getCurrentDateYMR(){
	    	SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD_FORMAT);  
	    	return sdf.format(System.currentTimeMillis());
	    }    
	 public static String getCurrentDateYMRHMS(){
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    	return sdf.format(System.currentTimeMillis());
	    }
	 
	 
	public static long getDistanceTimes(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		// long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			// sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// long[] times = {day, hour, min, sec};
		return min;
	}
	public static long getDistanceTimes2(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			 sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// long[] times = {day, hour, min, sec};
		return sec;
	}
	public static void main(String[] args) {
		System.out.println(getCurrentDateYYYYMM());
		String str1="2015-8-13 8:30:6";
		String str2="2015-8-13 8:30:9";
		System.out.println(getDistanceTimes2(str1, str2));
	}
}
