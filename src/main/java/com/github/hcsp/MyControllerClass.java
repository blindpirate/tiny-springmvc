package com.github.hcsp;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@MyController
public class MyControllerClass {
    @MyAutowired
    private MyServiceClass myServiceClass;

    @MyGetMapping("/hello")
    public ModelAndView index(@MyRequestParam("name") String name,
                              HttpServletRequest request,
                              HttpServletResponse response
    ) {
        ModelAndView mv = new ModelAndView();
        mv.getModelMap().put("greeting", myServiceClass.sayHello(name));
        mv.getModelMap().put("name", name);
        mv.setViewName("index");
        return mv;
    }

    @MyGetMapping("/helloJson")
    @MyResponseBody
    public Object helloJson(@MyRequestParam("name") String name,
                            HttpServletRequest request,
                            HttpServletResponse response
    ) {
        return Arrays.asList(myServiceClass.sayHello(name));
    }
}