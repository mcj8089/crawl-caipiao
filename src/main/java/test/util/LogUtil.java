package test.util;

public class LogUtil {

	public static void print(Object message) {
		System.out.println(message);
	}

	public static void logInfo(Object... a) {
		for( Object t : a ) {
			System.out.print(t + " ");
		}
		System.out.println();
	}
	
}