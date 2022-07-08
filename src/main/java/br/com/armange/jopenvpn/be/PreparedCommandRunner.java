package br.com.armange.jopenvpn.be;

public interface PreparedCommandRunner {

    CommandResult run(final String... outputLines);
}
