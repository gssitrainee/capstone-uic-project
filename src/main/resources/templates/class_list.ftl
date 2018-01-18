<#import "masterTemplate.ftl" as t>

<!doctype HTML>
<html>
<head>
<@t.headerMetaTags />
    <title>Capstone: Register a Class (Course)</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/classlist.css" rel="stylesheet">
    <link href="css/font-awesome.min.css" rel="stylesheet">

    <style type="text/css">
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

        h4 { padding: 20px 0; }



    </style>
</head>
<body>

<@t.navigationDiv />

<div class="container cont1">
    <div class="row top-space">
        <div class="col-sm-12">
            <h4>${className!'Course'} class list</h4>
            <table id="tblClassList" class="table table-hover">
                <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email Address</th>
                    <th>Score</th>
                    <th>Average</th>
                    <th>Drop</th>
                </tr>
                </thead>
                <tbody>
                <#if classList??>
                    <#list classList as student>
                    <tr>
                        <td>${student["firstName"]}</td>
                        <td>${student["lastName"]}</td>
                        <td>${student["email"]!"n/a"}</td>
                        <td>${student["totalScore"]?string["0.##"]}</td>
                        <td>${student["totalAverage"]?string["0.##"]}%</td>
                        <td><a href="javascript:void(0);" onclick="dropStudent('${classCode}','${student["_id"]}')" class="btn btn-danger"><i class="fa fa-trash" aria-hidden="true"></i></a></td>
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
<@t.footerDiv />

<@t.bootstrapCoreJS />

<script>
    var dropStudent = function(courseCode, studentId){
        bootbox.confirm({
            message: "Dropping student(s) cannot be undone. Do you want to proceed?",
            buttons: {
                cancel: {
                    label: '<i class="fa fa-times" aria-hidden="true"></i> No'
                },
                confirm: {
                    label: '<i class="fa fa-trash" aria-hidden="true"></i> Yes'
                }
            },
            callback: function (result) {
                if(result){
                    var my_url = "/dropStudent?su=" + studentId + "&cc=" + courseCode;
                    $.post(my_url, function(msg){
                        bootbox.alert({
                            message: msg,
                            callback: function () {
                                location.reload();
                            }
                        });
                    });
                }
            }
        });
    };


</script>
</body>
</html>