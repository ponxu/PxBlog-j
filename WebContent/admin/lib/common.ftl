<#macro page>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>${blog.g('blogname')} - 后台管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=${.output_encoding}">

<link href="${appPath}/static/css/common.css" rel="stylesheet">
<link href="${appPath}/admin/css/admin.css" rel="stylesheet">
<link href="${appPath}/static/qtip/jquery.qtip.min.css" rel="stylesheet">

<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="${appPath}/static/qtip/jquery.qtip.min.js"></script>
<script type="text/javascript" src="${appPath}/static/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${appPath}/static/validate/messages_cn.js"></script>

<script type="text/javascript">
	function showTip(tip) {
		$("#tipbar").qtip({
			style: {  classes: 'ui-tooltip-shadow ui-tooltip-rounded' },
			content: {text: tip},
			position: {
				my: "left center",
				at: "center center",
				viewport: $(window)
			},
			show: { event: false, ready: true },
			hide: { event: "mouseout", inactive: 5000 }
		});
	}
	
	$(function(){
		<#if message??>
		$('${message}');
		</#if>
		// 右侧宽度适应屏幕
		$("#main_box").width(document.body.clientWidth - 20 - 150);
		
		// 选中菜单栏
		var loc = location + "";
		$("ul.nav li a").each(function(i, n){
			var nhref = $(n).attr("href");
			if (loc.indexOf(nhref) > -1) {
				$(n).attr("id", "selected");
				return false;
			}
		});
	});
</script>


</head>
<body>
<div id="headerbg" class="shadow"></div>
<div id="headerbar">
	<h3 style="margin-top: 0px;"><a href="${appPath}/admin/Index.do" id="tipbar">控制面板</a></h3>
	<h4><a href="#">博客内容</a></h3>
	<ul class="nav">
		<li><a href="${appPath}/admin/Post_all.do">所有文章</a></li>
		<li><a href="${appPath}/admin/Post_edit.do">编辑文章</a></li>
		<li><a href="${appPath}/admin/Comment_list.do">评论管理</a></li>

		<li><a href="${appPath}/admin/Taxonomy_cat.do">分类目录</a></li>
		<li><a href="${appPath}/admin/Taxonomy_tag.do">文章标签</a></li>

		<li><a href="${appPath}/admin/Page_all.do">所有页面</a></li>
		<li><a href="${appPath}/admin/Page_edit.do">编辑页面</a></li>
		<li><a href="${appPath}/admin/Link.do">所有链接</a></li>
	</ul>
	
	<h4><a href="#">博客设置</a></h3>
	<ul class="nav">
		<li><a href="${appPath}/admin/PostManage.do">用户管理</a></li>
		<li><a href="${appPath}/admin/Blog.do">外观</a></li>
		<li><a href="${appPath}/admin/Blog.do">常规管理</a></li>
	</ul>
</div>

<div id="main_box">
<div class="main_width" id="topbar">
	<a href="${blog.g('blogurl')}" target="_blank">${blog.g('blogname')}</a>
	<a href="${appPath}/login_out" style="float: right;">退出</a>
</div>
<#nested>
</div>

</body>
</html>    
</#macro>