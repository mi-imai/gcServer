

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
            showNotification('top', 'right', 'danger', 'Failed to get files.')
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

function deleteFile(fileId, fileName) {
    bootbox.confirm("ファイルを削除してもよろしいですか？<br>ファイル名: " + fileName, function (result) {
        if (result) {
            var uploadURL ="/file/delete/" + fileId;
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
    });
}

function upload() {
    var files  = document.getElementById('upload-input').files

    var fd = new FormData();
    for (var i = 0; i < files.length; i++){
        fd.append('files', files[i]);
    }
    sendFileToServer(fd);
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
        async: true,
        xhr : function(){
            var XHR = $.ajaxSettings.xhr();
            if(XHR.upload){
                XHR.upload.addEventListener('progress',function(e){
                    document.getElementById("progress-bar").style.width = `${e.loaded/e.total*100}%`;
                }, false);
            }
            return XHR;
        },
        beforeSend: function(xhr, settings) {
            //送信前の処理
            //$("#status1").clear()
            showNotification('top', 'right','info', 'Uploading...')
            document.getElementById("progress").style.visibility = "visible";
            $("#status1").append("Uploading..<br>");
        },
        complete: function(xhr, textStatus) {
            //通信完了
            document.getElementById("progress").style.visibility = "hidden";
        },
        success: function(result, textStatus, xhr) {
            //ajax通信が成功した
            reloadFiles()
            const status = $("#status1");
            status.empty();
            status.append("Uploaded<br>")
            showNotification('top', 'right', 'success', 'Uploaded.')
            document.getElementById("progress-bar").style.width = 0;
        },
        error: function(xhr, textStatus, error) {
            //ajax通信が失敗した
            showNotification('top', 'right', 'danger', 'Failed to upload file.')
            document.getElementById("progress-bar").style.width = 0;
            $('#status1').append('送信に失敗しました<br>');
        }
    });
}

$(function() {

    reloadFiles()
    document.getElementById("progress").style.visibility = "hidden";

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