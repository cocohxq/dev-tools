$(document).ready(function () {
    //dubbo执行结果
    $("#dubbo .submit").unbind("click").bind('click',function () {
        ajaxLoad({
            id:"dubbo",
            eventName:"submit",
            toolName:"dubbo",
            eventSource:"submit",
            addCookie:false,
            resultType:"formatJson",
            getValCallBack:function (param) {
                var invokeConfig = {};
                $("#dubbo #form input").each(function () {
                    if($(this).attr("lay-verify") == "required" && !$(this).val()){
                        layer.alert('必填项不能为空');
                        return false;
                    }
                    if($(this).attr("name")){
                        invokeConfig[$(this).attr("name")] = $(this).val();
                    }
                });
                $("#dubbo #form select").each(function () {
                    if($(this).val() == -1){
                        layer.alert('必填项不能为空');
                        return false;
                    }
                    invokeConfig[$(this).attr("name")] = $(this).val();
                });
                invokeConfig.params = JSON.parse($("#param").val());
                param.param = JSON.stringify(invokeConfig);
                return true;
            },
            sucCallback:function (data) {
                $("#dubbo #result").html(data);
            }
        });
    });

    //jar,service,method,param的联动初始化
    dubboSelectCascade("jarName","interfaceClassName",["jarName"]);
    dubboSelectCascade("interfaceClassName","methodName",["jarName","interfaceClassName"]);
    dubboSelectCascade("methodName","param",["jarName","interfaceClassName","methodName"]);


});




function loadDubboParam(obj) {
    ajaxLoad({
        id:"dubbo",
        eventName:"get",
        toolName:"dubbo",
        eventSource:"loadParam",
        addCookie:false,
        getValCallBack:function (param) {
            $(obj).parents("#envContent:eq(0)").find("input").each(function () {
                var val = $(this).val();
                if(!val){
                    layer.alert('必填项不能为空');
                    return false;
                }
                param[$(this).attr("name")] = val;
            });
            return true;
        },
        sucCallback:function (data) {
            $("#dubbo select[name='jarName'] option").remove();
            $("#dubbo select[name='jarName']").append('<option value="-1">请选择</option>');
            for(var i=0;i<data.length;i++){
                $("#dubbo select[name='jarName']").append('<option value="'+data[i]+'">'+data[i]+'</option>');
            }
            layui.form.render('select');
            closeLayer();
            layer.msg('成功');
        }
    });
}

/**
 * dubbo的select级联
 * @param triggerName
 * @param actorName
 * @param ext
 */
function dubboSelectCascade(triggerName,actorName,ext) {
    var resultType = "";
    if(triggerName == "methodName"){
        resultType = "formatJson";
    }
    layui.form.on('select('+triggerName+')', function(data){
        console.log(data);
        ajaxLoad({
            id:"dubbo",
            eventName:"get",
            toolName:"dubbo",
            eventSource:triggerName,
            addCookie:false,
            resultType:resultType,
            getValCallBack:function (param) {
                if(data.value == -1){
                    return false;
                }
                for(var i=0;i<ext.length;i++){
                    var t = ext[i];
                    param[t]= $("#dubbo select[name='"+t+"']").val();
                    if(!param[t]){
                        return false;
                    }
                }
                return true;
            },
            sucCallback:function (data) {
                if(triggerName == "interfaceClassName"){//顺带把group和version带出来
                    $("#dubbo input[name='group']").val(data.group);
                    $("#dubbo input[name='version']").val(data.version);
                    $("#dubbo input[name='group']").val(data.group);
                    var methods = new Array();
                    for(var key in data.methodInfoMap){
                        methods.unshift(key);
                    }
                    data = methods;
                }
                if(actorName == 'param'){
                    $("#dubbo #param").val(data);
                }else{
                    $("#dubbo select[name='"+actorName+"'] option").remove();
                    $("#dubbo select[name='"+actorName+"']").append('<option value="-1">请选择</option>');
                    for(var i=0;i<data.length;i++){
                        $("#dubbo select[name='"+actorName+"']").append('<option value="'+data[i]+'">'+data[i]+'</option>');
                    }
                    layui.form.render('select');
                }
            }
        });
    });
}

