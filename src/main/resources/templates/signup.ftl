<#import "masterTemplate.ftl" as t>

<!DOCTYPE html>
<html>
<head>
<@t.headerMetaTags />
    <title>Capstone: Sign Up</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/signup.css">

    <style>

        body{
            background-image: url("QUIZ.png");
            background-size: 100% 100%;
            background-repeat: no-repeat;
            padding-top: 54px;
        }

    </style>
</head>

<body>
<@t.navigationDiv />

<div id="signupbox" class="row signupbox">
    <div class="col-md-6 title rightspace">Sign Up!
    </div>
    <div class="col-md-6 rightspace">
    <div class="panel panel-info">
        <hr />
        <div class="panel-body" >
            <form id="signupform" class="form-horizontal" action="/registerUser" method="post">

            <#if signup_error??>
                <div id="signupalert" style="display:none" class="alert alert-danger">
                    <p>Error: <span>${signup_error}</span></p>
                </div>
            </#if>

                <div class="form-group has-error has-feedback">
                    <label for="txtFirstName" class="col-md-9 control-label">First Name</label>
                    <div class="col-md-9">
                        <input type="text" id="txtFirstName" class="form-control" name="firstName" placeholder="First Name" value="${firstName!''}" required />
                    </div>
                </div>

                <div class="form-group has-error has-feedback">
                    <label for="txtLastName" class="col-md-3 control-label">Last Name</label>
                    <div class="col-md-9">
                        <input type="text" id="txtLastName" class="form-control" name="lastName" placeholder="Last Name" value="${lastName!''}" required />
                    </div>
                </div>

                <div class="form-group has-error has-feedback">
                    <label for="txtUsername" class="col-md-3 control-label">Username</label>
                    <div class="col-md-9">
                        <input type="text" class="form-control" id="txtUsername" name="username" placeholder="Username" value="${username!''}" required />
                        <span class="glyphicon glyphicon-remove form-control-feedback"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label for="password" class="col-md-3 control-label">Password</label>
                    <div class="col-md-9">
                        <input type="password" class="form-control" id="txtPassword" name="password" placeholder="Password" value="${password!''}" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="verify" class="col-md-3 control-label">Verify Password</label>
                    <div class="col-md-9">
                        <input type="password" class="form-control" id="txtVerifyPassword" placeholder="Verify Password" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="txtEmail" class="col-md-3 control-label">Email (Optional)</label>
                    <div class="col-md-9">
                        <input type="email" class="form-control" id="txtEmail" name="email" placeholder="Email Address (Optional)" value="${email!''}" required>
                    </div>
                </div>

                <div class="form-group">
                    <div class="radio">
                        <input type="radio" id="rdbTrue" name="userType" value="T" />
                        <label for="rdbTrue" class="radio-inline">Teacher</label>
                    </div>
                    <div class="radio">
                        <input type="radio" id="rdbFalse" name="userType" value="S" />
                        <label for="rdbFalse" class="radio-inline">Student</label>
                    </div>
                </div>
            </div>
            </form>
        </div>

        <hr />
        <div class="form-group">
            <div class="col-md-offset-3 col-md-9">
                <button id="btnSignUp" class="btn btn-info"><i class="fa fa-address-book-o" aria-hidden="true"></i>&nbsp;&nbsp;Sign Up</button>
            </div>
        </div>
    </div>
    </div>
</div>

<@t.bootstrapCoreJS />
<script src="js/signup.js"></script>
</body>
</html>
