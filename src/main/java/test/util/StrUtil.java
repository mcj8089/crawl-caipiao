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
	
    //Unicodeת���ķ���
	public static String unicodeToCn(String unicode) {
        /** �� \ u �ָ��Ϊjavaע��Ҳ��ʶ��unicode������м����һ���ո�*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // ����unicode�ַ����� \ u ��ͷ����˷ָ���ĵ�һ���ַ���""��
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }
 
    //����תUnicode
	public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            returnStr += "\\u" + Integer.toString(chars[i], 16);
        }
        return returnStr;
    }
}
