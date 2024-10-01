//package com.bank.app.api_gateway.external.service;
//
//import com.bank.app.api_gateway.dto.Account;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import reactor.core.publisher.Flux;
//
//
//import java.util.List;
//
//@FeignClient(name = "ACCOUNT-SERVICE")
//public interface AccountService {
//
//
//    @GetMapping("/account/user-account/{id}")
//    List<Account> getAccountById(@PathVariable Long id);
//
//}
