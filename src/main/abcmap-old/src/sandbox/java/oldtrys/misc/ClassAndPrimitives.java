package oldtrys.misc;

public class ClassAndPrimitives {

	public static void main(String[] args) {
		sandBox(args);
	}

	private static void sandBox(String[] args) {

		Object[] t1 = new Object[] { 1f, 1f, 1d };
		Object[] t2 = new Object[] { 1f, 1, 1d };

		for (int i = 0; i < t2.length; i++) {
			Object o1 = t1[i];
			Object o2 = t2[i];

			System.out.println("o1.getClass().isInstance(o2)");
			System.out.println(o1.getClass().isInstance(o2));
		}

	}
}
