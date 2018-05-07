package com.sweet.util.support;


import com.sweet.util.value.CommonValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.BitSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 * </p>
 * 
 * @author suxl
 *
 */
public class StringUtil extends StringUtils {

	protected static Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

	public static void main(String[] args) throws Exception {
		// String str = getSubstring("asda", 0, 15, "...");
		// String str2 = "400-699-6956";
		/*
		 * for(int i=0; i<10; i++){ int jj = getRandom(5800, 7100);
		 * System.out.println(jj + " "+(jj+64)); }
		 */
		/*
		 * JSONObject jo = new JSONObject(); jo.put("scs", "SUCCESS");
		 * System.out.println(JSONObject.toJSONString(new
		 * ResultInfo<JSONObject>(jo)));
		 */
		/*String time = "2016-12-01 00:00:00";
		int year = Integer.valueOf(time.substring(0, 4));
		System.out.println(year);*/
	}

	/**
	 * 获取范围内随机整数
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandom(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	/**
	 * 随机一个boolean值
	 * 
	 * @return
	 */
	public static boolean nextBoolean() {
		return Math.random() < 0.5;
	}

	private static BitSet dontNeedEncoding;

	static {
		dontNeedEncoding = new BitSet(256);
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			dontNeedEncoding.set(i);
		}
		dontNeedEncoding.set('+');
		/**
		 * 这里会有误差,比如输入一个字符串 123+456,它到底是原文就是123+456还是123 456做了urlEncode后的内容呢？
		 * <br>
		 * 其实问题是一样的，比如遇到123%2B456,它到底是原文即使如此，还是123+456 urlEncode后的呢？ <br>
		 * 在这里，我认为只要符合urlEncode规范的，就当作已经urlEncode过了<br>
		 * 毕竟这个方法的初衷就是判断string是否urlEncode过<br>
		 */

		dontNeedEncoding.set('-');
		dontNeedEncoding.set('_');
		dontNeedEncoding.set('.');
		dontNeedEncoding.set('*');
	}

	/**
	 * 判断str是否urlEncoder.encode过<br>
	 * 经常遇到这样的情况，拿到一个URL,但是搞不清楚到底要不要encode.<Br>
	 * 不做encode吧，担心出错，做encode吧，又怕重复了<Br>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean hasUrlEncoded(String str) {

		/**
		 * 支持JAVA的URLEncoder.encode出来的string做判断。 即: 将' '转成'+' <br>
		 * 0-9a-zA-Z保留 <br>
		 * '-'，'_'，'.'，'*'保留 <br>
		 * 其他字符转成%XX的格式，X是16进制的大写字符，范围是[0-9A-F]
		 */
		boolean needEncode = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (dontNeedEncoding.get((int) c)) {
				continue;
			}
			if (c == '%' && (i + 2) < str.length()) {
				// 判断是否符合urlEncode规范
				char c1 = str.charAt(++i);
				char c2 = str.charAt(++i);
				if (isDigit16Char(c1) && isDigit16Char(c2)) {
					continue;
				}
			}
			// 其他字符，肯定需要urlEncode
			needEncode = true;
			break;
		}

		return !needEncode;
	}

	/**
	 * 判断c是否是16进制的字符
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isDigit16Char(char c) {
		return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F');
	}

	/**
	 * 返回<b>需要的</b>字符串长度
	 * 
	 * @param str
	 * @param start
	 * @param end
	 * @param suffix
	 *            - 自定义后缀
	 * @return
	 */
	public static String getSubstring(String str, int start, int end, String suffix) {
		if (isBlank(str)) {
			return str;
		}
		if (suffix == null) {
			suffix = "";
		}
		int length = str.length();
		if (length > end) {
			return str.substring(start, end) + suffix;
		}

		return str;
	}

	/**
	 * 获取32位uuid唯一识别码，去掉中间的"-"
	 * 
	 * @return
	 */
	public static String getUUID() {
		String str = UUID.randomUUID().toString();
		return str.replaceAll("-", "");
	}

	/**
	 * 评分格式化
	 * 
	 * @return
	 */
	public static String fmtScore(Object score) {
		if (null == score || !NumberUtils.isNumber(score + "")) {
			return null;
		}
		double s = Math.abs(NumberUtils.toDouble(score + ""));
		/*
		 * if (s == 0) { return null; }
		 */
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return decimalFormat.format(s);
	}

	/**
	 * 价格格式化
	 * 
	 * @return
	 */
	public static String fmtPrice(Object price) {
		if (null == price || !NumberUtils.isNumber(price + "")) {
			return null;
		}
		double p = Math.abs(NumberUtils.toDouble(price + ""));
		/*
		 * if (p == 0) { return null; }
		 */
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		return decimalFormat.format(p);
	}

	/**
	 * 距离格式化
	 * 
	 * @param distance
	 * @return
	 */
	public static String fmtDistance(Object distance) {
		if (null == distance || !NumberUtils.isNumber(distance + "")) {
			return null;
		}
		double d = Math.abs(NumberUtils.toDouble(distance + ""));
		/*
		 * if (d == 0) { return null; }
		 */
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		return decimalFormat.format(d);
	}

	/**
	 * unicode转换成中文
	 * 
	 * @param theString
	 * @return
	 */
	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	/**
	 * 创建10位长度时间戳
	 * 
	 * @return
	 */
	public static String createTimestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	public static Integer bigDecimalToFen(BigDecimal fee) {
		if (null != fee) {
			Double dFee = fee.doubleValue() * 100;
			return dFee.intValue();
		}
		return 0;
	}

	/**
	 * Base64编码字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String base64Encoder(String str) throws Exception {
		if (StringUtil.isBlank(str)) {
			return str;
		}
		return Base64.getEncoder().encodeToString(str.getBytes(CommonValue.CHARSET));
	}

	/**
	 * Base64解码字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String base64Decoder(String str) throws Exception {
		if (StringUtil.isBlank(str)) {
			return str;
		}
		return new String(Base64.getDecoder().decode(str.getBytes(CommonValue.CHARSET)), CommonValue.CHARSET);
	}

	/**
	 * 获取括号内的内容，如：
	 * background-image:url(http://mmbiz.qpic.cn/mmbiz_jpg/yHA...wr3swA/0?wx_fmt=jpeg);
	 * 
	 * @param managers
	 * @return
	 */
	public static String getInsideContent(String managers) {
		Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
		Matcher matcher = pattern.matcher(managers);
		while (matcher.find()) {
			return matcher.group();
		}
		return "";
	}
	
	/**
	 * 判断字符串是否以数字结尾
	 * @param managers
	 * @return
	 */
	public static boolean isEndingWithNum(String str) {
		boolean flag = false;
		if(isNotBlank(str)){
			Pattern pattern = Pattern.compile("\\d+$");
			Matcher matcher = pattern.matcher(str);
			if(matcher.find()){
				flag = true;
			}
		}
		return flag;
	}
}
