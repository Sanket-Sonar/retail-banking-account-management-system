package com.bank.system.management.service;

public interface InterestCalculator {
    double calculate(
            double amount,
            double rate,
            int tenure);
}
