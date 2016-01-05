package com.prudnicki.loans.loan.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Loan extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String customerFirstName;

    private String customerLastName;

    private Long customerPesel;

    private BigDecimal amount;

    private LocalDateTime term;

    private BigDecimal interest;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private List<Extension> extensions = new ArrayList<>();

    public Loan() {}

    public Loan(LoanRequest request, BigDecimal interest) {
        this.amount = request.getAmount();
        this.customerFirstName = request.getFirstName();
        this.customerLastName = request.getLastName();
        this.customerPesel = request.getPesel();
        this.interest = interest;
        LocalDateTime currentTime = LocalDateTime.now();
        this.term = currentTime.plusDays(request.getTerm());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public Long getCustomerPesel() {
        return customerPesel;
    }

    public void setCustomerPesel(Long customerPesel) {
        this.customerPesel = customerPesel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTerm() {
        return term;
    }

    public void setTerm(LocalDateTime term) {
        this.term = term;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    public void extendWith(Extension extension) {
        extensions.add(extension);
        interest = extension.getInterestAfter();
        term = extension.getTermAfter();
    }
}
