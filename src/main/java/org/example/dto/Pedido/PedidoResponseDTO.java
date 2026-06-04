package org.example.dto.Pedido;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.PedidoStatus;
import org.example.model.entity.Pedido.ItemPedidoEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PedidoResponseDTO {

    private Long id;
    private PedidoStatus status;
    private BigDecimal valorTotal;
    private LocalDateTime dataCriacao;
    private List<ItemPedidoResponseDTO> itens;

}

