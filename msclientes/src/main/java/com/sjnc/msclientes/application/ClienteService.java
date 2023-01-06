package com.sjnc.msclientes.application;

import com.sjnc.msclientes.domain.Cliente;
import com.sjnc.msclientes.infra.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor // cria um construtor em tempo de compilação
public class ClienteService {

    private final ClienteRepository repository; // é uma dependência obrigatória, deve ser iniciada no construtor

    @Transactional
    public Cliente save(Cliente cliente){

        return repository.save(cliente);
    }
    public Optional<Cliente> getByCPF(String cpf){

        return repository.findByCpf(cpf);
    }

    public void getByCPF() {
    }
}
