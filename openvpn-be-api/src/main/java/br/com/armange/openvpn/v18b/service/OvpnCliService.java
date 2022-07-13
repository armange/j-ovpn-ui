package br.com.armange.openvpn.v18b.service;

import br.com.armange.openvpn.v18b.dto.*;

import java.io.File;

public interface OvpnCliService {

    OvpnConfigDto importConfig(File ovpnFile, String configName);

    BooleanResultDto removeConfigByPath(String path);

    BooleanResultDto removeConfigByConfigName(String configName);

    OvpnSessionDto startSessionByPath(OvpnCredentialDto credential, String path);

    OvpnSessionDto startSessionByConfigName(OvpnCredentialDto credential, String configName);

    OvpnConfigsListDto listConfigs();

    OvpnSessionsListDto listSessions();

    BooleanResultDto disconnectSessionByPath(String path);

    BooleanResultDto disconnectSessionByConfigName(String configName);

    OvpnCredentialDto findCredentials(final String path);
}
