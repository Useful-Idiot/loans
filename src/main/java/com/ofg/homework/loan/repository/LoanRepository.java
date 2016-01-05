package com.ofg.homework.loan.repository;

import com.ofg.homework.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByCustomerPesel(Long customerPesel);

}
