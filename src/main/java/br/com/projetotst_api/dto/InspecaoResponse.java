package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.Inspecao;
import br.com.projetotst_api.entity.RespostaInspecao;

import java.time.LocalDate;
import java.util.List;

public record InspecaoResponse(
        Long id,
        LocalDate data,
        String tipoId,
        String tipo,
        String local,
        String setor,
        String numeroOnibus,
        String placa,
        String responsavel,
        String observacoesGerais,
        String criadoPor,
        int totalItens,
        int conformes,
        int naoConformes,
        int pendentes,
        int conformidade,
        List<Resposta> respostas) {

    public record Resposta(
            String itemId,
            String descricao,
            String status,
            String observacao,
            String prioridade,
            LocalDate prazoCorrecao,
            String responsavelCorrecao) {
        static Resposta from(RespostaInspecao resposta) {
            return new Resposta(resposta.getItemId(), resposta.getDescricao(), resposta.getStatus(),
                    resposta.getObservacao(), resposta.getPrioridade(), resposta.getPrazoCorrecao(),
                    resposta.getResponsavelCorrecao());
        }
    }

    public static InspecaoResponse from(Inspecao inspecao) {
        List<Resposta> respostas = inspecao.getRespostas().stream().map(Resposta::from).toList();
        int conformes = (int) respostas.stream().filter(r -> "conforme".equals(r.status())).count();
        int naoConformes = (int) respostas.stream().filter(r -> "naoConforme".equals(r.status())).count();
        int pendentes = (int) respostas.stream().filter(r -> "pendente".equals(r.status())).count();
        int conformidade = respostas.isEmpty() ? 0 : Math.round(conformes * 100.0f / respostas.size());
        return new InspecaoResponse(inspecao.getId(), inspecao.getData(), inspecao.getTipoId(),
                inspecao.getTipo(), inspecao.getLocal(), inspecao.getSetor(), inspecao.getNumeroOnibus(),
                inspecao.getPlaca(), inspecao.getResponsavel(), inspecao.getObservacoesGerais(),
                inspecao.getUsuario().getNome(), respostas.size(), conformes, naoConformes, pendentes,
                conformidade, respostas);
    }
}
