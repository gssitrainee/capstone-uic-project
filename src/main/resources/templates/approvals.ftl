<#import "masterTemplate.ftl" as t>

<!DOCTYPE html>
<html lang="en">

    <head>
        <@t.headerMetaTags />
        <title>Capstone: Student-Class Registrations for Approval</title>

        <!-- Bootstrap core CSS -->
        <link href="css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="css/approvals.css">
            <link rel="stylesheet" href="css/font-awesome.css">

        <!-- Custom styles for this template -->
        <style>
            body {
                background-image: url("inner.jpg");
                background-size: 102% 102%;
                background-repeat: no-repeat;
                padding-top: 56px;
                padding-top: 54px;
            }
            @media (min-width: 992px) {
                body {
                    padding-top: 56px;
                }
            }

            div#divMain { padding: 25px; }
        </style>

    </head>

    <body class="bkg">
        <!-- Navigation -->
        <@t.navigationDiv />

        <!-- Page Content -->
        <div id="divMain" class="container cont1">
            <div class="row">
                <div class="col-sm-12">
                    <h4>Student Registration to Class for Approval</h4>
                    <br />
                    <table id="tblEnrollmentForApproval" class="table table-hover">
                        <thead class="thead1">
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
                                    <td><a href='javascript:void(0);' onclick="approveStudentClassEnrollment('${apv["student"]}','${apv["class"]}')" class="btn btn-primary" role="button">Approve</a></td>
                                    <td><a href='javascript:void(0);' onclick="denyStudentClassEnrollment('${apv["student"]}','${apv["class"]}')" class="btn btn-primary" role="button">Deny</a></td>
                                </tr>
                            </#list>
                        <#else>
                        <td colspan="4">No Data Available</td>
                        </#if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class=" ">
        <@t.footerDiv />
            </div>

        <@t.bootstrapCoreJS />


        <script>
            $(document).ready(function(){
                //Put initializations here
            });

            var reloadEnrollmentList = function(){
                var my_url = "/courseRegistrations";
                $.getJSON(my_url, function(json) {
                    $('table#tblEnrollmentForApproval tbody').empty();
                    $.each(json, function(idx, doc) {
                        $('table#tblEnrollmentForApproval tbody').append("<tr><td>" + doc.className + " (" + doc.class + ")</td><td>" + doc.studentName + "</td><td><a href='javascript:void(0);' onclick=\"approveStudentClassEnrollment('" + doc.student + "','" + doc.class + "')\" class=\"btn btn-primary\" role=\"button\">Approve</a></td><td><a href='javascript:void(0);' onclick=\"denyStudentClassEnrollment('" + doc.student + "','" + doc.class + "')\" class=\"btn btn-primary\" role=\"button\">Deny</a></td></tr>");
                    });
                });
            }

            var approveStudentClassEnrollment = function(student, classCode){
                var my_url = "/approveEnrollment?cc=" + classCode + "&su=" + student;
                $.post(my_url, function(msg){
                    bootbox.alert(msg);
                    reloadEnrollmentList();
                });
            };

            var denyStudentClassEnrollment = function(student, classCode){
                var my_url = "/denyEnrollment?cc=" + classCode + "&su=" + student;
                $.post(my_url, function(msg){
                    bootbox.alert(msg);
                    reloadEnrollmentList();
                });
            };
        </script>
    </body>

</html>