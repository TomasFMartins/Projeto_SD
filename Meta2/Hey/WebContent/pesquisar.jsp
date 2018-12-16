<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="style" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Pesquisa</title>
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

<div class="row">
    <div class="col-4">
        <li class="list-group-item list-group-item-info">Músicas</li>
        <div class="list-group" id="list-tab" role="tablist">
            <c:forEach items="${session.pesquisa.split('#')}" var="value">
                <c:if test="${value.split(';')[0] == 'musica'}">
                    <a class="list-group-item list-group-item-action" id="a" data-toggle="list" href="#<c:out value="${value.split(';')[1]}" />_<c:out value="${value.split(';')[3]}" />" role="tab" aria-controls="home"><c:out value="${value.split(';')[1]} - ${value.split(';')[2]}" /></a>
                </c:if>
            </c:forEach>
        </div>
    </div>
    <div class="col-8">
        <div class="tab-content" id="nav-tabContent">
            <c:forEach items="${session.pesquisa.split('#')}" var="value">
                <c:if test="${value.split(';')[0] == 'musica'}">
                    <div class="tab-pane fade" id="<c:out value="${value.split(';')[1]}" />_<c:out value="${value.split(';')[3]}" />" role="tabpanel" aria-labelledby="a">Musica: <c:out value="${value.split(';')[1]}" /><br>Álbum: <c:out value="${value.split(';')[2]}" /><br>Artista: <c:out value="${value.split(';')[3]}" /><br>Duração: <c:out value="${value.split(';')[4]}" /></div>
                </c:if>
            </c:forEach>
        </div>
    </div>
</div>
<br>
<div class="row">
    <div class="col-4">
        <li class="list-group-item list-group-item-info">Artistas</li>
        <div class="list-group" id="list-tab" role="tablist">
            <c:forEach items="${session.pesquisa.split('#')}" var="value">
                <c:if test="${value.split(';')[0]=='artista'}">
                    <a class="list-group-item list-group-item-action" id="list-home-list" data-toggle="list" href="#<c:out value="${value.split(';')[1]}" />_" role="tab" aria-controls="home"><c:out value="${value.split(';')[1]}" /></a>
                </c:if>
            </c:forEach>
        </div>
    </div>
    <div class="col-8">
        <div class="tab-content" id="nav-tabContent">
            <c:forEach items="${session.pesquisa.split('#')}" var="value">
                <c:if test="${value.split(';')[0] == 'artista'}">
                    <div class="tab-pane fade" id="<c:out value="${value.split(';')[1]}" />_" role="tabpanel" aria-labelledby="${value.split(';')[1]}_${value.split(';')[2]}">Artista: <c:out value="${value.split(';')[1]}" /><br>Álbuns: <c:out value="${value.split(';')[2]}" /></div>
                </c:if>
            </c:forEach>
        </div>
    </div>
</div>
<br>
<div class="row">
    <div class="col-4">
        <li class="list-group-item list-group-item-info">Álbuns</li>
        <div class="list-group" id="list-tab" role="tablist">
            <c:forEach items="${session.pesquisa.split('#')}" var="value">
                <c:if test="${value.split(';')[0] == 'album'}">
                    <a class="list-group-item list-group-item-action" id="list-home-list" data-toggle="list" href="#<c:out value="${value.split(';')[1]}" />_<c:out value="${value.split(';')[2]}" />" role="tab" aria-controls="home"><c:out value="${value.split(';')[1]} - ${value.split(';')[2]}" /></a>
                </c:if>
            </c:forEach>
        </div>
    </div>
    <div class="col-8">
        <div class="tab-content" id="nav-tabContent">
            <c:forEach items="${session.pesquisa.split('#')}" var="value">
                <c:if test="${value.split(';')[0] == 'album'}">
                    <div class="tab-pane fade" id="<c:out value="${value.split(';')[1]}" />_<c:out value="${value.split(';')[2]}" />" role="tabpanel" aria-labelledby="${value.split(';')[1]}_${value.split(';')[2]}">Álbum: <c:out value="${value.split(';')[1]}" /><br>Artista: <c:out value="${value.split(';')[2]}" /><br>Músicas: <c:out value="${value.split(';')[3]}" /></div>
                    <!--Botao que abre modal, depois caixa de texto e botao submite dentro do modal-->
                </c:if>
            </c:forEach>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
</body>
</html>