package test.util;

public class StrUtil  {
	
	public static boolean isEmpty(String text) {
		if (text == null || text.length() <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isNotEmpty(String text) {
		return !isEmpty(text);
	}
	
	public static String getLast(String text, Integer len) {
		if( StrUtil.isEmpty(text) ) {
			return "";
		}
		if( text.length() < len ) {
			return text;
		}
		return text.substring(text.length() - len);
	}
	
    //Unicode转中文方法
	public static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }
 
    //中文转Unicode
	public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            returnStr += "\\u" + Integer.toString(chars[i], 16);
        }
        return returnStr;
    }
}
