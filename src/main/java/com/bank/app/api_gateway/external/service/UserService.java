//package com.bank.app.api_gateway.external.service;
//
//import com.bank.app.api_gateway.dto.User;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import reactor.core.publisher.Mono;
//
//
//@FeignClient(name = "USER-SERVICE")
//public interface UserService {
//    @GetMapping("/users/{email}")
//    User getUserByEmail (@PathVariable String email);
//
//}
