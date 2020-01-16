const serverAddress = "localhost";
const serverPort = 8080;
let id = "";

function socket() {
    //Determine whether the current browser supports WebSocket
    if ('WebSocket' in window) {
        const ws = new WebSocket("ws://" + serverAddress + ":" + serverPort + "/customer/" + id);
        let before = null;
        //Connect failed callback
        ws.onerror = function () {
            $('#chatarea').append('<p class="text-center"><span class="bg-light">Connection error</span></p>');
        };
        //Connect succeed callback
        ws.onopen = function () {
            $('#chatarea').append('<p class="text-center"><span class="bg-light">Connection succeeded</span></p>');
        }
        //Receive message callback
        ws.onmessage = function (event) {
            let json = JSON.parse(event.data);
            /*if (json.message != null) {
                alert(json.message)
            }*/
            if (before == null) {
                before = json.customerInQueue;
            }
            switch (json.type) {
                case "MESSAGE":
                    $('#chatarea').append('<p class="text-left"><small class="bg-light">'
                        + json.serviceName + '&nbsp;' + getTime() + '</small><br>'
                        + json.message + '</p>');
                    break;
                case "FORWARD":
                    if (before != 0) {
                        $("#waitingBeforeYou").text(--before);
                    }
                    break;
                case "START_SERVICE":
                    $('#chatarea').append('<p class="text-center"><span class="bg-light">' + 'Connected service' + '</span></p>')
                        .append('<p><small class="bg-light">' + json.serviceName + '&nbsp;' + getTime() + '</small><br>'
                            + json.message + '</p>');
                    before = 0;
                    $("#waitingBeforeYou").text(0);
                    break;
                case "WAIT_SERVICE":
                    $("#waitingBeforeYou").text(json.customerInQueue);
                    break;
                case "SERVICE_DOWN":
                    alert("客服已掉线");
            }
            let scrollHeight = $('#chatarea').prop('scrollHeight');
            $('#chatarea').scrollTop(scrollHeight);
        }
        //Connect closed callback
        ws.onclose = function () {
            $('#chatarea').append('<p class="text-center"><span class="bg-light">Connection closed</span></p>');
        }
        //Close the websocket connection when the window is closed, preventing throwing exceptions.
        window.onbeforeunload = function () {
            ws.close();
            console.log("close...")
        }
        //Send message
        $('#send').click(function () {
            let message = $('#message').val();
            if (message != '') {
                ws.send(message);
                $('#chatarea').append('<p class="text-right"><small class="bg-light" ">' + getTime() + '</small><br>' + message + '</p>');
                $('#message').val('').focus();
            }
            let scrollHeight = $('#chatarea').prop('scrollHeight');
            $('#chatarea').scrollTop(scrollHeight);
        });
        // Press enter to send message
        $('#message').bind('keyup', function(event) {
            if (event.keyCode == "13") {
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