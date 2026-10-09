package br.com.projetotst_api.config;

import br.com.projetotst_api.entity.Risco;
import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;
import br.com.projetotst_api.repository.RiscoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("dev")
public class DevRiscoInitializer {

    @Bean
    CommandLineRunner criarRiscosIniciais(RiscoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new Risco("Oficina Mecânica", "Contato com Produtos Químicos",
                                NivelRisco.MEDIO, SituacaoRisco.ATIVO, "bi-wrench"),
                        new Risco("Acidente", "Atropelamento por Manobra de Ônibus",
                                NivelRisco.ALTO, SituacaoRisco.ATIVO, "bi-shield-exclamation"),
                        new Risco("Manutenção Elétrica", "Choque Elétrico",
                                NivelRisco.ALTO, SituacaoRisco.ATIVO, "bi-lightning-charge"),
                        new Risco("Lavagem de Veículos", "Chão Molhado e Escorregadio",
                                NivelRisco.MEDIO, SituacaoRisco.ATIVO, "bi-droplet")));
            }
        };
    }
}
