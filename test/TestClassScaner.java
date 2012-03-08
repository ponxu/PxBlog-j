import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ponxu.mvc.utils.ClassScaner;

public class TestClassScaner {
	@Test
	public void scan() {
		// 自定义过滤规则
		List<String> classFilters = new ArrayList<String>();
		classFilters.add("File*");

		// 创建一个扫描处理器，排除内部类 扫描符合条件的类
		ClassScaner handler = new ClassScaner(true, true, classFilters);

		System.out.println("开始递归扫描jar文件的包：org.apache.commons.io 下符合自定义过滤规则的类...");
		Set<Class<?>> calssList = handler.getPackageAllClasses("org.apache.commons.io", true);
		for (Class<?> cla : calssList) {
			System.out.println(cla.getName());
		}

		System.out.println("开始递归扫描file文件的包：michael.hessian 下符合自定义过滤规则的类...");
		classFilters.clear();
		calssList = handler.getPackageAllClasses("com.ponxu.web.action", true);
		for (Class<?> cla : calssList) {
			System.out.println(cla.getName());
		}
	}
}
