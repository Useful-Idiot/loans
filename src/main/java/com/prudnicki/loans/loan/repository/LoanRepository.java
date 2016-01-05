package com.prudnicki.loans.loan.repository;

import com.prudnicki.loans.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByCustomerPesel(Long customerPesel);

}
