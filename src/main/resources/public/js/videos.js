var video = new Object();
$(document).ready(function () {
    $('#divVplayer').hide();

    $('#txtVideoUrl').on('keydown', function(event){
        var keyCode = (event.keyCode ? event.keyCode : event.which);
        if (keyCode == 13) {
            reloadVideo(this.value);
        }
    });
});

var reloadVideo = function(youtubeId){
    if(youtubeId && 0 < youtubeId.length){
        //Cleanup
        var video_id = youtubeId.split("v=")[1];
        var youtubeUrl = "https://www.youtube.com/embed/" + video_id;

        $('#divVplayer iframe').remove();

        var iframeHTML = "<iframe id='ytplayer' type='text/html' width='560' height='315' src='" + youtubeUrl + "' frameborder='0' allowfullscreen></iframe>";
        //https://www.youtube.com/watch?v=__y8vWaVGqk
        //https://www.youtube.com/embed/__y8vWaVGqk
        //https://www.youtube.com/watch?v=__y8vWaVGqk?autoplay=1&origin=http://example.com

        $('#divVplayer').append(iframeHTML);
        $('#divVplayer').show();

    }
};

var clearVideoFields = function(){
    video = new Object();
    $('#txtTopic, #selClass').val('');
    $('#txtVideoUrl').val('');
    $('#divVplayer').hide();
};


var postVideo = function(){
    var _topic = $('#txtTopic').val();
    var _classCode = $('#selClass').val();
    var _link = $('#txtVideoUrl').val();

    if( (_topic && ''!=_topic) && (_classCode && ''!=_classCode) && (_link && ''!=_link) ){
        video._id = _topic.replace(/[^\w\s]/gi, '').replace(/\s/gi,'_').toLowerCase();
        video.classCode = _classCode;
        video.topic = _topic;
        video.videoLink = _link;

        console.log(JSON.stringify(video));

        $.post( "/addVideo", JSON.stringify(video), function(msg) {
            bootbox.alert(msg);
            clearVideoFields();
        }, "json");
    }
    else
        bootbox.alert("Please complete all video details.");
};