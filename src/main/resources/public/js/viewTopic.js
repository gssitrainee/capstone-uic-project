

$(document).ready(function(e){
    var videoLink = $('#txtVideoUrl').val();
    reloadVideo(videoLink);
});

var reloadVideo = function(youtubeId){
    if(youtubeId && 0 < youtubeId.length){
        var video_id = youtubeId.split("v=")[1];
        var youtubeUrl = "https://www.youtube.com/embed/" + video_id;
        $('#divVplayer iframe').remove();

        var iframeHTML = "<iframe id='ytplayer' type='text/html' width='560' height='315' src='" + youtubeUrl + "' frameborder='0' allowfullscreen></iframe>";

        $('#divVplayer').append(iframeHTML);
        $('#divVplayer').show();
    }
};

var submitTopicQuiz = function(){
    var topic_quiz = new Object();
    topic_quiz.topicId = $('#hndTopicId').val();
    topic_quiz.courseCode = $('#hndCourseCode').val();
    topic_quiz.courseName = $('#txtClass').val();
    topic_quiz.student = $('#hndStudent').val();
    topic_quiz.studentName = $('#hndStudentName').val();

    var questions = new Array();
    var objIds = $('#lstQuestions > li');

    var missedAQuestion = false;

    $.each(objIds, function( idx, obj ) {
        var objQuestion = new Object();
        var id = $(obj).find('input.itemId').val();
        var aType = $(obj).find('input.answerType').val();
        objQuestion.id = id;
        var answers = new Array();
        if(aType){
            if('BOOLEAN'==aType || 'SINGLE_ANSWER'==aType){
                var answer = $('input[name=\'' + id + '\']:checked').val();
                if(answer && ''!=answer) answers.push(answer);
            }
            else if('MULTIPLE_ANSWERS'==aType){
                $.each($("input[name='" + id + "']:checked"), function(){
                    answers.push($(this).val());
                });
            }
        }

        if(0 == answers.length){
            missedAQuestion = true;
        }
        else
            objQuestion.answers = answers;

        questions.push(objQuestion);
    });

    topic_quiz.questions = questions;

    if(!missedAQuestion){
        console.log(topic_quiz);
        $.post( "/submitTopicAnswers", JSON.stringify(topic_quiz), function( msg ) {
            // bootbox.alert(msg)
            bootbox.alert({
                message: "Successfully submitted topic answers!",
                callback: function () {
                    window.location = "/";
                }
            });

        }, "json");
    }
    else
        bootbox.alert("You may have missed a question.\nPlease review your answers and re-submit.");
};