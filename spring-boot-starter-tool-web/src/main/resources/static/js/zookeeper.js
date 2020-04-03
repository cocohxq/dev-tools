let nodes;
$(document).ready(function () {

});

function zookeeperInit(){
    nodes = [{title: "/",path: "/", parentPath:"",spread: false}];
    buildTree(nodes);
}


function buildTree(nodes){
    $("#nodes").remove();
    $("#treeArea").append("<ul id=\"nodes\"></ul>");

    layui.tree.render({
        elem: '#nodes', //传入元素选择器
        data: nodes,
        showLine: true,  //是否开启连接线
        // onlyIconControl: true,  //是否仅允许节点左侧图标控制展开收缩
        // accordion: true,
        click: function (node) {
            let data = node.data;
            loadZookeeperValue(data);
            if(!data.children){
                data.children = loadZookeeperChildren(data);
                //当前节点展开
                if(data.children && data.children.length > 0){
                    data.spread = true;
                }
                buildTree(nodes);
            }
        }
    });

}

function loadZookeeperValue(nodeData) {
    ajaxLoad({
        id:"zookeeper",
        eventName:"DATALOAD",
        toolName:"zookeeper",
        eventSource:"loadValue",
        addCookie:false,
        getValCallBack:function (param) {
            param.path = nodeData.path;
            return true;
        },
        sucCallback:function (data) {
            $("#zookeeper #valueArea").html(data);
        }
    });
}



function loadZookeeperChildren(nodeData) {
    var children;
    ajaxLoad({
        id:"zookeeper",
        eventName:"DATALOAD",
        toolName:"zookeeper",
        eventSource:"loadChildren",
        addCookie:false,
        async:false,
        getValCallBack:function (param) {
            param.path = nodeData.path;
            return true;
        },
        sucCallback:function (data) {
            children = data;
        }
    });
    return children;
}

