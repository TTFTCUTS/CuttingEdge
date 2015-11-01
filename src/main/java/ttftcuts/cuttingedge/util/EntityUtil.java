package ttftcuts.cuttingedge.util;

public class EntityUtil {
	private static int id = 0;
	
	public static int getNextEntityID() {
		return id++;
	}
}
