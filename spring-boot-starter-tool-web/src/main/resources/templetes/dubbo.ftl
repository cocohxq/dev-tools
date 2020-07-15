<div id="dubbo" class="tool" style="display: none">

    <div id="form" class="layui-form">
        <div style="float:right">
            <button class="layui-btn openWin" context-id="config">解析规则配置</button>
            <button class="layui-btn openWin" context-id="jarPath">jar来源配置</button>
            <button class="layui-btn" onclick="reload()">重新载入配置</button>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>jar名称</label></label>
            <div class="layui-input-block">
                <select id="artifactId" name="jarName" lay-filter="jarName">
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
                        <textarea id="param" style="width:450px;height:600px" class="layui-textarea"></textarea>
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
                    <textarea style="width:450px;height:600px" id="result" placeholder="" class="layui-textarea"></textarea>
                </div>
            </div>
        </div>
    </div>



    <div id="config" style="display:none;padding-top:10px">
        <div id="configContent">
            <div class="layui-form-item dubbo">
                <label class="layui-form-label" style="width: 60%;text-align: left"><span class="star">*</span>artifactId正则解析规则(符合artifactId+version+.jar的jar会参与解析接口)</label>
            </div>
            <div class="layui-form-item dubbo">
                <div class="layui-inline" style="width: 100%;">
                    <label class="layui-form-label" style="text-align: left"><span class="star">*</span>include:</label>
                    <div class="layui-input-inline" style="width: 30%">
                        <input type="text" name="artifactIdIncludeRulePattern" lay-verify="required" autocomplete="off" placeholder="*(-api|-client)" class="layui-input" value="">
                    </div>

                    <label class="layui-form-label">exclude:</label>
                    <div class="layui-input-inline" style="width: 40%">
                        <input type="text" name="artifactIdExcludeRulePattern" autocomplete="off" placeholder="elasticsearch.*|log4j.*|rocketmq.*" class="layui-input" value="">
                    </div>
                </div>
            </div>

            <div class="layui-form-item dubbo">
                <label class="layui-form-label" style="width: 20%;text-align: left"><span class="star">*</span>接口类名正则解析规则</label>
            </div>
            <div class="layui-form-item dubbo">
                <div class="layui-inline" style="width: 100%">
                    <label class="layui-form-label" style="text-align: left"><span class="star">*</span>include:</label>
                    <div class="layui-input-inline" style="width: 80%">
                        <input type="text" name="classRulePattern" lay-verify="required" autocomplete="off" placeholder=".*service" class="layui-input" value="">
                    </div>
                </div>
            </div>
            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button onclick="saveConfig(this)" type="reset" class="layui-btn layui-btn-primary">保存&重载</button>
                </div>
            </div>
        </div>
    </div>

    <div id="jarPath" style="display:none;padding-top:10px">
        <div id="jarPathContent">
            <div class="layui-form-item dubbo">
                <label class="layui-form-label"><span class="star">*</span>jar来源配置</label>
                <div class="layui-input-block">
                    <input type="text" name="jarPath" lay-verify="required" placeholder="war工程:/xxx/target/WEB-INF/lib,springboot工程:/xxx/xxx/xxx/xxx.jar" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button onclick="saveJarPath(this)" type="reset" class="layui-btn layui-btn-primary">保存&重载</button>
                </div>
            </div>
        </div>
    </div>

    <div id="jarList" style="display:none;padding-top:10px">
        <label>本次加载的jar包列表：</label>
        <div id="jarListContent">
        </div>
    </div>
</div>

<script type="text/javascript" src="../static/js/dubbo.js"></script>