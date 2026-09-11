package com.bank.system.management.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompoundInterestCalculatorTest {

    private final CompoundInterestCalculator calculator =
            new CompoundInterestCalculator();

    @Test
    void calculate_ShouldReturnCorrectAmount_ForOneYear() {

        double result = calculator.calculate(
                10000.0,
                6.5,
                1);

        double expected =
                10000.0 * Math.pow(1.065, 1);

        assertEquals(expected, result, 0.01);
    }

    @Test
    void calculate_ShouldReturnCorrectAmount_ForThreeYears() {

        double result = calculator.calculate(
                10000.0,
                7.0,
                3);

        double expected =
                10000.0 * Math.pow(1.07, 3);

        assertEquals(expected, result, 0.01);
    }

    @Test
    void calculate_ShouldReturnCorrectAmount_ForFiveYears() {

        double result = calculator.calculate(
                10000.0,
                7.5,
                5);

        double expected =
                10000.0 * Math.pow(1.075, 5);

        assertEquals(expected, result, 0.01);
    }

    @Test
    void calculate_ShouldReturnSameAmount_WhenRateIsZero() {

        double result = calculator.calculate(
                10000.0,
                0,
                5);

        assertEquals(10000.0, result, 0.01);
    }

    @Test
    void calculate_ShouldReturnZero_WhenAmountIsZero() {

        double result = calculator.calculate(
                0,
                7.5,
                5);

        assertEquals(0.0, result, 0.01);
    }
}