<#include "header.ftl"/>
<#include "footer.ftl"/>
<@header/>
<div class="layui-body">
    <div style="padding: 25px;">
        <!-- 内容主体区域 -->
        <#include "welcome.ftl"/>

        <#list groupList as group>
            <#list group.toolList as tool>
                <#include "${tool.toolName}.ftl"/>
            </#list>
        </#list>

    </div>
</div>
<@footer/>

