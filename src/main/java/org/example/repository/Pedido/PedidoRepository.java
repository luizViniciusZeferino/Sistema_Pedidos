package org.example.repository.Pedido;

import org.example.model.entity.Pedido.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
    List<PedidoEntity> findByUsuarioEntityId(Long usuarioId);
}
