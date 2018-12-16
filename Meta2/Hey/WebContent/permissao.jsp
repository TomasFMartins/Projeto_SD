<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Tomas
  Date: 16/12/2018
  Time: 03:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Permission Page</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="<s:url action="menupage" />">DropMusic</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <c:if test="${session.tipo == 'editor'}">
                <li class="nav-item">
                    <a class="nav-link" href="<s:url action="inserirpage" />">Inserir</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Alterar</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Remover</a>
                </li>
                <li class="nav-item active">
                    <a class="nav-link" href="#">Permissões</a>
                </li>
            </c:if>
            <li class="nav-item">
                <a class="nav-link" href="#">Playlist</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#">Dropbox</a>
            </li>
        </ul>
        <form action="pesquisar" method="post" class="form-inline my-2 my-lg-0">
            <input name="pesquisa" class="form-control mr-sm-2" type="search" placeholder="Pesquisa" aria-label="Search">
            <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Pesquisar</button>
        </form>
    </div>
</nav>



</body>
</html>
