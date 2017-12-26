<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<title>管理平台</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet"
	href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css"
	integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
	crossorigin="anonymous">
<script type="text/javascript"
	src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
</head>
<body>
	<div class="container">
		<div class="col-md-4 col-md-offset-4">
			<h1 class="text-center">请登录</h1>
			<br>
			<form action="${pageContext.request.contextPath }/user/login"
				method="post" onsubmit="return checkLogin();">
				<div class="form-group">
					<label for="username">登录用户</label> <input type="text"
						class="form-control" name="username" id="username"
						placeholder="用户名">
				</div>
				<div class="form-group">
					<label for="password">登录密码</label> <input type="password"
						class="form-control" id="password" name="password"
						placeholder="密码">
				</div>
				<div class="form-group">
					<label for="vcode">验证码</label><input type="text"
						class="form-control" id="vcode" name="vcode" placeholder="验证码"><span
						class="help-block"></span><img id="img_vcode" alt="验证码"
						src="${pageContext.request.contextPath }/user/vcode">
				</div>
				<button type="submit" class="btn btn-success">登录</button>
				<%-- <a href="${pageContext.request.contextPath }/user/registUI"
					class="btn btn-info">注册</a> --%>
			</form>

			<h4 class="text-danger">${sessionScope.login_msg }</h4>
		</div>
	</div>
	<%-- 	<%@ include file="../common/down.jsp"%> --%>
	<script type="text/javascript">
		$("#img_vcode")
				.click(
						function() {
							var path = '${pageContext.request.contextPath }/user/vcode?time='
									+ new Date();
							$("#img_vcode").attr("src", path);
						});
		function checkLogin() {
			var username = $("#username").val();
			var password = $("#password").val();
			var vcode = $("#vcode").val();
			if (username == "" || password == "" || vcode == "") {
				alert("请填写完整");
				return false;
			}
			return true;
		}
	</script>
</body>
</html>