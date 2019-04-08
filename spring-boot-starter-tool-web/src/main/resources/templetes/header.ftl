<#macro header>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>开发工具集</title>
    <link rel="stylesheet" href="../static/layui/css/layui.css"/>
    <style type="text/css">
        .star{
            color:red;
        }
    </style>
    <script type="text/javascript" src="../static/js/jquery.min.js"></script>
    <script src="../static/layui/layui.all.js"></script>
    <script src="../static/js/tool.js"></script>
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">Dev-tools</div>
        <!-- 头部区域（可配合layui已有的水平导航） -->
        <ul class="layui-nav layui-layout-left">
        </ul>
        <ul class="layui-nav layui-layout-right">
        </ul>
    </div>

    <div class="layui-side layui-bg-black">
        <div class="layui-side-scroll">
            <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
            <ul class="layui-nav layui-nav-tree"  lay-filter="test">
                <#list groupList as group>
                    <li class="layui-nav-item layui-nav-itemed">
                        <a class="" href="javascript:;">${group.groupName}</a>
                    <#list group.toolList as tool>
                    <dl class="layui-nav-child">
                        <dd toolName="${tool.toolName}"><a href="javascript:;">${tool.toolName}</a></dd>
                    </dl>
                    </#list>
                    </li>
                </#list>
            </ul>
        </div>
    </div>
</#macro>