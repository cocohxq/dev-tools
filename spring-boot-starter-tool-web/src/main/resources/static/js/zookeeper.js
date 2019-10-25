$(document).ready(function () {
    nodes = [{name: "/", path: "/", parentPath:"",spread: false}];
    buildTree(nodes);
});

function buildTree(nodes){
    $("#nodes").remove();
    $("#treeArea").append("<ul id=\"nodes\"></ul>");

    layui.tree({
        elem: '#nodes', //传入元素选择器
        nodes: nodes,
        click: function (node) {
            loadZookeeperValue(node);
            if(!node.children){
                node.children = loadZookeeperChildren(node);
                closeAndExpanse(node);
                buildTree(nodes);
            }
        }
    });

}

function loadZookeeperValue(node) {
    ajaxLoad({
        id:"zookeeper",
        eventName:"DATALOAD",
        toolName:"zookeeper",
        eventSource:"loadValue",
        addCookie:false,
        getValCallBack:function (param) {
            param.path = node.path;
            return true;
        },
        sucCallback:function (data) {
            $("#zookeeper #valueArea").html(data);
        }
    });
}



function loadZookeeperChildren(node) {
    var children;
    ajaxLoad({
        id:"zookeeper",
        eventName:"DATALOAD",
        toolName:"zookeeper",
        eventSource:"loadChildren",
        addCookie:false,
        async:false,
        getValCallBack:function (param) {
            param.path = node.path;
            return true;
        },
        sucCallback:function (data) {
            children = data;
        }
    });
    return children;
}


function closeAndExpanse(node) {
    closeChildren(nodes[0]);
    expandChildren(nodes[0],node.path);
}

function closeChildren(rootNode) {
    if(rootNode.children && rootNode.children.length > 0){
        rootNode.spread = false;
        rootNode.children.forEach(function (element) {
            closeChildren(element);
        });
    }
    return;


}

function expandChildren(rootNode,path) {
    rootNode.spread=true;
    if(rootNode.children && rootNode.children.length > 0){
        rootNode.children.forEach(function (element) {
            var reg = new RegExp("^"+element.path);
            if(reg.test(path)){
                element.spread = true;
                expandChildren(element,path);
            }
        });
    }
    return;
}

