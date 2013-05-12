<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style type="text/css">
div#input-box
{
 float: left;
 margin-left:400px;
 margin-top:100px;

}
div#title
{
 float: left;
 margin-top:200px;
 margin-left:480px;

}
div
{
	float: left;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<title>请先登录 - gugugua-web!</title>
</head>
<body>
<div id="title">
	<h2>gugugua-web!</h2>
</div>
<div style="clear:both;"></div>
<div id="input-box">	
	<form action="" method="post">
	<div class="input-box">
		<label>输入邮箱：</label>
		<input type="email" name="email" required="required"></input>
	</div>
	<div class="input-box">
		<label>密码：</label>
		<input type="password" name="password" required="required"></input>
	</div>
	<div>
		<input type="submit" value="登录"></input>
		<input type="hidden" value="${redirectUrl}" name="redirectUrl"/>
		<a href="./register"><input type="submit" value="注册"></input></a>
	</div>
</form>
<div class="input-box">
</body>
</html>