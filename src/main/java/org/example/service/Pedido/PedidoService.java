package org.example.service.Pedido;

import jakarta.transaction.Transactional;
import org.example.dto.Pedido.*;
import org.example.enums.PedidoStatus;
import org.example.model.entity.Pedido.HistoricoStatusPedidoEntity;
import org.example.model.entity.Pedido.ItemPedidoEntity;
import org.example.model.entity.Pedido.PedidoEntity;
import org.example.model.entity.Pedido.ProdutoEntity;
import org.example.model.entity.Usuario.UsuarioEntity;
import org.example.repository.Pedido.HistoricoStatusPedidoRepository;
import org.example.repository.Pedido.PedidoRepository;
import org.example.repository.Produto.ProdutoRepository;
import org.example.repository.Usuario.UsuarioRepository;
import org.example.service.Estoque.EstoqueService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstoqueService estoqueService;
    private final HistoricoStatusPedidoRepository historicoStatusPedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         UsuarioRepository usuarioRepository, EstoqueService estoqueService, HistoricoStatusPedidoRepository historicoStatusPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.estoqueService = estoqueService;
        this.historicoStatusPedidoRepository = historicoStatusPedidoRepository;
    }

    PedidoEntity pedido = null;

    @Transactional
    public PedidoEntity criarPedido(CriarPedidoRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        estoqueService.validarEstoque(dto.getItens());

        PedidoEntity pedido = new PedidoEntity();
        pedido.setUsuarioEntity(usuario);
        pedido.setStatus(PedidoStatus.CRIADO);
        pedido.setDataCriacao(LocalDateTime.now());

        List<ItemPedidoEntity> itens = new ArrayList<>();

        for (CriarPedidoItemDTO itemDTO : dto.getItens()) {
            ProdutoEntity produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            Integer quantidade = itemDTO.getQuantidade();

            ItemPedidoEntity item = new ItemPedidoEntity();
            item.setPedido(pedido);
            item.setProdutoEntity(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            estoqueService.baixarEstoque(produto, quantidade);

            itens.add(item);
        }

        pedido.setItens(itens);
        pedido.recalcularTotal();

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
         pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() == PedidoStatus.CANCELADO || pedido.getStatus() == PedidoStatus.FINALIZADO) {
            throw new RuntimeException("Pedido não pode ser cancelado");
        }

        HistoricoStatusPedidoEntity statusPedidoEntity = new HistoricoStatusPedidoEntity();
        PedidoStatus statusAnterior = pedido.getStatus();
        pedido.setStatus(PedidoStatus.CANCELADO);

        statusPedidoEntity.setStatusNovo(PedidoStatus.CANCELADO);
        statusPedidoEntity.setStatusAnterior(statusAnterior);
        statusPedidoEntity.setDataAlteracao(LocalDateTime.now());
        statusPedidoEntity.setPedido(pedido);

        for (ItemPedidoEntity item : pedido.getItens()) {
            ProdutoEntity produto = item.getProdutoEntity();
            Integer quantidade = item.getQuantidade();
            estoqueService.devolverEstoque(produto,quantidade);
        }

        pedidoRepository.save(pedido);
        historicoStatusPedidoRepository.save(statusPedidoEntity);

    }

    public void finalizarPedido(Long pedidoId) {
         pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if(pedido.getStatus() != PedidoStatus.CRIADO) {
            throw new RuntimeException("Pedido não pode ser finalizado");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsuarioEntity usuarioLogado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Long idUsuarioDonoPedido = pedido.getUsuarioEntity().getId();

        if(!Objects.equals(idUsuarioDonoPedido, usuarioLogado.getId())) {
            throw new RuntimeException("Pedido não pertence ao usuário");
        }

        HistoricoStatusPedidoEntity statusPedidoEntity = new HistoricoStatusPedidoEntity();
        PedidoStatus statusAnterior = pedido.getStatus();
        pedido.setStatus(PedidoStatus.FINALIZADO);

        statusPedidoEntity.setStatusNovo(PedidoStatus.FINALIZADO);
        statusPedidoEntity.setStatusAnterior(statusAnterior);
        statusPedidoEntity.setDataAlteracao(LocalDateTime.now());
        pedido.setDataFinalizacao(LocalDateTime.now());
        statusPedidoEntity.setPedido(pedido);

        for (ItemPedidoEntity item : pedido.getItens()) {
            ProdutoEntity produto = item.getProdutoEntity();
            Integer quantidade = item.getQuantidade();
            estoqueService.baixarEstoque(produto,quantidade);
        }

        pedidoRepository.save(pedido);
        historicoStatusPedidoRepository.save(statusPedidoEntity);

    }

    public List<HistoricoStatusPedidoResponseDTO> listarHistoricoPedido(Long pedidoId) {

        pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UsuarioEntity usuarioLogado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Long idUsuarioDonoPedido = pedido.getUsuarioEntity().getId();

        if (!Objects.equals(idUsuarioDonoPedido, usuarioLogado.getId())) {
            throw new RuntimeException("Pedido não pertence ao usuário");
        }

        List<HistoricoStatusPedidoEntity> historicos =
                historicoStatusPedidoRepository.findByPedido(pedido);

        List<HistoricoStatusPedidoResponseDTO> response = new ArrayList<>();

        for (HistoricoStatusPedidoEntity historico : historicos) {

            HistoricoStatusPedidoResponseDTO dto =
                    new HistoricoStatusPedidoResponseDTO();

            dto.setStatusAnterior(historico.getStatusAnterior());
            dto.setStatusNovo(historico.getStatusNovo());
            dto.setDataAlteracao(historico.getDataAlteracao());

            response.add(dto);
        }

        return response;
    }
}



