<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 14-12-2018
  Time: 03:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign Up Page</title>
</head>
<body>
    <s:form action="registar" method="post">
        <div class="form-group">
            <label for="formGroupExampleInput">Username</label>
            <input type="text" class="form-control" name="username" id="formGroupExampleInput" placeholder="">
        </div>
        <div class="form-group">
            <label for="exampleInputPassword1">Password</label>
            <input type="password" class="form-control" name="password" id="exampleInputPassword1" placeholder="Password">
        </div>

        <button type="submit" class="btn btn-primary">Submit</button>
    </s:form>
</body>
</html>
