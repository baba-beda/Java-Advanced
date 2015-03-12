package ru.ifmo.ctddev.zenkova.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * Created by daria on 03.03.15.
 */

public class Implementor implements JarImpler {
    /**
     * Set with full descriptions of all methods of the class
     */
    private static Set<String> methodNames = new HashSet<>();

    /**
     * String, that stores path to destination file
     */
    private String fileDir;

    /**
     * Converts Method to String, that contains full description of the method with modifiers, return type and parameters
     *
     * @param method an instance of Method
     * @return description of <code>method</code>
     */
    private static String getMethod(Method method) {
        return modifiersToString(method.getModifiers()) + method.getReturnType().getCanonicalName() + " " +
                method.getName() + "(" + getParams(method) + ")" + " {\n\t\t" + getDefaultReturnValue(method) + "\n\t}";
    }

    /**
     * Converts modifier constant to String, except for Abstract and Transient modifiers
     *
     * @param mod a modifier constant
     * @return String <code>modifier</code> according to <code>mod</code>
     */
    private static String modifiersToString(int mod) {

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
    private static String getParams(Method method) {
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
    private static String getParam(Parameter parameter) {
        return modifiersToString(parameter.getModifiers()) + parameter.getType().getCanonicalName() + " " + parameter.getName();
    }

    /**
     * Returns default return value of the given method
     *
     * @param method an instance of Method
     * @return String with correct default return value
     */
    private static String getDefaultReturnValue(Method method) {
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

    /**
     * Creates class, that implements interface token
     * @param token class file that contains interface
     * @param root name of file
     * @throws ImplerException special wrapping for all thrown exceptions
     */
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

    /**
     * Implements a class with given token in a jar form
     *
     * @param token type token to create implementation for.
     * @param jarFile target <tt>.jar</tt> file.
     * @throws ImplerException describing the error
     */
    public void implementJar(Class<?> token, File jarFile) throws ImplerException {
        if (token == null) {
            throw new NullPointerException("token is null!");
        }
        if (jarFile == null) {
            throw new NullPointerException("jarFile is null!");
        }

        File workingDir = new File(".");
        try {
            workingDir = Files.createTempDirectory("ImplTemp").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        implement(token, workingDir);
        String path;
        if (token.getPackage() != null) {
            path = token.getPackage().getName().replace(".", File.separator) + File.separator;
        } else {
            path = File.separator;
        }
        String name;
        if (token.getPackage() != null) {
            name = token.getPackage().getName() + ".";
        } else {
            name = ".";
        }
        int mam = compile(workingDir, workingDir.getAbsolutePath() +  File.separator + path + token.getSimpleName() + "Impl.java");
        System.out.println("# " + mam + "\n" + jarFile.getName() + "\n");
        createJar(name + token.getSimpleName() + "Impl", jarFile.getAbsolutePath(), path + token.getSimpleName() + "Impl.class", workingDir.getAbsolutePath());
    }

    /**
     * Compiles target file
     *
     * @param root file path
     * @param file file name
     * @return the exit code given by the compiler
     */
    private int compile(final File root, String file) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final List<String> args = new ArrayList<>();
        args.add(file);
        args.add("-cp");
        args.add(root.getPath() + File.pathSeparator + System.getProperty("java.class.path"));
        return compiler.run(null, null, null, args.toArray(new String[args.size()]));
    }

    /**
     * Creates a jar archive
     *
     * @param fullName full name
     * @param jarName archive name
     * @param filePath file path
     * @param workingDir working directory
     */

    private static void createJar(String fullName, String jarName, String filePath, String workingDir) {
        System.out.println(fullName + "\n" + jarName + "\n" + filePath);
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        //manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, fullName);
        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarName), manifest);
             FileInputStream fileInputStream = new FileInputStream(workingDir + File.separator + filePath)) {

            jarOutputStream.putNextEntry(new ZipEntry(filePath));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) > 0) {
                jarOutputStream.write(buffer, 0, length);
            }
            jarOutputStream.closeEntry();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * An entry point necessary to compile our jar archive
     *
     * @param args default args of any main
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Not enough arguments!");
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(args[0]);
            (new Implementor()).implementJar(clazz, new File("./out.jar"));
        } catch (ClassNotFoundException e) {
            System.err.println(e.toString());
        } catch (ImplerException e) {
            e.printStackTrace();
        }
    }
}
