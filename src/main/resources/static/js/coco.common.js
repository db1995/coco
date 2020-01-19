$(function () {
    window.onresize = function () {
        $('.coco-dialog').css("height", window.innerHeight * 0.6);
    };
    resize();
});

function getTime() {
    let today = new Date();
    let h = today.getHours();
    let m = today.getMinutes();
    let s = today.getSeconds();
    return h + ':' + (m > 9 ? m : "0" + m) + ':' + (s > 9 ? s : "0" + s);
}

function resize() {
    $('.coco-dialog').css("height", window.innerHeight * 0.6);
}