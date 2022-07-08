package br.com.armange.jopenvpn.be.openvpn.result;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.exception.InternalErrorException;
import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnSessionDto;
import br.com.armange.jopenvpn.be.openvpn.dto.StatusDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class SessionsListCommandResult implements CommandResult {

    public static final String PATH_PATTERN = "^\\s+Path:\\s?(.+[^\\s\\n])$";
    public static final String STATUS_PATTERN = "^\\s+Status:\\s?(.+[^\\s\\n])$";
    public static final String CONFIG_NAME_PATTERN = "^\\s+Config name:\\s?(.+[^\\s\\n])$";

    private final CommandResult commandResult;

    @Override
    public int getResultCode() {
        return commandResult.getResultCode();
    }

    @Override
    public String getResultMessage() {
        return commandResult.getResultMessage();
    }

    @Override
    public List<String> getResultLines() {
        return commandResult.getResultLines();
    }

    @Override
    public String getAdditionalMessage() {
        return commandResult.getAdditionalMessage();
    }

    @Override
    public void setAdditionalMessage(final String message) {
        commandResult.setAdditionalMessage(message);
    }

    @Override
    public void appendAdditionalMessage(final String message) {
        commandResult.appendAdditionalMessage(message);
    }

    public List<OpenVpnSessionDto> extractSessions() {
        final List<String> resultLines = commandResult.getResultLines();
        final ExtractedSessionsDto extractedSessionsDto = readLines(resultLines);

        return extractedSessionsDto.buildOpenVpnSessionDtoList();
    }

    private ExtractedSessionsDto readLines(final List<String> resultLines) {
        final ExtractedSessionsDto extractedSessionsDto = new ExtractedSessionsDto();

        for (int i = 0; i < resultLines.size(); i++) {
            readPathLine(resultLines, extractedSessionsDto, i);
            readConfigNameLine(resultLines, extractedSessionsDto, i);
            readStatusLine(resultLines, extractedSessionsDto, i);
        }

        return extractedSessionsDto;
    }

    private void readPathLine(
            final List<String> resultLines,
            final ExtractedSessionsDto extractedSessionsDto,
            final int i) {
        final String line = resultLines.get(i);

        if (line.matches("\\s*Path:.+")) {
            final Matcher matcher = extractedSessionsDto.getPathPattern().matcher(line);

            if (matcher.find()) {
                extractedSessionsDto.getPaths().add(matcher.group(1));
            }
        }
    }

    private void readConfigNameLine(final List<String> resultLines,
                                    final ExtractedSessionsDto extractedSessionsDto,
                                    final int i) {
        final String line = resultLines.get(i);

        if (line.matches("\\s*Config name:.+")) {
            final Matcher matcher = extractedSessionsDto.getConfigPattern().matcher(line);

            if (matcher.find()) {
                extractedSessionsDto.getConfigNames().add(matcher.group(1));
            }
        }
    }

    private void readStatusLine(final List<String> resultLines,
                                final ExtractedSessionsDto extractedSessionsDto,
                                final int i) {
        final String line = resultLines.get(i);

        if (line.matches("\\s*Status:.+")) {
            final Matcher matcher = extractedSessionsDto.getStatusPattern().matcher(line);

            if (matcher.find()) {
                extractedSessionsDto.getStatus().add(matcher.group(1));
            }
        }
    }

    @Getter
    private static class ExtractedSessionsDto {
        public static final String COULD_NOT_EXTRACT_SESSION = "Could not extract session data.";

        final List<String> paths = new ArrayList<>();
        final List<String> configNames = new ArrayList<>();
        final List<String> status = new ArrayList<>();
        final Pattern pathPattern = Pattern.compile(PATH_PATTERN);
        final Pattern statusPattern = Pattern.compile(STATUS_PATTERN);
        final Pattern configPattern = Pattern.compile(CONFIG_NAME_PATTERN);

        private boolean validate() {
            return paths.size() == configNames.size() && configNames.size() == status.size();
        }

        private List<OpenVpnSessionDto> buildOpenVpnSessionDtoList() {
            final List<OpenVpnSessionDto> sessions = new ArrayList<>();

            if (!validate()) {
                throw new InternalErrorException(COULD_NOT_EXTRACT_SESSION);
            }

            IntStream.range(0, paths.size())
                    .forEach(index ->
                            sessions.add(OpenVpnSessionDto
                                    .builder()
                                    .path(paths.get(index))
                                    .configName(configNames.get(index))
                                    .status(StatusDto.valueOfStatus(status.get(index)))
                                    .build()));

            return sessions;
        }
    }
}
