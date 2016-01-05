package com.prudnicki.loans.loan.repository;

import com.prudnicki.loans.loan.model.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long>, LoanRequestRepositoryExtended {

    Long countByIpAddressAndCreatedDateBetween(String ipAddress, LocalDateTime createdDateFrom,
                                               LocalDateTime createdDateTo);

}
