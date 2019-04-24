var curLayer;
$(document).ready(function () {
    //通用新增环境
    $(".addEnv").unbind("click").bind('click',function () {
        var parentId = "#"+$(this).parents(".tool").eq(0).attr("id");
        curLayer = layer.open({
            type: 1,
            skin: 'layui-layer-rim', //加上边框
            area: ['800px', '500px'], //宽高
            content: $(parentId +" #env").html()
        });
    });

    //左侧菜单切换
    $(".layui-nav-child dd").unbind("click").bind('click',function () {
        var toolName = $(this).attr("toolName");
        $(".tool").hide();
        $("#"+toolName).show();
        //填充cookie
        var dubboConfig = JSON.parse(getCookie(toolName+"_config"));
        if(dubboConfig) {
            $("#" + toolName + " input").each(function () {
                $(this).val(dubboConfig[$(this).attr("name")]);
            });
        }
    });

    //通用重置
    $(".reset").unbind("click").bind('click',function () {
        var parentId = $(this).parents(".tool").eq(0).attr("id");
        $("#"+parentId+" input").each(function () {
            $(this).val("");
        });

        $("#"+parentId+" textarea").each(function () {
            $(this).val("");
        });
        $("#"+parentId+" select").each(function () {
            $(this).val("");
        });
        layui.form.render('select');
    });

});


/**
 * 异步表单请求
 * @param url
 * @param param
 * @param sucCallback
 */
function ajaxLoad(formConfig) {
    var param = {};
    if(!formConfig.getValCallBack(param)){
        return;
    }
    param.eventName=formConfig.eventName;
    param.toolName=formConfig.toolName;
    param.eventSource=formConfig.eventSource;
    param.addCookie=formConfig.addCookie;
    param.resultType=formConfig.resultType;

    var retType = "json";
    if(formConfig.resultType == "formatJson"){
        retType = "text";
    }
    if(formConfig.async == undefined){
        formConfig.async = true;
    }

    var loadingLayer;

    $.ajax({
        type: 'POST',
        url: '/execute',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(param),
        async:formConfig.async,
        dataType: retType,
        beforeSend:function(){
            if(formConfig.loading){
                loadingLayer = layer.load(2, { //icon支持传入0-2
                    shade: [0.5, 'gray'], //0.5透明度的灰色背景
                    content: '加载中...'
                });
            }
        },
        success: function (data) {
            if(loadingLayer){
                layer.close(loadingLayer);
            }
            if(formConfig.resultType == "formatJson"){
                var datas = data.split("&&");
                if(null != datas && datas[0] != -1){
                    formConfig.sucCallback(datas[2]);
                }else{
                    layer.alert('请求异常,'+datas[1]);
                    return;
                }
            }else{
                if(data.code != -1){
                    formConfig.sucCallback(data.data);
                }else{
                    layer.alert('请求异常,'+data.message);
                    return;
                }
            }
        },
        error:function(e){
            console.log(e);
            layer.close(loadingLayer);
            layer.alert('请求错误');
        }
    });
}

//读取cookies
function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg)) {
        return unescape(arr[2]);
    }
    else {
        return null;
    }
}
//关闭弹层
function closeLayer() {
    layer.close(curLayer);
}
//加载
function loading(){
    var loadingLayer = layer.load(2, {
        shade: [0.5, 'gray'], //0.5透明度的灰色背景
        shadeClose: false,
        type: 2,
        content: 'loading...'
    });
    return loadingLayer;
}


