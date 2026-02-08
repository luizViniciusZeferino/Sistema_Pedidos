package org.example.controller.Pedido;

import org.example.dto.Pedido.CriarPedidoRequestDTO;
import org.example.dto.Pedido.PedidoResponseDTO;
import org.example.model.entity.Pedido.Pedido;
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
    public Pedido criarPedido(@RequestBody CriarPedidoRequestDTO dto) {
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
}

