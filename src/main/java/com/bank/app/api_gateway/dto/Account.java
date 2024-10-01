package com.bank.app.api_gateway.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    private Long id;                // Account ID (generated in account service)
    private String accountNumber;   // Account number
    private BigDecimal balance;     // Account balance
    private String accountType;
    private LocalDate dateOpened;   // Date when the account was opened

    // User details to pass to account service
    private Long userId;            // User ID from user-service
    private String userName;        // User's full name
    private String email;           // User's email
    private String phoneNumber;     // User's phone number
}
