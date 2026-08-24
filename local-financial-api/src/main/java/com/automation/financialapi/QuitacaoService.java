package com.automation.financialapi;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuitacaoService {

    private final FaturaRepository faturaRepository;
    private final BoletoQuitacaoRepository boletoQuitacaoRepository;

    public QuitacaoService(
            FaturaRepository faturaRepository,
            BoletoQuitacaoRepository boletoQuitacaoRepository
    ) {
        this.faturaRepository = faturaRepository;
        this.boletoQuitacaoRepository = boletoQuitacaoRepository;
    }

    public SimulacaoQuitacaoResponse simular(int idConta) {

        FaturaRecord fatura =
                buscarFatura(idConta);

        return new SimulacaoQuitacaoResponse(
                fatura.idConta(),
                fatura.idFatura(),
                fatura.valorTotal(),
                fatura.valorMinimo()
        );
    }

    public GerarBoletoQuitacaoResponse gerarBoleto(
            int idConta
    ) {

        FaturaRecord fatura =
                buscarFatura(idConta);

        int idBoleto =
                boletoQuitacaoRepository.inserir(
                        fatura.idConta(),
                        fatura.idFatura(),
                        fatura.valorTotal()
                );

        return new GerarBoletoQuitacaoResponse(
                idBoleto,
                fatura.idConta(),
                fatura.idFatura(),
                fatura.valorTotal(),
                0
        );
    }

    private FaturaRecord buscarFatura(int idConta) {

        return faturaRepository
                .buscarPorIdConta(idConta)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Fatura não encontrada para a conta."
                        )
                );
    }
}