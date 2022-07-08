package br.com.armange.jopenvpn.be.openvpn.result;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.exception.InternalErrorException;
import br.com.armange.jopenvpn.be.openvpn.command.OpenVpnCommand;
import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnConfigDto;
import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnSessionDto;
import br.com.armange.jopenvpn.be.openvpn.dto.StatusDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class ConfigsListCommandResult implements CommandResult {

    public static final String PATH_PATTERN = "(^[^\\s]+)\\s*";
    public static final String CONFIG_NAME_PATTERN = PATH_PATTERN;

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

    public List<OpenVpnConfigDto> extractProfiles() {
        final List<String> resultLines = commandResult.getResultLines()
                .stream().skip(4).collect(Collectors.toList());
        final ExtractedConfigsDto extractedConfigsDto = readLines(resultLines);

        return extractedConfigsDto.buildOpenVpnConfigDtoList();
    }

    private ExtractedConfigsDto readLines(final List<String> resultLines) {
        final ExtractedConfigsDto extractedConfigsDto = new ExtractedConfigsDto();

        for (int i = 0; i < resultLines.size(); i++) {
            readPathLine(resultLines, extractedConfigsDto, i);
            readConfigNameLine(resultLines, extractedConfigsDto, i);
        }

        return extractedConfigsDto;
    }

    private void readPathLine(final List<String> resultLines,
                              final ExtractedConfigsDto extractedConfigsDto,
                              final int i) {
        if (i == 0 || i == 4 || i > 4 && i % 4 == 0) {
            final String line = resultLines.get(i);
            final Matcher matcher = extractedConfigsDto.getPathPattern().matcher(line);

            if (matcher.find()) {
                extractedConfigsDto.getPaths().add(matcher.group(1));
            }
        }
    }

    private void readConfigNameLine(final List<String> resultLines,
                                    final ExtractedConfigsDto extractedConfigsDto,
                                    final int i) {
        if (i == 2 || i == 6 || i > 6 && (i +2) % 4 == 0) {
            final String line = resultLines.get(i);
            final Matcher matcher = extractedConfigsDto.getConfigPattern().matcher(line);

            if (matcher.find()) {
                extractedConfigsDto.getConfigNames().add(matcher.group(1));
            }
        }
    }

    @Getter
    private static class ExtractedConfigsDto {
        public static final String COULD_NOT_EXTRACT_CONFIG =
                "Could not extract configuration data.";

        final List<String> paths = new ArrayList<>();
        final List<String> configNames = new ArrayList<>();
        final Pattern pathPattern = Pattern.compile(PATH_PATTERN);
        final Pattern configPattern = Pattern.compile(CONFIG_NAME_PATTERN);

        private boolean validate() {
            return paths.size() == configNames.size();
        }

        private List<OpenVpnConfigDto> buildOpenVpnConfigDtoList() {
            final List<OpenVpnConfigDto> configs = new ArrayList<>();

            if (!validate()) {
                throw new InternalErrorException(COULD_NOT_EXTRACT_CONFIG);
            }

            final SessionsListCommandResult sessions = (SessionsListCommandResult) OpenVpnCommand
                    .LIST_SESSIONS.run();

            final List<OpenVpnSessionDto> openVpnSessionDtos = sessions.extractSessions();

            IntStream.range(0, paths.size())
                    .forEach(index -> {
                        final String path = paths.get(index);
                        final String configName = configNames.get(index);

                        configs.add(OpenVpnConfigDto
                                .builder()
                                .path(path)
                                .configName(configName)
                                .status(findStatus(openVpnSessionDtos, configName))
                                .build());
                    });

            return configs;
        }

        private StatusDto findStatus(final List<OpenVpnSessionDto> openVpnSessionDtos,
                                     final String configName) {
            return openVpnSessionDtos.parallelStream()
                    .filter(item -> item.getConfigName().equals(configName))
                    .map(OpenVpnSessionDto::getStatus)
                    .findFirst()
                    .orElse(StatusDto.UNKNOWN);
        }
    }
}
