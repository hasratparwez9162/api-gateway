package com.bank.app.api_gateway.controller;
import com.bank.app.api_gateway.dto.LoginRequest;
import com.bank.app.api_gateway.dto.User;
import com.bank.app.api_gateway.service.KeycloakUserService;
import com.bank.core.entity.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/apigateway")

public class Controller {


    private RestTemplate template;
    private final KeycloakUserService keycloakUserService;
    public Controller (RestTemplate template, KeycloakUserService keycloakUserService){
        this.template=template;
        this.keycloakUserService = keycloakUserService;
    }





    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody UserRegistrationRequest request) {
        // 1. Check if the user exists in the UserService
        User user = template.getForObject("http://127.0.0.1:8081/users/"+request.getEmail(), User.class);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found in the system.");
        }

         //2. Check if the user has an account in AccountService
        List accounts = template.getForObject("http://localhost:8082/account/user-account/"+user.getId(), List.class);
//        System.out.println(accounts instanceof List);
        if (accounts == null || accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No account found for the user.");
        }

        // 3. Validate the account number from the request by iterating through the accounts
        boolean accountFound = false;
        LinkedHashMap account;
        for (Object ob : accounts) {
//            System.out.println(ob.getClass());
            account = (LinkedHashMap) ob;
            if (account.get("accountNumber").equals(request.getAccountNumber())) {
                accountFound = true;
                break;
            }
        }

        if (!accountFound) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account number does not match.");
        }

        // 4. Create the user in Keycloak
        UserRegistrationRequest createdUser = keycloakUserService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("User created Successfully");
    }
    @PostMapping("/login")
    public Map<String, Object> login (@RequestBody LoginRequest loginRequest) throws Exception {
       return keycloakUserService.login(loginRequest.getUsername() , loginRequest.getPassword());
    }
    @PutMapping("/{input}/forget-password")
    public void forgetPassword(@PathVariable String input){
        keycloakUserService.forgotPassword(input);
    }

}
