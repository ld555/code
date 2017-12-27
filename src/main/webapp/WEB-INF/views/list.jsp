<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
	<div class="modal fade" id="infoAddModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">信息添加</h4>
				</div>
				<form id="modal_form" class="form-horizontal"
					action="${pageContext.request.contextPath }/user/add" method="post">
					<div class="modal-body">
						<div class="form-group">
							<label class="col-sm-2 control-label">姓名</label>
							<div class="col-sm-10">
								<input type="text" name="realname" id="realname"
									class="form-control" placeholder="姓名"> <span
									class="help-block"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10">
								<input type="text" name="email" id="email" class="form-control"
									placeholder="邮箱"> <span class="help-block"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">QQ</label>
							<div class="col-sm-10">
								<input type="text" name="qq" id="qq" class="form-control"
									placeholder="QQ"> <span class="help-block"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">微信</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" name="wechat"
									id="wechat" placeholder="微信"> <span class="help-block"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">电话</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" name="telephone"
									id="telephone" placeholder="电话"> <span
									class="help-block"></span>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button type="submit" class="btn btn-primary" id="add_user">保存</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="container">
		<br> <br>
		<div class="col-md-12 text-right">
			<!-- 	<button id="info_refresh_list" type="button" class="btn btn-primary">刷新列表</button>
			<button id="info_searchByCondition" type="button"
				class="btn btn-warning">条件查询</button> -->
			<button id="btn_add_user" type="button" class="btn btn-success">添加用户</button>
			<!-- <button id="info_add_modal_btn" type="button" class="btn btn-info">单条添加</button>
			<button id="info_del_modal_btn" class="btn btn-danger">删除选择</button> -->
		</div>
		<div class="col-md-12">
			<table class="table table-striped table-hover">
				<thead>
					<tr>
						<th>id</th>
						<th>姓名</th>
						<th>邮箱</th>
						<th>QQ</th>
						<th>微信</th>
						<th>电话</th>
						<th>序列码</th>
						<th>注册时间</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${requestScope.pageInfo.list }" var="item">
						<tr>
							<td>${item.id }</td>
							<td><c:if test="${empty item.realname}">无</c:if> <c:if
									test="${!empty item.realname}">${item.realname}</c:if></td>
							<td><c:if test="${empty item.email}">无</c:if> <c:if
									test="${!empty item.email}">${item.email}</c:if></td>
							<td><c:if test="${empty item.qq}">无</c:if> <c:if
									test="${!empty item.qq}">${item.qq}</c:if></td>
							<td><c:if test="${empty item.wechat}">无</c:if> <c:if
									test="${!empty item.wechat}">${item.wechat}</c:if></td>
							<td><c:if test="${empty item.telephone}">无</c:if> <c:if
									test="${!empty item.telephone}">${item.telephone}</c:if></td>
							<td>${item.openid }</td>
							<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"
									value="${item.addtime }"></fmt:formatDate></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<div class="col-md-12">
			<nav aria-label="Page navigation">
				<ul class="pagination">
					<li><a
						href="${pageContext.request.contextPath}/user/list?pn=1">首页 </a></li>
					<li><a
						href="${pageContext.request.contextPath}/user/list?pn=${requestScope.pageInfo.prePage }"
						aria-label="Previous"> <span aria-hidden="true">&laquo;</span>
					</a></li>
					<c:forEach var="item"
						items="${requestScope.pageInfo.navigatepageNums }">
						<c:if test="${item == requestScope.pageInfo.pageNum }">
							<li class="active"><a
								href="${pageContext.request.contextPath}/user/list?pn=${item }">${item }</a></li>
						</c:if>
						<c:if test="${item != requestScope.pageInfo.pageNum }">
							<li><a
								href="${pageContext.request.contextPath}/user/list?pn=${item }">${item }</a></li>
						</c:if>
					</c:forEach>
					<li><a
						href="${pageContext.request.contextPath}/user/list?pn=${requestScope.pageInfo.nextPage }"
						aria-label="Next"> <span aria-hidden="true">&raquo;</span>
					</a></li>
					<li><a
						href="${pageContext.request.contextPath}/user/list?pn=${requestScope.pageInfo.pages }">末页
					</a></li>
				</ul>
			</nav>
		</div>

	</div>
</body>
<script
	src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"
	integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
	crossorigin="anonymous"></script>
<script type="text/javascript">
	$("#btn_add_user").click(function() {

		$("#infoAddModal").modal({
			backdrop : "static"
		});
	});
</script>
</html>