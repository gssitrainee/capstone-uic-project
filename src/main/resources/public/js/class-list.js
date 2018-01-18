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