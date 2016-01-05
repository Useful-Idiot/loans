package com.prudnicki.loans.loan.controller;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.time.LocalDateTime;

public class ControllerTestUtil {

    public static String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        getConfiguredConverter().write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    public static String convertLocalDateTime(LocalDateTime dateTime) {
        return getConfiguredConverter().getObjectMapper().convertValue(dateTime, String.class);
    }

    public static MappingJackson2HttpMessageConverter getConfiguredConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.getObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return converter;
    }

}
