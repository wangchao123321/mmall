package com.wangchao.util;

import com.wangchao.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper=new ObjectMapper();

    static {
        // 对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);

        // 取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        // 忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        // 所有的日期格式都统一为一下的样式,即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDATD_FORMAT));

        // 忽略 在json字符串中存在,但是在java对象中不存在对应属性的情况, 防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String obj2String(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("obj2String");
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("obj2StringPretty");
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)? (T)str : objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.warn("string2Obj");
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T string2Obj(String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return  null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)? str : objectMapper.readValue(str,typeReference));
        } catch (IOException e) {
            log.warn("string2Obj");
            e.printStackTrace();
            return null;
        }
    }


    public static <T> T string2Obj(String str, Class<?> collectionClass,Class<?>... elementClass){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClass);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("string2Obj");
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        User user=new User();
        user.setId(1);
        user.setEmail("wangchao@qq.com");
        user.setUsername("wangchao");

        String userJson=JsonUtil.obj2String(user);
        String userJsonp=JsonUtil.obj2StringPretty(user);
        System.out.println(userJson);
        System.out.println(userJsonp);

        User user1 = JsonUtil.string2Obj(userJson, User.class);
        System.out.println(user1.getEmail());

    }
}
