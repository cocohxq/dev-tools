<div id="dubbo" class="tool" style="display: none">

    <div id="form" class="layui-form">
        <div style="float:right">
            <button class="layui-btn addEnv">新增环境</button>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>jar名称</label></label>
            <div class="layui-input-block">
                <select name="jarName" lay-filter="jarName">
                    <#if jarInfos??>
                        <option value="-1">请选择</option>
                        <#list jarInfos as jarInfo>
                            <option value="${jarInfo}">${jarInfo}</option>
                        </#list>
                    </#if>
                </select>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>接口名称</label></label>
            <div class="layui-input-block">
                <select name="interfaceClassName" lay-filter="interfaceClassName">
                </select>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>group</label>
            <div class="layui-input-block">
                <input type="text" name="group" lay-verify="required" autocomplete="off" placeholder="dubbo 服务group" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>version</label>
            <div class="layui-input-block">
                <input type="text" name="version" lay-verify="required" autocomplete="off" placeholder="0.0.1" class="layui-input">
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>接口方法</label></label>
            <div class="layui-input-block">
                <select name="methodName" lay-filter="methodName">
                </select>
            </div>
        </div>


        <div class="layui-form-item">
            <div style="float:left;">
                <div class="layui-form-item">
                    <div class="layui-form-text">
                        <label class="layui-form-label">入参：</label>
                        <textarea id="param" style="width:300px;height:600px"></textarea>
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button onclick="submitInvoke()" class="layui-btn submit" lay-submit="" lay-filter="demo1">立即提交</button>
                        <button type="reset" class="layui-btn layui-btn-primary reset">重置</button>
                    </div>
                </div>
            </div>
            <div class="layui-form-text" style="float:left;">
                <label class="layui-form-label" >结果：</label>
                <div class="layui-input-block">
                    <textarea style="width:440px;height:600px" id="result" placeholder="" class="layui-textarea"></textarea>
                </div>
            </div>
        </div>
    </div>



    <div id="env" style="display:none;padding-top:10px">
        <div id="envContent">
            <div class="layui-form-item dubbo">
                <label class="layui-form-label"><span class="star">*</span>接口jar路径</label>
                <div class="layui-input-block">
                    <input type="text" name="jarPath" lay-verify="required" placeholder="war工程:/xxx/target/WEB-INF/lib,springboot工程:/xxx/xxx/xxx/xxx.jar" autocomplete="off" class="layui-input">
                </div>
            </div>

            <div class="layui-form-item dubbo">
                <label class="layui-form-label"><span class="star">*</span>jar包名过滤</label>
                <div class="layui-input-block">
                    <input type="text" name="nameContainStr" lay-verify="required" autocomplete="off" placeholder="include/exclude提供dubbo 如：-client,-api/elasticsearch,log4j,rocketmq-client" class="layui-input" value="-api/">(多个，分隔)
                </div>
            </div>

            <div class="layui-form-item dubbo">
                <label class="layui-form-label"><span class="star">*</span>service类package名</label>
                <div class="layui-input-block">
                    <input type="text" name="packageName" lay-verify="required" autocomplete="off" placeholder="提供dubbo服务的service类所在package名" class="layui-input" value="service">(多个，分隔)
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button onclick="loadDubboParam(this)" type="reset" class="layui-btn layui-btn-primary">加载入参</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="../static/js/dubbo.js"></script>