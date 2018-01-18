$(document).ready(function(){
    $('#formClassSearch').on("submit", function(){
        var searchKey = $("#txtSearchKey").val();
        if(''!=searchKey){
            var my_url = "/searchCourse?searchKey=" + searchKey;
            $.getJSON(my_url, function(json) {
                $('table#tblClassSearch tbody').empty();
                $.each(json, function(idx, doc) {
                    $('table#tblClassSearch tbody').append("<tr><td>" + doc._id + "</td><td>" + doc.className + "</td><td>" + doc.classDescription + "</td><td>" + (doc.instructor ? doc.instructor : doc.teacher) + "</td><td><a href=\"javascript:void(0);\" onclick=\"submitClassEnrollment('" + doc._id + "');\" class='btn btn-primary' role='button'><i class=\"fa fa-plus-circle\" aria-hidden=\"true\"></i>&nbsp;Enroll</a></td></tr>");
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
