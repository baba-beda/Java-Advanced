package ru.ifmo.ctddev.zenkova.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by daria on 03.03.15.
 */
public class Implementor implements Impler {
    static Set<String> methodNames = new HashSet<>();

    public static String getMethod(Method method) {
        return modifiersToString(method.getModifiers()) + method.getReturnType().getCanonicalName() + " " +
        method.getName() + "(" + getParams(method) + ")" + " {\n\t\t"  + getDefaultReturnValue(method) + "\n\t}";
    }

    public static String modifiersToString(int mod) {

        String modifier = Modifier.toString(mod & ((Modifier.ABSTRACT | Modifier.TRANSIENT) ^ Integer.MAX_VALUE));
        if (modifier.length() > 0) {
            modifier += " ";
        }
        return modifier;
    }

    public static String getParams(Method method) {
        StringBuilder builder = new StringBuilder();
        for (Parameter p : method.getParameters()) {
            builder.append(getParam(p)).append(", ");
        }

        if (builder.length() >= 2) {
            builder.delete(builder.length() - 2, builder.length());
        }
        return builder.toString();
    }

    public static String getParam(Parameter parameter) {
        return modifiersToString(parameter.getModifiers()) + parameter.getType().getCanonicalName() + " " + parameter.getName();
    }

    public static String getDefaultReturnValue(Method method) {
        if (method.getReturnType().isPrimitive()) {
            if (method.getReturnType().equals(void.class)) {
                return "";
            }
            if (method.getReturnType().equals(boolean.class)) {
                return "return false;";
            }
            return "return 0;";
        }
        return "return null;";
    }

    @Override
    public void implement(Class<?> token, File root) throws ImplerException {
        if (token.isPrimitive()) {
            throw new ImplerException("Class is primitive");
        }

        if (Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Class is final");
        }


        File outputFile = new File(root, token.getCanonicalName().replace(".", File.separator) + "Impl.java");
        outputFile.getParentFile().mkdirs();
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile.toPath(), Charset.forName("UTF-8"))) {

            Method[] methods = token.getMethods();


            if (token.getPackage() != null) {
                writer.write("package " + token.getPackage().getName() + ";\n");
            }

            String classImplName = "public class " + token.getSimpleName() + "Impl " + "implements " + token.getSimpleName() + " {\n";

            for (Method m : methods) {

                methodNames.add(getMethod(m));
            }

            writer.write("\n");

            writer.write(classImplName);

            for (String method : methodNames) {
                writer.write("\t" + method + "\n");
            }

            writer.write("}");
        } catch (IOException e) {
            throw new ImplerException("Root file is invalid");
        }

    }

    public static void main(String[] args) {
    }
}
