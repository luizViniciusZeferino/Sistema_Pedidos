package org.example.dto.Pedido;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.PedidoStatus;

import java.time.LocalDateTime;


@Getter
@Setter
public class HistoricoStatusPedidoResponseDTO {
    private PedidoStatus statusAnterior;
    private PedidoStatus statusNovo;
    private LocalDateTime dataAlteracao;
}


