package com.prudnicki.loans.loan.acceptance;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.prudnicki.loans.Application;
import com.prudnicki.loans.loan.model.Loan;
import com.prudnicki.loans.loan.model.LoanRequest;
import com.prudnicki.loans.loan.repository.LoanRepository;
import com.prudnicki.loans.loan.repository.LoanRequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.jayway.restassured.RestAssured.*;
import static com.prudnicki.loans.loan.TestDataUtil.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(
        value = {
                "risk.request.daily_limit=1",
                "risk.request.hours_until_nights_end=24"
        },
        randomPort = true)
@SpringApplicationConfiguration(classes = Application.class)
public class AcceptanceTest {

    private static final String LOCATION = "Location";
    private static final int CREATED = 201;
    private static final int BAD_REQUEST = 400;
    private static final String LOANS_URL = "/loans/";
    private static final String EXTENSIONS_URL = "/extensions/";

    @Autowired
    private LoanRepository repository;
    @Autowired
    private LoanRequestRepository requestRepository;
    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    @Value("${local.server.port}")
    private int port;

    @Value("${risk.request.max_value}")
    private BigDecimal maxLoanValue;
    @Value("${loan.rejection}")
    private String rejectionMessage;
    @Value("${loan.extension_multiplier}")
    private BigDecimal extensionMultiplier;
    @Value("${loan.extension_days}")
    private Long extensionDays;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        repository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    public void customerCanApplyForLoan() {
        LoanRequest request = createLoanRequest();

        Response loanCreationResponse = postLoanRequest(request)
                .then()
                .statusCode(CREATED)
                .and().extract().response();

        get(loanCreationResponse.header(LOCATION)).then()
                .assertThat()
                .body("id", isA(Integer.class))
                .body("amount", equalTo(request.getAmount().floatValue()))
                .body("term", notNullValue())
                .body("interest", notNullValue())
                .body("customerFirstName", equalTo(request.getFirstName()))
                .body("customerLastName", equalTo(request.getLastName()))
                .body("customerPesel", equalTo(request.getPesel()))
                .body("createdDate", notNullValue());
    }

    private Response postLoanRequest(LoanRequest request) {
        return given().contentType(ContentType.JSON).body(request).post(LOANS_URL);
    }

    @Test
    public void loanRequestIsRejectedForMaxLoanAtNight() {
        //test is configured to recognize whole 24 hours as 'night'
        LoanRequest request = createLoanRequest();
        request.setAmount(maxLoanValue);

        postLoanRequest(request)
                .then()
                .statusCode(BAD_REQUEST)
                .body(equalTo(rejectionMessage));
    }

    @Test
    public void loanRequestIsRejectedForTooManyLoanRequests() {
        //1 max loan request per ip in acceptance test
        LoanRequest request = createLoanRequest();

        postLoanRequest(request)
                .then()
                .statusCode(CREATED);

        postLoanRequest(request)
                .then()
                .body(equalTo(rejectionMessage));
    }

    @Test
    public void customerCanExtendLoan() {
        Loan loan = repository.save(createLoan());
        String loanExtensionsUrl = LOANS_URL + loan.getId() + EXTENSIONS_URL;

        Response extensionCreationResponse = post(loanExtensionsUrl).then()
                .assertThat().statusCode(CREATED)
                .and().extract().response();

        //location points to valid extension
        get(extensionCreationResponse.header(LOCATION)).then()
                .assertThat()
                .body("id", isA(Integer.class))
                .body("interestBefore", equalTo(loan.getInterest().floatValue()))
                .body("interestAfter", equalTo(interestAfterExtension(loan.getInterest())))
                .body("termBefore", equalTo(localDateTimeAsString(loan.getTerm())))
                .body("termAfter", equalTo(termAfterExtensionsAsString(loan.getTerm())))
                .body("createdDate", notNullValue());

        //loan gets extended
        get(LOANS_URL + loan.getId()).then()
                .assertThat()
                .body("term", equalTo(termAfterExtensionsAsString(loan.getTerm())))
                .body("interest", equalTo(interestAfterExtension(loan.getInterest())));
    }

    private float interestAfterExtension(BigDecimal interest) {
        return interest.multiply(extensionMultiplier).floatValue();
    }

    private String localDateTimeAsString(LocalDateTime dateTime) {
        return converter.getObjectMapper().convertValue(dateTime, String.class);
    }

    private String termAfterExtensionsAsString(LocalDateTime initialTerm) {
        LocalDateTime extendedTerm = initialTerm.plusDays(extensionDays).truncatedTo(ChronoUnit.DAYS);
        return localDateTimeAsString(extendedTerm);
    }

    @Test
    public void customerCanSeeHistoryOfLoansAndExtensions() {
        //2 loans for one customer, first with 2 extensions, second with none
        repository.save(createLoanWithExtensions());
        Loan simpleLoan = repository.save(createLoan());
        //1 loan for another customer
        Loan otherCustomersLoan = createLoan();
        otherCustomersLoan.setCustomerPesel(simpleLoan.getCustomerPesel() + 1);
        repository.save(otherCustomersLoan);

        get("/loans?customerPesel=" + simpleLoan.getCustomerPesel()).then()
                .assertThat()
                .body("", hasSize(2))
                .body("[0].extensions", hasSize(2))
                .body("[1].extensions", hasSize(0));
    }


}
