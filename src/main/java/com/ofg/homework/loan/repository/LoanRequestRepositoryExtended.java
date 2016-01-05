package com.ofg.homework.loan.repository;

import java.time.LocalDateTime;

public interface LoanRequestRepositoryExtended {

    Long countByIpAddressForDay(String ipAddress, LocalDateTime day);
}
