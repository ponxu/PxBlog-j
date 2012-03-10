<#import "lib/common.ftl" as com>

<@com.page>
<script type="text/javascript" src="${appPath}/static/kindeditor/kindeditor-min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		var editor = KindEditor.create('textarea[name="content"]', {
			themeType: "simple",
			uploadJson: '${appPath}/admin/kindeditor_jsp/upload_json.jsp',
			fileManagerJson: "${appPath}/kindeditor_jsp/admin/file_manager_json.jsp",
			allowFileManager: true
		});
	});
	
</script>

<div class="main_width" id="postdiv">
	<form action="${appPath}/admin/Page_save.do" method="post">
		<input type="hidden" name="id" value="${post.id!'0'}">
		<input type="hidden" name="post_type" value="${post.post_type!'page'}">
		<input type="hidden" name="post_status" value="${post.post_status!'publish'}">
		<input type="hidden" name="comment_status" value="${post.comment_status!'open'}">
		
		<div class="divikep">
			<div style="float: right;">
				<input type="submit" class="btn rcorner" value=" 保 存 ">
			</div>
			标题:
			<input type="text" name="title" id="title" class="input rcorner" style="width: 500px;" value="${post.post_title!'请输入文章标题!'}">
			类型:
			<#if post.post_type=='page'>普通</#if>
			<#if post.post_type=='board'>留言</#if>
			<#if post.post_type=='about'>关于</#if>
		</div>
		<div class="divikep">
			<textarea name="content" id="content" cols="1" rows="1" style="width: 100%; height: 450px;">${post.post_content!"在这里输入内容!"}</textarea>
		</div>
	</form>
</div>
</@com.page>