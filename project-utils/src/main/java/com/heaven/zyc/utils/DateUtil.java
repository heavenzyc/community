package com.heaven.zyc.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

	/**
	 * 日期短格式yyyy-MM-dd
	 */
	private static final String DATE_SHORT = "yyyy-MM-dd";

	private static final String DATE_SHORT_TWO = "yyyy.MM.dd";

	/**
	 * 24小时格式
	 */
	private static final String DATE_MINUTES = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * yyyy-MM-dd HH:mm
	 */
	private static final String DATE_HOUR_MINUTES = "yyyy-MM-dd HH:mm";

	/**
	 * 12小时格式
	 */
	private static final String DATE_MINUTES_12 = "yyyy-MM-dd hh-mm-ss";
	/**
	 * 中文的yyyy年MM月dd日
	 */
	private static final String DATE_CH_STYLE = "yyyy年MM月dd日";

	/**
	 * @param dateStr
	 *            -日期字符串 <br>
	 *            int len -移动天数;eg: +1(明天),-1(昨天)
	 * @return String
	 * @description -将日期字符串 按天 前后移动 (yyyy-MM-dd)
	 * @author HuangCheng
	 */
	public String dayMove(String dateStr, int len) {
		return dateMove(dateStr, len, Calendar.DATE, DATE_SHORT);
	}

	/**
	 * @param dateStr
	 *            -日期字符串 <br>
	 *            int len -移动的月数;eg:+1(下个月),-1(上个月)
	 * @return String
	 * @description -将日期字符串 按月 前后移动 (yyyy-MM-dd)
	 * @author HuangCheng
	 */
	public String mouthMove(String dateStr, int len) {
		return dateMove(dateStr, len, Calendar.MONTH, DATE_SHORT);
	}

	/**
	 * @param dateStr
	 *            -日期字符串 <br>
	 *            int len -移动的年数;eg:+1(明年),-1(去年)
	 * @return String
	 * @description -将日期字符串 按年 前后移动 (yyyy-MM-dd)
	 * @author HuangCheng
	 */
	public String yearMove(String dateStr, int len) {
		return dateMove(dateStr, len, Calendar.YEAR, DATE_SHORT);
	}

	/**
	 * @param dateStr
	 *            -需要处理的字符串 <br>
	 *            int len -移动天数 <br>
	 *            Calendar.DATE field -按(天/月/年)移动
	 * @return String 返回日期字符串
	 * @description -返回日期字符串dateStr按移动字段field,移动天数len移动后的字符串
	 * @author HuangCheng
	 */
	public String dateMove(String dateStr, int len, int field) {
		return dateMove(dateStr, len, field, DATE_SHORT);
	}

	/**
	 * @param dateStr
	 *            -需要处理的字符串 <br>
	 *            int len -移动天数 <br>
	 *            Calendar.DATE field -按(天/月/年)移动 <br>
	 *            String pattern -格式
	 * @return String
	 * @description -返回日期字符串按移动字段field，移动天数len, 格式pattern
	 * @author HuangCheng
	 */
	public String dateMove(String dateStr, int len, int field, String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(stringToDate(dateStr, pattern));
		cal.add(field, len);
		return dateToString(cal.getTime(), pattern);
	}

	/**
	 * @return String
	 * @description 返回系统时间的字符串 (yyyy-MM-dd hh-mm-ss),12小时格式
	 * @author HuangCheng
	 */
	public static String getTime() {
		return dateToString(new Date(), DATE_MINUTES_12);
	}

	/**
	 * @return String
	 * @description 返回系统时间的字符串24小时格式 (yyyy-MM-dd HH:mm:ss)
	 * @author HuangCheng
	 */
	public static String getTime24() {
		return getTime24(new Date());
	}

	/**
	 * @param date
	 *            传入日期
	 * @return
	 * @description 返回系统时间的字符串24小时格式 (yyyy-MM-dd HH:mm:ss)
	 */
	public static String getTime24(Date date) {
		return dateToString(date, DATE_MINUTES);
	}

	/**
	 * 获取中文格式
	 * @param date
	 * @return
	 */
	public static String getChStyle(Date date) {
		return dateToString(date, DATE_CH_STYLE);
	}

	/**
	 * 返回格式： yyyy-MM-dd HH:mm
	 * @param date
	 * @return
	 */
	public static String getDayHour24(Date date){
		return dateToString(date, DATE_HOUR_MINUTES);
	}
	/**
	 * @return String
	 * @description 返回系统日期的字符串 (yyyy-MM-dd)
	 * @author HuangCheng
	 */
	public static String getDate() {
		return dateToString(new Date(), DATE_SHORT);
	}

    public static Date getDate(int i){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, i);
        return c.getTime();
    }

	/**
	 * 将时间减少几分钟
	 * 
	 * @param date
	 *            日期
	 * @param minute
	 *            分钟
	 * @return
	 */
	public static Date getDateReducingTime(Date date, int minute) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, -minute);
		return calendar.getTime();
	}

	/**
	 * 将天数增加
	 * 
	 * @param date
	 *            日期
	 * @param day
	 *            增加的天数
	 * @return
	 */
	public static Date getDateAddDay(Date date, int day) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	public static Date getDateAddMonth(Date date, int month) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 将天数减少
	 * 
	 * @param date
	 *            日期
	 * @param day
	 *            增加的天数
	 * @return
	 */
	public static Date getDateReducingDay(Date date, int day) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -day);
		return calendar.getTime();
	}

	/**
	 * 将当前时间减少几分钟
	 * 
	 * @param minute
	 * @return
	 */
	public static Date getCurrentDateReducingTime(int minute) {
		Calendar calendar = new GregorianCalendar();
		return getDateReducingTime(calendar.getTime(), minute);
	}

	/**
	 * @return String
	 * @description -返回系统现在时间的毫秒数
	 * @author HuangCheng
	 */
	public static String getTimeMilliseconds() {
		return String.valueOf(new Date().getTime());
	}

	/**
	 * @param pattern
	 *            -格式 <br>
	 *            Date date -日期对象
	 * @return String -日期字符串
	 * @description 将日期对象date转化成格式pattern的日期字符串
	 * @author HuangCheng
	 */
	public static String dateToString(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * @param date
	 * @return String
	 * @description 返回指定时间的字符串 (yyyy-MM-dd hh-mm-ss),12小时格式
	 * @author HuangCheng
	 */
	public static String timeToString(Date date) {
		return dateToString(date, DATE_MINUTES_12);
	}

	/**
	 * @param
	 * @return String 日期的字符串
	 * @description 返回指定日期的字符串 (yyyy-MM-dd)
	 * @author HuangCheng
	 */
	public static String dateToString(Date date) {
		if (date != null) {
			return dateToString(date, DATE_SHORT);
		} else {
			return null;
		}
	}

	/**
	 * @param
	 * @return String 日期的字符串
	 * @description 返回指定日期的字符串 (yyyy.MM.dd)
	 * @author HuangCheng
	 */
	public static String dateToString2(Date date) {
		if (date != null) {
			return dateToString(date, DATE_SHORT_TWO);
		} else {
			return null;
		}
	}

	/**
	 * @param dateStr
	 *            -日期字符串 <br>
	 *            String pattern -转化格式
	 * @return Date -转化成功返回该格式的日期对象,失败返回null
	 * @description -按格式pattern将字符串dateStr转化为日期
	 * @author HuangCheng
	 */
	public static Date stringToDate(String dateStr, String pattern) {
		Date date = null;
		if (null!=dateStr && !"".equals(dateStr)) {
			try {
				date = new SimpleDateFormat(pattern).parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * @param timeStr
	 *            -日期字符串
	 * @return Date
	 * @description -将日期字符串timeStr转化为日期对象 (yyyy-MM-dd hh-mm-ss),12小时格式
	 * @author HuangCheng
	 */
	public static Date stringToTime(String timeStr) {
		return stringToDate(timeStr, DATE_MINUTES_12);
	}

	/**
	 * 24小时格式
	 * @param timeStr
	 * @return
	 */
	public static Date stringTo24Time(String timeStr) {
		return stringToDate(timeStr, DATE_HOUR_MINUTES);
	}

	/**
	 * @param dateStr
	 *            -日期字符串
	 * @return Date
	 * @description -将日期字符串dateStr转化为日期对象 (yyyy-MM-dd)
	 * @author HuangCheng
	 */
	public static Date stringToDate(String dateStr) {
		return stringToDate(dateStr, DATE_SHORT);
	}

	/**
	 * @param dateString
	 *            -日期字符串 <br>
	 *            Stirng pattern -格式
	 * @return String
	 * @description -将日期字符按pattern串格式化
	 * @author HuangCheng
	 */
	public static String format(String dateString, String pattern) {
		String result = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			result = dateFormat.format(dateFormat.parse(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param dateString
	 * @return String
	 * @description -将日期字符按DATE_SHORT串格式化
	 * @author HuangCheng
	 */
	public static String format(String dateString) {
		return format(dateString, DATE_SHORT);
	}

	/**
	 * @param dateString
	 * @return String
	 * @description -将日期字符按DATE_SHORT串格式化
	 * @author HuangCheng
	 */
	public static String format2(String dateString) {
		return format(dateString, DATE_SHORT_TWO);
	}

	/**
	 * 日期格式化为，短格式的日期对象 (yyyy-MM-dd)
	 * 
	 * @param date
	 * @return
	 */
	public static Date formatDate(Date date) {
		String dateString = dateToString(date);
		return stringToDate(dateString);
	}

	/**
	 * 判断当前日期 > 传入的日期 返回true。 判断当前日期 <= 传入的日期 返回false。
	 * 
	 * @param vDate
	 * @return
	 */
	public static boolean isAfterDate(Date vDate) {
		Calendar nowDate = GregorianCalendar.getInstance();
		return nowDate.after(vDate);
	}

    public static boolean isBeforeDate(Date vDate) {
        Calendar nowDate = GregorianCalendar.getInstance();
        return nowDate.before(vDate);
    }

	/**
	 * 判断日期的相隔天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDistDates(Date startDate, Date endDate) {
		long totalDate = 0;
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar endalendar = Calendar.getInstance();
		endalendar.setTime(endDate);
		long timestart = startCalendar.getTimeInMillis();
		long timeend = endalendar.getTimeInMillis();
		totalDate = Math.abs((timeend - timestart)) / (1000 * 60 * 60 * 24);
		return Long.valueOf(totalDate).intValue();
	}

	/**
	 * 判断startDate在endDate前还是后
	 * @param startDate
	 * @param endDate
	 * @return 1 为startDate > endDate;  0为 startDate = endDate; -1 为startDate < endDate
	 */
	public static int getDistByDate(Date startDate, Date endDate){
		return formatDate(startDate).compareTo(formatDate(endDate));
	}
	/**
	* 返回1 是昨天之前的日期
	* 返回0 是今天
	* 返回-1 是明天之后的日期
	 * @param date
	 */
	public static int getDistByNowDate(Date date){
		return formatDate(new Date()).compareTo(formatDate(date));
	}
	/**
	 * 判断传入的日期是否为昨天之前的日期
	 * @param date
	 * @return
	 */
	public static boolean beforNowDate(Date date){
		return getDistByNowDate(date) == 1 ? true : false;
	}
	/**
	 * 判断传入的日期与服务器当前日期的相隔天数
	 * 
	 * @param vDate
	 * @return
	 */
	public static int getDistCurrentDates(Date vDate) {
		return getDistDates(new Date(), vDate);
	}

	/**
	 * 判断日期相隔的时、分、秒
	 */
	public static Long[] getDistTime(Date startDate, Date endDate) {
		long hour = 0;
		long min = 0;
		long sec = 0;
		if (startDate != null && endDate != null) {
			long time1 = startDate.getTime();
			long time2 = endDate.getTime();
			long diff;
			if (time1 < time2) {
				hour = 0L;
				min = 0L;
				sec = 0L;
			} else {
				diff = time1 - time2;
				hour = diff / (60 * 60 * 1000);
				min = ((diff / (60 * 1000)) - hour * 60);
				sec = (diff / 1000 - hour * 60 * 60 - min * 60);
			}
		}
		Long[] times = { hour, min, sec };
		return times;
	}

	/**
	 * 判断日期相隔的天、时、分
	 */
	public static Long[] getDistMinute(Date startDate, Date endDate) {
		long day = 0;
		long hour = 0;
		long min = 0;
		if (startDate != null && endDate != null) {
			long time1 = startDate.getTime();
			long time2 = endDate.getTime();
			long diff;
			if (time1 < time2) {
				day = 0L;
				hour = 0L;
				min = 0L;
			} else {
				diff = time1 - time2;
				day = diff / (1000 * 60 * 60 * 24);
				hour = (diff / (60 * 60 * 1000) - day * 24);
				min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			}
		}
		Long[] times = { day, hour, min };
		return times;
	}

	/**
	 * 判断传入日期与服务器当期日期相隔的时、分、秒
	 */
	public static Long[] getDistCurrentTime(Date vDate) {
		return getDistTime(vDate, new Date());
	}

	/**
	 * 判断传入日期与服务器当期日期相隔的天、时、分
	 */
	public static Long[] getDistCurrentMinute(Date vDate) {
		return getDistMinute(vDate, new Date());
	}

	/**
	 * 返回当前日期的星期数 星期天为1，星期一为：2星期二为3，以此类推星期天为7
	 * 
	 * @return
	 */
	public static int getDayOfWeek() {
		Calendar nowDate = GregorianCalendar.getInstance();
		return nowDate.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取传入日期的星期
	 * 
	 * @param date
	 * @return
	 */
	public static String getDayWeek(Date date) {
		String[] weeks = { "sunday", "monday", "tuesday", "wednesday",
				"thursday", "friday", "saturday" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayinweek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return weeks[dayinweek];
	}

	/**
	 * 当前时间与传入时间的比较
	 */
	public static boolean getDistTime(String startTime, String endTime) {
		Calendar nowTime = Calendar.getInstance();
		Calendar sTime = Calendar.getInstance();
		Calendar eTime = Calendar.getInstance();
		nowTime.set(Calendar.SECOND, 0);
		String[] sa = startTime.split(":");
		String[] ea = endTime.split(":");
		if (sa.length > 1 && ea.length > 1) {
            int startHour = Integer.valueOf(sa[0]).intValue();
            int startMin =  Integer.valueOf(sa[1]).intValue();
            int endHour = Integer.valueOf(ea[0]).intValue();
            int endMin = Integer.valueOf(ea[1]).intValue();

            sTime.set(Calendar.HOUR_OF_DAY, startHour);
            sTime.set(Calendar.MINUTE, startMin);
            eTime.set(Calendar.HOUR_OF_DAY, endHour);
            eTime.set(Calendar.MINUTE, endMin);
            //有可能是跨凌晨营业
            if(startHour <= endHour){
                return nowTime.compareTo(sTime) > 0 && nowTime.compareTo(eTime) < 0;
            }else{
                //将这里形式的日期划分为两段21:00 - 06:00
                //21:00 - 23:59修改结束时间
                if( nowTime.get(Calendar.HOUR_OF_DAY) >= startHour &&  nowTime.get(Calendar.HOUR_OF_DAY)  <= 23){
                    return  true;
                }
                //00:00 - 06:00修改开始和结束时间
                if( nowTime.get(Calendar.HOUR_OF_DAY) >= 0 &&  nowTime.get(Calendar.HOUR_OF_DAY)  <= endHour){
                    nowTime.add(Calendar.DATE,1);
                    if( nowTime.compareTo(eTime) < 0 ){
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
	}

    public static boolean inRange(Date startDate,String startTime,Date endDate,String  endTime){
        String[] sa = startTime.split(":");
        String[] ea = endTime.split(":");
        GregorianCalendar start =  new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();
        start.setTime(startDate);
        end.setTime(endDate);
        int startHour = 0 ;
        int startMin=0;
        int endHour = 0;
        int endMin = 0;
        if (sa.length > 1 && ea.length > 1) {
            startHour = Integer.valueOf(sa[0]).intValue();
            startMin = Integer.valueOf(sa[1]).intValue();
            endHour = Integer.valueOf(ea[0]).intValue();
            endMin = Integer.valueOf(ea[1]).intValue();
        }
        start.set(Calendar.HOUR_OF_DAY,startHour);
        start.set(Calendar.MINUTE,startMin);
        start.set(Calendar.SECOND,0);
        start.set(Calendar.MILLISECOND,0);
        end.set(Calendar.HOUR_OF_DAY,endHour);
        end.set(Calendar.MINUTE,endMin);
        end.set(Calendar.SECOND,0);
        end.set(Calendar.MILLISECOND,0);
        Date now = new Date();
        return start.getTime().getTime()<=now.getTime() && end.getTime().getTime()>=now.getTime();
    }

	public static long compareDate(String date, String compareDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long time1 = 0;
		long time2 = 0;
		try {
			time1 = sdf.parse(date).getTime();
			time2 = sdf.parse(compareDate).getTime();
		} catch (ParseException e) {
			// nothing to do
		}
		return time1 - time2;
	}

	/**
	 * 根据出生日期计算年龄
	 */
	public static int getAge(Date birthDay) {
		if (birthDay == null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthDay)) {
			return 0;
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		int age = yearNow - yearBirth;
		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				age--;
			}
		}
		return age;
	}
	/**
	 * 判断时间是否超出了规定分钟
	 * @param createAt 判断的时间
     *  @param timeNum 多少分钟超时
	 * @return true超过了，false没超过
	 */
	public static boolean isTimeOut(Date createAt, int timeNum){
		Calendar nowCal = Calendar.getInstance();
		Calendar beginCal = (Calendar)nowCal.clone();
		beginCal.setTime(createAt);
		beginCal.add(Calendar.MINUTE, timeNum);
		return nowCal.getTimeInMillis() > beginCal.getTimeInMillis() ? true : false;
	}
	/**
	 * 判断当前时间是否在传入的时间格式内
	 * 传入的值格式为  09:00-18:00  或者  null  或者  ""
	 * 没有时间段就返回false,在范围内返回true
	 * @param stringTime
	 * @return
	 */
	public static boolean isInArrangTime(String stringTime){
		if(StringUtils.isBlank(stringTime)){
			return false;
		}
		String[] rangetime = stringTime.split("-");
		//格式不正确
		if(rangetime.length <= 1){
			return false;
		}
		Calendar nowCal = Calendar.getInstance();
		Calendar beginCal = (Calendar)nowCal.clone();
		Calendar endCal =  (Calendar)nowCal.clone();
		String[] beginTime = rangetime[0].split(":");
		try{
			//开始小时判断
			beginCal.set(Calendar.HOUR, Integer.valueOf(beginTime[0]));
			beginCal.set(Calendar.MINUTE, Integer.valueOf(beginTime[1]));
			String[] endTime = rangetime[1].split(":");
			//结束消息
			endCal.set(Calendar.HOUR, Integer.valueOf(endTime[0]));
			endCal.set(Calendar.MINUTE, Integer.valueOf(endTime[1]));
			if( beginCal.getTimeInMillis() <= nowCal.getTimeInMillis() && nowCal.getTimeInMillis() <= endCal.getTimeInMillis()){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			//格式不正确
			return false;
		}
	}
	/**
	 * 根据出生日期计算年龄
	 */
	public static int getAge(String strBirthDay, String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date birthDay = null;
		try {
			birthDay = df.parse(strBirthDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return getAge(birthDay);
	}

    public static Date parseEndDate(Date date,String timeStr){
        int pos = timeStr.indexOf(":");
        int hour = Integer.parseInt(timeStr.substring(0, pos));
        int minute = Integer.parseInt(timeStr.substring(pos + 1));
        Calendar gc =  GregorianCalendar.getInstance();
        gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY,hour);
        gc.set(Calendar.MINUTE,minute);
        gc.set(Calendar.SECOND,0);
        return gc.getTime();
    }

    /**
     * 检测当前日期是否在传入的日期范围内
     * 在范围内返回true;反之false
     * 不带时间比较(当天也算)
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean getRangeDate(Date startDate, Date endDate) {
        Calendar nowCalendar =  GregorianCalendar.getInstance();
        nowCalendar.setTime(DateUtil.formatDate(nowCalendar.getTime()));
        Calendar startCalendar = GregorianCalendar.getInstance();
        startCalendar.setTime(DateUtil.formatDate(startDate));
        Calendar endCalendar = GregorianCalendar.getInstance();
        endCalendar.setTime(DateUtil.formatDate(endDate));
        if(nowCalendar.after(endCalendar) || nowCalendar.before(startCalendar)){
            return false;
        }else{
            return true;
        }
    }

	/**
	 * 获取本月的第一天
	 * @return
	 */
	public static String getMonthFirstDay(Date date) {     
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
	    return dateToString(calendar.getTime(), DATE_SHORT);
	}
	/**   
	 * 得到本月的最后一天   
	 * @return   
	 */
	public static String getMonthLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return dateToString(calendar.getTime(), DATE_SHORT);
	}

	/**
	 * 计算两个时间过去的时间
	 * @param compareDate 比较的时间
	 * @param nowDate 当前时间
	 * @return
	 */
	public static String getShowTime(Date compareDate, Date nowDate) {
		long l1 = compareDate.getTime();
		long l2 = nowDate.getTime();
		long difference = Math.abs(l2 - l1);
		String showTime =  calcTime(difference / 1000);
		if(showTime == null){
			showTime = getDayHour24(compareDate);
		}
		return showTime;
	}

	public static String calcTime(long timeInSeconds) {
		long hours, minutes, seconds;
		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		seconds = timeInSeconds;
		if(hours > 24){
			return null;
		}
		if(hours > 0 && minutes == 0){
			return hours+"小时前";
		} else if(hours > 0 && minutes > 0){
			return (hours+1)+"小时前";
		} else if(minutes > 0 && seconds == 0){
			return minutes +"分钟前";
		} else {
			return (minutes + 1) +"分钟前";
		} 
	}
}