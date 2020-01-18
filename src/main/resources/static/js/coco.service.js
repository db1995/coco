const serverAddress = "localhost";
const serverPort = 8080;
const customerIdSet = new Set();

function socket(token) {
    //Determine whether the current browser supports WebSocket
    if ('WebSocket' in window) {
        loadStatistics();
        const ws = new WebSocket("ws://" + serverAddress + ":" + serverPort + "/service/" + token);
        let waiting = null;
        //Connect failed callback
        ws.onerror = function () {
            //$('.chatarea').append('<p class="text-center"><span class="bg-light">Connection error</span></p>');
        };
        //Connect succeed callback
        ws.onopen = function () {
            //$('.chatarea').append('<p class="text-center"><span class="bg-light">Connection succeeded</span></p>');
        }
        //Receive message callback
        ws.onmessage = function (event) {
            let json = JSON.parse(event.data);
            if (waiting == null) {
                waiting = json.customerInQueue;
            }
            let chatareaObject = $("#" + json.customerId).children(".chatarea");
            let customerId = json.customerId;
            switch (json.type) {
                case "MESSAGE":
                    chatareaObject.append('<p class="text-left"><small class="bg-light">' + getTime() + '</small><br>' + json.message + '</p>');
                    const e = $("[href='#" + customerId + "']").children(".badge");
                    if (e.hasClass("badge-danger") || $("[href='#" + customerId + "']").hasClass("active")) {
                        break;
                    }
                    if (e.text() == "") {
                        e.text(1);
                    } else {
                        let num = parseInt(e.text());
                        e.text(++num);
                    }
                    break;
                case "FORWARD":
                    if (waiting != 0) {
                        $("#waiting").text(--waiting);
                    }
                    break;
                case "START_SERVICE":
                    customerIdSet.add(customerId);
                    $(".list-group").append('<a class="rounded-pill list-group-item list-group-item-action d-flex justify-content-between align-items-center" data-toggle="list" href="#' + json.customerId + '" role="tab">CUST ' + customerIdSet.size + '<span class="badge badge-danger badge-pill">NEW</span></a>');
                    const ch = '<div class="tab-pane" id="' + customerId + '" role="tabpanel">\n' +
                        '                    <div id="chatarea_' + customerId + '" class="border rounded border-secondary bg-white p-2 chatarea"\n' +
                        '                         style="word-wrap: break-word;overflow-y: auto;">\n' +
                        '                    </div>\n' +
                        '                    <div class="input-group mb-3 bg-white">\n' +
                        '                        <input id="message_' + customerId + '" autofocus="autofocus" class="form-control message" maxlength="30"\n' +
                        '                               placeholder="Say something(within 30 chars)"\n' +
                        '                               type="text">\n' +
                        '                        <div class="input-group-append">\n' +
                        '                            <button id="send_' + customerId + '" class="btn btn-outline-primary send" type="button">Send</button>\n' +
                        '                        </div>\n' +
                        '                    </div>\n' +
                        '                </div>';
                    $("#nav-tabContent").append(ch);
                    chatareaObject.append('<p class="text-center"><span class="bg-light">' + 'Connected service' + '</span></p>');
                    resize();
                    if (waiting != 0) {
                        $("#waiting").text(--waiting);
                    }
                    $("#list-tab").on("click","[href='#"+customerId+"']",function(){
                        $(".tab-pane").removeClass("active");
                        $("#"+customerId).addClass("active");
                        $("[href='#"+customerId+"']").children(".badge")
                            .removeClass("badge-danger").addClass("badge-warning").text("");
                        $("#message_"+customerId).focus();
                    });
                    $(".tab-pane").on("click", ".send", function () {
                        let customerId = ($(this).attr("id").split("_"))[1];
                        let messageObj = $(this).parent().prev(".message");
                        if (messageObj.val() !== '') {
                            let msg = {"customerId": customerId, "message": messageObj.val()};
                            ws.send(JSON.stringify(msg));
                            $("#" + customerId).children('.chatarea').append('<p class="text-right"><small class="bg-light" ">' + getTime() + '</small><br>' + messageObj.val() + '</p>');
                        }
                        messageObj.val('').focus();
                        let scrollHeight = $('.chatarea').prop('scrollHeight');
                        $('.chatarea').scrollTop(scrollHeight);
                    });
                    // Press enter to send message
                    $('#message_'+customerId).bind('keyup', function(event) {
                        if (event.keyCode == "13") {
                            $('#send_'+customerId).trigger('click');
                        }
                    });
                    break;
                case "WAIT_SERVICE":
                    $("#waiting").text(++waiting);
                    break;
                case "CUSTOMER_LEFT":
                    $('#chatarea_'+customerId).append('<div class="card text-center">\n' +
                        '  <div class="card-body">\n' +
                        '    <h5 class="card-title">Customer has left</h5>\n' +
                        '    <p class="card-text">The service time: 12m</p>\n' +
                        '    <a id="close_'+ customerId +'" href="#" class="btn btn-warning">Close dialog</a>\n' +
                        '  </div>\n' +
                        '</div>');
                    $("[href='#"+customerId+"']").addClass("bg-secondary").css("border-color", "#ffffff");
                    $('#nav-tabContent').on('click', '#close_'+customerId, function () {
                        $("[href='#statistics']").trigger('click');
                        $("[href='#"+customerId+"'], #chatarea_"+customerId+", #"+customerId).remove();
                    });
                    break;
            }
            let scrollHeight = chatareaObject.prop('scrollHeight');
            chatareaObject.scrollTop(scrollHeight);
        }
        //Connect closed callback
        ws.onclose = function () {
            //$('.chatarea').append('<p class="text-center"><span class="bg-light">Connection closed</span></p>');
        }
        //Close the websocket connection when the window is closed, preventing throwing exceptions.
        window.onbeforeunload = function () {
            ws.close();
        }
        //Send message
        $('.send').click(function () {
            let customerId = ($(this).attr("id").split("_"))[1];
            // alert(customerId)
            let message = $(this).parent().prev(".message");
            if (message != '') {
                let msg = {"customerId": customerId, "message": message.val()};
                ws.send(JSON.stringify(msg));
                $("#" + customerId).children('.chatarea').append('<p class="text-right"><small class="bg-light" ">' + getTime() + '</small><br>' + message + '</p>');
                message.val('').focus();
            }
            let scrollHeight = $('.chatarea').prop('scrollHeight');
            $('.chatarea').scrollTop(scrollHeight);
        });
    } else {
        alert('Your browser does not support WebSocket. Please change your browser and try again.');
    }
}

$(function () {
    $("#login-form").trigger("click");
    $("#login").click(function () {
        $.ajax({
            url: "/service/login",
            data: {"username": $("#email").val(), "password": $("#password").val()},
            type: "post",
            success: function (token) {
                if (token != "") {
                    socket(token);
                    $(".close").trigger("click");
                }
            }
        });
    });

    $("#pill-statistics").click(function () {
        loadStatistics();
    });
});

// Load statistics
function loadStatistics() {
    $.ajax({
        url: "/service/statistics",
        dataType: "json",
        success: function (data) {
            $("#todayServed").text(data.todayServed);
            $("#todayOnline").text(data.todayOnline);
            $("#todayScore").text(data.todayScore);
            $("#totalServed").text(data.totalServed);
            $("#totalOnline").text(data.totalOnline);
            $("#totalScore").text(data.totalScore);
        }
    });
}