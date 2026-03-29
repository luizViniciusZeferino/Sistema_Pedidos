package org.example.service.Estoque;

import org.example.model.entity.Pedido.ProdutoEntity;

public class EstoqueService {



    public void validarEstoque(ProdutoEntity produto, Integer quantidadeSolicitada) {

        Integer estoqueAtual = produto.getEstoque();

        if (estoqueAtual < quantidadeSolicitada) {
            throw new RuntimeException("Estoque insuficiente");
        }
    }
}