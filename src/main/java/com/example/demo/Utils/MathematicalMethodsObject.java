package com.example.demo.Utils;

import java.text.DecimalFormat;

public class MathematicalMethodsObject {

    public static double roundValue(Double value) {
        String newValue = new DecimalFormat("##.##").format(value);
        newValue = newValue.replace(",", ".");

        return Double.parseDouble(newValue);
    }
}
