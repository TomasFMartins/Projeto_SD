<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 14-12-2018
  Time: 03:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login Page</title>
</head>
<body>
    <s:form action="login" method="post">
        <s:text name="Username:" />
        <input name="username" type="text"><br>
        <s:text name="Password: " />
        <s:password name="password" /><br>
        <s:submit />
    </s:form>
</body>
</html>
