<div id="redis" class="tool" style="display: none">

    <div id="form" class="layui-form">
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>db</label></label>
            <div class="layui-input-block">
                <select name="dbIndex" lay-filter="dbIndex">
                </select>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>key搜索</label>
            <div class="layui-input-block">
                <input type="text" name="keyStr" autocomplete="off" placeholder="支持通配：如*web*" class="layui-input">
            </div>
        </div>

        <div class="layui-form-item">
            <div class="layui-input-block">
                <button onclick="loadRedisParam(this)" type="reset" class="layui-btn layui-btn-primary">过滤key</button>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>key列表</label></label>
            <div class="layui-input-block">
                <select name="key" lay-filter="key">
                </select>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label"><span class="star">*</span>方法列表</label></label>
            <div class="layui-input-block">
                <select name="method" lay-filter="key">
                    <option value="query">query</option>
                    <option value="delete">delete</option>
                </select>
            </div>
        </div>


        <div id="outClass" style="display: none">
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="star">*</span>valueClass(填jdk类或选择已加载的非jdk类)</label>
                <div class="layui-input-block">
                    <input type="text" name="valueClass" autocomplete="off" placeholder="jdk自带，例如：java.lang.Long" class="layui-input" value="java.lang.String">
                </div>
                <br>
                <div class="layui-input-block">
                    <select name="classes" lay-filter="">
                    </select>
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button type="reset" class="layui-btn layui-btn-primary addEnv openWin" context-id="env">添加源码&重载</button>
                </div>
            </div>
        </div>

        <div class="layui-form-item layui-form-text">
            <label class="layui-form-label" >value：</label>
            <div class="layui-input-block">
                <textarea id="result"
placeholder="Tips:
1.更新值:更新操作仅限value类型为String的
2:更新时间 需要add的毫秒数，支持负数" class="layui-textarea"></textarea>
            </div>
        </div>


        <div class="layui-form-item">
            <div class="layui-input-block">
                <button onclick="commit()" class="layui-btn delete" lay-submit="" lay-filter="demo1">提交</button>
                <button type="reset" class="layui-btn layui-btn-primary reset">重置</button>
            </div>
        </div>
    </div>



    <div id="env" style="display:none;padding-top:10px">
        <div id="envContent">
            <div class="layui-form-item">
                <div class="layui-form-text" style="float:left;">
                    <label class="layui-form-label" >源码(依赖的非jdk的class也要加入提交)：</label>
                    <div class="layui-input-block">
                        <textarea style="width:680px;height:400px" id="source" placeholder="" class="layui-textarea"></textarea>
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

<script type="text/javascript" src="../static/js/redis.js"></script>