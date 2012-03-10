<#macro body title=''>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<#if title=''>
<title>${blog.g('blogname')}</title>
<#else>
<title>${title}</title>
</#if>
<meta http-equiv="Content-Type" content="text/html; charset=${.output_encoding}">

<link href="${blog.theme}/default.css" rel="stylesheet">

</head>
<body>
<div id="main_width">
	<div id="header_bar" class="hava_red_a">
		<div class="header-left">
			<div style="height: 25px;"></div>
			<h1>&nbsp;&nbsp;<a href="${blog.g('blogurl')}">${blog.g('blogname')}</a></h1>
		</div>
		<div class="header-left">
			<div style="height: 40px;"></div>
			(${blog.g('blogdescription')})
		</div>
		<div class="header-right">
			<div style="height: 40px;"></div>
			<ul id="header_menu">
				<li><a href="${blog.g('blogurl')}">首页</a></li>
				<li><a href="${blog.g('blogurl')}/tag">标签</a></li>
				<li><a href="${blog.g('blogurl')}/board">留言</a></li>
				<li><a href="${blog.g('blogurl')}/about">关于</a></li>
			</ul>
		</div>
	</div><!--header_bar-->
	
	<div id="content">
	<#nested>
	
	<div class="clear"></div>
	</div><!--content-->
	
	<div id="footer" class="hava_red_a">
		<a href="${appPath}/login">登录</a>
		查询:${blog.queryCount}
		<#if blog.querySql?has_content>
		<ul>
			<#list blog.querySql as sql>
			<li>${sql}</li>
			</#list>
		</ul>
		</#if>
	</div><!--footer-->
</div><!--main_width-->
</body>
</html>
</#macro>
