<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Erro!</title>
	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
</head>
<body>

	<script type="text/javascript">

		var websocket = null;

		window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
			connect('ws://' + window.location.host + '/Hey/ws');
		}

		function connect(host) { // connect to the host websocket
			if ('WebSocket' in window)
				websocket = new WebSocket(host);
			else if ('MozWebSocket' in window)
				websocket = new MozWebSocket(host);
			else {
				return;
			}

			websocket.onopen    = onOpen; // set the 4 event listeners below
			websocket.onclose   = onClose;
			websocket.onmessage = onMessage;
			websocket.onerror   = onError;
		}

		function onOpen(event) {
			if(!"${session.site}".includes("loginpage"))
				websocket.send("${session.username}"+"#"+"${session.tipo}");

		}

		function onClose(event) {
			console.log("On Close" + event);
		}

		function onMessage(message) { // print the received message
			if(message.data.includes("Promovido")){
				alert("Foi Promovido a Editor!");
				location.href="<s:url action = "promovidoAction"/>";
			}
			else if(message.data.includes("Foi alterado as músicas do álbum"))
				alert(message.data);
		}

		function onError(event) {
			console.log("On Error" + event);
		}


	</script>
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
								<a class="nav-link" href="<s:url action="permissaopage" />">Permissões</a>
							</li>
						</c:if>
						<li class="nav-item">
							<a class="nav-link" href="<s:url action="dropboxpage" />">Dropbox</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="<s:url action="indexPage" />">Logout</a>
						</li>
					</ul>
					<form action="pesquisar" method="get" class="form-inline my-2 my-lg-0">
						<input name="pesquisa" class="form-control mr-sm-2" type="search" placeholder="Pesquisa" aria-label="Search">
						<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Pesquisar</button>
					</form>
				</div>
			</nav>
		</c:when>
	</c:choose>
	<div class="alert alert-danger" role="alert">
		<h4 class="alert-heading">Erro!</h4>
		<p>Ocorreu o seguinte erro: ${session.erro}</p>
		<br>
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
	</div>


	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
</body>
</html>