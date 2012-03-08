<#import "lib/common.ftl" as com> 
<@com.page>

<script type="text/javascript">
	function del(id) {
		if (!confirm("确定删除吗?")) return;
		var url = "${appPath}/admin/Post_del.do?id=" + id;
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
			<th>标题</th>
			<th width="180">分类/标签</th>
			<th width="180">写作时间</th>
			<th width="50">评论</th>
			<th width="50">状态</th>
			<th width="90">编辑</th>
		</tr>
	</thead>
	<tbody>
	<#list list as post>
		<tr title="${post.post_title}" id="tr${post.id}">
			<td align="center"><input type="checkbox"></td>
			<td align="center">${post.id}</td>
			<td>${post.post_title}</td>
			<td>
				<#if post_terms??>
				<ul class="tag_list">
					<#list post_terms as t>
						<#if post.id == t.object_id>
				<li>${t.name}</li>
						</#if>
					</#list>
				</ul>
				</#if>
			</td>
			<td>${post.post_date?substring(0,19)}</td>
			<td align="center"><a href="${appPath}/admin/Comment_list.do?postid=${post.id}">${post.comment_count}</a></td>
			
			<td align="center">
				<#if post.post_status='publish'>
					发布
				<#else>
					保存
				</#if>
			</td>
			
			<td align="center">
				<a href="${appPath}/admin/Post_edit.do?id=${post.id}">编辑</a>
				<a href="#" onclick="del('${post.id}')">删除</a>
			</td>
		</tr> 
	</#list>
	</tbody>
</table>
<#if page??>
	<div class="page_bar">
		<#if page.index!=1>
		<a href="${appPath}/admin/Post_all.do?pageIndex=${page.index - 1}">←前一页</a>
		</#if>
		
		${page.index} of ${page.count}
		
		<#if page.index < page.count>
		<a href="${appPath}/admin/Post_all.do?pageIndex=${page.index + 1}">后一页→</a>
		</#if>
	</div>
</#if>
</@com.page>
