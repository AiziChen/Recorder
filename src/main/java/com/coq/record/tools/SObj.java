package com.coq.record.tools;

import com.coq.record.tools.S_;
import com.coq.record.type.Pointers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Symbolic Tools
 *
 * @author Quaneyc
 */
public class SObj {
    public static Map<String, Pointers> mapPointers(String sObj, Class<?> tClass) throws NoSuchFieldException {
        Map<String, Pointers> result = new HashMap<>();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int i = sObj.indexOf(fieldName);
            if (i == -1) {
                throw new NoSuchFieldException(tClass.getName() + " class field: " + fieldName);
            }
            Pointers ps = readPointer(i, sObj);
            result.put(fieldName, ps);
        }
        return result;
    }

    private static Pointers readPointer(int index, String sObj) {
        Pointers ps = new Pointers();
        StringBuilder sb = new StringBuilder();
        for (int i = index; i < sObj.length(); ++i) {
            char c = sObj.charAt(i);
            if (c == '(') {
                continue;
            } else if (c == ')') {
                break;
            }
            sb.append(c);
        }
        String[] numbers = sb.toString().split("\\s+");
        long n1 = Long.parseLong(numbers[1]);
        long n2 = Long.parseLong(numbers[2]);
        ps.setStart(n1);
        ps.setLen(n2);
        return ps;
    }

    public static List<Integer> getListPointer(String listObj) {
        List<Integer> result = new ArrayList<>();
        // start with "(list"
        String cdr = listObj;
        while (!S_.cdr(cdr).getCdrValue().equals("()")) {
            cdr = S_.cdr(cdr).getCdrValue();
            String car = S_.car(cdr).getCarValue();
            String positionStr = S_.car(car).getCarValue();
            result.add(Integer.parseInt(positionStr));
        }

        return result;
    }
}
