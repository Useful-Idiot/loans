package com.prudnicki.loans.loan.controller;

import com.prudnicki.loans.loan.model.Loan;
import com.prudnicki.loans.loan.model.LoanRequest;
import com.prudnicki.loans.loan.repository.LoanRepository;
import com.prudnicki.loans.loan.service.LoanService;
import com.prudnicki.loans.loan.service.RiskTooHightException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static com.prudnicki.loans.loan.TestDataUtil.createLoan;
import static com.prudnicki.loans.loan.TestDataUtil.createLoanRequest;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoanControllerTest {

    private static final String URL_ROOT = "/loans";
    private static final Long LOAN_ID = 10L;
    private static final String REJECTION_MESSAGE = "Rejected";

    @Mock
    private LoanRepository repository;
    @Mock
    private LoanService service;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        LoanController controller = new LoanController(repository, service, REJECTION_MESSAGE);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(ControllerTestUtil.getConfiguredConverter())
                .build();
    }

    @Test
    public void shouldReturnBadRequestForEmptyRequest() throws Exception {
        //given

        //when
        ResultActions response = mockMvc.perform(post(URL_ROOT)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundForNotExisitngId() throws Exception {
        //given
        Long loanId = 123L;
        when(repository.findOne(loanId)).thenReturn(null);

        //when
        ResultActions response = mockMvc.perform(get(URL_ROOT + "/" + loanId.intValue()));

        //then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnLoan() throws Exception {
        //given
        Loan loan = createLoan();
        loan.setId(LOAN_ID);
        when(repository.findOne(loan.getId())).thenReturn(loan);

        //when
        ResultActions response = mockMvc.perform(get(URL_ROOT + "/" + loan.getId().intValue()));

        //then
        verifyLoanAgainstResponse(loan, response);
    }

    private void verifyLoanAgainstResponse(Loan loan, ResultActions response) throws Exception {
        verifyLoanAgainstResponse(loan, response, "$.");
    }

    private void verifyLoanAgainstResponse(Loan loan, ResultActions response,
                                           String fieldSelectorPrefix) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(jsonPath(fieldSelectorPrefix + "customerFirstName", is(loan.getCustomerFirstName())))
                .andExpect(jsonPath(fieldSelectorPrefix + "customerLastName", is(loan.getCustomerLastName())))
                .andExpect(jsonPath(fieldSelectorPrefix + "customerPesel", is(loan.getCustomerPesel())))
                .andExpect(jsonPath(fieldSelectorPrefix + "amount", is(loan.getAmount().doubleValue())))
                .andExpect(jsonPath(fieldSelectorPrefix + "interest", is(loan.getInterest().doubleValue())))
                .andExpect(jsonPath(fieldSelectorPrefix + "term",
                        is(ControllerTestUtil.convertLocalDateTime(loan.getTerm()))));
        if (loan.getId() != null) {
            response.andExpect(jsonPath(fieldSelectorPrefix + "id", is(loan.getId().intValue())));
        }
    }

    @Test
    public void shouldReturnMultipleLoans() throws Exception {
        //given
        Loan loan = createLoan();
        when(repository.findAll()).thenReturn(Arrays.asList(loan, loan));

        //when
        ResultActions response = mockMvc.perform(get(URL_ROOT));

        //then
        response.andExpect(jsonPath("$", hasSize(2)));
        verifyLoanAgainstResponseWithArrayIndex(loan, response, 0);
        verifyLoanAgainstResponseWithArrayIndex(loan, response, 1);
    }

    private void verifyLoanAgainstResponseWithArrayIndex(Loan loan, ResultActions response,
                                                         int index) throws Exception {
        verifyLoanAgainstResponse(loan, response, "$[" + index + "].");
    }

    @Test
    public void shouldReturnCustomersLoans() throws Exception {
        //given
        Loan loan = createLoan();
        when(repository.findAllByCustomerPesel(loan.getCustomerPesel())).thenReturn(Arrays.asList(loan, loan));

        //when
        ResultActions response = mockMvc.perform(get(URL_ROOT + "?customerPesel=" + loan.getCustomerPesel()));

        //then
        response.andExpect(jsonPath("$", hasSize(2)));
        verifyLoanAgainstResponseWithArrayIndex(loan, response, 0);
        verifyLoanAgainstResponseWithArrayIndex(loan, response, 1);
    }

    @Test
    public void shouldCreateLoan() throws Exception {
        //given
        LoanRequest request = createLoanRequest();
        Loan loan = createLoan();
        loan.setId(LOAN_ID);
        when(service.createLoan(any(LoanRequest.class))).thenReturn(loan);

        //when
        ResultActions response = mockMvc.perform(post(URL_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ControllerTestUtil.json(request)));

        //then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith(URL_ROOT + "/" + loan.getId())));
    }

    @Test
    public void shouldRejectLoan() throws Exception {
        //given
        LoanRequest request = createLoanRequest();
        when(service.createLoan(any())).thenThrow(new RiskTooHightException());

        //when
        ResultActions response = mockMvc.perform(post(URL_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ControllerTestUtil.json(request)));

        //then
        response.andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(REJECTION_MESSAGE)));
    }

}