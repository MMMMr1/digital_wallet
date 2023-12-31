package com.michalenok.wallet.keycloak;

import com.michalenok.wallet.model.dto.request.UserCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.*;
import static com.michalenok.wallet.keycloak.KeycloakConfig.keycloak;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Log4j2
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {
    public String addUser(UserCreateDto userDTO) {
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userDTO.password());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.mail());
        user.setEmail(userDTO.mail());
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);
        UsersResource instance = getInstance();
        Response response = instance.create(user);
        String userId = getCreatedId(response);
        setRole(userId, userDTO.role());
        return userId;
    }

    private void setRole(String userId, Set<String> roles) {
        RealmResource realmResource = keycloak.realm("wallet");
        UsersResource usersResource = realmResource.users();
        List<RoleRepresentation> roleRepresentations = new ArrayList<>();
        roles.stream().
                forEach(role ->
                        roleRepresentations.add(realmResource.roles().get(role).toRepresentation()));
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel().add(roleRepresentations);
    }

    public UsersResource getInstance() {
        log.info("UsersResource getInstance {}", KeycloakConfig.getInstance().realm(KeycloakConfig.realm).users());
        return KeycloakConfig.getInstance().realm("wallet").users();
    }
}
