<#import "lib/common.ftl" as com> 
<@com.page>

<script type="text/javascript">
	function del(id) {
		if (!confirm("确定删除吗?")) return;
		var url = "${appPath}/admin/Comment_del.do?id=" + id;
		$.getJSON(url, null, function(data){
			if (data == 1) $("#tr" + id).fadeOut();
		});
	}
</script>

<table class="list rcorner">
	<thead>
		<tr>
			<th width="20"><input type="checkbox"></th>
			<th width="50">序号</th>
			<th>内容</th>
			<th width="180">作者</th>
			<th width="180">Email</th>
			<th width="180">网站</th>
			
			<th width="180">时间</th>
			<th width="90">编辑</th>
		</tr>
	</thead>
	<tbody>
	<#list list as c>
		<tr id="tr${c.comment_id}">
			<td align="center"><input type="checkbox"></td>
			<td align="center">${c.comment_id}</td>
			
			<td align="center">${c.comment_content}</td>
			
			<td>${c.comment_author}</td>
			<td>${c.comment_author_email}</td>
			<td><a href="${c.comment_author_url}" target="_blank">${c.comment_author_url}</a></td>
			
			<td>${c.comment_date?substring(0,19)}</td>
			<td align="center">
				<a href="#" onclick="del('${c.comment_id}')">删除</a>
			</td>
		</tr> 
	</#list>
	</tbody>
</table>
<#if page??>
	<div class="page_bar">
		<#if page.index!=1>
		<a href="${appPath}/admin/Comment_list.do?postid=${postid!'0'}&pageIndex=${page.index - 1}">←前一页</a>
		</#if>
		
		${page.index} of ${page.count}
		
		<#if page.index < page.count>
		<a href="${appPath}/admin/Comment_list.do?postid=${postid!'0'}&pageIndex=${page.index + 1}">后一页→</a>
		</#if>
	</div>
</#if>
</@com.page>
