package com.prudnicki.loans.loan.repository;

import com.prudnicki.loans.Application;
import com.prudnicki.loans.loan.TestDataUtil;
import com.prudnicki.loans.loan.model.LoanRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static com.prudnicki.loans.loan.TestDataUtil.createLoanRequest;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class LoanRequestRepositoryTest {

    @Autowired
    private LoanRequestRepository requestRepository;

    @Before
    public void setUp() {
        requestRepository.deleteAll();
    }

    @Test
    public void shouldCountByIpAddressAndDay() {
        //given
        Iterable<LoanRequest> persistedRequests = requestRepository.save(
                Arrays.asList(createLoanRequest(), createLoanRequest())
        );
        LocalDateTime createdDate = persistedRequests.iterator().next().getCreatedDate();

        //when
        long count = requestRepository.countByIpAddressForDay(TestDataUtil.DEFAULT_IP_ADDRESS, createdDate);

        //then
        assertEquals("Invalid number createInstance requests found", 2L, count);
    }

    @Test
    public void shouldCountOnlyTodaysRequest() {
        //given
        LoanRequest todaysRequest = requestRepository.save(createLoanRequest());
        LocalDateTime createdDate = todaysRequest.getCreatedDate();
        saveRequestWithCreatedDate(createdDate.truncatedTo(ChronoUnit.DAYS).minusSeconds(1));
        saveRequestWithCreatedDate(createdDate.truncatedTo(ChronoUnit.DAYS).plusDays(1));

        //when
        long count = requestRepository.countByIpAddressForDay(TestDataUtil.DEFAULT_IP_ADDRESS, createdDate);

        //then
        assertEquals("Invalid number createInstance requests found", 1L, count);
    }

    private void saveRequestWithCreatedDate(LocalDateTime newCreatedDate) {
        LoanRequest request = createLoanRequest();
        request = requestRepository.save(request);
        request.setCreatedDate(newCreatedDate);
        requestRepository.save(request);
    }

}