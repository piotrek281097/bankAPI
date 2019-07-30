package com.example.demo.Utils;

import com.example.demo.DTOs.CurrencyDto;
import com.example.demo.exceptions.ConnectionException;
import com.example.demo.exceptions.CurrencyIsNotAvailableException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;

public class MathematicalMethodsObject {

    public static double roundValue(Double value) {
        String newValue = new DecimalFormat("##.##").format(value);
        newValue = newValue.replace(",", ".");

        return Double.parseDouble(newValue);
    }

    public static Double convertCurrencies(String currency1, String currency2, Double valueOfTransfer) {
        if(!currency1.equals(currency2)) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = "https://api.exchangeratesapi.io/latest?base=" + currency1;
                ResponseEntity<CurrencyDto> response = restTemplate.getForEntity(url, CurrencyDto.class);
                CurrencyDto currencyDto = response.getBody();

                return MathematicalMethodsObject.roundValue(valueOfTransfer * currencyDto.getRates().get(currency2));
            } catch (NullPointerException ex) {
                throw new CurrencyIsNotAvailableException("Blad w konwersji walut");
            } catch (ResourceAccessException ex) {
                throw new ConnectionException("Blad w polaczeniu z API");
            }
        }
        else {
            return valueOfTransfer;
        }
    }
}
