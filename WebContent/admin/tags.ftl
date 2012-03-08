<#import "lib/common.ftl" as com> 
<@com.page>

<script type="text/javascript">
	function deltag(id) {
		if (!confirm("确定删除吗?")) return;
		var url = "${appPath}/admin/Taxonomy_deltag.do?id=" + id;
		$.getJSON(url, null, function(data){
			if (data == 1) $("#li" + id).fadeOut();
		});
	}
</script>

<form action="${appPath}/admin/Taxonomy_add.do" method="post">
	<input type="text" name="name" id="name" class="input rcorner" style="width: 220px;">
	<input type="hidden" name="type" id="type" value="post_tag">
	<input type="submit" class="btn rcorner" value="新增">
</form>


<#if list?has_content>
<ul class="cat_list">
<#list list as c>
	<li id="li${c.term_taxonomy_id}">
		${c.name}
		&nbsp;&nbsp;
		(${c.count})
		&nbsp;
		<a href="#" onclick="deltag('${c.term_taxonomy_id}')">X</a>
	</li>
</#list>
</ul>
<#else>
没有内容!!
</#if>
</@com.page>
