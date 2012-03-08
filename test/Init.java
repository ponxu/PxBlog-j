import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ponxu.blog.service.InstallBlog;
import com.ponxu.dbutils.DBManager;
import com.ponxu.dbutils.wrap.impl.MapRowWrapper;

public class Init {
	@Test
	public void createtalbe() {
		InstallBlog.createTables(null);
	}

	private String _toUpper(String str) {
		String nstr = str;
		int i = -1;
		while ((i = nstr.indexOf("_")) > -1) {
			String last = nstr.substring(i + 1);
			nstr = nstr.substring(0, i) + last.substring(0, 1).toUpperCase() + last.substring(1);
		}
		return nstr;
	}

	@Test
	public void test_() {
		System.out.println(_toUpper("comment_id"));
	}

	@Test
	public void pojo() throws IOException {
		String pkg = "com.ponxu.pojo";
		String pojoPath = "E:/Android/workspace/PxBLog/src/com/ponxu/pojo/";

		String tablePrefix = "pxb_";
		String showTablesSql = "show tables";
		List<Map<String, String>> tables = DBManager.executeQuery(MapRowWrapper.getInstance(), showTablesSql);
		for (Map<String, String> t : tables) {
			String tablename = t.get("table_name");
			if (tablename.startsWith(tablePrefix)) {
				String pojoName = tablename.substring(tablePrefix.length());
				pojoName = pojoName.substring(0, 1).toUpperCase() + pojoName.substring(1);
				if (pojoName.endsWith("s")) pojoName = pojoName.substring(0, pojoName.length() - 1);
				pojoName = _toUpper(pojoName);
				System.out.println(pojoName);

				String descSql = "desc " + tablename;
				List<Map<String, String>> cols = DBManager.executeQuery(MapRowWrapper.getInstance(), descSql);
				System.out.println(cols);

				File java = new File(pojoPath + pojoName + ".java");
				if (java.exists()) java.delete();
				java.createNewFile();
				PrintWriter pw = new PrintWriter(java);

				pw.println("package " + pkg + ";");
				pw.println();
				pw.println("import com.ponxu.annotation.Col;");
				pw.println("import com.ponxu.annotation.PK;");
				pw.println("import com.ponxu.annotation.Table;");
				pw.println();
				pw.println("@Table(\"" + tablename.substring(tablePrefix.length()) + "\")");
				pw.println("public class " + pojoName + " extends POJO {");

				for (Map<String, String> col : cols) {
					String colName = col.get("column_name").toLowerCase();
					String type = col.get("column_type");
					String key = col.get("column_key");

					if (type.indexOf("int") > -1) type = "long";
					else if (type.indexOf("date") > -1) type = "Date";
					else type = "String";

					if ("PRI".equalsIgnoreCase(key)) pw.println("@PK(\"" + colName + "\")");
					pw.println("@Col(\"" + colName + "\")");
					pw.println("private " + type + " " + _toUpper(colName) + ";");
				}

				pw.print("}");
				pw.flush();
				pw.close();
			}
		}
	}
}
