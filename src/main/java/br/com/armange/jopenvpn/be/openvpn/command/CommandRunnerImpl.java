package br.com.armange.jopenvpn.be.openvpn.command;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.CommandRunner;
import br.com.armange.jopenvpn.be.exception.InternalErrorException;
import br.com.armange.jopenvpn.be.openvpn.result.DefaultCommandResult;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class CommandRunnerImpl implements CommandRunner {

    private static final String ERROR_MESSAGE = "An internal error occurred and the last action " +
            "could not be completed.";

    @Override
    public CommandResult run(final String... command) {
        return run(null, command);
    }

    @Override
    public CommandResult run(final List<String> outputLines, final String... command) {
        try {
            final Process process = startProcess(outputLines, command);
            final boolean result = process.waitFor(10, TimeUnit.SECONDS);

            if (!result) {
                process.destroy();

                final CommandResult commandResult = extractResultMessage(process, 1);

                commandResult.appendAdditionalMessage(ERROR_MESSAGE);

                return commandResult;
            }

            return extractResultMessage(process, process.exitValue());
        } catch (final IOException e) {
            log.error(e.getMessage(), e);

            throw new InternalErrorException(e.getMessage(), e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();

            log.error(e.getMessage(), e);

            throw new InternalErrorException(e.getMessage(), e);
        }
    }

    private Process startProcess(final List<String> outputLines,
                                 final String[] command) throws IOException {
        final ProcessBuilder processBuilder = new ProcessBuilder(command);

        processBuilder.redirectErrorStream(true);

        final Process process = processBuilder.start();

        writeOutput(outputLines, process);

        return process;
    }

    private void writeOutput(final List<String> outputLines, final Process process) {
        if (outputLines != null && !outputLines.isEmpty()) {
            try (final OutputStreamWriter writer = new OutputStreamWriter(
                    process.getOutputStream())) {
                outputLines.forEach(writeLine(writer));
            } catch (final IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
            }
        }
    }

    private Consumer<String> writeLine(final OutputStreamWriter writer) {
        return line -> {
            try {
                writer.write(line);
                writer.write("\n");
                writer.flush();
            } catch (final IOException e) {
                log.error(e.getMessage(), e);

                throw new InternalErrorException(e);
            }
        };
    }

    private CommandResult extractResultMessage(final Process process, final int resultCode) {
        final List<String> inputs = new ArrayList<>();
        final DefaultCommandResult commandResult = new DefaultCommandResult(resultCode, inputs);

        try (
                final var bufferedInputReader = new BufferedReader(new
                        InputStreamReader(process.getInputStream()));
                final var bufferedErrorReader = new BufferedReader(new
                        InputStreamReader(process.getErrorStream()))
        ) {
            addAllLines(bufferedInputReader, inputs, commandResult);
            addAllLines(bufferedErrorReader, inputs, commandResult);
        } catch (final IOException e) {
            commandResult.appendAdditionalMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }

        return commandResult;
    }

    private void addAllLines(final BufferedReader bufferedErrorReader,
                             final List<String> inputs,
                             final DefaultCommandResult commandResult) {
        try {
            inputs.addAll(bufferedErrorReader.lines().collect(Collectors.toList()));
        } catch (final Exception e) {
            commandResult.appendAdditionalMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }
    }
}
