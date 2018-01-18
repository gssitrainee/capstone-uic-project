<#import "masterTemplate.ftl" as t>

<!DOCTYPE html>
<html>
<head>
<@t.headerMetaTags />
    <title>Capstone: Welcome</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/welcome.css">
    <link rel="stylesheet" href="css/font-awesome.css">
<@t.headerFavicons />

    <style type="text/css">
        body {
            background-image: url("inner.jpg");
            background-size: 100% 100%;
            background-repeat: no-repeat;
            padding-top: 54px;
        }

        @media (min-width: 992px) {
            body {
                padding-top: 56px;
            }
        }

        div#divMainContainer {
            padding: 25px;
        }

        h4 {
            padding: 20px 0;
        }

        .label {
            text-align: right
        }

        .error {
            color: red
        }
    </style>
</head>
<body class="bkg">
<!-- Navigation -->
<@t.navigationDiv />

<!-- Page Content -->
<div id="divMainContainer" class="container cont1" style="font-family: Montserrat">
<#if 'T'==userType>
    <div class="row sm-flex-center">
        <div class="col-sm-4" style="text-transform: capitalize">
            <h4>Classes</h4>
            <div class="list">
                <#if userClasses??>
                    <ul class="list-group">
                        <#list userClasses as cls>
                            <li class="list-group-item"><a
                                    href="/course?code=${cls["_id"]}">${cls["className"]}<#if cls["_id"]??>
                                (${cls["_id"]})</#if></a></li>
                        </#list>
                    </ul>
                </#if>
            </div>
        </div>
        <div class="col-sm-8 pull-right">
            <h4>Student Registration to Class for Approval</h4>
            <table id="tblEnrollmentForApproval" class="table table1 table-hover" style="background-color: #ffffff">
                <thead>
                <tr>
                    <th>Class</th>
                    <th>Student</th>
                    <th>Accept</th>
                    <th>Deny</th>
                </tr>
                </thead>
                <tbody>
                    <#if forApproval??>
                        <#list forApproval as apv>
                        <tr>
                            <td>${apv["className"]} (${apv["class"]})</td>
                            <td>${apv["studentName"]}</td>
                            <td><a href='javascript:void(0);'
                                   onclick="approveStudentClassEnrollment('${apv["student"]}','${apv["class"]}')"
                                   class="btn btn-primary" role="button">Approve</a></td>
                            <td><a href='javascript:void(0);'
                                   onclick="denyStudentClassEnrollment('${apv["student"]}','${apv["class"]}')"
                                   class="btn btn-primary" role="button">Deny</a></td>
                        </tr>
                        </#list>
                    <#else>
                    <td colspan="4">No Data Available</td>
                    </#if>
                </tbody>
            </table>
        </div>
    </div>
<#elseif userType?? && 'S'==userType>
    <div class="row sm-flex-center">
        <div class="col-sm-6">
            <p>Enrolled Courses (Subjects)</p>
            <div class="list">
                <#if userClasses??>
                    <ul class="list-group">
                        <#list userClasses as cls>
                            <li class="list-group-item">${cls["className"]}<#if cls["_id"]??> (${cls["_id"]})</#if></li>
                        </#list>
                    </ul>
                </#if>
            </div>
        </div>
        <div class="col-sm-6 pull-right">
            <p>Active Topics</p>
            <#if topics??>
                <ul class="list-group">
                    <#list topics as t>
                        <li class="list-group-item" onclick="window.location='/viewTopic?tid=${t["_id"]}'">
                            (${t["classCode"]}) : ${t["topic"]} <#if t["taken"]?? && t["taken"]><strong> - Taken
                            (${t["scores"]}/${t["items"]})</strong></#if></li>
                    <#--<li class="list-group-item" onclick="window.location='/viewTopic?tid=${t["_id"]}'">(${t["classCode"]}) : ${t["topic"]} <#if t["taken"]?? && t["taken"]><strong> - Taken (${t["percentage"]}%)</strong></#if></li>-->
                    </#list>
                </ul>
            </#if>
        </div>
    </div>
<#else>
    <div class="row sm-flex-center">
        <div class="col-sm-12">
            <h1>Capstone Project: Quiz Bank</h1>
        </div>
    </div>
</#if>
</div>


<div class="row size">
    <div class="footerwrapper">
        <div class="footer-menu-container">
            <div class="menu menu1 col-sm-4">
                <h3>Developers</h3>
                <a href="#">John Patrick Agduma</a>
                <a href="#">Alfonso Louise Alfonso</a>
                <a href="#">Jon Kirk Donio</a>
                <a href="#">Niko Jay Mateo</a>
            </div>
            <div class="menu menu2 col-sm-4">
                <h3>Company</h3>
                <a href="#">About us</a>
                <a href="#">Contact us</a>
                <a href="#">Policy</a>
            </div>
            <div class="menu menu3 col-sm-4">
                <h3>Social Media</h3>
                <a href="#">Facebook</a>
                <a href="#">Twitter</a>
                <a href="#">Gmail</a>
            </div>
        </div>

    </div>
    <div class="footer-copyright">

        <p>© 2017 Culturama </p>

    </div>


</div>

</div>



<@t.bootstrapCoreJS />


<script>
    $(document).ready(function () {
        //Put initializations here
    });

    var reloadEnrollmentList = function () {
        var my_url = "/courseRegistrations";
        $.getJSON(my_url, function (json) {
            $('table#tblEnrollmentForApproval tbody').empty();
            $.each(json, function (idx, doc) {
                $('table#tblEnrollmentForApproval tbody').append("<tr><td>" + doc.className + " (" + doc.class + ")</td><td>" + doc.studentName + "</td><td><a href='javascript:void(0);' onclick=\"approveStudentClassEnrollment('" + doc.student + "','" + doc.class + "')\" class=\"btn btn-primary\" role=\"button\">Approve</a></td><td><a href='javascript:void(0);' onclick=\"denyStudentClassEnrollment('" + doc.student + "','" + doc.class + "')\" class=\"btn btn-primary\" role=\"button\">Deny</a></td></tr>");
            });
        });
    }

    var approveStudentClassEnrollment = function (student, classCode) {
        var my_url = "/approveEnrollment?cc=" + classCode + "&su=" + student;
        $.post(my_url, function (msg) {
            bootbox.alert(msg);
            reloadEnrollmentList();
        });
    };

    var denyStudentClassEnrollment = function (student, classCode) {
        var my_url = "/denyEnrollment?cc=" + classCode + "&su=" + student;
        $.post(my_url, function (msg) {
            bootbox.alert(msg);
            reloadEnrollmentList();
        });
    };
</script>

</body>
</html>
