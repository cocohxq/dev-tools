$(document).ready(function () {
    //加载db列表
    ajaxLoad({
        id:"redis",
        eventName:"PAGELOAD",
        toolName:"redis",
        eventSource:"init",
        addCookie:false,
        getValCallBack:function (param) {
            return true;
        },
        sucCallback:function (data) {
            $("#redis select[name='dbIndex'] option").remove();
            $("#redis select[name='dbIndex']").append('<option value="-1">请选择</option>');
            for(var i=0;i<data.redisInfo.dbList.length;i++){
                $("#redis select[name='dbIndex']").append('<option value="'+data.redisInfo.dbList[i]+'">'+data.redisInfo.dbList[i]+'</option>');
            }

            if(data.outClassSupply){
                $("#redis select[name='classes'] option").remove();
                $("#redis select[name='classes']").append('<option value="-1">请选择</option>');
                for(var i=0;i<data.classes.length;i++){
                    $("#redis select[name='classes']").append('<option value="'+data.classes[i]+'">'+data.classes[i]+'</option>');
                }

                $("#outClass").show();
            }

            layui.form.render('select');
        }
    });
});
//添加依赖
function addRef(obj) {
    let windowObj = $(obj).parents("#envContent:eq(0)");//window里面的内容是copy的，会出现id重复的问题，要通过这种方式来唯一定位
    let source = windowObj.find("#source:eq(0)").val();
    if(!source || source == ""){
        return;
    }
    let sources = JSON.parse(sessionStorage.getItem("sources"));
    if(!sources){
        sources = new Array();
    }
    sources.push(source);
    sessionStorage.setItem("sources", JSON.stringify(sources));
    windowObj.find("#source:eq(0)").val("");
}

//提交源码
function loadSource(obj) {
    ajaxLoad({
        id:"redis",
        eventName:"DATALOAD",
        toolName:"redis",
        eventSource:"load",
        addCookie:false,
        loading:true,
        getValCallBack:function (param) {
            addRef(obj);
            let sources = JSON.parse(sessionStorage.getItem("sources"));
            if(!sources || sources.length == 0){
                layer.alert('没有可提交的源码');
                return false;
            }
            param.sources = JSON.stringify(sources);
            sessionStorage.removeItem("sources");
            return true;
        },
        sucCallback:function (data) {
            $("#redis select[name='classes'] option").remove();
            $("#redis select[name='classes']").append('<option value="-1">请选择</option>');
            for(var i=0;i<data.length;i++){
                $("#redis select[name='classes']").append('<option value="'+data[i]+'">'+data[i]+'</option>');
            }
            layui.form.render('select');
            layer.msg('成功');
        }
    });
}


function loadRedisParam(obj) {
    ajaxLoad({
        id:"redis",
        eventName:"DATALOAD",
        toolName:"redis",
        eventSource:"keys",
        addCookie:false,
        loading:true,
        getValCallBack:function (param) {
            var dbIndex = $("#redis select[name='dbIndex']").val();
            if(!dbIndex || dbIndex == -1){
                layer.alert('db必填项不能为空');
                return false;
            }
            var keyStr = $("#redis input[name='keyStr']").val();
            if(!keyStr){
                layer.alert('key必填项不能为空');
                return false;
            }
            param.dbIndex = dbIndex;
            param.keyStr=keyStr;
            return true;
        },
        sucCallback:function (data) {
            $("#redis select[name='key'] option").remove();
            $("#redis select[name='key']").append('<option value="-1">请选择</option>');
            for(var i=0;i<data.length;i++){
                $("#redis select[name='key']").append('<option value="'+data[i]+'">'+data[i]+'</option>');
            }
            layui.form.render('select');
            layer.msg('成功');
        }
    });
}

/**
 * 提交
 * @param triggerName
 */
function commit() {

    let method = $("#redis select[name='method']").val();
    if(!method){
        layer.alert('方法必填项不能为空');
        return false;
    }

    ajaxLoad({
        id:"redis",
        eventName:"DATALOAD",
        toolName:"redis",
        eventSource:method,
        addCookie:false,
        loading:true,
        resultType:"formatJson",
        getValCallBack:function (param) {
            var dbIndex = $("#redis select[name='dbIndex']").val();
            if(!dbIndex || dbIndex == -1){
                layer.alert('db必填项不能为空');
                return false;
            }
            var key = $("#redis select[name='key']").val();
            if(!key || key == -1){
                layer.alert('key必填项不能为空');
                return false;
            }
            param.dbIndex = dbIndex;
            param.key=key;
            param.value=$("#redis #result").val();
            param.valueClass=$("#redis input[name='valueClass']").val();
            let sltclass = $("#redis select[name='classes']").val();
            if(sltclass && sltclass != "-1"){
                param.valueClass=sltclass;
            }
            return true;
        },
        sucCallback:function (data) {
            $("#redis #result").val(data);
        }
    });
}

