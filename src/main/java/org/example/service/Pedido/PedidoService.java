package org.example.service.Pedido;

import jakarta.transaction.Transactional;
import org.example.dto.Pedido.ItemPedidoResponseDTO;
import org.example.dto.Pedido.PedidoResponseDTO;
import org.example.enums.PedidoStatus;
import org.example.dto.Pedido.CriarPedidoItemDTO;
import org.example.dto.Pedido.CriarPedidoRequestDTO;
import org.example.model.entity.Pedido.ItemPedidoEntity;
import org.example.model.entity.Pedido.PedidoEntity;
import org.example.model.entity.Pedido.ProdutoEntity;
import org.example.model.entity.Usuario.UsuarioEntity;
import org.example.repository.Pedido.PedidoRepository;
import org.example.repository.Produto.ProdutoRepository;
import org.example.repository.Usuario.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public PedidoEntity criarPedido(CriarPedidoRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        PedidoEntity pedido = new PedidoEntity();
        pedido.setUsuarioEntity(usuario);
        pedido.setStatus(PedidoStatus.CRIADO);
        pedido.setDataCriacao(LocalDateTime.now());

        List<ItemPedidoEntity> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CriarPedidoItemDTO itemDTO : dto.getItens()) {
            ProdutoEntity produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produto.getEstoque() < itemDTO.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente");
            }

            produto.setEstoque(produto.getEstoque() - itemDTO.getQuantidade());

            ItemPedidoEntity item = new ItemPedidoEntity();
            item.setPedido(pedido);
            item.setProdutoEntity(produto);
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

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return pedidoRepository.findByUsuarioEntityId(usuario.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private PedidoResponseDTO toResponseDTO(PedidoEntity pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setStatus(pedido.getStatus());
        dto.setValorTotal(pedido.getValorTotal());
        dto.setDataCriacao(pedido.getDataCriacao());

        List<ItemPedidoResponseDTO> itens = pedido.getItens()
                .stream()
                .map(item -> {
                    ItemPedidoResponseDTO itemDTO = new ItemPedidoResponseDTO();
                    itemDTO.setProdutoId(item.getProdutoEntity().getId());
                    itemDTO.setProdutoNome(item.getProdutoEntity().getNome());
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
        PedidoEntity pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() == PedidoStatus.CANCELADO || pedido.getStatus() == PedidoStatus.FINALIZADO) {
            throw new RuntimeException("Pedido não pode ser cancelado");
        }

        pedido.setStatus(PedidoStatus.CANCELADO);

        for (ItemPedidoEntity item : pedido.getItens()) {
            ProdutoEntity produto = item.getProdutoEntity();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
        }
    }

    public void finalizarPedido(Long pedidoId) {
        PedidoEntity pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if(pedido.getStatus() != PedidoStatus.CRIADO) {
            throw new RuntimeException("Pedido não pode ser finalizado");
        } else {
            pedido.setStatus(PedidoStatus.FINALIZADO);
            pedido.setDataFinalizacao(LocalDateTime.now());
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsuarioEntity usuarioLogado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Long idUsuarioDonopedido = pedido.getUsuarioEntity().getId();

        if(!Objects.equals(idUsuarioDonopedido, usuarioLogado.getId())) {
            throw new RuntimeException("Pedido não pertence ao usuário");
        }

        pedidoRepository.save(pedido);

    }



}

