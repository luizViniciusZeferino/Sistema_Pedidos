package org.example.controller.Pedido;

import org.example.dto.Pedido.CriarPedidoRequestDTO;
import org.example.dto.Pedido.HistoricoStatusPedidoResponseDTO;
import org.example.dto.Pedido.PedidoResponseDTO;
import org.example.model.entity.Pedido.PedidoEntity;
import org.example.service.Pedido.PedidoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public PedidoEntity criarPedido(@RequestBody CriarPedidoRequestDTO dto) {
        return pedidoService.criarPedido(dto);
    }

    @GetMapping
    public List<PedidoResponseDTO> listarPedidos() {
        return pedidoService.listarMeusPedidos();
    }

    @PostMapping("/{id}/cancelar")
    public void cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
    }

    @PostMapping("/{id}/finalizar")
    public void finalizarPedido(@PathVariable Long id) { pedidoService.finalizarPedido(id); }

    @GetMapping("/pedidos/{id}/historico")
    public List<HistoricoStatusPedidoResponseDTO> historicoPedido(@PathVariable Long id) {

        return pedidoService.listarHistoricoPedido(id);
    }
}

