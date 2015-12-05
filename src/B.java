import com.lpsedu.helper.des3.Des3;


public class B {
	public static void main(String[] args) { // 添加新安全算法,如果用JCE就要把它添加进去
		System.out.println(Des3.encode("mysql://sa:13980439852@192.168.0.210:3306/pixian?characterEncoding=utf-8"));
		System.out.println(Des3.encode("mysql://sa:13980439852@121.201.7.201:3309/diag?characterEncoding=utf-8"));
		System.out.println(Des3.decode("XLpG1xWhTOTwpA7Yk4l+W7Sk0XEqeFDiqooc7W+5PqVNpPZ3JvLcFFTM+1Mp7RFIl8k07NAqrdRbaMvA+QulfUaozRH1EAEi"));
		System.out.println(Des3.decode("XLpG1xWhTOTwpA7Yk4l+W7Sk0XEqeFDiv9MtWgIiAnP2fup0QYAcVRFQPEfk7WUeWYBBl7qUGDkW8ytBlZsbS9BvlW1pr1Kshfy/UqbEojk="));
    }
}
