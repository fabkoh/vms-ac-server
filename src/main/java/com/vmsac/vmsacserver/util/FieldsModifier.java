package com.vmsac.vmsacserver.util;

import com.vmsac.vmsacserver.model.EntranceEventType;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Component
public class FieldsModifier {

    public void modify(Object o, Map<String, Object> fields, Class c) {
        fields.forEach((k, v) -> {
            // use reflection to get field k and set it to value v
            Field field = ReflectionUtils.findField(c, k);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, o, v);
            }
        });
    }
}
