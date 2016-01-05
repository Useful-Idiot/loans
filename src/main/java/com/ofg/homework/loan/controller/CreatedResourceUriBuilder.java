package com.ofg.homework.loan.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class CreatedResourceUriBuilder {

    public static URI buildResourceIdUriFromCurrentRequest(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
