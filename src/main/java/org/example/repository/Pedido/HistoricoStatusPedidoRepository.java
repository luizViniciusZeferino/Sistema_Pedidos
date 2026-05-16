package org.example.repository.Pedido;

import org.example.model.entity.Pedido.HistoricoStatusPedidoEntity;
import org.example.model.entity.Pedido.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoStatusPedidoRepository extends JpaRepository<HistoricoStatusPedidoEntity, Long> {
    List<HistoricoStatusPedidoEntity> findByUsuarioEntityId(Long usuarioId);
    List<HistoricoStatusPedidoEntity> findByPedido(PedidoEntity pedido);
}
