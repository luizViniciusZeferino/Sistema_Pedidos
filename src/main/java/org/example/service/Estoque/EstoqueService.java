package org.example.service.Estoque;

import org.example.dto.Pedido.CriarPedidoItemDTO;
import org.example.model.entity.Pedido.ProdutoEntity;
import org.example.repository.Produto.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    @Autowired
    ProdutoRepository produtoRepository;

    public void validarEstoque(List<CriarPedidoItemDTO> itens) {
        for (CriarPedidoItemDTO item : itens) {
            ProdutoEntity produtoDb = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            Integer estoqueAtual = produtoDb.getEstoque();

            if (estoqueAtual < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente");
            }
        }
    }

    public void baixarEstoque(ProdutoEntity produto, Integer quantidadeSolicitada) {
        Integer estoqueAtual = produto.getEstoque();

        if (estoqueAtual < quantidadeSolicitada || quantidadeSolicitada <= 0) {
            throw new RuntimeException("Estoque insuficiente");
        }

        Integer novoEstoque = estoqueAtual - quantidadeSolicitada;
        produto.setEstoque(novoEstoque);

        produtoRepository.save(produto);
    }

    public void devolverEstoque(ProdutoEntity produto, Integer quantidadeSolicitada) {
        produto.setEstoque(produto.getEstoque() + quantidadeSolicitada);

        produtoRepository.save(produto);
    }
}