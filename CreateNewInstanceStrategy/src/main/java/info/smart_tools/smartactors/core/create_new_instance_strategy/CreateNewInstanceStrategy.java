package info.smart_tools.smartactors.core.create_new_instance_strategy;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Implementation of {@link IResolveDependencyStrategy}
 * <pre>
 * Strategy allows to create new instances of specified classes
 * </pre>
 *
 * @since 1.8
 */
public class CreateNewInstanceStrategy implements IResolveDependencyStrategy {

    /**
     * Minimum numbers of incoming args for creation class
     */
    private static final int MIN_ARGS_LENGTH = 3;

    /**
     * Local function for creation new instances of classes
     */
    private Function<Object[], Object> creationFunction;

    /**
     * Class constructor
     * Create instance of {@link CreateNewInstanceStrategy}
     * <pre>
     *     For current implementation incoming object obj must have follow structure:
     *     {
     *         {@link String} - class path,
     *         {@link String} - package name,
     *         {@link String} - short name of class to be created,
     *         {@link String} - full name of class what will be created
     *             by instance of {@link IResolveDependencyStrategy},
     *         {@link String} - type of first argument,
     *         {@link String} - type of second argument,
     *         ...
     *         {@link String} - type of N argument
     *     }
     * </pre>
     * @param args needed parameters for creation
     * @throws IllegalArgumentException if any errors occurred
     */
    public CreateNewInstanceStrategy(final Object ... args) {
        try {
            Object classPath = args[0];
            Object[] lessArray = Arrays.copyOfRange(args, 1, args.length);
            String sourceCode = buildString(lessArray);

            String fullClassName = lessArray[0] + "." + lessArray[1];
            Class<?> func = InMemoryCodeCompiler.compile((String) classPath, fullClassName, sourceCode);
            Method m = func.getDeclaredMethod("createNewInstance");
            this.creationFunction = (Function<Object[], Object>) m.invoke(null);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create instance of CreateNewInstanceStrategy.", e);
        }
    }

    /**
     * Represent new instance of class by given param
     * @param <T> type of object
     * @param args needed parameters for resolve dependency
     * @return instance of object
     * @throws ResolveDependencyStrategyException if any errors occurred
     */
    public <T> T resolve(final Object ... args)
            throws ResolveDependencyStrategyException {
        try {
            return (T) creationFunction.apply(args);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Object resolution failed.", e);
        }
    }

    /**
     * Build string with java code by given parameters
     * @param args list of needed parameters
     * @return string with java code
     */
    static String buildString(final Object ... args) {
        StringBuilder sourceCode = new StringBuilder();

        sourceCode.append("package "); sourceCode.append(args[0]); sourceCode.append(";\n");
        sourceCode.append("import java.util.function.Function;\n");

        sourceCode.append("public class "); sourceCode.append(args[1]); sourceCode.append(" {\n");
        sourceCode.append("    public static Function<Object[], Object> createNewInstance() {\n");
        sourceCode.append("        return (Object[] object) -> {\n");
        sourceCode.append("            return new ");
        sourceCode.append(args[2]); sourceCode.append("(");
        int length = args.length;
        for (int i = MIN_ARGS_LENGTH; i < length; i++) {
            sourceCode.append("(");
            sourceCode.append(args[i]);
            sourceCode.append(")");
            sourceCode.append("(");
            sourceCode.append("object[");
            sourceCode.append(i - MIN_ARGS_LENGTH);
            sourceCode.append("]");
            sourceCode.append(")");
            if (i + 1 < length) {
                sourceCode.append(", ");
            }
        }
        sourceCode.append(");\n");
        sourceCode.append("        };\n");
        sourceCode.append("    }\n");
        sourceCode.append("}\n");

        return sourceCode.toString();
    }
}
