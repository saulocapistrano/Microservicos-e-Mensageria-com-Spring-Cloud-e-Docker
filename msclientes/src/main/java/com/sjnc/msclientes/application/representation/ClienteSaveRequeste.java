package com.sjnc.msclientes.application.representation;

import com.sjnc.msclientes.domain.Cliente;
import lombok.Data;

@Data
public class ClienteSaveRequeste {
    private String cpf;
    private String nome;
    private Integer idade;

    public Cliente toModel(){
        return new Cliente(cpf, nome, idade);
    }

}
