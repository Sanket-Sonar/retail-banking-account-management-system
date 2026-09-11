package com.bank.system.management.service.impl;

import com.bank.system.management.service.InterestCalculator;
import org.springframework.stereotype.Component;

@Component
public class CompoundInterestCalculator implements InterestCalculator {

    @Override
    public double calculate(double amount, double rate, int tenure) {
        return amount * Math.pow((1 + rate / 100), tenure);
    }
}
