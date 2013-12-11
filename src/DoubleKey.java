public class DoubleKey {
	public static Integer getKey(double d) {
		return (int) Math.floor(d * 1000);
	}
}
