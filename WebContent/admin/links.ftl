<#import "lib/common.ftl" as com> 
<@com.page>

<script type="text/javascript">
	function del(id) {
		if (!confirm("确定删除吗?")) return;
		var url = "${appPath}/admin/Link_del.do?id=" + id;
		$.getJSON(url, null, function(data){
			if (data == 1) $("#li" + id).fadeOut();
		});
	}
</script>

<form action="${appPath}/admin/Link_add.do" method="post">
	URL
	<input type="text" name="url" id="url" class="input rcorner" style="width: 400px;"><br><br>
	名字
	<input type="text" name="name" id="name" class="input rcorner" style="width: 400px;"><br><br>
	描述
	<input type="text" name="description" id="description" class="input rcorner" style="width: 500px;">
	<input type="hidden" name="target" value="_blank">

	<input type="submit" class="btn rcorner" value="新增">
</form>

<br>
<#if list?has_content>
<ul class="cat_list">
<#list list as l>
	<li id="li${l.link_id}">
		<a href="${l.link_url}" target="${l.link_target}" title="${l.link_description}">${l.link_name}</a>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="#" onclick="del('${l.link_id}')">X</a>
	</li>
</#list>
</ul>
<#else>
没有内容!!
</#if>
</@com.page>
