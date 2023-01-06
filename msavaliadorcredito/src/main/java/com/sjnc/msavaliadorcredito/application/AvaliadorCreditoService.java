package com.sjnc.msavaliadorcredito.application;

import com.sjnc.msavaliadorcredito.application.ex.DadosClientesNotFoundException;
import com.sjnc.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import com.sjnc.msavaliadorcredito.application.ex.ErroSolicitacaoCartaoException;
import com.sjnc.msavaliadorcredito.domain.model.*;
import com.sjnc.msavaliadorcredito.infra.CartoesResourceClient;
import com.sjnc.msavaliadorcredito.infra.ClientResourceClient;
import com.sjnc.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClientResourceClient clientesClient;
    private final CartoesResourceClient cartoesClient;
    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;
    public SituacaoCliente obterSituacaoCliente(String cpf)
            throws DadosClientesNotFoundException, ErroComunicacaoMicroservicesException {

       try{
           ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
           ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);


           return SituacaoCliente
                   .builder()
                   .cliente(dadosClienteResponse.getBody())
                   .cartoes(cartoesResponse.getBody())
                   .build();
       }catch(FeignException.FeignClientException.FeignClientException e){
           int status = e.status();
           if(HttpStatus.NOT_FOUND.value()== status){
                throw new DadosClientesNotFoundException();
           }
           throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
       }

    }

    @PostMapping
    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)
            throws DadosClientesNotFoundException, ErroComunicacaoMicroservicesException{
       try {

           ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
           ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAteh(renda);

           List<Cartao> cartoes = cartoesResponse.getBody();
           var listaCartoesAprovado = cartoes.stream().map(cartao -> {


                DadosCliente dadosCliente = dadosClienteResponse.getBody();

              BigDecimal limiteBasico = cartao.getLimiteBasico();
            //BigDecimal rendaBD =   BigDecimal.valueOf(renda);
              BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());

              var fator = idadeBD.divide(BigDecimal.valueOf(10));
            BigDecimal limiteAprovado = fator.multiply(limiteBasico);
            CartaoAprovado aprovado = new CartaoAprovado();
              aprovado.setCartao(cartao.getNome());
              aprovado.setBandeira(cartao.getBandeira());
              aprovado.setLimiteAprovado(limiteAprovado);

              return aprovado;
            }).collect(Collectors.toList());

           return new RetornoAvaliacaoCliente(listaCartoesAprovado);

       }catch(FeignException.FeignClientException.FeignClientException e){
           int status = e.status();
           if(HttpStatus.NOT_FOUND.value()== status){
               throw new DadosClientesNotFoundException();
           }
           throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
       }


    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados) {
        try {
            emissaoCartaoPublisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        }catch (Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }

}
