package br.com.armange.jopenvpn.be.openvpn.command;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.PreparedCommandRunner;

import java.util.List;
import java.util.function.Function;

public enum OpenVpnCommand {

    IMPORT_CONFIG(LazyCommandRunnerConstructor.prepareConstructor(ConfigImportCommandRunner.class)),
    START_SESSION(LazyCommandRunnerConstructor.prepareConstructor(SessionStartCommandRunner.class)),
    DISCONNECT_SESSION_BY_CONFIG(LazyCommandRunnerConstructor.prepareConstructor(SessionDisconnectByConfigNameCommandRunner.class)),
    LIST_SESSIONS(LazyCommandRunnerConstructor.prepareConstructor(SessionsListCommandRunner.class)),
    LIST_CONFIGS(LazyCommandRunnerConstructor.prepareConstructor(ConfigsListCommandRunner.class)),
    DISCONNECT_SESSION_BY_PATH(LazyCommandRunnerConstructor.prepareConstructor(SessionDisconnectByPathCommandRunner.class)),

    REMOVE_CONFIG_BY_PATH(LazyCommandRunnerConstructor.prepareConstructor(ConfigRemoveCommandRunner.class)),
    ;

    private final Function<String[], ?> preparedCommandRunner;

    OpenVpnCommand(final Function<String[], ?> preparedCommandRunner) {
        this.preparedCommandRunner = preparedCommandRunner;
    }

    public CommandResult run(final List<String> outputLines, final String... params) {
        final PreparedCommandRunner cmd = (PreparedCommandRunner) preparedCommandRunner
                .apply(params);

        return cmd.run(outputLines.toArray(new String[]{}));
    }

    public CommandResult run(final String... params) {
        final PreparedCommandRunner cmd = (PreparedCommandRunner) preparedCommandRunner
                .apply(params);

        return cmd.run();
    }

    public CommandResult run() {
        final PreparedCommandRunner cmd = (PreparedCommandRunner) preparedCommandRunner
                .apply(new String[]{});

        return cmd.run();
    }
}
