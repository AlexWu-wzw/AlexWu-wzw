package main.util;

import main.model.Transition;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CopyFunction {

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    public static HashMap<Integer, Transition> copy(HashMap<Integer, Transition> original) {
        HashMap<Integer, Transition> copy = new HashMap<Integer, Transition>();
        for (Map.Entry<Integer, Transition> entry : original.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }


}
