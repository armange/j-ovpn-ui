package br.com.armange.jopenvpn.be.dbus;

import br.com.armange.jopenvpn.be.exception.UncheckedDBusException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenVpnDBusRepository {

    public static final String NET_OPENVPN_V3_CONFIGURATION = "net.openvpn.v3.configuration";
    public static final String AUTH_REGEX = "<auth-user-pass>\\n([\\S\\s]+)</auth-user-pass>";

    public static synchronized Optional<AuthUserPass> findCredentials(final String path) {
        final String importedOvpnData = fetchFromRemoteObject(path);
        final Pattern pattern = Pattern.compile(AUTH_REGEX);
        final Matcher matcher = pattern.matcher(importedOvpnData);

        if (matcher.find()) {
            final String result = matcher.group(1);
            final String[] auth = result.split("\n");

            return Optional.of(new AuthUserPass(auth[0], "SuperSecret"));
        }

        return Optional.empty();
    }

    private static String fetchFromRemoteObject(final String path) {
        try (final DBusConnection connection = DBusConnectionBuilder.forSystemBus().build()) {
            return fetchFromRemoteObject(path, connection);
        } catch (final IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        } catch (final DBusException e) {
            throw new UncheckedDBusException(e.getMessage(), e);
        }
    }

    private static String fetchFromRemoteObject(final String path,
                                                final DBusConnection connection) {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final AtomicReference<UncheckedDBusException> exceptionRef = new AtomicReference<>();

        while (atomicInteger.get() < 3) {
            try {
                return connection.getRemoteObject(NET_OPENVPN_V3_CONFIGURATION, path,
                        OpenVpnDBusInterface.class).Fetch();
            } catch (final Exception e) {
                if (exceptionRef.get() == null) {
                    exceptionRef.set(new UncheckedDBusException(e.getMessage(), e));
                } else {
                    exceptionRef.get().addSuppressed(e);
                }

                atomicInteger.incrementAndGet();
            }

            sleepUnchecked(1000);
        }

        throw exceptionRef.get();
    }

    private static void sleepUnchecked(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new UncheckedDBusException(e.getMessage(), e);
        }
    }
}
