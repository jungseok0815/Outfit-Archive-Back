package com.fasthub.backend.cmm;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class CmmController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping("/*/*")
    public ResponseEntity<?> handleAllRequests(
            HttpServletRequest request, @RequestBody(required = false) String body) {
        String path = request.getRequestURI();
        String[] pathList = path.split("/");
        String className = getClassName(pathList[2]);
        String methodName = pathList[3];
        log.info("body : " + body);
        if (pathList[1].equals("api"))  handleApiRequest(className, methodName, body);
        return ResponseEntity.ok("Request processed");
    }



    public Object handleApiRequest(String className, String methodName, String jsonbody){
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
            log.info("[실행 class : " + clazz.getName() + "]");
            Object instance = applicationContext.getBean(clazz);
            log.info("[실행 instance :"+ instance + "]");
            Method method = clazz.getDeclaredMethod(methodName);
            log.info("[실행 method : " + method + "]");

            log.info("jsonBody : " + jsonbody);


            method.setAccessible(true);
            return method.invoke(instance);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            log.error("search error class and method");
            throw new RuntimeException(e);
        }
    }

    public Method findMethod(Class<?> serviceClass, String methodName, String jsonBody) throws NoSuchMethodException, JsonProcessingException {
        List<Method> candidates = new ArrayList<>();
        for (Method m : serviceClass.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                candidates.add(m);
            }
        }
        // 하나뿐이면 바로 반환
        if (candidates.size() == 1) return candidates.get(0);

        Map<String, Object> jsonMap = objectMapper.readValue(jsonBody, Map.class);

        candidates.forEach(item -> {
            log.info("item : " + item);
        });

        return null;
    }



    /**
     * 첫 글자만 대문자로 반환
     * @param str
     * @return
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {return str;}
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * class path 반환
     * @param className
     * @return
     */
    public String getClassName(String className){
        return "com.fasthub.backend.oper."+className+".service."+capitalize(className)+"Service";
    }


}
