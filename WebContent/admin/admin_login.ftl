<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>${blog.g('blogname')} - 登录</title>
<meta http-equiv="Content-Type" content="text/html; charset=${.output_encoding}">
<link href="${appPath}/static/css/common.css" rel="stylesheet">
</head>
<body>
	<div id="logindiv" style="margin: 250px auto 0px auto; width: 660px; text-align: center;">
		<form action="${appPath}/login_in" method="post">
			<label for="username">用户名:</label>
			<input type="text" class="input rcorner" style="width: 200px;" name="username" id="username">
			
			&nbsp;&nbsp;&nbsp;
			<label for="username">密码:</label>
			<input type="password" class="input rcorner" style="width: 200px;" name="password" id="password">

			&nbsp;&nbsp;&nbsp;
			<input type="submit" class="btn rcorner" value=" 登  录 ">
		</form>
	</div>
	<#if message??>
	<div style="width: 100%; margin-top: 50px; text-align: center; color: #f10;">${message}</div>
	</#if>
</body>
</html>