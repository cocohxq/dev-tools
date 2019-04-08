<#include "header.ftl"/>
<#include "footer.ftl"/>
<@header/>
    <div class="layui-body">
        <div style="padding: 25px;">
            <!-- 内容主体区域 -->
                <#include "welcome.ftl"/>

            <#if tools?contains(',dubbo,')>
                <#include "dubbo.ftl"/>
            </#if>

            <#if tools?contains(',redis,')>
                <#include "redis.ftl"/>
            </#if>

        </div>
    </div>
<@footer/>

