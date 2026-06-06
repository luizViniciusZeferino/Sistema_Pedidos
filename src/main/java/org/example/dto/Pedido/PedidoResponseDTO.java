package org.example.dto.Pedido;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.PedidoStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponseDTO {

    private Long id;
    private PedidoStatus status;
    private BigDecimal valorTotal;
    private LocalDateTime dataCriacao;
    private List<ItemPedidoResponseDTO> itens;
}

