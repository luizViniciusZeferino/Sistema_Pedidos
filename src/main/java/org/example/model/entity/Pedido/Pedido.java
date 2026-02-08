package org.example.model.entity.Pedido;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.PedidoStatus;
import org.example.model.entity.Usuario.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    private BigDecimal valorTotal;

    private LocalDateTime dataCriacao;
}

