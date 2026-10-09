package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.EntregaEpi;
import br.com.projetotst_api.entity.EntregaEpiItem;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record EntregaEpiResponse(
        Long id,
        Long funcionarioId,
        String funcionario,
        LocalDate dataEntrega,
        String observacoes,
        String epi,
        Integer quantidade,
        LocalDate dataValidade,
        String ca,
        List<Alocacao> remessas,
        List<Item> itens) {

    public record Alocacao(Long remessaId, Integer quantidade, LocalDate dataValidade) {
    }
    public record Item(String nome, Integer quantidade) {
    }

    public static EntregaEpiResponse from(EntregaEpi entrega) {
        List<Alocacao> remessas = entrega.getItens().stream()
                .map(item -> new Alocacao(item.getEpi().getId(), item.getQuantidade(),
                        item.getEpi().getValidade()))
                .toList();
        List<String> nomes = entrega.getItens().stream()
                .map(item -> item.getEpi().getNome()).distinct().toList();
        int quantidadeTotal = entrega.getItens().stream()
                .mapToInt(EntregaEpiItem::getQuantidade).sum();
        LocalDate validade = entrega.getItens().stream()
                .map(item -> item.getEpi().getValidade()).min(LocalDate::compareTo).orElse(null);
        String ca = entrega.getItens().stream()
                .map(item -> item.getEpi().getCa()).distinct().reduce((a, b) -> a + ", " + b).orElse("");
        Map<String, Integer> itens = new LinkedHashMap<>();
        entrega.getItens().forEach(item ->
                itens.merge(item.getEpi().getNome(), item.getQuantidade(), Integer::sum));
        return new EntregaEpiResponse(entrega.getId(), entrega.getUsuario().getId(),
                entrega.getUsuario().getNome(), entrega.getDataEntrega(), entrega.getObservacoes(),
                String.join(", ", nomes), quantidadeTotal, validade, ca, remessas,
                itens.entrySet().stream().map(item -> new Item(item.getKey(), item.getValue())).toList());
    }
}
