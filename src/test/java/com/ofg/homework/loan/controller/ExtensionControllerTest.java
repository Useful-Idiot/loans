package com.ofg.homework.loan.controller;

import com.ofg.homework.loan.model.Extension;
import com.ofg.homework.loan.model.Loan;
import com.ofg.homework.loan.repository.ExtensionRepository;
import com.ofg.homework.loan.repository.LoanRepository;
import com.ofg.homework.loan.service.ExtensionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.ofg.homework.loan.TestDataUtil.*;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExtensionControllerTest {

    private static final Long LOAN_ID = 10L;
    private static final Long EXTENSION_ID = 10L;
    private static final String ALL_LOANS_EXTENSIONS_URL = "/loans/" + LOAN_ID + "/extensions/";
    private static final String SINGLE_EXTENSION_URL = ALL_LOANS_EXTENSIONS_URL + EXTENSION_ID;

    @Mock
    private ExtensionRepository extensionRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private ExtensionService service;
    @InjectMocks
    private ExtensionController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(ControllerTestUtil.getConfiguredConverter())
                .build();
    }

    @Test
    public void shouldReturnNotFoundForInvaliExtensionId() throws Exception {
        //given
        when(extensionRepository.findOne(EXTENSION_ID)).thenReturn(null);

        //when
        ResultActions response = mockMvc.perform(get(SINGLE_EXTENSION_URL));

        //then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundForInvalidLoanId() throws Exception {
        //given
        Loan loan = createLoanWithExtensions();
        Long invalidLoanId = 12L;
        loan.setId(invalidLoanId);
        when(extensionRepository.findOne(EXTENSION_ID)).thenReturn(loan.getExtensions().get(0));

        //when
        //URL contains valid LOAN_ID != invaliLoanId
        ResultActions response = mockMvc.perform(get(SINGLE_EXTENSION_URL));

        //then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindExtensionById() throws Exception {
        //given
        Loan loan = createLoanWithExtensions();
        Extension extension = loan.getExtensions().get(0);
        when(extensionRepository.findOne(EXTENSION_ID)).thenReturn(extension);

        //when
        ResultActions response = mockMvc.perform(get(SINGLE_EXTENSION_URL));

        //then
        verifyExtensionAgainstResponse(extension, response);
    }

    private void verifyExtensionAgainstResponse(Extension extension, ResultActions response) throws Exception {
        verifyExtensionAgainstResponse(extension, response, "$.");
    }

    private void verifyExtensionAgainstResponse(Extension extension, ResultActions response,
                                                String fieldSelectorPrefix) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(jsonPath(fieldSelectorPrefix + "interestBefore",
                        is(extension.getInterestBefore().doubleValue())))
                .andExpect(jsonPath(fieldSelectorPrefix + "interestAfter",
                        is(extension.getInterestAfter().doubleValue())))
                .andExpect(jsonPath(fieldSelectorPrefix + "termBefore",
                        is(ControllerTestUtil.convertLocalDateTime(extension.getTermBefore()))))
                .andExpect(jsonPath(fieldSelectorPrefix + "termAfter",
                        is(ControllerTestUtil.convertLocalDateTime(extension.getTermAfter()))));
        if (extension.getId() != null) {
            response.andExpect(jsonPath(fieldSelectorPrefix + "id", is(extension.getId().intValue())));
        }
    }

    @Test
    public void shouldListLoansExtensions() throws Exception {
        //given
        Loan loan = createLoanWithExtensions();
        when(extensionRepository.findAllByLoan_Id(LOAN_ID)).thenReturn(loan.getExtensions());

        //when
        ResultActions response = mockMvc.perform(get(ALL_LOANS_EXTENSIONS_URL));

        //then
        verifyExtensionAgainstResponseWithArrayIndex(loan.getExtensions().get(0), response, 0);
        verifyExtensionAgainstResponseWithArrayIndex(loan.getExtensions().get(1), response, 1);
    }

    private void verifyExtensionAgainstResponseWithArrayIndex(Extension extension, ResultActions response,
                                                             Integer index) throws Exception {
        verifyExtensionAgainstResponse(extension, response, "$[" + index + "].");
    }

    @Test
    public void shouldReturnNotFoundOnExtensionOfMissingLoan() throws Exception {
        //given
        when(loanRepository.findOne(LOAN_ID)).thenReturn(null);

        //when
        ResultActions response = mockMvc.perform(post(ALL_LOANS_EXTENSIONS_URL));

        //then
        response.andExpect(status().isNotFound());
    }


    @Test
    public void shouldExtendLoan() throws Exception {
        //given
        Loan loan = createLoan();
        Extension extension = createExtension();
        extension.setId(EXTENSION_ID);
        when(loanRepository.findOne(LOAN_ID)).thenReturn(loan);
        when(service.extendLoan(loan)).thenReturn(extension);

        //when
        ResultActions response = mockMvc.perform(post(ALL_LOANS_EXTENSIONS_URL));

        //then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith(SINGLE_EXTENSION_URL)));
    }
}