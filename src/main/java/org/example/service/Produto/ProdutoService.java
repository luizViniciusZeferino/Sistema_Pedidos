package org.example.service.Produto;

import org.example.dto.Produto.ProdutoRequestDTO;
import org.example.dto.Produto.ProdutoResponseDTO;
import org.example.model.entity.Pedido.ProdutoEntity;
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

        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setNome(dto.getNome());
        produtoEntity.setPreco(dto.getPreco());
        produtoEntity.setEstoque(dto.getEstoque());

        ProdutoEntity salvo = produtoRepository.save(produtoEntity);
        return toResponseDTO(salvo);
    }

    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        ProdutoEntity produtoEntity = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return toResponseDTO(produtoEntity);
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO dto) {
        validar(dto);

        ProdutoEntity produtoEntity = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produtoEntity.setNome(dto.getNome());
        produtoEntity.setPreco(dto.getPreco());
        produtoEntity.setEstoque(dto.getEstoque());

        ProdutoEntity atualizado = produtoRepository.save(produtoEntity);
        return toResponseDTO(atualizado);
    }

    public void deletarProduto(Long id) {
        ProdutoEntity produtoEntity = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produtoRepository.delete(produtoEntity);
    }

    private void validar(ProdutoRequestDTO dto) {
        if (dto.getPreco().signum() <= 0) {
            throw new RuntimeException("Preço deve ser maior que zero");
        }
        if (dto.getEstoque() < 0) {
            throw new RuntimeException("Estoque não pode ser negativo");
        }
    }

    private ProdutoResponseDTO toResponseDTO(ProdutoEntity produtoEntity) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produtoEntity.getId());
        dto.setNome(produtoEntity.getNome());
        dto.setPreco(produtoEntity.getPreco());
        dto.setEstoque(produtoEntity.getEstoque());
        return dto;
    }
}
