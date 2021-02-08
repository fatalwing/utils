package com.townmc.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期时间处理帮助类
 * @author meng
 */
public class DateUtil {
	/**
	 * 时间精确到年月日时分秒
	 */
	public final static String FULL_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 时间精确到年月日
	 */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/**
	 * 时间精确到年月日小时
	 */
	public final static String DATE_HOUR_PATTERN = "yyyy-MM-dd HH";
	/**
	 * 时间精确到年月日小时
	 */
	public final static String DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
	/**
	 * 时间精确到年月日，没有分隔符
	 */
	public final static String NOT_SEPARATOR_DATE_PATTERN = "yyyyMMdd";

	/**
	 * 时间类型
	 */
	public enum TIME_ZONE {UTC, GMT, CST}

	public DateUtil() {
	}

	/**
	 * 获得UTC时间
	 * @return Date
	 */
	public static Date utcDate() {
		// 1、取得本地时间：
		Calendar cal = Calendar.getInstance() ;
		// 2、取得时间偏移量：
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		// 3、取得夏令时差：
		int dstOffset = cal.get(Calendar.DST_OFFSET);
		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		return cal.getTime() ;
	}

	/**
	 * 将当前时间转换成字符串
	 *
	 * @return String 转换后的时间字符串
	 */
	public static String dateToString() {
		return dateToString(FULL_TIME_PATTERN, new Date());
	}

	/**
	 * 将当前时间转换成指定格式的字符串
	 *
	 * @param pattern
	 *            String 指定格式
	 * @return String 转换后的时间字符串
	 */
	public static String dateToString(String pattern) {
		return dateToString(pattern, new Date());
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
        return dateToString(pattern, date, null);
    }

	/**
	 * 将时间转换成规定格式的字符串
	 *
	 * @param pattern
	 *            String 格式
	 * @param date
	 *            Date 需要转换的时间
     * @Param zone
     *            TimeZone 时区
	 * @return String 转换后的时间字符串
	 */
	public static String dateToString(String pattern, Date date, TimeZone zone) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		if (null != zone) {
			format.setTimeZone(zone);
		}
		return format.format(date);
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

	/**
	 * 两个时间的差距。目前支持秒、分钟、小时、天、月、年
	 * 两个时间参数不分前后。效果一样。
	 * @param one 第一个时间
	 * @param two 第二个时间
	 * @param precision 精度 与Calendar中的常量定义对应 13表示秒 12表示分钟 11表示小时 6表示天 2表示月 1表示年
	 * @return long
	 */
	public static long difference(Date one, Date two, int precision) {
		if (null == one || null == two) {
			throw new LogicException("parameter_is_null", "日期参数不能为空");
		}

		Calendar calOne = Calendar.getInstance();
		calOne.setTime(one.compareTo(two) < 0 ? one : two);

		Calendar calTwo = Calendar.getInstance();
		calTwo.setTime(one.compareTo(two) < 0 ? two : one);

		long diffYear, diffMonth, diffDay, diffHour, diffMinute, diffSecond;

		// 计算年的间隔
		diffYear = calTwo.get(Calendar.YEAR) - calOne.get(Calendar.YEAR);

		// 计算天的间隔
		diffDay = diffYear * 365 + calTwo.get(Calendar.DAY_OF_YEAR) - calOne.get(Calendar.DAY_OF_YEAR);
		// 碰上闰年要多加一天
		for (int i = calOne.get(Calendar.YEAR); i < calTwo.get(Calendar.YEAR); i++) {
			if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
				diffDay++;
			}
		}

		// 计算月的间隔
		diffMonth = (diffYear * 12) + calTwo.get(Calendar.MONTH) - calOne.get(Calendar.MONTH);

		// 计算小时间隔
		diffHour = (diffDay * 24) + calTwo.get(Calendar.HOUR_OF_DAY) - calOne.get(Calendar.HOUR_OF_DAY);

		// 计算分钟间隔
		diffMinute = (diffHour * 60) + calTwo.get(Calendar.MINUTE) - calOne.get(Calendar.MINUTE);

		// 计算秒间隔
		diffSecond = (diffMinute * 60) + calTwo.get(Calendar.SECOND) - calOne.get(Calendar.SECOND);

		long result;
		switch (precision) {
			case Calendar.YEAR:
				result = diffYear;
				break;
			case Calendar.MONTH:
				result = diffMonth;
				break;
			case Calendar.DAY_OF_YEAR:
				result = diffDay;
				break;
			case Calendar.HOUR_OF_DAY:
				result = diffHour;
				break;
			case Calendar.MINUTE:
				result = diffMinute;
				break;
			case Calendar.SECOND:
				result = diffSecond;
				break;
			default:
				throw new LogicException("parameter_precision_not_support", "精度参数不支持。仅支持：1年份2月份6天数11小时12分钟13秒");
		}

		return result;
	}

    public static String[] fetchAllTimeZoneIds() {
		String[] ids = TimeZone.getAvailableIDs();
		Vector<String> v = new Vector<>(Arrays.asList(ids));
        v.sort(String.CASE_INSENSITIVE_ORDER);
        v.copyInto(ids);
		return ids;
    }

}
