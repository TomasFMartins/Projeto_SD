<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DropMusic</title>
	<style type="text/css">
		.blogin{
			float: left;
			position: relative;
			top: 220px;
			left: 41%;
			background-color: white;
			border: none;
		}

		.bsign{
			float: right;
			position: relative;
			right: 39%;
			top: 220px;
			background-color: white;
			border: none;
		}

		.blogin:hover{
			color: black;
		}

		.bsign:hover{
			color: black;
		}

        .text1{
            width: 192px;
            height: 36px;
            font-size: 30px;
            font-family: Drugs;
            font-weight: bold;
        }

        .vl{
            width:4px;
            float: left;
            background-color:#000;
            position:absolute;
            top:200px;
            height: 100px;
            left:50%;
            z-index: 1;
        }

    </style>
</head>
<body>
    <!--Auto Login-->
    <s:form action="loginpage" method="post">
        <button type="submit" class="blogin"><span class="text1">Login</span></button>
    </s:form>
    <div class="vl"></div>
    <s:form action="signuppage" method="post">
        <button type="submit" class="bsign"><span class="text1">Sign Up</span></button>
    </s:form>
</body>
</html>