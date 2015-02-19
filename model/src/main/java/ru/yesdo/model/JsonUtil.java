package ru.yesdo.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * User: Krainov
 * Date: 13.02.2015
 * Time: 17:47
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static String toJson(Object o) throws IOException {
        StringWriter writer = new StringWriter();
        objectMapper.writeValue(writer,o);
        return writer.toString();
    }

    public static String toSafeJson(Object o) {
        try{
            StringWriter writer = new StringWriter();
            objectMapper.writeValue(writer,o);
            return writer.toString();
        } catch (Exception e) {
            logger.warn("Error to Json",e);
        }
        return null;
    }

    public static <T extends Object> T toObject(Class<T> clazz, String json) throws IOException {
        return objectMapper.readValue(json,clazz);
    }

    public static void main(String[] args) throws IOException {
        Contact contact = new Contact();
        contact.setLocation(11.0,11.0);

        System.out.println(JsonUtil.toJson(contact));
    }
}
