package org.example.controller.Produto;

import org.example.dto.Produto.ProdutoRequestDTO;
import org.example.dto.Produto.ProdutoResponseDTO;
import org.example.service.Produto.ProdutoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ProdutoResponseDTO criarProduto(@RequestBody ProdutoRequestDTO dto) {
        return produtoService.criarProduto(dto);
    }

    @GetMapping
    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoService.listarProdutos();
    }

    @GetMapping("/{id}")
    public ProdutoResponseDTO buscarProduto(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ProdutoResponseDTO atualizarProduto(@PathVariable Long id,
                                               @RequestBody ProdutoRequestDTO dto) {
        return produtoService.atualizarProduto(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
    }
}
