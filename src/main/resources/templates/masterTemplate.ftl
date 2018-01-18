<#macro headerMetaTags>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
</#macro>

<#macro headerFavicons>
    <link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
    <link rel="manifest" href="/manifest.json">
    <meta name="theme-color" content="#ffffff">
</#macro>

<#macro bootstrapCoreJS>
    <!-- Bootstrap core JavaScript -->
    <script src="js/jquery.min.js"></script>
    <script src="js/popper.min.js"></script>
    <script src="js/bootbox.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
</#macro>




<#macro navigationDiv>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top nav-header">
        <div class="container">

            <#if username??>
                <a class="navbar-brand" style="text-transform: capitalize" href="/${hdrLink!"login"}">${hdrLabel!"Already a user?"}</a>
            <#else>
                <a class="navbar-brand " style="text-transform: capitalize" href="/login">Already a user?</a>
            </#if>

            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarResponsive">
                <ul class="navbar-nav ml-auto">
                    <#if sessionId?? && userType??>
                        <#if 'T'==userType>
                            <li class="nav-item">
                                <a class="nav-link nav-linkN" href="/newCourse"><i class="fa fa-cog" aria-hidden="true"></i> Course</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link nav-linkN" href="/approvals"> <i class="fa fa-check-square-o" aria-hidden="true"></i> Registrations</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link nav-linkN" href="/postTopic"> <i class="fa fa-book" aria-hidden="true"></i> Topics </a>
                            </li>
                        <#elseif 'S'==userType>
                            <li class="nav-item">
                                <a class="nav-link nav-linkN" href="/classSearch"> <i class="fa fa-search" aria-hidden="true"></i> Course Search </a>
                            </li>
                        </#if>
                        <#if sessionId??>
                            <li class="nav-item">
                                <a class="nav-link nav-linkN" href="/logout"> <i class="fa fa-sign-out" aria-hidden="true"></i> Logout </a>
                            </li>
                        </#if>
                    <#else>
                        <#if showSignUp?? && showSignUp>
                            <li class="nav-item">
                                <a class="nav-link nav-linkN" href="/signup"> <i class="fa fa-user-plus" aria-hidden="true"></i> Sign up</a>
                            </li>
                        </#if>
                        <li class="nav-item">
                            <a class="nav-link nav-linkN" href="/login"><i class="fa fa-sign-in" aria-hidden="true"></i> Log in </a>
                        </li>
                    </#if>
                </ul>
            </div>
        </div>
    </nav>
</#macro>

<#macro footerDiv>
<#--footer-->
<div class="row size">
    <div class="footerwrapper">
        <div class="footer-menu-container">
            <div class="menu menu1 col-md-4">
                <h3>Developers</h3>
                <a href="#">John Patrick Agduma</a>
                <a href="#">Alfonso Louise Alfonso</a>
                <a href="#">Jon Kirk Donio</a>
                <a href="#">Niko Jay Mateo</a>
            </div>
            <div class="menu menu2 col-md-4">
                <h3>Company</h3>
                <a href="#">About us</a>
                <a href="#">Contact us</a>
                <a href="#">Policy</a>
            </div>
            <div class="menu menu3 col-md-4">
                <h3>Social Media</h3>
                <a href="#">Facebook</a>
                <a href="#">Twitter</a>
                <a href="#">Gmail</a>
            </div>
        </div>

    </div>
    <div class="footer-copyright">
        <p>© 2017 Culturama</p>

    </div>



</div>
</#macro>






<#macro masterTemplate title="">
<!DOCTYPE html
        PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>${title}</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <style>
        body {
            padding-top: 54px;
        }
        @media (min-width: 992px) {
            body {
                padding-top: 56px;
            }
        }
    </style>
</head>
<body>
<div>
    <h1>${title}</h1>
    <div>
        <#nested />
    </div>
</div>

<!-- Bootstrap core JavaScript -->
<script src="js/jquery.min.js"></script>
<script src="js/popper.min.js"></script>
<script src="js/bootbox.min.js"></script>
<script src="js/bootstrap.min.js"></script>

</body>
</html>
</#macro>