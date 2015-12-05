import java.util.Random;

public class Test {
	public static void main(String[] args) {
		int len = 10000;
		int[] arr = new int[len];
		int[] arr2 = new int[len];
		Random random = new Random(arr.length);
		for (int i = 0; i < arr.length; i++) {
			int t = random.nextInt();
			arr[i] = t;
			arr2[i] = t;
		}
		sort2(arr2);
		sort(arr);
		for (int i = 0; i < arr2.length; i++) {
			if (arr[i] != arr2[i]) {
				System.out.println("no");
			}
		}
	}
	public static void sort(int[] arr) {
		long s = System.currentTimeMillis();
		for (int i = 0; i < arr.length - 1; i++) {
			for (int j = i; j < arr.length; j++) {
				if (arr[i] > arr[j]) {
					swap(arr, i, j);
				}
			}
		}
		long e = System.currentTimeMillis();
		System.out.println(e - s);
	}
	public static void sort2(int[] arr) {
		long s = System.currentTimeMillis();
		for (int i = 0; i < arr.length - 1; i++) {
			for (int j = i; j < arr.length; j++) {
				if (arr[i] > arr[j]) {
					swap2(arr, i, j);
				}
			}
		}
		long e = System.currentTimeMillis();
		System.out.println(e - s);
	}
	public static void swap(int[] arr, int i, int j) {
		arr[i] = arr[i] ^ arr[j];
		arr[j] = arr[i] ^ arr[j];
		arr[i] = arr[i] ^ arr[j];
	}
	public static void swap2(int[] arr, int i, int j) {
		int t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
}
