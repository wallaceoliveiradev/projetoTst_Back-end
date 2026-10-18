# projetoTst_Back-end

Back-end e API do projeto integrador.

## Treinamentos, certificados e reciclagens

Os cursos existentes em `/api/treinamentos` continuam sendo o catálogo. O histórico de conclusões fica em uma entidade separada: cada conclusão cria um ciclo não editável com colaborador, curso, dados de emissão, certificado, validade aplicada e datas. Uma nova reciclagem cria outro ciclo e marca o ciclo vigente anterior como `SUBSTITUIDO`; o arquivo antigo permanece disponível no histórico.

Todos os endpoints abaixo exigem autenticação. Operações de escrita e leitura administrativa são restritas a `ADMIN`. Colaboradores podem consultar apenas os próprios registros e baixar os próprios certificados.

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/api/treinamentos/historico` | Lista todos os ciclos para administrador ou os ciclos do colaborador autenticado. |
| `POST` | `/api/treinamentos/historico` | Registra uma conclusão como `multipart/form-data`, com a parte JSON `dados` e o arquivo `certificado`. |
| `GET` | `/api/treinamentos/historico/{id}/certificado` | Baixa o arquivo do ciclo, autorizado para administrador ou titular. |
| `POST` | `/api/treinamentos/historico/{id}/invalidar` | Invalida um ciclo por motivo extraordinário, sem apagar o certificado. |
| `GET` | `/api/treinamentos/parametros` | Lista regras de validade por curso e, opcionalmente, cargo. |
| `POST` | `/api/treinamentos/{treinamentoId}/parametros` | Cria ou atualiza a regra geral ou a regra para um cargo. |
| `PUT` | `/api/treinamentos/{treinamentoId}/parametros/{parametroId}` | Atualiza uma regra existente. |
| `DELETE` | `/api/treinamentos/{treinamentoId}/parametros/{parametroId}` | Exclui a regra de validade selecionada (somente administrador). |

O vencimento é calculado no servidor como `dataConclusao + validadeMeses`. O status é recalculado em cada consulta: `VENCIDO` após a data limite, `ALERTA` dentro da antecedência configurada e `VALIDO` antes dessa janela. O padrão de alerta é 60 dias. Para NR-33, na ausência de regra explícita, trabalhadores e vigias têm validade de 12 meses e supervisores de 24 meses. Regras por cargo cadastradas têm precedência sobre a regra geral e os valores padrão do curso.

O upload aceita PDF, PNG, JPEG, GIF, BMP e WebP, validando a assinatura do arquivo e limitando-o a 10 MB. O conteúdo é armazenado no banco de dados junto ao ciclo de histórico.

Excluir uma regra não remove certificados nem altera vencimentos já calculados: cada certificado conserva a validade e a antecedência registradas no momento da conclusão. Novos registros passam a usar a regra restante, ou o padrão do curso/NR.

O cadastro atual de usuários possui apenas os perfis `ADMIN` e `COLABORADOR`, sem relação de gestor. Por isso, os alertas do colaborador ficam disponíveis para o próprio titular e para administradores; ainda não há entrega direcionada a um gestor específico.
