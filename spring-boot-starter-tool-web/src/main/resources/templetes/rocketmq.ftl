<div id="rocketmq" class="tool" style="display: none">

    <div id="form" class="layui-form">

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>topic</label>
            <div class="layui-input-block">
                <input type="text" name="topic" autocomplete="off" placeholder="topic" class="layui-input">
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">tags</label>
            <div class="layui-input-block">
                <input type="text" name="tags" autocomplete="off" placeholder="tags" class="layui-input">
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>方法列表</label></label>
            <div class="layui-input-block">
                <select name="method" lay-filter="key">
                    <option value="send">send</option>
                </select>
            </div>
        </div>


        <div id="outClass" style="display: none">
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="star">*</span>valueClass(填jdk类或选择已加载的非jdk类)</label>
                <div class="layui-input-block">
                    <input type="text" name="valueClass" autocomplete="off" placeholder="jdk自带，例如：java.lang.Long"
                           class="layui-input" value="java.lang.String">
                </div>
                <br>
                <div class="layui-input-block">
                    <select name="classes" lay-filter="">
                    </select>
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button type="reset" class="layui-btn layui-btn-primary addEnv openWin" context-id="env">添加源码&重载
                    </button>
                </div>
            </div>
        </div>



        <div class="layui-form-item">
            <div style="float:left;">
                <div class="layui-form-item">
                    <div class="layui-form-text">
                        <label class="layui-form-label">msg：</label>
                        <div class="layui-input-block">
                <textarea id="msg"
                          placeholder="Tips:
填入需要发送的内容(复杂对象请用json格式) " style="width:450px;height:600px" class="layui-textarea"></textarea>
                        </div>
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button onclick="commit()" class="layui-btn delete" lay-submit="" lay-filter="demo1">提交</button>
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


    <div id="env" style="display:none;padding-top:10px">
        <div id="envContent">
            <div class="layui-form-item">
                <div class="layui-form-text" style="float:left;">
                    <label class="layui-form-label">源码(依赖的非jdk的class也要加入提交)：</label>
                    <div class="layui-input-block">
                        <textarea style="width:680px;height:400px" id="source" placeholder=""
                                  class="layui-textarea"></textarea>
                    </div>
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button onclick="addRef(this)" type="reset" class="layui-btn">添加依赖</button>
                    <button onclick="loadSource(this)" type="reset" class="layui-btn">提交源码</button>
                </div>
            </div>
        </div>
    </div>

</div>

<script type="text/javascript" src="../static/js/rocketmq.js"></script>