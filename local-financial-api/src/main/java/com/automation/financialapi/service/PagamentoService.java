package com.automation.financialapi.service;

import com.automation.financialapi.dto.PagamentoRequest;
import com.automation.financialapi.dto.PagamentoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PagamentoService {

    public PagamentoResponse buscarPorId(long id) {

        if (id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id do pagamento deve ser maior que zero"
            );
        }

        if (id != 123) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Pagamento não encontrado"
            );
        }

        return new PagamentoResponse(
                id,
                "PROCESSADO"
        );
    }

    public PagamentoResponse criar(
            PagamentoRequest request
    ) {

        if (request.id() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id do pagamento deve ser maior que zero"
            );
        }

        if (request.status() == null || request.status().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status do pagamento é obrigatório"
            );
        }

        return new PagamentoResponse(
                request.id(),
                request.status()
        );
    }
}