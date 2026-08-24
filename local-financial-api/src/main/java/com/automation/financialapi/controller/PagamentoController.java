package com.automation.financialapi.controller;

import com.automation.financialapi.dto.PagamentoRequest;
import com.automation.financialapi.dto.PagamentoResponse;
import com.automation.financialapi.service.PagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping("/{id}")
    public PagamentoResponse buscar(@PathVariable long id) {
        return pagamentoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse criar(
            @RequestBody PagamentoRequest request
    ) {
        return pagamentoService.criar(request);
    }
}