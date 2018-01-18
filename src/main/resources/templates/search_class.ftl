<#import "masterTemplate.ftl" as t>

<!DOCTYPE html>
<html lang="en">

    <head>
        <@t.headerMetaTags />
        <title>Capstone: Homepage</title>

        <!-- Bootstrap core CSS -->
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/font-awesome.min.css">
            <link rel="stylesheet" href="css/search_class.css">
            <link rel="stylesheet" href="css/font-awesome.css">

        <!-- Custom styles for this template -->
        <style>
            body {
                background-image: url("inner.jpg");
                background-size: 100%;
                background-repeat: no-repeat;
                padding-top: 54px;
            }
            @media (min-width: 992px) {
                body {
                    padding-top: 56px;
                }
            }
        </style>
    </head>

    <body class="bkg">
        <@t.navigationDiv />

        <!-- Page Content -->
        <div class="container cont1">
            <div class="row">
                <div class="col-lg-12 text-left">
                    <br />

                    <div id="status-alert" class="alert alert-success" style="display:none;">
                        <p style="margin-bottom: 0px;"><span id="spnStatus"></span></p>
                    </div>

                    <h1 class="mt-5">Search Classes</h1>

                    <form id="formClassSearch" action="classSearch" method="post">
                        <div class="input-group">
                            <input type="text" id="txtSearchKey" name="searchKey" class="form-control" placeholder="Search Course Name" title="Enter key to search">
                            <div class="input-group-btn">
                                <button class="btn btn-default" type="submit"><i class="fa fa-search" aria-hidden="true"></i></button>
                            </div>
                        </div>
                    </form>

                    <br />
                    <hr />
                    <br />

                    <table id="tblClassSearch" class="table table1 table-hover">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Instructor</th>
                                <th>Enroll</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#if classes??>
                                <#list classes as cls>
                                    <tr>
                                        <td>${cls["classCode"]}</td>
                                        <td>${cls["className"]}</td>
                                        <td>${cls["classDescription"]}</td>
                                        <td>${cls["instructor"]}</td>
                                        <td><a href="/enrollClass?cc=${cls["classCode"]}" class="btn btn-primary" role="button">Enroll</a></td>
                                    </tr>
                                </#list>
                            <#else>
                                <tr>
                                    <td colspan="4">No Data Available</td>
                                </tr>
                            </#if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <@t.footerDiv />
        <@t.bootstrapCoreJS />

        <script>
            $(document).ready(function(){
                $('#formClassSearch').on("submit", function(){
                    var searchKey = $("#txtSearchKey").val();
                    if(''!=searchKey){
                        var my_url = "/searchCourse?searchKey=" + searchKey;
                        $.getJSON(my_url, function(json) {
                            $('table#tblClassSearch tbody').empty();
                            $.each(json, function(idx, doc) {
                                $('table#tblClassSearch tbody').append("<tr><td>" + doc._id + "</td><td>" + doc.className + "</td><td>" + doc.classDescription + "</td><td>" + (doc.instructor ? doc.instructor : doc.teacher) + "</td><td><a href=\"javascript:void(0);\" onclick=\"submitClassEnrollment('" + doc._id + "');\" class='btn btn-primary' role='button'>Enroll</a></td></tr>");
                            });
                        });
                    }
                    else
                        alert('Please enter class name to search');

                    return false;
                });

                $("#txtSearchKey").on('keypress', function(){
                    $('#spnStatus').text("");
                    $('#status-alert').hide("slow");
                });

                initializeTable();
            });

            var initializeTable = function () {
                $("#txtSearchKey").val("");
                $('table#tblClassSearch tbody').empty();
            };

            var submitClassEnrollment = function (classCode) {
                var my_url = "/enrollClass?cc="+classCode;
                $.post(my_url, function(msg){
                    bootbox.alert(msg);
                    initializeTable();
                });
            };

            var displayStatusMessage = function(msg){
               $('#spnStatus').text(msg);
               $('#status-alert').show();
            };
        </script>
    </body>
</html>