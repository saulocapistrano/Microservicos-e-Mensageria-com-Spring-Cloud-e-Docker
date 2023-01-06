package com.sjnc.mscartoes.aplication;


import com.sjnc.mscartoes.domain.Cartao;
import com.sjnc.mscartoes.infra.repository.CartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartaoService {

    private final CartaoRepository repository;

    @Transactional
    public Cartao save(Cartao cartao){
        return repository.save(cartao);
    }

    public List<Cartao> getCartoesRendaMenorIgual(Long renda){
        var redaBigDecimal = BigDecimal.valueOf(renda);
        return  repository.findByRendaLessThanEqual(redaBigDecimal);
    }


}
