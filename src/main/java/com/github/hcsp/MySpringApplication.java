package com.github.hcsp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.ClassPath;
import org.apache.catalina.LifecycleException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MySpringApplication {
    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, LifecycleException {
        Map<Class, Object> container = new HashMap<>();
        ClassPath classpath = ClassPath.from(MySpringApplication.class.getClassLoader());
        List<Class<?>> componentClasses = classpath.getTopLevelClassesRecursive(MySpringApplication.class.getPackage().getName())
                .stream().map(ClassPath.ClassInfo::getName).map(name -> {
                    try {
                        return Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(klass -> klass.getAnnotation(MyController.class) != null || klass.getAnnotation(MyService.class) != null)
                .collect(Collectors.toList());

        for (Class klass : componentClasses) {
            container.put(klass, klass.getConstructor().newInstance());
        }
        for (Object bean : container.values()) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.getAnnotation(MyAutowired.class) != null) {
                    field.setAccessible(true);
                    field.set(bean, container.get(field.getType()));
                }
            }
        }

        MyServer.startServer(new DispatcherServlet(container));
    }

}

class DispatcherServlet extends HttpServlet {
    Map<Class, Object> container;

    public DispatcherServlet(Map<Class, Object> container) {
        this.container = container;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        for (Object bean : container.values()) {
            if (bean.getClass().getAnnotation(MyController.class) != null) {
                for (Method method : bean.getClass().getMethods()) {
                    MyGetMapping anno = method.getAnnotation(MyGetMapping.class);
                    if (anno != null && anno.value().equals(uri)) {
                        try {
                            Class[] paramTypes = method.getParameterTypes();
                            Object[] params = new Object[paramTypes.length];

                            // 参数注入
                            for (int i = 0; i < paramTypes.length; ++i) {
                                Class paramType = paramTypes[i];
                                if (paramType == HttpServletRequest.class) {
                                    params[i] = req;
                                } else if (paramType == HttpServletResponse.class) {
                                    params[i] = resp;
                                } else {
                                    Annotation[] annosOnParam = method.getParameterAnnotations()[i];
                                    for (Annotation annoOnParam : annosOnParam) {
                                        if (annoOnParam.annotationType() == MyRequestParam.class) {
                                            params[i] = req.getParameter(((MyRequestParam) annoOnParam).value());
                                            break;
                                        }
                                    }
                                }
                            }

                            Object mv = method.invoke(bean, params);
                            if (mv instanceof ModelAndView) {
                                File template = new File(getClass().getResource("/view/" + ((ModelAndView) mv).getViewName() + ".mytemplate").toURI());
                                String content = new String(Files.readAllBytes(template.toPath()));
                                for (Map.Entry<String, Object> entry : ((ModelAndView) mv).getModelMap().entrySet()) {
                                    String k = entry.getKey();
                                    Object v = entry.getValue();
                                    content = content.replace("${" + k + "}", v.toString());
                                }
                                resp.setHeader("Content-Type", "text/html");
                                resp.getOutputStream().print(content);
                                resp.getOutputStream().flush();
                                return;
                            } else if (method.getAnnotation(MyResponseBody.class) != null) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                String json = objectMapper.writeValueAsString(mv);
                                resp.setHeader("Content-Type", "application/json");
                                resp.getOutputStream().print(json);
                                resp.getOutputStream().flush();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

