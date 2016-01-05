package com.ofg.homework.loan.repository;

import com.ofg.homework.loan.model.Extension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtensionRepository extends JpaRepository<Extension, Long> {

    List<Extension> findAllByLoan_Id(Long loanId);

}
