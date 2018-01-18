$(document).ready(function(){
    reloadVideo($('#txtVideoUrl').val());

    $('#txtVideoUrl').on('keydown', function(event){
        var keyCode = (event.keyCode ? event.keyCode : event.which);
        if (keyCode == 13) {
            reloadVideo(this.value);
        }
    });

    $('#selVideoTopics').on('change', function () {
        if(''!=this.value){
            loadVideoDetails(this.value);
        }
    });

    $('#selQuestionType').on('change', function(){
        switch (this.value) {
            case 'BOOLEAN':
                $('#divBooleanChoices').show();
                $('#divTextAnswers').hide();
                $('#divSingleAnswer').hide();
                $('#divMultipleAnswer').hide();
                break;
            case 'SINGLE_ANSWER':
                $('#divBooleanChoices').hide();
                $('#divTextAnswers').show();
                $('#divSingleAnswer').show();
                $('#divMultipleAnswer').hide();
                break;
            case 'MULTIPLE_ANSWERS':
                $('#divBooleanChoices').hide();
                $('#divTextAnswers').show();
                $('#divSingleAnswer').hide();
                $('#divMultipleAnswer').show();
                break;
            default:
                $('#divBooleanChoices').hide();
                $('#divTextAnswers').hide();
                $('#divSingleAnswer').hide();
                $('#divMultipleAnswer').hide();
        }
    });
});

var removeQuestion = function(){
    var message = "This will be permanently removed. Do you want to proceed?";
    bootbox.confirm(message, function(result){
        if(result){
            var id = $('#hndId').val();
            var index = -1;
            if(id && ''!=id){
                $.each( items, function( idx, obj ) {
                    if(id == obj.id){
                        index = idx;
                        return false;
                    }
                });
                items.splice(index, 1);
                reloadItemList();
            }
        }
    });
};

var reloadItemList = function(){
    $('#listQuestions').empty();
    $.each( items, function( key, item ) {
        $('#listQuestions').append("<li onclick=\"displayItemDetails('" + item.id +"')\">" + item.question + "</li>")
    });
};

var findItem = function(id){
    var item = null;
    $.each( items, function(idx, obj) {
        if(id == obj.id){
            item = obj;
            return false;
        }
    });
    return item;
};

var displayItemDetails = function(id){
    var item = findItem(id);
    if(item){
        $('#hndId').val(item.id);
        $('#txtQuestion').val(item.question);
        $('#selQuestionType').val(item.answerType);

        if(item.choices){
            if('BOOLEAN'!=item.answerType){
                for(var i=0; i < item.choices.length; i++){
                    var choice = item.choices[i];
                    if(''!=choice){
                        if(i==0) $('#txtChoiceA').val(choice);
                        if(i==1) $('#txtChoiceB').val(choice);
                        if(i==2) $('#txtChoiceC').val(choice);
                        if(i==3) $('#txtChoiceD').val(choice);
                        if(i==4) $('#txtChoiceE').val(choice);
                        if(i==5) $('#txtChoiceF').val(choice);
                        if(i==6) $('#txtChoiceG').val(choice);
                    }
                }
            }
        }

        if(item.answers){
            if('MULTIPLE_ANSWERS'==item.answerType){
                for(var i=0; i < item.answers.length; i++){
                    var answer = item.answers[i];
                    if(''!=answer){
                        if(i==0) $('#txtAnswerA').val(answer);
                        if(i==1) $('#txtAnswerB').val(answer);
                        if(i==2) $('#txtAnswerC').val(answer);
                        if(i==3) $('#txtAnswerD').val(answer);
                        if(i==4) $('#txtAnswerE').val(answer);
                        if(i==5) $('#txtAnswerF').val(answer);
                        if(i==6) $('#txtAnswerG').val(answer);
                    }
                }
            }
            else if('SINGLE_ANSWER'==item.answerType){
                var answer = item.answers[0];
                $('#txtSingleAnswer').val(answer);
            }
            else if('BOOLEAN'==item.answerType){
                var answer = item.answers[0];
                $("input[name=bChoice][value='" + answer + "']").prop("checked",true);
            }
        }

        $('#myModal').modal('show');

        refreshAnswerFieldsByType(item.answerType);
    }
};

var refreshAnswerFieldsByType = function(answerType){
    console.log("answerType: " + answerType);

    switch (answerType) {
        case 'BOOLEAN':
            $('#divBooleanChoices').show();
            $('#divTextAnswers').hide();
            $('#divSingleAnswer').hide();
            $('#divMultipleAnswer').hide();
            break;
        case 'SINGLE_ANSWER':
            $('#divBooleanChoices').hide();
            $('#divTextAnswers').show();
            $('#divSingleAnswer').show();
            $('#divMultipleAnswer').hide();
            break;
        case 'MULTIPLE_ANSWERS':
            $('#divBooleanChoices').hide();
            $('#divTextAnswers').show();
            $('#divSingleAnswer').hide();
            $('#divMultipleAnswer').show();
            break;
        default:
            $('#divBooleanChoices').hide();
            $('#divTextAnswers').hide();
            $('#divSingleAnswer').hide();
            $('#divMultipleAnswer').hide();
    }
};

