# API do Projeto TST

API REST para apoiar a gestão de Segurança do Trabalho, conectada ao sistema web do projeto. Ela reúne recursos para colaboradores, riscos ocupacionais, EPIs, inspeções, treinamentos, indicadores e relatórios.

## Tecnologias

- Java 21 e Spring Boot
- Spring Web, Spring Data JPA, Spring Security e Bean Validation
- MySQL/MariaDB
- Autenticação por token JWT
- Maven (o repositório inclui Maven Wrapper)

## Requisitos

- JDK 21
- MySQL ou MariaDB
- Git (opcional, para clonar o repositório)

## Configuração e execução

1. Crie no MySQL um banco chamado `tst_db` ou ajuste a URL nas configurações da aplicação.
2. Configure URL, usuário e senha do banco, perfil, credenciais administrativas e segredo JWT. Prefira variáveis de ambiente ou configuração local não versionada para valores privados.
3. Na raiz do projeto, execute:

   **Windows**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

   **Linux/macOS**
   ```bash
   ./mvnw spring-boot:run
   ```

A aplicação inicia por padrão em `http://localhost:8080`. O SQL de apoio está em `tst_db.sql`; revise-o antes de executar, pois pode criar ou modificar dados.

## Recursos principais

As rotas abaixo usam o prefixo `/api`:

| Recurso | Rotas base | O que oferece |
| --- | --- | --- |
| Autenticação | `/auth/login` | Login e emissão de token JWT |
| Usuários | `/usuarios` | Cadastro e administração; `/usuarios/me` para dados do usuário autenticado |
| Riscos | `/riscos` e `/usuarios/{id}/riscos` | Catálogo, filtros e associação de riscos aos colaboradores |
| EPIs | `/epis` e `/epis/entregas` | Cadastro de equipamentos e controle de entregas |
| Inspeções | `/inspecoes` | Registro, consulta e atualização de inspeções |
| Treinamentos | `/treinamentos` | Catálogo, histórico de conclusões, certificados e regras de validade |
| Dashboard | `/dashboard` | Indicadores para acompanhamento gerencial |
| Relatórios | `/relatorios/resumo` e `/relatorios/dados` | Dados e resumo filtrados por período |

A maioria das rotas exige autenticação. Envie o token recebido no login no cabeçalho `Authorization: Bearer <token>`. O acesso administrativo é limitado pelo perfil `ADMIN`; colaboradores têm acesso aos dados permitidos para seu próprio usuário.

### Treinamentos e certificados

O histórico de conclusão é separado do catálogo de cursos. Cada nova reciclagem cria um novo registro, mantendo os ciclos e certificados anteriores. Os certificados são enviados como `multipart/form-data`, com as partes `dados` (JSON) e `certificado` (arquivo). O vencimento considera a validade configurada para o treinamento, e a API calcula os estados de validade e alerta.

## Testes

Execute os testes automatizados com o Maven Wrapper:

```bash
./mvnw test
```

No Windows, use ` .\mvnw.cmd test`.

## Organização do código

O código principal fica em `src/main/java/br/com/projetotst_api`, separado em controllers, services, repositories, entities, DTOs, configuração e segurança. As configurações da aplicação ficam em `src/main/resources` e os testes em `src/test`.

## Trabalho em equipe

- Faça commits de código, documentação e arquivos necessários para reproduzir o projeto.
- Não versione senhas, tokens, segredos ou dados reais de usuários; cada integrante deve configurar seus valores localmente.
- Mantenha os arquivos do Maven Wrapper no repositório para padronizar a execução entre os integrantes.
- Combine alterações de banco e mantenha scripts SQL versionados quando forem necessários para preparar o ambiente.
