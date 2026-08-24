package com.automation.financialapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quitacao")
public class QuitacaoController {

    private final QuitacaoService quitacaoService;

    public QuitacaoController(
            QuitacaoService quitacaoService
    ) {
        this.quitacaoService = quitacaoService;
    }

    @PostMapping("/simular")
    public SimulacaoQuitacaoResponse simular(
            @RequestBody SimulacaoQuitacaoRequest request
    ) {

        return quitacaoService.simular(
                request.idConta()
        );
    }

    @PostMapping("/gerar-boleto")
    public ResponseEntity<GerarBoletoQuitacaoResponse> gerarBoleto(
            @RequestBody GerarBoletoQuitacaoRequest request
    ) {

        GerarBoletoQuitacaoResponse response =
                quitacaoService.gerarBoleto(
                        request.idConta()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}