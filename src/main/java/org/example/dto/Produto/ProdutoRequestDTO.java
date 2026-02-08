package org.example.dto.Produto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoRequestDTO {

    private String nome;
    private BigDecimal preco;
    private Integer estoque;
}

