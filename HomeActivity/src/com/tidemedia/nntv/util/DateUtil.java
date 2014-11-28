package com.tidemedia.nntv.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	
	public static long toTimeMillis(String time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date parse = sdf.parse(time);
			return parse.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 使用默认格式(yyyy-MM-dd HH:mm:ss e.g 2014-03-24 12:00:00)解析日期，返回日期对应值
	 * @param time
	 * @return
	 */
	public static long toTimeMillis(String time) {
		return toTimeMillis(time, DEFAULT_TIME_FORMAT);
	}
	
	public static String toDateString(long time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}
	
	public static String toTimebackString(String time) {
		long timeMillis = toTimeMillis(time);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long today = cal.getTimeInMillis();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		long thisYear = cal.getTimeInMillis();
		cal.setTimeInMillis(timeMillis);
		if (timeMillis >= today) {
			long timeLag = System.currentTimeMillis() - timeMillis;
			if (timeLag <= 0) {
				return "1秒前";
			} else if (timeLag < MINUTE) {
				return timeLag / SECOND + "秒前";
			} else if (timeLag < HOUR) {
				return timeLag / MINUTE + "分钟前";
			} else {
				return "今天 " + toCouple(cal.get(Calendar.HOUR_OF_DAY)) + ":" + toCouple(cal.get(Calendar.MINUTE));
			}
		} else if (timeMillis >= thisYear) {
			return (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日 " + toCouple(cal.get(Calendar.HOUR_OF_DAY)) + ":" + toCouple(cal.get(Calendar.MINUTE));
		} else {
			return cal.get(Calendar.YEAR) + "年 " + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日 " + toCouple(cal.get(Calendar.HOUR_OF_DAY)) + ":" + toCouple(cal.get(Calendar.MINUTE));
		}
	}
	
	private static String toCouple(int n) {
		if (n < 0 || n > 99) {
			throw new IllegalArgumentException("n's condition : 0 <= n < 100");
		}
		return n < 10 ? "0" + n : "" + n;
	}
}
