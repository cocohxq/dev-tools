package com.dev.tool.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompileUtil {

    private static Logger logger = LoggerFactory.getLogger(CompileUtil.class);

    public static List<String> compile(List<String> javaSouces,String outputPath) {
        if(null == javaSouces){
            throw new RuntimeException("源码为空");
        }
        //获取编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        //初始化诊断收集器  可以查看编译错误
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        // 该JavaFileManager实例是com.sun.tools.javac.file.JavacFileManager  用来生成源javafileObject和编译后的javafileObject
        JavaFileManager manager= new DevToolFileManager(compiler.getStandardFileManager(collector, null, null),outputPath);

        //用字符串生成输入源，不用fileManeger来查找文件目录
        List<String> fullClassNames = new ArrayList<>();
        List<JavaFileObject> javaFileObjects = new ArrayList<>();
        Pattern pattern = Pattern.compile("(enum|interface|class)\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
        javaSouces.stream().forEach(source -> {
            Matcher matcher = pattern.matcher(source);
            if(!matcher.find()){
                return;
//                    throw new RuntimeException("找不到类/接口名称");
            }
            String className = matcher.group(2);
            String packageName = source.substring(source.indexOf("package ")+8,source.indexOf(";"));
            fullClassNames.add(String.format("%s%s%s",packageName,".",className));
            javaFileObjects.add(new DevToolJavaFileObject(className, JavaFileObject.Kind.SOURCE,source,null));

        });

        //编译选项
        List<String> options = Arrays.asList("-target","1.8");

        //生成编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null,manager,collector,options,null,javaFileObjects);

        Boolean result = task.call();
        if(!result){
            String err = collector.getDiagnostics().toString();
            logger.error("编译源码异常"+err);
            throw new RuntimeException(err);
        }
        return fullClassNames;
    }


    /**
     * java文件对象
     * 包括java  class都属于这种
     */
    private static class DevToolJavaFileObject extends SimpleJavaFileObject{

        private String stringSource;
        private String name;
        private String outputPath;

        public DevToolJavaFileObject(String name, Kind kind,String stringSource,String outputPath) {
            super(URI.create("String:///"+name+kind.extension), kind);
            this.stringSource = stringSource;
            this.name = name;
            this.outputPath = outputPath;
        }

        //编译前查找源码调用
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return stringSource;//获取源码
        }

        //编译后输出编译fileManager调用
        @Override
        public OutputStream openOutputStream() throws IOException {
            try {
                File classFile = new File(FileUtils.concatPath(outputPath,name+kind.extension));
                if(classFile.exists()){
                    classFile.delete();
                }
                FileUtils.createFile(classFile.getPath());
                FileOutputStream fos = new FileOutputStream(classFile);
                return fos;
            } catch (Exception e) {
                logger.error("打开输出流异常",e);
                throw new RuntimeException(e);
            }
        }
    }


    private static class DevToolFileManager extends ForwardingJavaFileManager{

        private String outputPath;

        public DevToolFileManager(JavaFileManager fileManager,String outputPath) {
            super(fileManager);
            this.outputPath = outputPath;
        }


        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            DevToolJavaFileObject devToolJavaFileObject = new DevToolJavaFileObject(className,kind,null,outputPath);
            //会使用到DevToolJavaFileObject.openOutputStream来输出字节码
            return devToolJavaFileObject;
        }
    }


    public static void main(String[] args) {

//        compile(Arrays.asList(source,source2),"/Users/hanxianqiang");
    }



}