package com.sjnc.msclientes.application;



import com.sjnc.msclientes.application.representation.ClienteSaveRequeste;
import com.sjnc.msclientes.domain.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
;import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientesResource {

    private final ClienteService service;


    @GetMapping
    public String status() {
        log.info("Obtendo o status do microserviço de clientes");
        return "ok";
    }

    // requisisção para salvar os dados do cliente
    @PostMapping
    public ResponseEntity save(@RequestBody ClienteSaveRequeste requeste) {
        Cliente cliente = requeste.toModel();
        service.save(cliente);
        URI headerLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .query("cpf={cpf}")
                .buildAndExpand(cliente.getCpf())
                .toUri();
        return ResponseEntity.created(headerLocation).build();
    }

   @GetMapping(params = "cpf")
    public  ResponseEntity dadosCliente(@RequestParam("cpf") String cpf){
        var cliente = service.getByCPF(cpf);
        if (cliente.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }


}
