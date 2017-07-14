package com.townmc.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public final static String FULL_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";// 时间精确到年月日时分秒
	public final static String DATE_PATTERN = "yyyy-MM-dd";// 时间精确到年月日
	public final static String DATE_HOUR_PATTERN = "yyyy-MM-dd HH";// 时间精确到年月日小时
	public final static String DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";// 时间精确到年月日小时
	public final static String NOT_SEPARATOR_DATE_PATTERN = "yyyyMMdd";// 时间精确到年月日，没有分隔符

	public DateUtil() {
	}

	/**
	 * 将时间转换成规定格式的字符串
	 * 
	 * @param pattern
	 *            String 格式
	 * @param date
	 *            Date 需要转换的时间
	 * @return String 转换后的时间字符串
	 */
	public static String dateToString(String pattern, Date date) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	

	/**
	 * 将当前时间转换成字符串
	 * 
	 * @return String 转换后的时间字符串
	 */
	public static String currenDateToString() {
		return dateToString(FULL_TIME_PATTERN, new Date());
	}

	/**
	 * 将当前时间转换成指定格式的字符串
	 * 
	 * @param pattern
	 *            String 指定格式
	 * @return String 转换后的时间字符串
	 */
	public static String currenDateToString(String pattern) {
		return dateToString(pattern, new Date());
	}

	/**
	 * 比较两日期是否相等
	 * 
	 * @param pattern
	 *            String 格式
	 * @param sFirstDate
	 *            String 比较的第一个日期字符串
	 * @param sSecondlyDate
	 *            String 比较的第二个日期字符串
	 * @return boolean true 第一个日期在第二个日期之前； false 第一个日期和第二个日期相等或之后
	 */
	public static boolean compareDate(String pattern, String sFirstDate, String sSecondlyDate) {
		Date firstDate = formatDate(pattern, sFirstDate);
		Date secondlyDate = formatDate(pattern, sSecondlyDate);
		return firstDate.before(secondlyDate);
	}

	/**
	 * 格式化日期
	 * 
	 * @param pattern
	 *            String 格式
	 * @param sDate
	 *            String 日期字符串
	 * @return Date 格式化后的日期
	 */
	public static Date formatDate(String pattern, String sDate) {
		Date date = null;
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			date = dateFormat.parse(sDate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return date;
	}

	/**
	 * 功能：滚动日期
	 * 
	 * @param date
	 *            Date 需要滚动的日期
	 * @param field
	 *            int 需要滚动的字段（参照Calendar的字段）
	 * @param step
	 *            int 步长 正数时是增加时间，而负数时是减少时间
	 * @return Date 滚动后的日期
	 */
	public static Date rollDate(Date date, int field, int step) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, step);
		return calendar.getTime();
	}

	/**
	 * 设置时间
	 * 
	 * @param date
	 *            Date 设置的时间
	 * @param field
	 *            int 设置的字段（参照Calendar的字段）
	 * @param value
	 *            int 时间
	 * @return Date 设置后的时间
	 */
	public static Date setDate(Date date, int field, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(field, value);
		return calendar.getTime();
	}

	/**
	 * 获得时间中的字段的值
	 * 
	 * @param field
	 *            int 时间字段（参照Calendar的字段）
	 * @return int 时间字段值
	 */
	public static int getField(int field) {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(field);
	}

}