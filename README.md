## 通用帮助类
### 说明
经常用到的对象、日期、数字、字符串、线程、json、xml、excel、http请求帮助类。  

### 日期
`DateUtil`  

```java
/**
 * 获得UTC时间
 * @return Date
 */
public static Date utcDate();

/**
 * 将当前时间转换成字符串
 *
 * @return String 转换后的时间字符串
 */
public static String dateToString();

/**
 * 将当前时间转换成指定格式的字符串
 *
 * @param pattern
 *            String 指定格式
 * @return String 转换后的时间字符串
 */
public static String dateToString(String pattern);

/**
 * 将时间转换成规定格式的字符串
 *
 * @param pattern
 *            String 格式
 * @param date
 *            Date 需要转换的时间
 * @return String 转换后的时间字符串
 */
public static String dateToString(String pattern, Date date);

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
public static String dateToString(String pattern, Date date, TimeZone zone);

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
public static boolean compareDate(String pattern, String sFirstDate, String sSecondlyDate);

/**
 * 格式化日期
 *
 * @param pattern
 *            String 格式
 * @param sDate
 *            String 日期字符串
 * @return Date 格式化后的日期
 */
public static Date formatDate(String pattern, String sDate);

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
public static Date rollDate(Date date, int field, int step);

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
public static Date setDate(Date date, int field, int value);

/**
 * 获得时间中的字段的值
 *
 * @param field
 *            int 时间字段（参照Calendar的字段）
 * @return int 时间字段值
 */
public static int getField(int field);

/**
 * 两个时间的差距。目前支持秒、分钟、小时、天、月、年
 * 两个时间参数不分前后。效果一样。
 * @param one 第一个时间
 * @param two 第二个时间
 * @param precision 精度 与Calendar中的常量定义对应 13表示秒 12表示分钟 11表示小时 6表示天 2表示月 1表示年
 * @return long
 */
public static long difference(Date one, Date two, int precision);
```

### 字符串
`StringUtil`  

```java

```
