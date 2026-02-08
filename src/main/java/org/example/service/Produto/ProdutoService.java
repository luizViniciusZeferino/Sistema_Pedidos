package org.example.service.Produto;

import org.example.dto.Produto.ProdutoRequestDTO;
import org.example.dto.Produto.ProdutoResponseDTO;
import org.example.model.entity.Pedido.Produto;
import org.example.repository.Produto.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO dto) {
        validar(dto);

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setEstoque(dto.getEstoque());

        Produto salvo = produtoRepository.save(produto);
        return toResponseDTO(salvo);
    }

    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return toResponseDTO(produto);
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO dto) {
        validar(dto);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setEstoque(dto.getEstoque());

        Produto atualizado = produtoRepository.save(produto);
        return toResponseDTO(atualizado);
    }

    public void deletarProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produtoRepository.delete(produto);
    }

    private void validar(ProdutoRequestDTO dto) {
        if (dto.getPreco().signum() <= 0) {
            throw new RuntimeException("Preço deve ser maior que zero");
        }
        if (dto.getEstoque() < 0) {
            throw new RuntimeException("Estoque não pode ser negativo");
        }
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setPreco(produto.getPreco());
        dto.setEstoque(produto.getEstoque());
        return dto;
    }
}
