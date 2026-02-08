package org.example.dto.Pedido;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CriarPedidoRequestDTO {
    private List<CriarPedidoItemDTO> itens;
}

