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