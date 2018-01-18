$(document).ready(function(){
    $('#btnSignUp').on('click', function () {
        console.log('clicked');
        registerUser();
    });

});

var validateEmail = function(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
};

var registerUser = function(){
    var username = $('#txtUsername').val();
    var firstName = $('#txtFirstName').val();
    var lastName = $('#txtLastName').val();
    var email = $('#txtEmail').val();
    var password = $('#txtPassword').val();
    var verifyPassword = $('#txtVerifyPassword').val();
    var userType = $('input[name=userType]:checked').val();
    var type = "T" == userType ? 'Teacher' : 'Student';

    var type = "Unassigned";
    if(username && firstName && lastName && password && verifyPassword && userType){
        if("T" == userType)
            type = "Teacher";
        else if("S" == userType)
            type = "Student";

        if(""!=email && !validateEmail(email))
            bootbox.alert("Email Address is not valid!");
        else if(password == verifyPassword){
            bootbox.confirm({
                message: "Registering user as a " + type + ". Do you want to continue?",
                buttons: {
                    cancel: {
                        label: '<i class="fa fa-times-circle-o" aria-hidden="true"></i> No'
                    },
                    confirm: {
                        label: '<i class="fa fa-check-circle-o" aria-hidden="true"></i> Yes'
                    }
                },
                callback: function (choice) {
                    if(choice)
                        $('#signupform').submit();
                }
            });
        }
        else
            bootbox.alert("Passwords does not match!");
    }
    else
        bootbox.alert("Please complete all the fields!");
};