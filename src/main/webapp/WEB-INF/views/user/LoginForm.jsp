<%--
  Created by IntelliJ IDEA.
  User: data12
  Date: 2022-09-13
  Time: 오후 2:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>로그인 화면</title>
    <link rel="stylesheet" href="/css/table.css">
    <script type="text/javascript">

        function doLoginUserCheck(f){
            if (f.user_id.value === ""){
                alert("아이디를 입력하세요")
                f.user_id.focus();
                return false;
            }

            if (f.password.value() === ""){
                alert("비밀번호를 입력하세요.")
                f.password,focus();
                return false;
            }
        }
    </script>
</head>
<body>
<h2>로그인하기</h2>
</hr>
</br>
<form name="f" method="post" action="/user/getUserLoginCheck" onsubmit="return doLoginUserCheck(this);">
    <div class="divTable minimalistBlack">
        <div class="divTableBody">
            <div class="divTableRow">
                <div class="divTableCell">아이디
                </div>
                <div class="divTableCell">
                    <input type="text" name="user_id">
                </div>
            </div>
            <div class="divTableRow">
                <div class="divTableCell">비밀번호
                </div>
                <div class="divTableCell">
                    <input type="password" name="password">
            </div>
            </div>
        </div>
        <div><input type="submit" value="로그인"></div>
    </div>
</form>

</body>
</html>
