$(document).ready(function () {
});

function rocketmqInit() {
    //加载db列表
    ajaxLoad({
        id: "rocketmq",
        eventName: "PAGELOAD",
        toolName: "rocketmq",
        eventSource: "init",
        addCookie: false,
        getValCallBack: function (param) {
            return true;
        },
        sucCallback: function (data) {
            if (data.notJdkClassSupport) {
                $("#rocketmq select[name='classes'] option").remove();
                $("#rocketmq select[name='classes']").append('<option value="-1">请选择</option>');
                for (var i = 0; i < data.classes.length; i++) {
                    $("#rocketmq select[name='classes']").append('<option value="' + data.classes[i] + '">' + data.classes[i] + '</option>');
                }

                $("#outClass").show();
            }

            layui.form.render('select');
        }
    });
}

//添加依赖
function addRef(obj) {
    let windowObj = $(obj).parents("#envContent:eq(0)");//window里面的内容是copy的，会出现id重复的问题，要通过这种方式来唯一定位
    let source = windowObj.find("#source:eq(0)").val();
    if (!source || source == "") {
        return;
    }
    let sources = JSON.parse(sessionStorage.getItem("sources"));
    if (!sources) {
        sources = new Array();
    }
    sources.push(source);
    sessionStorage.setItem("sources", JSON.stringify(sources));
    windowObj.find("#source:eq(0)").val("");
}

//提交源码
function loadSource(obj) {
    ajaxLoad({
        id: "rocketmq",
        eventName: "DATALOAD",
        toolName: "rocketmq",
        eventSource: "load",
        addCookie: false,
        loading: true,
        getValCallBack: function (param) {
            addRef(obj);
            let sources = JSON.parse(sessionStorage.getItem("sources"));
            if (!sources || sources.length == 0) {
                layer.alert('没有可提交的源码');
                return false;
            }
            param.sources = JSON.stringify(sources);
            sessionStorage.removeItem("sources");
            return true;
        },
        sucCallback: function (data) {
            $("#rocketmq select[name='classes'] option").remove();
            $("#rocketmq select[name='classes']").append('<option value="-1">请选择</option>');
            for (var i = 0; i < data.length; i++) {
                $("#rocketmq select[name='classes']").append('<option value="' + data[i] + '">' + data[i] + '</option>');
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

    let method = $("#rocketmq select[name='method']").val();
    if (!method) {
        layer.alert('方法必填项不能为空');
        return false;
    }

    ajaxLoad({
        id: "rocketmq",
        eventName: "DATALOAD",
        toolName: "rocketmq",
        eventSource: method,
        addCookie: false,
        loading: true,
        resultType: "formatJson",
        getValCallBack: function (param) {
            var topic = $("#rocketmq input[name='topic']").val();
            if (!topic || topic == -1) {
                layer.alert('topic必填项不能为空');
                return false;
            }


            param.topic = topic;
            param.tags = $("#rocketmq input[name='tags']").val();
            param.msg = $("#rocketmq #msg").val();
            param.valueClass = $("#rocketmq input[name='valueClass']").val();
            let sltclass = $("#rocketmq select[name='classes']").val();
            if (sltclass && sltclass != "-1") {
                param.valueClass = sltclass;
            }
            return true;
        },
        sucCallback: function (data) {
            $("#rocketmq #result").val(data);
        }
    });
}

