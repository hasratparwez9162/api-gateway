package com.bank.app.api_gateway.service;

import com.bank.app.api_gateway.Util.PasswordUtil;
import com.bank.app.api_gateway.exception.UnVerifiedEmailException;
import com.bank.app.api_gateway.exception.UserNotFoundException;
import com.bank.core.entity.UserRegistrationRequest;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class KeycloakUserService {


    private static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";
    @Value("${keycloak.realm}")
    private String realm;
    private Keycloak keycloak;
    private RestTemplate template;

    public KeycloakUserService(Keycloak keycloak, RestTemplate template) {
        this.keycloak = keycloak;
        this.template= template;
    }


    public UserRegistrationRequest createUser(UserRegistrationRequest userRegistrationRecord) {

        UserRepresentation user=new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.getUsername());
        user.setEmail(userRegistrationRecord.getEmail());
        user.setFirstName(userRegistrationRecord.getFirstName());
        user.setLastName(userRegistrationRecord.getLastName());
        user.setEmailVerified(true);


        CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecord.getPassword());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        List<CredentialRepresentation> list = new ArrayList<>();
        list.add(credentialRepresentation);
        user.setCredentials(list);

        UsersResource usersResource = getUsersResource();
        System.out.println(user);

        Response response = usersResource.create(user);

        if(Objects.equals(201,response.getStatus())){

            List<UserRepresentation> representationList = usersResource.searchByUsername(userRegistrationRecord.getUsername(), true);
            if(!CollectionUtils.isEmpty(representationList)){
                UserRepresentation userRepresentation1 = representationList.stream().filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified())).findFirst().orElse(null);
                assert userRepresentation1 != null;

            }
            return  userRegistrationRecord;
        }

        return null;
    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }
    public Map<String, Object> login(String username, String encryptedPassword) throws Exception {
        System.out.println(encryptedPassword);
        String password = PasswordUtil.decrypt(encryptedPassword);
        System.out.println(password);
        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set the form parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", "aditi-bank");
        params.add("username", username);
        params.add("password", password);
        params.add("grant_type", "password");
        params.add("client_secret", "CzaYhuk31GPdOkmyPtWxj4JSXP64K1l5"); // Optional

        // Create an HttpEntity with the parameters and headers
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Create a RestTemplate to make the request
        RestTemplate restTemplate = new RestTemplate();

        // The Keycloak token URL (adjust for your server and realm)
        String keycloakTokenUrl = "http://127.0.0.1:9090/realms/aditi/protocol/openid-connect/token";

        // Send the request and get the response
        ResponseEntity<Map> response = restTemplate.exchange(
                keycloakTokenUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Please Enter Your Correct Details");
        }
    }
    public void forgotPassword(String input) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> representationList;
        if (isValidEmail(input)) {
            representationList = usersResource.searchByEmail(input, true);
        } else {
            representationList = usersResource.searchByUsername(input, true);
        }

        UserRepresentation userRepresentation = representationList.stream().findFirst().orElse(null);


        if (userRepresentation != null ) {
            if(userRepresentation.isEmailVerified()) {
                UserResource userResource = usersResource.get(userRepresentation.getId());
                List<String> actions = new ArrayList<>();
                actions.add(UPDATE_PASSWORD);
                try {
                    userResource.executeActionsEmail(actions);
                } catch (Exception e) {
                    throw new RuntimeException("Error from downstream server: " + e.getMessage(), e);
                }
                return;
            }
            throw new UnVerifiedEmailException("Email Id Not Verify");
        }
        throw new UserNotFoundException("Username Not Found");
    }
    private boolean isValidEmail(String input) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return input.matches(emailRegex);
    }

}















