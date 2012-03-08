import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestReg {
	@Test
	public void reg() {
		String reg = "/post/(.*?)(\\d*?)\\.html";
		String str = "/post/77.html";
		Pattern p = Pattern.compile(reg);

		Matcher m = p.matcher(str);
		if (m.find()) {
			System.out.println("count: " + m.groupCount());
			System.out.println("g0: " + m.group(0));
			System.out.println("g1: " + m.group(1));
			System.out.println("g2: " + m.group(2));
		}
	}
	
	@Test
	public void reg2() {
		String reg = "/post(.*)";
		String str = "/post";
		Pattern p = Pattern.compile(reg);

		Matcher m = p.matcher(str);
		if (m.find()) {
			System.out.println("count: " + m.groupCount());
			System.out.println("g0: " + m.group(0));
			System.out.println("g1: " + m.group(1));
			System.out.println("g2: " + m.group(2));
		}
	}
}
