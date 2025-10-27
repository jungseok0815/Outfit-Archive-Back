package com.fasthub.backend.cmm;


import com.fasthub.backend.cmm.result.Params;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class CmmController {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/*/*")
    public ResponseEntity<?> handleAllRequests(
            HttpServletRequest request, @RequestBody(required = false) String body) {
        String path = request.getRequestURI();
        String[] pathList = path.split("/");

        String className = getClassName(pathList[2]);
        String methodName = pathList[3];
        Params params = setinngRequestParams(request);

        if (pathList[1].equals("api"))  handleApiRequest(className, methodName, params);
        return ResponseEntity.ok("Request processed");
    }

    public String getClassName(String className){
        return "com.fasthub.backend.oper."+className+".service."+capitalize(className)+"Service";
    }

    public Object handleApiRequest(String className, String methodName, Object... args){
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
            Object instance = applicationContext.getBean(clazz);
            Class<?> paramsClass = Class.forName("com.fasthub.backend.cmm.result.Params");
            Method method = clazz.getDeclaredMethod(methodName, paramsClass);
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            log.error("search error class and method");
            throw new RuntimeException(e);
        }
    }

    public Params setinngRequestParams(HttpServletRequest request){
        Params params = new Params();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            log.info("key : " + key);
            log.info("vlaue : " + values);
            params.add(key, values);
        }
        return params;
    }

    /**
     * 첫 글자만 대문자로 변환
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {return str;}
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}
