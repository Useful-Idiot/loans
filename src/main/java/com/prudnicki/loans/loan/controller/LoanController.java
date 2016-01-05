package com.prudnicki.loans.loan.controller;

import com.prudnicki.loans.loan.model.Loan;
import com.prudnicki.loans.loan.model.LoanRequest;
import com.prudnicki.loans.loan.repository.LoanRepository;
import com.prudnicki.loans.loan.service.LoanService;
import com.prudnicki.loans.loan.service.RiskTooHightException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private LoanRepository repository;
    private LoanService service;

    private String rejectionMessage;

    @Autowired
    public LoanController(LoanRepository repository, LoanService service,
                          @Value("${loan.rejection}") String rejectionMessage) {
        this.repository = repository;
        this.service = service;
        this.rejectionMessage = rejectionMessage;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Loan> create(@Valid @RequestBody LoanRequest request) {
        Loan loan = service.createLoan(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(CreatedResourceUriBuilder.buildResourceIdUriFromCurrentRequest(loan.getId()));
        return new ResponseEntity<>(loan, headers, HttpStatus.CREATED);
    }

    @ExceptionHandler(RiskTooHightException.class)
    public ResponseEntity<String> riskTooHigh() {
        return new ResponseEntity<>(rejectionMessage, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Loan> list() {
        return repository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Loan find(@PathVariable Long id) {
        Loan loan = repository.findOne(id);
        if (loan == null) {
            throw new ResourceNotFoundException();
        }
        return loan;
    }

    @RequestMapping(method = RequestMethod.GET, params = "customerPesel")
    public List<Loan> findByPesel(@RequestParam Long customerPesel) {
        return repository.findAllByCustomerPesel(customerPesel);
    }

}
