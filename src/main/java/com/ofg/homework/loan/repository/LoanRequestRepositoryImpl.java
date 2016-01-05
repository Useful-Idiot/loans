package com.ofg.homework.loan.repository;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LoanRequestRepositoryImpl implements LoanRequestRepositoryExtended {

    @Autowired
    private LoanRequestRepository repository;

    @Override
    public Long countByIpAddressForDay(String ipAddress, LocalDateTime day) {
        LocalDateTime dayStart = day.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nextDayStart = dayStart.plusDays(1).minusSeconds(1);
        return repository.countByIpAddressAndCreatedDateBetween(ipAddress, dayStart, nextDayStart);
    }

}
