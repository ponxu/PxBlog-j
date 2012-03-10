<#import "lib/common.ftl" as com> 
<@com.page>

<script type="text/javascript">
	function del(id) {
		if (!confirm("确定删除吗?")) return;
		var url = "${appPath}/admin/Page_del.do?id=" + id;
		$.getJSON(url, null, function(data){
			if (data == 1) $("#tr" + id).fadeOut();
		});
	}
	
	function to(type) {
		location = "${appPath}/admin/Page_edit.do?type=" + type;
	}
	
</script>

<p style="text-align: right;">
新建:
<input type="button" class="btn rcorner" onclick="to('page')" value="普通页面">
<input type="button" class="btn rcorner" onclick="to('about')" value="关于页面">
<input type="button" class="btn rcorner" onclick="to('board')" value="留言页面">
</p>

<table class="list rcorner">
	<thead>
		<tr>
			<th width="20"><input type="checkbox"></th>
			<th width="50">序号</th>
			<th>标题</th>
			<th width="60">类型</th>
			<th width="180">写作时间</th>
			<th width="90">编辑</th>
		</tr>
	</thead>
	<tbody>
	<#list list as post>
		<tr title="${post.post_title}" id="tr${post.id}">
			<td align="center"><input type="checkbox"></td>
			<td align="center">${post.id}</td>
			<td>${post.post_title}</td>
			<td align="center">${post.post_type}</td>
			<td>${post.post_date?substring(0,19)}</td>
			<td align="center">
				<a href="${appPath}/admin/Page_edit.do?id=${post.id}&type=${post.post_type}">编辑</a>
				<a href="#" onclick="del('${post.id}')">删除</a>
			</td>
		</tr> 
	</#list>
	</tbody>
</table>
<#if page??>
	<div class="page_bar">
		<#if page.index!=1>
		<a href="${appPath}/admin/Page_all.do?pageIndex=${page.index - 1}">←前一页</a>
		</#if>
		
		${page.index} of ${page.count}
		
		<#if page.index < page.count>
		<a href="${appPath}/admin/Page_all.do?pageIndex=${page.index + 1}">后一页→</a>
		</#if>
	</div>
</#if>
</@com.page>
