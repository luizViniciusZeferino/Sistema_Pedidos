package org.example.dto.Pedido;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarPedidoItemDTO {
    private Long produtoId;
    private Integer quantidade;
}

