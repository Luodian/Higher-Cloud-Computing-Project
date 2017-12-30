<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TEST</title>
</head>
<body>
<h1>TEST</h1>
<h2>test upload</h2>
<form method="post" action="ImgUpload" enctype="multipart/form-data">
    userid:
    <input type="text" name="user_id"/>
    choose a file:
    <input type="file" name="uploadFile" />
    <br/><br/>
    <input type="submit" value="上传" />
</form>
<br>**********************************

<h2>test register</h2>

<form method="post" action="Register" >
    email:
    <input type="text" name="email_addr"/>
    nickname:
    <input type="text" name="nick_name" />
    pwd:
    <input type="text" name="pwd" />
    <br/><br/>
    <input type="submit" value="上传" />
</form>
<br>**********************************


<h2>test ip upload</h2>

<form method="post" action="UploadIP">
    machine_id:
    <input type="text" name="machine_id"/>
    ip:
    <input type="text" name="ip" />
    <input type="submit" value="上传" />
</form>
<br>**********************************

<h2>test pk upload</h2>

<form method="post" action="UploadPK">
    machine_id:
    <input type="text" name="machine_id"/>
    pk:
    <input type="text" name="public_key" />
    <input type="submit" value="上传" />
</form>
<br>**********************************

<h2>test new task</h2>

<form method="post" action="SubmitTask">
    machine_id:
    <input type="text" name="machine_id"/>
    user_id:
    <input type="text" name="user_id" />
    task_name:
    <input type="text" name="task_name" />
    public_key:
    <input type="text" name="public_key" />
    <input type="submit" value="上传" />
</form>
<br>**********************************


<h2>test task fin1</h2>

<form method="post" action="TaskFinish">
    machine_id:
    <input type="text" name="machine_id"/>
    user_id:
    <input type="text" name="user_id" />
    task_id:
    <input type="text" name="task_id" />
    actor:
    <input type="text" name="actor" />
    <input type="submit" value="上传" />
</form>
<br>**********************************


<h2>test task fin2</h2>

<form method="post" action="TaskFinish">
    machine_id:
    <input type="text" name="machine_id"/>
    user_id:
    <input type="text" name="user_id" />
    task_id:
    <input type="text" name="task_id" />
    actor:
    <input type="text" name="actor" />
    <input type="submit" value="上传" />
</form>
<br>**********************************


<h2>test pwd reset Val1</h2>

<form method="post" action="PwdResetVal">
    user_id:
    <input type="text" name="user_id"/>
    email_addr:
    <input type="text" name="email_addr" />
    
    <input type="submit" value="上传" />
</form>
<br>**********************************

<h2>test pwd reset Val2</h2>

<form method="post" action="PwdResetVal">
    user_id:
    <input type="text" name="user_id"/>
    email_addr:
    <input type="text" name="email_addr" />
    
    <input type="submit" value="上传" />
</form>
<br>**********************************


<h2>test pwd reset Val3</h2>

<form method="post" action="PwdResetVal">
    user_id:
    <input type="text" name="user_id"/>
    email_addr:
    <input type="text" name="email_addr" />
    
    <input type="submit" value="上传" />
</form>
<br>**********************************


<h2>test email verification_code</h2>

<form method="post" action="Active">
    user_id:
    <input type="text" name="user_id"/>
    verification_code:
    <input type="text" name="verification_code" />
    
    <input type="submit" value="上传" />
</form>
<br>**********************************


<h2>test pwd reset</h2>

<form method="post" action="ResetPwd">
    user_id:
    <input type="text" name="user_id"/>
    pwd:
    <input type="text" name="pwd" />
    
    <input type="submit" value="上传" />
</form>
<br>**********************************



</body>
</html>