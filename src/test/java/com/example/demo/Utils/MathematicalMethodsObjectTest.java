package com.example.demo.Utils;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

public class MathematicalMethodsObjectTest {


    @Test
    public void testShouldReturnRoundedValue() {
        Double expectedValue = 3.33;
        Double value = MathematicalMethodsObject.roundValue(3.333333);

        assertThat(value, is(expectedValue));
    }

    @Test
    public void testShouldReturnTheSameValueAsCurrencyIsTheSame() {
        String currency1 = "USD";
        String currency2 = "USD";
        Double moneyTransfer = 100.00;
        Double expectedValue = 100.00;

        Double moneyAfterConverting = MathematicalMethodsObject.convertCurrencies(currency1, currency2, moneyTransfer);

        assertThat(moneyAfterConverting, is(expectedValue));
    }

    @Test
    public void testShouldReturnConvertedValue() {
        String currency1 = "EUR";
        String currency2 = "PLN";
        Double moneyTransfer = 1.00;
        Double expectedValue = 4.28;

        Double moneyAfterConverting = MathematicalMethodsObject.convertCurrencies(currency1, currency2, moneyTransfer);

        assertThat(moneyAfterConverting, is(expectedValue));
    }
}