<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>报警日志</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet"
          href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
          crossorigin="anonymous">
    <!-- <script type="text/javascript"
        src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script> -->
</head>
<body>
<div class="panel panel-default">
    <div class="panel panel-default">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <!-- <th>期号</th>
                <th>号码</th>
                 -->

                <th>时间</th>
                <th>号码</th>
                <th>类型</th>
                <th>日志</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${requestScope.pageInfo.list }" var="item">
                <tr>
                        <%-- <td>${item.periods}</td>
                        <td>${item.number}</td>
                         --%>

                    <td><fmt:formatDate pattern="MMdd"
                                        value="${item.addtime }"></fmt:formatDate>#${item.periods}</td>
                    <td>${fn:replace(item.number, " ", "")} </td>
                    <td>${item.type }</td>
                    <td>${item.notice}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="col-md-12">
        <nav aria-label="Page navigation">
            <ul class="pagination">
                <%-- <li><a href="${pageContext.request.contextPath}/api/logs?pn=1">首页
                </a></li> --%>
                <li><a
                        href="${pageContext.request.contextPath}/api/logs?pn=${requestScope.pageInfo.prePage }"
                        aria-label="Previous"> <span aria-hidden="true">&laquo;</span>
                </a></li>
                <c:forEach var="item"
                           items="${requestScope.pageInfo.navigatepageNums }">
                    <c:if test="${item == requestScope.pageInfo.pageNum }">
                        <li class="active"><a
                                href="${pageContext.request.contextPath}/api/logs?pn=${item }">${item }</a></li>
                    </c:if>
                    <c:if test="${item != requestScope.pageInfo.pageNum }">
                        <li><a
                                href="${pageContext.request.contextPath}/api/logs?pn=${item }">${item }</a></li>
                    </c:if>
                </c:forEach>
                <li><a
                        href="${pageContext.request.contextPath}/api/logs?pn=${requestScope.pageInfo.nextPage }"
                        aria-label="Next"> <span aria-hidden="true">&raquo;</span>
                </a></li>
                <%-- <li><a
                    href="${pageContext.request.contextPath}/api/logs?pn=${requestScope.pageInfo.pages }">末页
                </a></li> --%>
            </ul>
        </nav>
    </div>

</div>
</body>
</html>