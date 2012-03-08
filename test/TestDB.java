import org.junit.Test;

import com.ponxu.dbutils.DBManager;
import com.ponxu.dbutils.wrap.impl.MapRowWrapper;

public class TestDB {
	@Test
	public void wrap() {
		System.out.println(DBManager.executeQuery(MapRowWrapper.getInstance(), "select * from non_option"));
	}
}
