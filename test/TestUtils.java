import java.util.Arrays;

import org.junit.Test;

import com.ponxu.utils.StringUtils;

public class TestUtils {

	@Test
	public void testArrayToString() {
		Object[] ids = { "1", "2" };
		String s = Arrays.toString(ids);
		System.out.println(s.replace("[", "(").replace("]", ")"));
	}
	
	@Test
	public void testMd5() {
		System.out.println(StringUtils.md5("499407568@qq.com"));
	}
}
