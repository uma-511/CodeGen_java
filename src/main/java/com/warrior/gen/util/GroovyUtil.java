package com.warrior.gen.util;

import com.warrior.gen.exception.GenException;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.File;
import java.io.IOException;

/***
 * groovy脚本调用类
 * @author Rooike
 *
 */
public class GroovyUtil {

	private static GroovyUtil groovyUtil;
	private GroovyObject groovyClass;
	private GroovyScriptEngine engine;
	private Binding binding;
	private String scriptPath;

	public static GroovyUtil getInstance(){
		if (groovyUtil==null) {
			groovyUtil = new GroovyUtil();
		}
		return groovyUtil;
	}

	private GroovyUtil(){}

	/**
	 * 加载groovy类
	 */
	public void loadClass(File scriptPath){
		try {
			GroovyClassLoader loader = new GroovyClassLoader();
			Class<?> fileCreator = loader.parseClass(scriptPath);
			groovyClass = (GroovyObject)fileCreator.newInstance();
			loader.close();
		} catch (CompilationFailedException e) {
			throw new GenException("脚本加载错误："+e.getMessage(),e);
		} catch (InstantiationException e) {
			throw new GenException("脚本加载错误："+e.getMessage(),e);
		} catch (IllegalAccessException e) {
			throw new GenException("脚本加载错误："+e.getMessage(),e);
		} catch (IOException e) {
			throw new GenException("脚本加载错误："+e.getMessage(),e);
		}
	}
	/**
	 * 执行函数
	 * @param methodName
	 * @param args
	 * @return
	 */
	public Object runMethod(String methodName,Object args){
		Object ret = null;
		if (groovyClass==null) {
			throw new GenException("未加载任何groovy类！！！");
		}
		ret = groovyClass.invokeMethod(methodName, args);
		return ret;
	}

	public void loadScript(String path){
		loadScript(path, null);
	}
	public void loadScript(String path,String [] roots){
		try {
			this.scriptPath = path;
			if (roots==null) {
				engine = new GroovyScriptEngine("");
			}else{
				engine = new GroovyScriptEngine(roots);
			}
		    binding = new Binding();
			binding.setVariable("language", "Groovy");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void addParam(String key,Object value){
		binding.setProperty(key,value);
	}
	public Object runScript(){
		Object ret = null;
		try {
			engine.run(scriptPath, binding);
		} catch (ResourceException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 执行groovy脚本
	 * @param path 脚本路径
	 * @return
	 */
	public Object runScript(String path){
		return runScript(path,null);
	}
	/***
	 * 执行groovy脚本
	 * @param path 脚本路径
	 * @param roots 脚本根路径
	 * @return
	 */
	public Object runScript(String path,String [] roots){
		Object ret= null;
		try {
			GroovyScriptEngine engine=null;
			if (roots==null) {
				engine = new GroovyScriptEngine("");
			}else{
				engine = new GroovyScriptEngine(roots);
			}
			Binding binding = new Binding();
			binding.setVariable("language", "Groovy");
			ret = engine.run(path,binding);
		} catch (IOException e) {
			throw new GenException("脚本运行错误："+e.getMessage(),e);
		} catch (ResourceException e) {
			throw new GenException("脚本加载错误："+e.getMessage(),e);
		} catch (ScriptException e) {
			throw new GenException("脚本语法错误："+e.getMessage(),e);
		}
        return ret;
	}
}
