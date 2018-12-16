<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hey!</title>
</head>
<body>
	<c:choose>
		<c:when test="${session.loggedin == true}">
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
		</c:when>
	</c:choose>
	<p>Ocorreu o seguinte erro: ${session.erro}</p>
	<c:choose>
		<c:when test="${session.site == 'loginpage'}">
			<p><a href="<s:url action = "loginpage" />">Voltar</a></p>
		</c:when>
		<c:when test="${session.site == 'signuppage'}">
			<p><a href="<s:url action = "signuppage" />">Voltar</a></p>
		</c:when>
		<c:when test="${session.site == 'inserir'}">
			<p><a href="<s:url action = "inserirpage" />">Voltar</a></p>
		</c:when>
		<c:when test="${session.site == 'menupage'}">
			<p><a href="<s:url action = "menupage" />">Voltar</a></p>
		</c:when>
	</c:choose>
</body>
</html>