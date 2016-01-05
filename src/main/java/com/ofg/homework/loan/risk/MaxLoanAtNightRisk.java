package com.ofg.homework.loan.risk;

import com.ofg.homework.loan.model.LoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class MaxLoanAtNightRisk implements Risk {

    private BigDecimal maxLoanValue;
    private Integer hoursUntilNightEnd;

    @Autowired
    public MaxLoanAtNightRisk(@Value("${risk.request.max_value}") BigDecimal maxLoanValue,
                              @Value("${risk.request.hours_until_nights_end}") Integer hoursTillNightEnd) {
        this.maxLoanValue = maxLoanValue;
        this.hoursUntilNightEnd = hoursTillNightEnd;
    }

    @Override
    public boolean isRiskTooHigh(LoanRequest request) {
        return isRequestedAtNight(request) && isAboveMaxValue(request);
    }

    private boolean isRequestedAtNight(LoanRequest request) {
        LocalDateTime midnight = request.getCreatedDate().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nightsEnd = midnight.plusHours(hoursUntilNightEnd);
        LocalDateTime requestTime = request.getCreatedDate();
        return requestTime.compareTo(midnight) >= 0 && requestTime.compareTo(nightsEnd) <= 0;
    }

    private boolean isAboveMaxValue(LoanRequest request) {
        return request.getAmount().compareTo(maxLoanValue) >= 0;
    }

}
