<#import "masterTemplate.ftl" as t>

<!doctype HTML>
<html>
<head>
<@t.headerMetaTags />

    <title>Capstone: Add a video topic</title>

    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/video.css" rel="stylesheet">
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <style>
        body{
            background-image: url("inner.jpg");
            background-size: 101.5% 101.5%;
            background-repeat: no-repeat;

        }
        .right-inner-addon {
            position: relative;
        }

        .right-inner-addon i {
            position: absolute;
            right: 15px;
            padding: 10px 12px;
            pointer-events: none;
        }
    </style>
</head>
<body>
<@t.navigationDiv />

<div class="container">
    <div class="row">
        <div class="panel panel-info" style="margin-top: 100px;">
            <h4><i class="fa fa-youtube" aria-hidden="true"></i>&nbsp;Video</h4>
            <hr />
            <div class="panel-body">
                <div class="form-group has-error has-feedback">
                    <label for="selClass" class="col-md-3 control-label">Class</label>
                    <div class="col-md-11">
                        <select name="sel_class" id="selClass" class="form-control">
                        <#if userClasses??>
                            <ul class="list-group">
                                <option class="list-group-item" value="">Select a Class</option>
                                <#list userClasses as cls>
                                    <option class="list-group-item" value="${cls["_id"]}">${cls["className"]}</option>
                                </#list>
                            </ul>
                        <#else>
                            <option value="N_A">No Class Available</option>
                        </#if>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="txtTitle" class="col-md-3 control-label">Topic</label>
                    <div class="col-md-11">
                        <input type="text" class="form-control" id="txtTopic" name="subject" size="120" value=""><br />
                    </div>
                </div>

                <div id="divVplayer" class="form-group">
                    <iframe></iframe>
                </div>

                <div class="form-group right-inner-addon">
                    <div class="col-md-11">
                        <i class="fa fa-eye" aria-hidden="true"></i>
                        <input type="search" class="form-control" id="txtVideoUrl" name="videolink" placeholder="https://www.youtube.com/watch?v=<YoutubeVideoId>" title="Enter valid youtube video link and press ENTER key to refresh the video." value="https://www.youtube.com/watch?v=__y8vWaVGqk" />
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row top-space">
        <div class="form-group">
            <div class="col-md-offset-3 col-md-11">
                <button type="button" class="btn btn-primary btn-md" onclick="postVideo()"><i class="fa fa-youtube" aria-hidden="true"></i>&nbsp;&nbsp;Add Video</button>
            </div>
        </div>
    </div>
</div>
<@t.footerDiv />
<@t.bootstrapCoreJS />

<script>
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
</script>

</body>
</html>

