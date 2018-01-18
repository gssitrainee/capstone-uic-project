<#import "masterTemplate.ftl" as t>

<!DOCTYPE html>
<html lang="en">

<head>
<@t.headerMetaTags />
    <title>Capstone: Student Search</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/studentSearch.css" rel="stylesheet">
    <link href="css/font-awesome.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <style>
        body {
            background-image: url("inner.jpg");
            background-size: 102% 102%;
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

<body>
<@t.navigationDiv />

<!-- Page Content -->
<div class="container cont1">
    <div class="row">
        <div class="col-lg-12 text-left">
            <br />

            <div id="status-alert" class="alert alert-success" style="display:none;">
                <p style="margin-bottom: 0px;"><span id="spnStatus"></span></p>
            </div>

            <h1 class="mt-5"><i class="fa fa-search" aria-hidden="true"></i>&nbsp;Search Student(s)</h1>

            <form id="formStudentSearch" action="studentSearch" method="get">
                <input type="hidden" id="hndCourse" name="course" value="${classCode!''}" />
                <div class="input-group">
                    <input type="text" id="txtSearchKey" name="searchKey" class="form-control" placeholder="Search student name" title="Enter key to search">
                    <div class="input-group-btn">
                        <button class="btn btn-default" type="submit"><i class="fa fa-search" aria-hidden="true"></i></button>
                    </div>
                </div>
            </form>

            <br />
            <hr />
            <br />

            <table id="tblStudentList" class="table table-hover">
                <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email Address</th>
                    <th>Register</th>
                </tr>
                </thead>
                <tbody>
                <td colspan="4">No Data Available</td>
                </tbody>
            </table>
        </div>
    </div>
</div>
<@t.footerDiv />
<@t.bootstrapCoreJS />

<script>
    $(document).ready(function(){
        $('#formStudentSearch').on("submit", function(){
            var course = $("#hndCourse").val();
            var searchKey = $("#txtSearchKey").val();
            if(''!=searchKey){
                var my_url = "/studentSearch?searchKey=" + searchKey;
                $.getJSON(my_url, function(json) {
                    $('table#tblStudentList tbody').empty();
                    $.each(json, function(idx, doc) {
                        $('table#tblStudentList tbody').append("<tr><td>" + doc.firstName + "</td><td>" + doc.lastName + "</td><td>" + doc.email + "</td><td><a href=\"javascript:void(0);\" onclick=\"registerStudent('" + course + "', '" + doc._id + "');\" class='btn btn-primary' role='button'><i class=\"fa fa-plus-circle\" aria-hidden=\"true\"></i></a></td></tr>");
                    });
                });
            }
            else
                alert('Please enter student name to search');

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
        $('table#tblStudentList tbody').empty();
    };

    var displayStatusMessage = function(msg){
        $('#spnStatus').text(msg);
        $('#status-alert').show();
    };


    var registerStudent = function(course, studentId){
        var my_url = "/studentClassRegistration?cc=" + course + "&su=" + studentId;
        $.post(my_url, function(msg){
            bootbox.alert(msg);
            initializeTable();
        });
    };
</script>
</body>
</html>