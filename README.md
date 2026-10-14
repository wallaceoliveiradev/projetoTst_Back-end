# projetoTst_Back-end
Back-end e api do projeto integrador

## Redefinir a conta administrativa de desenvolvimento

A conta inicial usa o e-mail definido em `app.admin.email` e a senha definida em
`app.admin.password` no ambiente local do backend. O inicializador cria a conta
quando ela não existe; por padrão, ele não altera uma conta já existente.

Se a senha configurada mudou e a conta de desenvolvimento já estava no banco,
defina temporariamente `app.admin.reset-password=true` no ambiente local e
reinicie a API. O backend atualiza o hash da senha e ativa essa conta. Depois
que a redefinição ocorrer, remova a opção ou volte-a para `false` e reinicie a
API. Essa opção só funciona com o perfil `dev`; não a habilite em produção.
No perfil `dev`, o backend também cria os quatro riscos de demonstração da tela
quando a tabela de riscos está vazia.

## API de riscos

Todos os endpoints abaixo exigem `Authorization: Bearer <token>`. A gestão do
catálogo e das associações é restrita a administradores; cada usuário pode
consultar as próprias associações.

- `GET /api/riscos?busca=eletrico&nivel=ALTO&situacao=ATIVO` lista e filtra.
- `GET /api/riscos/{id}` consulta um risco.
- `POST /api/riscos` cadastra: `{ "setor": "Manutenção", "risco": "Choque elétrico", "nivel": "ALTO", "situacao": "ATIVO", "icone": "bi-lightning-charge" }`.
- `PUT /api/riscos/{id}` atualiza os mesmos campos. Use `situacao: "INATIVO"` para desativar sem perder histórico.
- `GET /api/usuarios/{usuarioId}/riscos` lista as associações de um usuário (admin).
- `GET /api/usuarios/me/riscos` lista as associações do usuário autenticado.
- `PUT /api/usuarios/{usuarioId}/riscos` substitui as associações (admin), por exemplo:
  `{ "riscos": [{ "riscoId": 1, "validade": "2027-12-31", "validado": true }] }`.
  Envie uma lista vazia para remover todas as associações do usuário.

Uma associação armazena validade e validação; a resposta também informa se a
data de validade ainda está vigente. Exemplos executáveis estão em
`postman/gestao-riscos.postman_collection.json`.

## API de inspeções e checklist

Todos os endpoints exigem `Authorization: Bearer <token>`. Colaboradores
consultam e atualizam somente as próprias inspeções; administradores podem
consultar e atualizar todas.

- `GET /api/inspecoes` lista as inspeções visíveis ao usuário.
- `GET /api/inspecoes/{id}` consulta o cabeçalho e as respostas.
- `POST /api/inspecoes` grava uma inspeção e suas respostas na mesma transação.
- `PUT /api/inspecoes/{id}` substitui o cabeçalho e as respostas na mesma
  transação.

As datas são enviadas no formato ISO `YYYY-MM-DD`. Cada item deve ter uma
resposta explícita (`conforme`, `naoConforme` ou `pendente`); itens não
conformes também exigem uma observação. Exemplo de criação:

```json
{
  "data": "2026-10-06",
  "tipoId": "onibus",
  "tipo": "Pré-operacional",
  "local": "Garagem Austin",
  "setor": "Operacional",
  "numeroOnibus": "3021",
  "placa": "RIO-3A21",
  "responsavel": "Responsável pela inspeção",
  "observacoesGerais": "",
  "respostas": [
    {
      "itemId": "pneus",
      "descricao": "Pneus em condições adequadas",
      "status": "naoConforme",
      "observacao": "Desgaste irregular.",
      "prioridade": "Alta",
      "prazoCorrecao": "2026-10-13",
      "responsavelCorrecao": "Manutenção"
    }
  ]
}
```
