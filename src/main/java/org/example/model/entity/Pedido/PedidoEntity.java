package org.example.model.entity.Pedido;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.Pedido.ItemPedidoResponseDTO;
import org.example.enums.PedidoStatus;
import org.example.model.entity.Usuario.UsuarioEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "pedido")

public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UsuarioEntity usuarioEntity;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedidoEntity> itens;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    private BigDecimal valorTotal;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataFinalizacao;

    public void recalcularTotal() {
        BigDecimal valorTotalPedidos = BigDecimal.ZERO;
        for(ItemPedidoEntity item : itens) {
            BigDecimal precoItem =  item.getPrecoUnitario();
            Integer quantidadeItem = item.getQuantidade();
            BigDecimal valorTotalItens = (precoItem).multiply(BigDecimal.valueOf(quantidadeItem));
            valorTotalPedidos = valorTotalPedidos.add(valorTotalItens);
        }
        this.valorTotal = valorTotalPedidos;
    }

}

