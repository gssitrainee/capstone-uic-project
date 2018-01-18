<#import "masterTemplate.ftl" as t>

<!doctype HTML>
<html>
<head>
<@t.headerMetaTags />

    <title>Capstone: Create a new Quiz</title>

    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/topics.css">

    <style>
        body{
            background-image: url("inner.jpg");
            background-size: 102% 102%;
            background-repeat: no-repeat;
        }

    </style>

</head>
<body>
<@t.navigationDiv />

<div class="container">
    <div class="row sm-flex-center top-space">
        <div class="col-sm-7 column-separator">
            <div class="panel panel-info">
                <h4><i class="fa fa-file-text-o" aria-hidden="true"></i>&nbsp;&nbsp;Topic</h4>
                <div class="panel-body" style="border-right: 1px solid #eee;">
                    <input type="hidden" id="hndTopicId" name="topicId" value="${topicId}" />
                    <input type="hidden" id="hndCourseCode" name="courseCode" value="${courseCode}" />

                    <div class="form-group has-error has-feedback">
                        <label for="txtClass" class="col-md-3 control-label">Course</label>
                        <div class="col-md-11">
                            <input type="text" id="txtClass" class="form-control" value='${course!""}' readonly />
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="txtTitle" class="col-md-3 control-label">Video Topics</label>
                        <div class="col-md-11">
                            <select id="selVideoTopics" class="form-control">
                                <option value="">Select a video</option>
                                <#if videos??>
                                    <#list videos as v>
                                        <option class="list-group-item" value="${v["_id"]}">${v["topic"]}</option>
                                    </#list>
                                </#if>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="txtTitle" class="col-md-3 control-label">Topic</label>
                        <div class="col-md-11">
                            <input type="text" class="form-control" id="txtTopic" name="subject" size="120" value='${topic!""}' readonly><br />
                        </div>
                    </div>

                    <div id="divVplayer" class="form-group">
                    <#if showVideoPlayer?? && showVideoPlayer==true>
                        <iframe></iframe>
                    </#if>
                    </div>

                    <div class="form-group right-inner-addon">
                        <div class="col-md-11">
                            <i class="fa fa-eye" aria-hidden="true"></i>
                            <input type="search" class="form-control" id="txtVideoUrl" name="videolink" placeholder="https://www.youtube.com/watch?v=<YoutubeVideoId>" title="Enter valid youtube video link and press ENTER key to refresh the video." value='${videoLink!""}' />
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="txtTopicSummary" class="col-md-3 control-label">Summary</label>
                        <div class="col-md-11">
                            <textarea class="form-control" id="txtTopicSummary" name="summary" placeholder="Topic Summary" title="Enter Content Summary" cols="30" rows="10">${summary!""}</textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-5 pull-right">
        <#if listTopScores??>
            <div class="panel panel-info">
                <h4>Top Scores</h4>
                <div class="panel-body underlined" style="border-right: 1px solid #eee;">
                    <table id="tblClassList" class="table table-hover">
                        <thead>
                        <tr>
                            <th>Student</th>
                            <th>Score</th>
                        </tr>
                        </thead>
                        <tbody>
                            <#if listTopScores??>
                                <#list listTopScores as ts>
                                <tr>
                                    <td>${ts["studentName"]}</td>
                                    <td>${ts["topicScore"]}</td>
                                </tr>
                                </#list>
                            </#if>
                        </tbody>
                    </table>
                </div>
            </div>
        <#elseif items??>
            <div class="panel panel-info">
                <h4><i class="fa fa-question-circle" aria-hidden="true"></i>&nbsp;Questions</h4>
                <div class="panel-body underlined" style="border-right: 1px solid #eee;">
                    <ol id="listQuestions">
                        <#list items as i>
                            <li onclick="displayItemDetails('${i["id"]}')">${i["question"]}</li>
                        </#list>
                    </ol>
                </div>
                <div>
                    <button type="button" class="btn btn-info btn-md" onclick="createNewItem()"><i class="fa fa-plus-square-o" aria-hidden="true"></i>&nbsp;Add Question</button>
                </div>
            </div>
        <#else>
            <div class="panel panel-info">
                <h4>Topic Not Found!</h4>
            </div>
        </#if>
        </div>

        <!-- Modal for Add Question -->
        <div class="modal fade" id="myModal" role="dialog">
            <div class="modal-dialog">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">Question</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="hndId" name="id" value="" />
                        <div class="form-group">
                            <label for="txtQuestion" class="control-label">Problem:</label>
                            <div>
                                <textarea class="form-control" id="txtQuestion" name="question" placeholder="Question" title="Enter Question (Sentence)"></textarea>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="selQuestionType" class="control-label">Answer Type:</label>
                            <div>
                                <select name="questionType" id="selQuestionType" class="form-control">
                                    <option value="">Select Answer Type</option>
                                    <option value="BOOLEAN">Boolean</option>
                                    <option value="SINGLE_ANSWER">Single Answer</option>
                                    <option value="MULTIPLE_ANSWERS">Multiple Answers</option>
                                </select>
                            </div>
                        </div>

                        <div id="divBooleanChoices" class="form-group">
                            <label class="control-label">Answer:</label>
                            <div class="radio">
                                <input type="radio" id="rdbTrue" name="bChoice" value="True" />
                                <label for="rdbTrue" class="radio-inline">True</label>
                            </div>
                            <div class="radio">
                                <input type="radio" id="rdbFalse" name="bChoice" value="False" />
                                <label for="rdbFalse" class="radio-inline">False</label>
                            </div>
                        </div>

                        <div id="divTextAnswers" class="form-group">
                            <h5>Choices</h5>
                            <div id="divChoices">
                                <p>
                                    <input type="text" class="form-control" id="txtChoiceA" name="choices" class="form-control" placeholder="a.)" title="Enter Option or Choice that the student can select in answering the question." />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtChoiceB" name="choices" class="form-control" placeholder="b.)" title="Enter Option or Choice that the student can select in answering the question." />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtChoiceC" name="choices" class="form-control" placeholder="c.)" title="Enter Option or Choice that the student can select in answering the question." />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtChoiceD" name="choices" class="form-control" placeholder="d.)" title="Enter Option or Choice that the student can select in answering the question." />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtChoiceE" name="choices" class="form-control" placeholder="e.)" title="Enter Option or Choice that the student can select in answering the question." />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtChoiceF" name="choices" class="form-control" placeholder="f.)" title="Enter Option or Choice that the student can select in answering the question." />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtChoiceG" name="choices" class="form-control" placeholder="g.)" title="Enter Option or Choice that the student can select in answering the question." />
                                </p>
                            </div>

                            <h5>Answer(s)</h5>
                            <div id="divSingleAnswer">
                                <p>
                                    <input type="text" class="form-control" id="txtSingleAnswer" name="sanswer" class="form-control" placeholder="[Answer]" />
                                </p>
                            </div>
                            <div id="divMultipleAnswer">
                                <p>
                                    <input type="text" class="form-control" id="txtAnswerA" name="manswer" class="form-control" placeholder="[Answer]" />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtAnswerB" name="manswer" class="form-control" placeholder="[Answer]" />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtAnswerC" name="manswer" class="form-control" placeholder="[Answer]" />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtAnswerD" name="manswer" class="form-control" placeholder="[Answer]" />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtAnswerE" name="manswer" class="form-control" placeholder="[Answer]" />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtAnswerF" name="manswer" class="form-control" placeholder="[Answer]" />
                                </p>
                                <p>
                                    <input type="text" class="form-control" id="txtAnswerG" name="manswer" class="form-control" placeholder="[Answer]" />
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-info" data-dismiss="modal" onclick="saveQuestion()"><i class="fa fa-floppy-o" aria-hidden="true"></i>&nbsp;Save</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><i class="fa fa-times" aria-hidden="true"></i>&nbsp;Close</button>
                        <button type="button" class="btn btn-danger" data-dismiss="modal" onclick="removeQuestion()"><i class="fa fa-trash-o" aria-hidden="true"></i>&nbsp;Remove</button>
                    </div>
                </div>

            </div>
        </div>
        <!-- End: Modal For Add Question -->

        <#if allowTopicSubmit?? && allowTopicSubmit>
            <div class="row top-space">
                <div class="form-group">
                    <!-- Button -->
                    <div class="col-md-offset-3 col-md-11">
                        <button type="button" class="btn btn-primary btn-lg" onclick="updateTopicDetails()">Update Topic</button>
                    </div>
                </div>
            </div>
        </#if>

    </div>

</div>

<@t.bootstrapCoreJS />
<script src="js/editTopic.js"></script>
<script>
    var topic = new Object();
    var items = JSON.parse('${jsonItems}');
</script>
</body>
</html>