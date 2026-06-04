package org.example.model.entity.Pedido;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.PedidoStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "historico_pedido" )


public class HistoricoStatusPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PedidoEntity pedido;

    @Enumerated(EnumType.STRING)
    private PedidoStatus statusAnterior;
    @Enumerated(EnumType.STRING)
    private PedidoStatus statusNovo;

    private LocalDateTime dataAlteracao;
}
