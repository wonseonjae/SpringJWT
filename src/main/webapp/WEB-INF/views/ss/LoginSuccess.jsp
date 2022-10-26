<%@ page import="kopo.springjwt.util.CmmUtil" %><%--
  Created by IntelliJ IDEA.
  User: data12
  Date: 2022-10-11
  Time: 오후 2:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    //Controller에 저장된 세션으로 로그인할때 생성됨
    String userName = CmmUtil.nvl((String) request.getAttribute("userName"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>로그인 성공</title>
</head>
<body>
<%=userName%> 로그인 성공
</body>
</html>
