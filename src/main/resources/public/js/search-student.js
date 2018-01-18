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
