const serverAddress = "localhost";
const serverPort = 8080;
let id = "";
let closeTipTimeout;
let closeTimeout;

function handleTimeout(ws) {
    closeTipTimeout = window.setTimeout(function () {
        $('#coco-dialog').append('<p class="text-center"><span class="bg-light">Connection will be closed after 1 minute</span></p>');
        closeTimeout = window.setTimeout(function () {
            ws.close();
        }, 120 * 1000);
    }, 60 * 1000);
}

function socket() {
    //Determine whether the current browser supports WebSocket
    if ('WebSocket' in window) {
        const ws = new WebSocket("ws://" + serverAddress + ":" + serverPort + "/customer/" + id);
        let before = null;
        //Connect failed callback
        ws.onerror = function () {
            $('#coco-dialog').append('<p class="text-center"><span class="bg-light">Connection error</span></p>');
        };
        //Connect succeed callback
        ws.onopen = function () {
            $('#coco-dialog').append('<p class="text-center"><span class="bg-light">Connection succeeded</span></p>');
            handleTimeout(ws);
        };
        //Receive message callback
        ws.onmessage = function (event) {
            let json = JSON.parse(event.data);
            /*if (json.message != null) {
                alert(json.message)
            }*/
            if (before == null) {
                before = json.customerInQueue;
            }
            let dialog = $('#coco-dialog');
            let waitingBeforeYouObj = $("#waitingBeforeYou");
            switch (json.type) {
                case "MESSAGE":
                    dialog.append('<p class="text-left"><small class="bg-light">'
                        + json.serviceName + '&nbsp;' + getTime() + '</small><br>'
                        + json.message + '</p>');
                    break;
                case "FORWARD":
                    if (before !== 0) {
                        waitingBeforeYouObj.text(--before);
                    }
                    break;
                case "START_SERVICE":
                    dialog.append('<p class="text-center"><span class="bg-light">' + 'Connected service' + '</span></p>')
                        .append('<p><small class="bg-light">' + json.serviceName + '&nbsp;' + getTime() + '</small><br>'
                            + json.message + '</p>');
                    before = 0;
                    waitingBeforeYouObj.text(0);
                    break;
                case "WAIT_SERVICE":
                    waitingBeforeYouObj.text(json.customerInQueue);
                    break;
                case "SERVICE_DOWN":
                    dialog.append('<p class="text-center"><span class="bg-light">' + json.message + '</span></p>');
                    ws.close();
                    break;
            }
            let scrollHeight = dialog.prop('scrollHeight');
            dialog.scrollTop(scrollHeight);
        };
        //Connect closed callback
        ws.onclose = function () {
            $('#coco-dialog').append('<p class="text-center"><span class="bg-light">Connection closed</span></p>');
        };
        //Close the websocket connection when the window is closed, preventing throwing exceptions.
        window.onbeforeunload = function () {
            ws.close();
        };
        //Send message
        $('#send').click(function () {
            let messageObj = $('#message');
            let messageVal = messageObj.val();
            let dialog = $('#coco-dialog');
            if (messageVal !== '') {
                ws.send(messageVal);
                dialog.append('<p class="text-right"><small class="bg-light" ">' + getTime() + '</small><br>' + messageVal + '</p>');
                messageObj.val('').focus();
            }
            let scrollHeight = dialog.prop('scrollHeight');
            dialog.scrollTop(scrollHeight);
            window.clearTimeout(closeTimeout);
            window.clearTimeout(closeTipTimeout);
            handleTimeout(ws);
        });
        // Press enter to send message
        $('#message').bind('keyup', function (event) {
            if (event.keyCode === 13) {
                $('#send').trigger('click');
            }
        });
    } else {
        alert('Your browser does not support WebSocket. Please change your browser and try again.');
    }
}

$(function () {
    $.ajax({
        url: "/customer",
        type: "get",
        success: function (result) {
            id = result;
            socket(id);
        }
    });
});