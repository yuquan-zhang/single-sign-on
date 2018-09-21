
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@include file="common.jsp"%>
<html>
<head>
    <title>登陆</title>
    <link rel="stylesheet" href="${basePath}/statics/thirdPart/bootstrap-3.3.7-dist/css/bootstrap.min.css">

    <!-- jQuery library -->
    <script src="${basePath}/statics/thirdPart/jquery-3.3.1.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="${basePath}/statics/thirdPart/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container" style="top: 30px; width: 50%; margin: auto">
    <p style="color:red;">${errorMsg}</p>
    <form action="${basePath}/login" method="post">
        <div class="form-group">
            <label for="username">Username:</label>
            <input name="username" type="text" class="form-control" id="username">
        </div>
        <div class="form-group">
            <label for="pwd">Password:</label>
            <input name="password" type="password" class="form-control" id="pwd">
        </div>
        <div class="checkbox">
            <label><input name="rememberMe" type="checkbox"> Remember me</label>
        </div>
        <div class="form-group hidden">
            <label for="subSystemUrl">redirectUrl:</label>
            <input name="subSystemUrl" value="${subSystemUrl}" type="text" class="form-control" id="subSystemUrl">
        </div>
        <button type="submit" class="btn btn-default">Submit</button>
    </form>
</div>

</body>
</html>