var loadVideoDetails = function(id){
    console.log('[id]: ' + id);
    if(id){
        $.getJSON( "/getVideoDetails?v=" + id, function(data) {
            if(data){
                $('#txtTopic').val(data.topic);
                $('#txtVideoUrl').val(data.videoLink);
                reloadVideo(data.videoLink);
            }
        }, "json");
    }
};

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

var createNewItem = function(){
    clearItemFields();
    $('#myModal').modal('show');
    refreshAnswerFieldsByType('init');
};

var clearItemFields = function(){
    $('#hndId').val("");
    $('#txtQuestion').val("");
    $('#selQuestionType').val("");
    $('#txtChoiceA').val("");
    $('#txtChoiceB').val("");
    $('#txtChoiceC').val("");
    $('#txtChoiceD').val("");
    $('#txtChoiceE').val("");
    $('#txtChoiceF').val("");
    $('#txtChoiceG').val("");

    $("input[name=bChoice]").prop("checked",false);

    $('#txtSingleAnswer').val("");
    $('#txtAnswerA').val("");
    $('#txtAnswerB').val("");
    $('#txtAnswerC').val("");
    $('#txtAnswerD').val("");
    $('#txtAnswerE').val("");
    $('#txtAnswerF').val("");
    $('#txtAnswerG').val("");
};

var saveQuestion = function(){
    var id = $('#hndId').val();

    var index = -1;
    var item = null;
    if(id && ''!=id){
        $.each( items, function( idx, obj ) {
            if(id == obj.id){
                item = obj;
                index = idx;
                return false;
            }
        });
    }
    else{
        item = new Object();
    }

    var question = $('#txtQuestion').val();
    var qType = $('#selQuestionType').val();

    var choices = new Array();
    var answers = new Array();

    if('BOOLEAN'==qType){
        choices.push('True');
        choices.push('False');
        answers.push($('input[name=bChoice]:checked').val());
    }
    else {
        if(''!=$('#txtChoiceA').val().trim()) choices.push($('#txtChoiceA').val());
        if(''!=$('#txtChoiceB').val().trim()) choices.push($('#txtChoiceB').val());
        if(''!=$('#txtChoiceC').val().trim()) choices.push($('#txtChoiceC').val());
        if(''!=$('#txtChoiceD').val().trim()) choices.push($('#txtChoiceD').val());
        if(''!=$('#txtChoiceE').val().trim()) choices.push($('#txtChoiceE').val());
        if(''!=$('#txtChoiceF').val().trim()) choices.push($('#txtChoiceF').val());
        if(''!=$('#txtChoiceG').val().trim()) choices.push($('#txtChoiceG').val());

        if('SINGLE_ANSWER'==qType){
            if(''!=$('#txtSingleAnswer').val().trim()) answers.push($('#txtSingleAnswer').val())
        }
        else if('MULTIPLE_ANSWERS'==qType){
            if(''!=$('#txtAnswerA').val().trim()) answers.push($('#txtAnswerA').val());
            if(''!=$('#txtAnswerB').val().trim()) answers.push($('#txtAnswerB').val());
            if(''!=$('#txtAnswerC').val().trim()) answers.push($('#txtAnswerC').val());
            if(''!=$('#txtAnswerD').val().trim()) answers.push($('#txtAnswerD').val());
            if(''!=$('#txtAnswerE').val().trim()) answers.push($('#txtAnswerE').val());
            if(''!=$('#txtAnswerF').val().trim()) answers.push($('#txtAnswerF').val());
            if(''!=$('#txtAnswerG').val().trim()) answers.push($('#txtAnswerG').val());
        }

    }

    if(question){
        item.id = question.replace(/[^\w\s]/gi, '').replace(/\s/gi,'_').toLowerCase();
        item.question = question;
    }

    if(qType) item.answerType = qType;

    item.choices = choices;
    item.answers = answers;

    bootbox.alert("Item Successfully Saved!");
    console.log(JSON.stringify(item))

    if(0 <= index)
        items[index] = item;
    else
        items.push(item);

    clearItemFields();
    reloadItemList();
};

var updateTopicDetails = function(){
    var _topicId = $('#hndTopicId').val();
    var _classCode = $('#hndCourseCode').val();

    if( (_topicId && ''!=_topicId) && (_classCode && ''!=_classCode) ){
        topic._id = _topicId;
        topic.classCode = _classCode;
        topic.topic = $('#txtTopic').val();
        topic.videoLink = $('#txtVideoUrl').val();
        topic.summary = $('#txtTopicSummary').val();
        topic.items = items;

        console.log(JSON.stringify(topic));

        if(topic.items && 0 < topic.items.length){
            $.post( "/updateTopic", JSON.stringify(topic), function(msg) {
                bootbox.alert({
                    message: msg,
                    callback: function () {
                        //clearTopicFields();
                    }
                });
            }, "json");
        }
        else
            bootbox.alert("Please enter some questions.");
    }
    else
        bootbox.alert("Please fill up all topic fields.");
};
