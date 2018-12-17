<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Tomas
  Date: 16/12/2018
  Time: 05:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Promotion Page</title>
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
            websocket.send("${session.username}"+"#"+"${session.tipo}"+"#promover#"+"${session.promovido}");
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
                <li class="nav-item active">
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

<div class="alert alert-success" role="alert">
    <h4 class="alert-heading">Sucesso!</h4>
    <p>O username selecionado foi promovido a Editor!</p>
</div>
</body>
</html>
