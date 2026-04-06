package com.anthony.failsafeapi.controller;

import com.anthony.failsafeapi.model.TicketRequest;
import com.anthony.failsafeapi.model.TicketResponse;
import com.anthony.failsafeapi.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/clasificar")
    public TicketResponse classify(@RequestBody TicketRequest request) {
        return ticketService.classifyTicket(request.description());
    }
}
