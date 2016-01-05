package com.prudnicki.loans.loan.controller;

import com.prudnicki.loans.loan.model.Extension;
import com.prudnicki.loans.loan.model.Loan;
import com.prudnicki.loans.loan.repository.ExtensionRepository;
import com.prudnicki.loans.loan.repository.LoanRepository;
import com.prudnicki.loans.loan.service.ExtensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loans/{loanId}/extensions")
public class ExtensionController {

    private ExtensionRepository extensionRepository;
    private LoanRepository loanRepository;
    private ExtensionService service;

    @Autowired
    public ExtensionController(ExtensionRepository extensionRepository, LoanRepository loanRepository,
                               ExtensionService service) {
        this.extensionRepository = extensionRepository;
        this.loanRepository = loanRepository;
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Extension> list(@PathVariable Long loanId) {
        return extensionRepository.findAllByLoan_Id(loanId);
    }

    @RequestMapping(value = "/{extensionId}", method = RequestMethod.GET)
    public Extension find(@PathVariable Long loanId, @PathVariable Long extensionId) {
        Extension extension = extensionRepository.findOne(extensionId);

        if (extension == null) {
            throw new ResourceNotFoundException();
        }
        boolean extensionOfOtherLoan = !extension.getLoan().getId().equals(loanId);
        if (extensionOfOtherLoan) {
            throw new ResourceNotFoundException();
        }

        return extension;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Extension> create(@PathVariable Long loanId) {
        Loan loan = loanRepository.findOne(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException();
        }

        Extension extension = service.extendLoan(loan);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(CreatedResourceUriBuilder.buildResourceIdUriFromCurrentRequest(extension.getId()));
        return new ResponseEntity<>(extension, headers, HttpStatus.CREATED);
    }
}
