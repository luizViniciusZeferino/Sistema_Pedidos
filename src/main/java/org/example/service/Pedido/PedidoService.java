package org.example.service.Pedido;

import jakarta.transaction.Transactional;
import org.example.dto.Pedido.ItemPedidoResponseDTO;
import org.example.dto.Pedido.PedidoResponseDTO;
import org.example.enums.PedidoStatus;
import org.example.dto.Pedido.CriarPedidoItemDTO;
import org.example.dto.Pedido.CriarPedidoRequestDTO;
import org.example.model.entity.Pedido.ItemPedido;
import org.example.model.entity.Pedido.Pedido;
import org.example.model.entity.Pedido.Produto;
import org.example.model.entity.Usuario.Usuario;
import org.example.repository.Pedido.PedidoRepository;
import org.example.repository.Produto.ProdutoRepository;
import org.example.repository.Usuario.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Pedido criarPedido(CriarPedidoRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setStatus(PedidoStatus.CRIADO);
        pedido.setDataCriacao(LocalDateTime.now());

        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CriarPedidoItemDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produto.getEstoque() < itemDTO.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente");
            }

            produto.setEstoque(produto.getEstoque() - itemDTO.getQuantidade());

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            total = total.add(produto.getPreco()
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));

            itens.add(item);
        }

        pedido.setItens(itens);
        pedido.setValorTotal(total);

        return pedidoRepository.save(pedido);
    }

    public List<PedidoResponseDTO> listarMeusPedidos() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return pedidoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setStatus(pedido.getStatus());
        dto.setValorTotal(pedido.getValorTotal());
        dto.setDataCriacao(pedido.getDataCriacao());

        List<ItemPedidoResponseDTO> itens = pedido.getItens()
                .stream()
                .map(item -> {
                    ItemPedidoResponseDTO itemDTO = new ItemPedidoResponseDTO();
                    itemDTO.setProdutoId(item.getProduto().getId());
                    itemDTO.setProdutoNome(item.getProduto().getNome());
                    itemDTO.setQuantidade(item.getQuantidade());
                    itemDTO.setPrecoUnitario(item.getPrecoUnitario());
                    return itemDTO;
                })
                .toList();

        dto.setItens(itens);
        return dto;
    }

    @Transactional
    public void cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() != PedidoStatus.CRIADO) {
            throw new RuntimeException("Pedido não pode ser cancelado");
        }

        pedido.setStatus(PedidoStatus.CANCELADO);

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
        }
    }
}

