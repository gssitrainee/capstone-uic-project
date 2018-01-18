<#import "masterTemplate.ftl" as t>

<!DOCTYPE html>

<img xmlns="http://www.w3.org/1999/html">
    <head>
        <@t.headerMetaTags />
        <title>Capstone: Login</title>

        <!-- Bootstrap core CSS -->
        <link href="css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="css/login.css">
            <link rel="stylesheet" href="css/font-awesome.css">

        <style type="text/css">
            body {
                background-image: url("login1.jpg");
                background-size: 100%;
                background-repeat: no-repeat;
                /*padding-top: 54px;*/
                /*background-image: url("../public/images/login.jpg");*/
            }
            @media (min-width: 992px) {
                body {
                    padding-top: 56px;
                }
            }

            .label {text-align: right}
            .error {color: red}
        </style>
  </head>
    <body class="bkg">
        <@t.navigationDiv />
        <div class="container top-space1">
          <div id="loginbox " class="loginbox1 mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
              <div class="panel panel-info" >
                  <div class="panel-heading">
                      <div class="panel-title Signin">Sign In</div>
                  </div>

                  <div style="padding-top:30px" class="panel-body" >
                      <br />
                      <#if login_error??>
                          <div id="login-alert" class="alert alert-danger">
                              <p style="margin-bottom: 0px;"><span>${login_error}</span></p>
                          </div>
                      </#if>

                      <form id="loginform" class="form-horizontal" role="form" action="/login" method="post">
                          <div style="margin-bottom: 25px" class="input-group">
                              <span class="input-group-addon"><i class="fa fa-user" aria-hidden="true"></i></span>
                              <input id="login-username" type="text" class="form-control" name="username" value="${(username)!''}" placeholder="Username or Email">
                          </div>
                          <div style="margin-bottom: 25px" class="input-group">
                              <span class="input-group-addon"><i class="fa fa-lock" aria-hidden="true"></i></span>
                              <input id="login-password" type="password" class="form-control" name="password" placeholder="Password">
                          </div>
                          <div style="margin-top:10px" class="form-group">
                              <div class="col-sm-12 controls">
                                  <input type="submit" id="btn-login" class="btn btn-success btn-login" value="Login" />
                              </div>
                          </div>
                      </form>
                  </div>
              </div>
          </div>
        </div>

        <@t.bootstrapCoreJS />
    </body>
</html>
