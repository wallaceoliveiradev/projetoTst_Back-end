-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 18/11/2026 às 02:42
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `tst_db`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `arquivo_certificado_treinamento`
--

CREATE TABLE `arquivo_certificado_treinamento` (
  `id` bigint(20) NOT NULL,
  `conteudo` tinyblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `entrega_epi`
--

CREATE TABLE `entrega_epi` (
  `id` bigint(20) NOT NULL,
  `data_entrega` date NOT NULL,
  `observacoes` varchar(1000) DEFAULT NULL,
  `usuario_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `entrega_epi`
--

INSERT INTO `entrega_epi` (`id`, `data_entrega`, `observacoes`, `usuario_id`) VALUES
(1, '2026-11-17', 'teste de registro', 2),
(2, '2026-11-17', 'teste de resgistro', 2);

-- --------------------------------------------------------

--
-- Estrutura para tabela `entrega_epi_item`
--

CREATE TABLE `entrega_epi_item` (
  `id` bigint(20) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `entrega_id` bigint(20) NOT NULL,
  `epi_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `entrega_epi_item`
--

INSERT INTO `entrega_epi_item` (`id`, `quantidade`, `entrega_id`, `epi_id`) VALUES
(1, 1, 1, 1),
(2, 1, 2, 6);

-- --------------------------------------------------------

--
-- Estrutura para tabela `epis`
--

CREATE TABLE `epis` (
  `id` bigint(20) NOT NULL,
  `ca` varchar(80) NOT NULL,
  `categoria` varchar(40) NOT NULL,
  `data_entrada` date NOT NULL,
  `nome` varchar(120) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `validade` date NOT NULL
) ;

--
-- Despejando dados para a tabela `epis`
--

INSERT INTO `epis` (`id`, `ca`, `categoria`, `data_entrada`, `nome`, `quantidade`, `validade`) VALUES
(1, 'CA 498', 'Cabeça', '2026-11-17', 'Capacete de Segurança', 9, '2030-11-17'),
(2, 'CA 16462', 'Olhos e Face', '2026-11-17', 'Óculos de Proteção', 10, '2030-11-17'),
(3, 'CA 5674', 'Ouvidos', '2026-11-17', 'Protetor Auricular', 10, '2030-11-17'),
(4, 'CA 30916', 'Mãos', '2026-11-17', 'Luva Mecânica', 10, '2030-11-17'),
(5, 'CA 44608', 'Mãos', '2026-11-17', 'Luva Anticorte', 10, '2030-11-17'),
(6, 'CA 40142 / CA 21159', 'Pés', '2026-11-17', 'Botina de Segurança', 9, '2030-11-17'),
(7, 'CA 41514', 'Respiratória', '2026-11-17', 'Máscara PFF2', 10, '2030-11-17'),
(8, 'CA 34183', 'Corpo', '2026-11-17', 'Macacão de Proteção', 10, '2030-11-17'),
(9, 'CA 18856', 'Corpo', '2026-11-17', 'Avental de Raspa', 1, '2030-11-17'),
(10, 'CA 46614 / CA 34336', 'Olhos e Face', '2026-11-17', 'Máscara para Solda', 12, '2030-11-17');

-- --------------------------------------------------------

--
-- Estrutura para tabela `historico_treinamento`
--

CREATE TABLE `historico_treinamento` (
  `id` bigint(20) NOT NULL,
  `carga_horaria` int(11) NOT NULL,
  `certificado_content_type` varchar(100) NOT NULL,
  `certificado_nome` varchar(255) NOT NULL,
  `conteudo_programatico` varchar(4000) NOT NULL,
  `data_conclusao` date NOT NULL,
  `data_invalidacao` date DEFAULT NULL,
  `data_vencimento` date NOT NULL,
  `dias_antecedencia_alerta` int(11) NOT NULL,
  `instrutor` varchar(120) NOT NULL,
  `motivo_invalidacao` varchar(500) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `validade_aplicada_meses` int(11) NOT NULL,
  `arquivo_certificado_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `treinamento_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `inspecao`
--

CREATE TABLE `inspecao` (
  `id` bigint(20) NOT NULL,
  `data` date NOT NULL,
  `local_inspecao` varchar(160) DEFAULT NULL,
  `numero_onibus` varchar(40) DEFAULT NULL,
  `observacoes_gerais` varchar(2000) DEFAULT NULL,
  `placa` varchar(20) DEFAULT NULL,
  `responsavel` varchar(120) NOT NULL,
  `setor` varchar(120) DEFAULT NULL,
  `tipo` varchar(60) NOT NULL,
  `tipo_id` varchar(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `parametro_treinamento`
--

CREATE TABLE `parametro_treinamento` (
  `id` bigint(20) NOT NULL,
  `cargo` varchar(100) DEFAULT NULL,
  `dias_antecedencia_alerta` int(11) NOT NULL,
  `validade_meses` int(11) NOT NULL,
  `treinamento_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `resposta_inspecao`
--

CREATE TABLE `resposta_inspecao` (
  `id` bigint(20) NOT NULL,
  `descricao` varchar(220) NOT NULL,
  `item_id` varchar(80) NOT NULL,
  `observacao` varchar(1000) DEFAULT NULL,
  `prazo_correcao` date DEFAULT NULL,
  `prioridade` varchar(20) DEFAULT NULL,
  `responsavel_correcao` varchar(120) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `inspecao_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `risco`
--

CREATE TABLE `risco` (
  `id` bigint(20) NOT NULL,
  `icone` varchar(64) DEFAULT NULL,
  `nivel` enum('ALTO','BAIXO','MEDIO') NOT NULL,
  `risco` varchar(180) NOT NULL,
  `setor` varchar(120) NOT NULL,
  `situacao` enum('ATIVO','INATIVO') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `risco`
--

INSERT INTO `risco` (`id`, `icone`, `nivel`, `risco`, `setor`, `situacao`) VALUES
(1, 'bi-wrench', 'MEDIO', 'Contato com Produtos Químicos', 'Oficina Mecânica', 'ATIVO'),
(2, 'bi-shield-exclamation', 'ALTO', 'Atropelamento por Manobra de Ônibus', 'Acidente', 'ATIVO'),
(3, 'bi-lightning-charge', 'ALTO', 'Choque Elétrico', 'Manutenção Elétrica', 'ATIVO'),
(4, 'bi-droplet', 'MEDIO', 'Chão Molhado e Escorregadio', 'Lavagem de Veículos', 'ATIVO');

-- --------------------------------------------------------

--
-- Estrutura para tabela `treinamento`
--

CREATE TABLE `treinamento` (
  `id` bigint(20) NOT NULL,
  `carga_horaria` int(11) NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `nome` varchar(80) NOT NULL,
  `norma` varchar(255) DEFAULT NULL,
  `participantes` int(11) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `validade` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `treinamento_colaborador`
--

CREATE TABLE `treinamento_colaborador` (
  `treinamento_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuario`
--

CREATE TABLE `usuario` (
  `id` bigint(20) NOT NULL,
  `cargo` varchar(100) DEFAULT NULL,
  `email` varchar(180) NOT NULL,
  `nome` varchar(120) NOT NULL,
  `perfil` varchar(30) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `status` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `usuario`
--

INSERT INTO `usuario` (`id`, `cargo`, `email`, `nome`, `perfil`, `senha`, `status`) VALUES
(1, 'Administrador', 'admin@localhost', 'Administrador', 'ADMIN', '$2a$10$5gLq/OhCLFAY9HINOkjo4.sFijumWMXqYw8t05YvByCXZ1awk86Ye', 'ATIVO'),
(2, 'Motorista', 'joaodasilva@empresa.com', 'João da Silva', 'COLABORADOR', '$2a$10$6Kn.LDwu0fLSs3GyX5WCHOD.oSCIP8griTg1LKry3TX7.TzKj.vU6', 'ATIVO');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuario_risco`
--

CREATE TABLE `usuario_risco` (
  `id` bigint(20) NOT NULL,
  `validade` date NOT NULL,
  `validado` bit(1) NOT NULL,
  `risco_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `arquivo_certificado_treinamento`
--
ALTER TABLE `arquivo_certificado_treinamento`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `entrega_epi`
--
ALTER TABLE `entrega_epi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKncmfsnuth2t6f53yr2fuje6j` (`usuario_id`);

--
-- Índices de tabela `entrega_epi_item`
--
ALTER TABLE `entrega_epi_item`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKlbmodp5kr1htyhvc0m2rxp5l7` (`entrega_id`),
  ADD KEY `FK1wen5rrx7qci5ymrwe33328t` (`epi_id`);

--
-- Índices de tabela `epis`
--
ALTER TABLE `epis`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `historico_treinamento`
--
ALTER TABLE `historico_treinamento`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKsc8i4lg4cc903wegk1tn7bxc` (`arquivo_certificado_id`),
  ADD KEY `FKiipqx3884t04ji98enac7shh2` (`usuario_id`),
  ADD KEY `FKqtco1gok4tlkv95yabm7nibpl` (`treinamento_id`);

--
-- Índices de tabela `inspecao`
--
ALTER TABLE `inspecao`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKnjl27b4e3e49i64viectlg2i9` (`usuario_id`);

--
-- Índices de tabela `parametro_treinamento`
--
ALTER TABLE `parametro_treinamento`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKleboyqukdl735f4ba9xlaoguy` (`treinamento_id`,`cargo`);

--
-- Índices de tabela `resposta_inspecao`
--
ALTER TABLE `resposta_inspecao`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK7ju5i3eprjxti0tqih49e1yrd` (`inspecao_id`,`item_id`);

--
-- Índices de tabela `risco`
--
ALTER TABLE `risco`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `treinamento`
--
ALTER TABLE `treinamento`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `treinamento_colaborador`
--
ALTER TABLE `treinamento_colaborador`
  ADD UNIQUE KEY `UKe1nhnxk629ql0a6lv60f72raa` (`treinamento_id`,`usuario_id`),
  ADD KEY `FK52lofkfruxfyvmmty3xdc5fvt` (`usuario_id`);

--
-- Índices de tabela `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK5171l57faosmj8myawaucatdw` (`email`);

--
-- Índices de tabela `usuario_risco`
--
ALTER TABLE `usuario_risco`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKissyavf7e0u3ysjsyy9joxgy7` (`usuario_id`,`risco_id`),
  ADD KEY `FKaykgiddwto1kv5lletuqlq6ai` (`risco_id`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `arquivo_certificado_treinamento`
--
ALTER TABLE `arquivo_certificado_treinamento`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `entrega_epi`
--
ALTER TABLE `entrega_epi`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `entrega_epi_item`
--
ALTER TABLE `entrega_epi_item`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `epis`
--
ALTER TABLE `epis`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `historico_treinamento`
--
ALTER TABLE `historico_treinamento`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `inspecao`
--
ALTER TABLE `inspecao`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `parametro_treinamento`
--
ALTER TABLE `parametro_treinamento`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `resposta_inspecao`
--
ALTER TABLE `resposta_inspecao`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `risco`
--
ALTER TABLE `risco`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de tabela `treinamento`
--
ALTER TABLE `treinamento`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `usuario_risco`
--
ALTER TABLE `usuario_risco`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `entrega_epi`
--
ALTER TABLE `entrega_epi`
  ADD CONSTRAINT `FKncmfsnuth2t6f53yr2fuje6j` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);

--
-- Restrições para tabelas `entrega_epi_item`
--
ALTER TABLE `entrega_epi_item`
  ADD CONSTRAINT `FK1wen5rrx7qci5ymrwe33328t` FOREIGN KEY (`epi_id`) REFERENCES `epis` (`id`),
  ADD CONSTRAINT `FKlbmodp5kr1htyhvc0m2rxp5l7` FOREIGN KEY (`entrega_id`) REFERENCES `entrega_epi` (`id`);

--
-- Restrições para tabelas `historico_treinamento`
--
ALTER TABLE `historico_treinamento`
  ADD CONSTRAINT `FK2yaj49ymld3mu8btwtmhj1b6p` FOREIGN KEY (`arquivo_certificado_id`) REFERENCES `arquivo_certificado_treinamento` (`id`),
  ADD CONSTRAINT `FKiipqx3884t04ji98enac7shh2` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  ADD CONSTRAINT `FKqtco1gok4tlkv95yabm7nibpl` FOREIGN KEY (`treinamento_id`) REFERENCES `treinamento` (`id`);

--
-- Restrições para tabelas `inspecao`
--
ALTER TABLE `inspecao`
  ADD CONSTRAINT `FKnjl27b4e3e49i64viectlg2i9` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);

--
-- Restrições para tabelas `parametro_treinamento`
--
ALTER TABLE `parametro_treinamento`
  ADD CONSTRAINT `FKbhgwistqq21rd9rtkkdvbhs1o` FOREIGN KEY (`treinamento_id`) REFERENCES `treinamento` (`id`);

--
-- Restrições para tabelas `resposta_inspecao`
--
ALTER TABLE `resposta_inspecao`
  ADD CONSTRAINT `FKiyg6r7dkojocxmrq3d1dq1t84` FOREIGN KEY (`inspecao_id`) REFERENCES `inspecao` (`id`);

--
-- Restrições para tabelas `treinamento_colaborador`
--
ALTER TABLE `treinamento_colaborador`
  ADD CONSTRAINT `FK52lofkfruxfyvmmty3xdc5fvt` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  ADD CONSTRAINT `FKkgdh4yt96lisnaxke4kw5b4de` FOREIGN KEY (`treinamento_id`) REFERENCES `treinamento` (`id`);

--
-- Restrições para tabelas `usuario_risco`
--
ALTER TABLE `usuario_risco`
  ADD CONSTRAINT `FKaykgiddwto1kv5lletuqlq6ai` FOREIGN KEY (`risco_id`) REFERENCES `risco` (`id`),
  ADD CONSTRAINT `FKrgvbk9ijdq339rfenerk73j1l` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
