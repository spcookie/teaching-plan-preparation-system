package com.cqut.tps.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cqut.tps.annotation.Api;
import com.cqut.tps.annotation.Para;
import com.cqut.tps.util.SessionUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Augenstern
 * @date 2021/12/6
 */
public abstract class BaseServlet extends HttpServlet {

    private final Map<String, Method> METHOD_MAP;

    private void loadingMethod() {
        Class<? extends BaseServlet> servlet = this.getClass();
        Method[] methods = servlet.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Api.class)) {
                String path = method.getAnnotation(Api.class).path();
                assert false;
                METHOD_MAP.put(path, method);
            }
        }
    }

    public BaseServlet() {
        METHOD_MAP = new HashMap<>();
        this.loadingMethod();
    }

    private String api(String url) {
        WebServlet servlet = this.getClass().getAnnotation(WebServlet.class);
        String val = servlet.value()[0];
        String root = val.substring(0, val.lastIndexOf('/'));
        String regex = "(?<=" + root + ").+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("错误的URL" + url);
    }

    private Method handler(HttpServletRequest req) {
        String api = this.api(req.getRequestURL().toString());
        System.out.println("请求的接口是-> " + api);
        return METHOD_MAP.get(api);
    }

    private List<Object> query(HttpServletRequest req,HttpServletResponse resp, Method method) {
        Parameter[] parameters = method.getParameters();
        List<Object> para = new ArrayList<>();
        String bodyData = null;
        if ("POST".equals(req.getMethod())) {
            try {
                ServletInputStream in = req.getInputStream();
                byte[] bytes = in.readAllBytes();
                bodyData = new String(bytes, StandardCharsets.UTF_8);
                System.out.println("请求体: " + bodyData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("请求参数:");
        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();
            Para name = parameter.getAnnotation(Para.class);
            if (type.getName().startsWith("javax.servlet.http.HttpServletRequest")) {
                para.add(req);
                continue;
            }
            if (type.getName().startsWith("javax.servlet.http.HttpServletResponse")) {
                para.add(resp);
                continue;
            }
            if (type.getName().startsWith("java.lang")) {
                String p = req.getParameter(name.name());
                if (p == null) {
                    throw new RuntimeException("参数 -> "+ parameter.getName() + "映射错误");
                } else {
                    System.out.println(name.name() + ": " + p);
                    Object o = p;
                    if (!type.equals(String.class)) {
                        o = this.typeConversion(p, type);
                    }
                    para.add(o);
                }
            } else {
                if (type.getName().startsWith("java.util")) {
                    Type paraType = parameter.getParameterizedType();
                    JSONArray objects = JSONArray.parseArray(bodyData);
                    List<Object> list = listConversion(paraType, objects);
                    para.add(list);
                } else {
                    para.add(JSON.parseObject(bodyData, type));
                }
            }
        }
        return para;
    }

    public List<Object> listConversion(Type type, JSONArray jsonArray) {
        List<Object> objects;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
        if (actualTypeArgument instanceof ParameterizedType) {
            objects = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);
                List<Object> list = listConversion(actualTypeArgument, array);
                objects.add(list);
            }
        } else {
            String string = jsonArray.toJSONString();
            objects = JSONArray.parseArray(string, (Class<Object>) actualTypeArgument);
        }
        return objects;
    }

    private Object typeConversion(String s, Class<?> c) {
        String name = c.getName();
        String simpleName = c.getSimpleName();
        try {
            Class<?> clazz = Class.forName(name);
            Method method = clazz.getMethod("parse" + fixType(simpleName), String.class);
            return method.invoke(null, s);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return s;
    }

    private String fixType(String type) {
        return switch (type) {
            case "Integer" -> "Int";
            case "Character" -> "Char";
            default -> type;
        };
    }

    private void handle(Method handler, List<Object> query, HttpServletResponse resp) {
        try {
            Object result = handler.invoke(this, query.toArray());
            if (result != null) {
                resp.getWriter().print(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect));
            }
        } catch (IllegalAccessException | InvocationTargetException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("方法" + handler.getName() + "调用异常", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        SessionUtil.set(req.getSession());
        Method handler = handler(req);
        List<Object> query = query(req, resp, handler);
        handle(handler, query, resp);
        SessionUtil.remove();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.doGet(req, resp);
    }
}
