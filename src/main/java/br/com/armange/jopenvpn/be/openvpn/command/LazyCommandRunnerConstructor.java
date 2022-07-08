package br.com.armange.jopenvpn.be.openvpn.command;

import br.com.armange.jopenvpn.be.CommandRunner;
import br.com.armange.jopenvpn.be.PreparedCommandRunner;
import br.com.armange.jopenvpn.be.exception.InternalErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LazyCommandRunnerConstructor {

    public static <T extends PreparedCommandRunner> Function<String[], T> prepareConstructor(final Class<T> sourceClass) {
        return params -> {
            try {
                final Constructor<T> constructor = findConstructor(sourceClass, params);

                return newInstance(params, constructor);
            } catch (NoSuchMethodException
                     | InstantiationException
                     | IllegalAccessException
                     | InvocationTargetException e) {
                throw new InternalErrorException(e);
            }
        };
    }

    private static <T> Constructor<T> findConstructor(final Class<T> sourceClass,
                                                      final String[] params)
            throws NoSuchMethodException {
        if (params.length == 0) {
            return sourceClass.getConstructor(CommandRunner.class);
        }

        final List<Class<?>> classList = Arrays
                .stream(params)
                .map(String::getClass)
                .collect(Collectors.toList());
        final List<Class<?>> finalClassList = new ArrayList<>();

        finalClassList.add(CommandRunner.class);
        finalClassList.addAll(classList);

        return sourceClass.getConstructor(finalClassList.toArray(new Class[]{}));
    }

    private static <T> T newInstance(final Object[] params,
                                     final Constructor<T> constructor)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        final CommandRunner commandRunner = new CommandRunnerImpl();

        if (params.length == 0) {
            return constructor.newInstance(commandRunner);
        }

        final List<Object> paramsList = Arrays.stream(params).collect(Collectors.toList());
        final List<Object> finalParamsList = new ArrayList<>();

        finalParamsList.add(commandRunner);
        finalParamsList.addAll(paramsList);

        return constructor.newInstance(finalParamsList.toArray());
    }
}
