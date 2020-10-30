

function reloadFiles() {
    let request = new XMLHttpRequest();
    request.open("get", "/file/list", true);
    request.send(null);
    request.onload = function (event) {
        if (request.readyState === 4 && request.status === 200) {
            const files = $("#files");
            files.empty();
            files.append(request.responseText);
        }else{
            alert("エラーが発生しました。");
        }
    };
    // Ajaxが異常終了した場合
    request.onerror = function (event) {
        alert("エラーが発生しました。");
    }
}

function showNotification(from, align, type, text){

    $.notify({
        icon: "add_alert",
        message: text

    },{
        type: type,
        timer: 4000,
        placement: {
            from: from,
            align: align
        }
    });
}

function deleteFile(fileName) {

    var uploadURL ="/file/delete/" + fileName;
    $.ajax({
        url: uploadURL,
        type: "GET",
        contentType:false,
        processData: false,
        cache: false,
        timeout: 30000,
        beforeSend: function(xhr, settings) {

        },
        complete: function(xhr, textStatus) {
            //通信完了
        },
        success: function(result, textStatus, xhr) {
            //ajax通信が成功した
            reloadFiles()
            showNotification('top', 'right', 'danger', 'Deleted.')
        },
        error: function(xhr, textStatus, error) {
            //ajax通信が失敗した
            $('#status1').append('削除に失敗しました<br>');
        }
    });
}

function upload() {
    sendFileToServer(new FormData($("#upload-form").get(0)));
}

function sendFileToServer(formData){
    var uploadURL ="/file/upload"; //Upload URL
    $.ajax({
        url: uploadURL,
        type: "POST",
        contentType:false,
        processData: false,
        cache: false,
        data: formData,
        timeout: 30000,
        beforeSend: function(xhr, settings) {
            //送信前の処理
            //$("#status1").clear()
            $("#status1").append("Uploading..<br>");
        },
        complete: function(xhr, textStatus) {
            //通信完了
        },
        success: function(result, textStatus, xhr) {
            //ajax通信が成功した
            reloadFiles()
            const status = $("#status1");
            status.empty();
            status.append("Uploaded<br>")
            showNotification('top', 'right', 'success', 'Uploaded.')
        },
        error: function(xhr, textStatus, error) {
            //ajax通信が失敗した
            $('#status1').append('送信に失敗しました<br>');
        }
    });
}

$(function() {

    reloadFiles()

    function handleFileUpload(files,obj){
        var fd = new FormData();
        for (var i = 0; i < files.length; i++){
            fd.append('files', files[i]);
        }
        sendFileToServer(fd);
    }
    $(document).ready(function(){
        var obj = $("#dragdroparea");
        obj.on('dragenter', function (e) {
            e.stopPropagation();
            e.preventDefault();
        });
        obj.on('dragover', function (e) {
            e.stopPropagation();
            e.preventDefault();
        });
        obj.on('drop', function (e) {
            e.preventDefault();
            var files = e.originalEvent.dataTransfer.files;
            handleFileUpload(files);
        });
        $(document).on('dragenter', function (e){
            e.stopPropagation();
            e.preventDefault();
        });
        $(document).on('dragover', function (e){
            e.stopPropagation();
            e.preventDefault();
        });
        $(document).on('drop', function (e){
            e.stopPropagation();
            e.preventDefault();
        });
    });
});