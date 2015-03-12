package ru.ifmo.ctddev.zenkova.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by daria on 03.03.15.
 */

public class Implementor implements JarImpler {
    static Set<String> methodNames = new HashSet<>();
    String fileDir;

    /**
     * Converts Method to String, that contains full description of the method with modifiers, return type and parameters
     *
     * @param method an instance of Method
     * @return description of <code>method</code>
     */
    public static String getMethod(Method method) {
        return modifiersToString(method.getModifiers()) + method.getReturnType().getCanonicalName() + " " +
                method.getName() + "(" + getParams(method) + ")" + " {\n\t\t" + getDefaultReturnValue(method) + "\n\t}";
    }

    /**
     * Converts modifier constant to String, except for Abstract and Transient modifiers
     *
     * @param mod a modifier constant
     * @return String <code>modifier</code> according to <code>mod</code>
     */
    public static String modifiersToString(int mod) {

        String modifier = Modifier.toString(mod & ((Modifier.ABSTRACT | Modifier.TRANSIENT) ^ Integer.MAX_VALUE));
        if (modifier.length() > 0) {
            modifier += " ";
        }
        return modifier;
    }

    /**
     * Returns String with all parameters of the method
     *
     * @param method an instance of Method
     * @return parameters in String
     */
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

    /**
     * Converts parameter to String
     *
     * @param parameter of a method
     * @return description of the <code>parameter</code>
     */
    public static String getParam(Parameter parameter) {
        return modifiersToString(parameter.getModifiers()) + parameter.getType().getCanonicalName() + " " + parameter.getName();
    }

    /**
     * Returns default return value of the given method
     *
     * @param method an instance of Method
     * @return String with correct default return value
     */
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
        fileDir = root.getAbsolutePath() + File.separator + token.getPackage().getName().replace(".", File.separator) + File.separator;
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

    @Override
    public void implementJar(Class<?> aClass, File file) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        ArrayList<String> args = new ArrayList<>();
        implement(aClass, file);
        args.add(fileDir);
        final int exitCode = compiler.run(null, null, null, fileDir + aClass.getSimpleName() + "Impl.java");
    }
}
