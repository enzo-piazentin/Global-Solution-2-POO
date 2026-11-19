# Territory Predictive Insights — Chuva e Alagamento na Zona Norte de SP

Global Solution 2º/2026 · POO · Turma de Fevereiro · Tema **CitizenTech**

Sistema de console que lê sensores (simulados), grava medições e alertas em banco de dados, gera duas previsões simples sobre as leituras gravadas e emite alertas explicados. O objetivo é apoiar a resposta comunitária a **chuvas intensas e alagamentos** na bacia do Córrego Cabuçu de Baixo (Brasilândia e Freguesia do Ó).

**Caminho escolhido: Spring Boot + JPA** (Aulas 13 a 15), com `MedicaoRepository` e `AlertaRepository` (Spring Data JPA) e banco **Oracle da FIAP** (driver `ojdbc11`).

---

## 1. Público beneficiado

**Quem sofre o problema:** moradores das áreas baixas e das margens de córregos da Zona Norte de São Paulo. Todo verão eles convivem com enxurradas, ruas alagadas, perda de móveis e risco de deslizamento nas encostas com solo encharcado.

**Quem usa o sistema:** a associação de bairro e os agentes comunitários de Defesa Civil do território.

**Qual decisão o sistema apoia:** *"Precisamos agir hoje?"* Por exemplo: fazer um mutirão para limpar bocas de lobo, avisar as famílias das áreas baixas, abrir o ponto de apoio comunitário ou acionar a Defesa Civil (199). O sistema mostra o valor previsto, o limite estourado e uma ação recomendada. **Quem decide é a equipe.**

---

## 2. Como executar

### Pré-requisitos
- JDK 17 ou superior
- Eclipse ou IntelliJ com suporte a Maven. **Não é preciso instalar o Maven**: o projeto traz o Maven Wrapper (`mvnw` / `mvnw.cmd`).
- Para o banco principal: acesso ao **Oracle da FIAP** (`oracle.fiap.com.br:1521:ORCL`) com RM e senha. O driver `ojdbc11` vem pelo Maven.

### Passo 1: criar as tabelas no Oracle (uma única vez)
1. Abra o **SQL Developer** e conecte no Oracle da FIAP com o RM que será usado no projeto.
2. Abra o arquivo **`banco/script-oracle.sql`** e execute o script inteiro (**F5 - Run Script**). Ele cria as tabelas `medicoes` e `alertas`.
3. O script também traz, comentadas, consultas para conferir os dados gravados e o comando para recomeçar do zero.

> Como `spring.jpa.hibernate.ddl-auto=none`, o Hibernate **não cria as tabelas no Oracle**: sem o script, a primeira gravação falha.

### Passo 2: informar o RM e a senha (sem colocar a senha no GitHub)
As credenciais **não ficam no `application.properties`**. Elas são lidas de um arquivo local, que o Git ignora:

1. Na pasta raiz do projeto (onde está o `pom.xml`), copie o arquivo **`credenciais.exemplo.properties`** e renomeie a cópia para **`credenciais.properties`**.
2. Preencha com o RM e a senha do Oracle:
   ```properties
   DB_USER=RM563500
   DB_PASSWORD=sua_senha
   ```
3. Pronto. O `credenciais.properties` está no `.gitignore` e **nunca vai para o repositório**.

Alternativas: definir as variáveis de ambiente `DB_USER` e `DB_PASSWORD`, ou, se preferir, editar diretamente as linhas `spring.datasource.username` e `spring.datasource.password` do `application.properties` (nesse caso, **não façam commit da senha**).

### Passo 3: executar

**Pelo Eclipse / IntelliJ**
1. `File → Import → Maven → Existing Maven Projects` (no IntelliJ: `File → Open` e selecione o `pom.xml`).
2. Clique com o botão direito em `br.com.fiap.territory.SistemaMonitoramento` → `Run As → Java Application` (IntelliJ: `Run`).
3. Use o menu no console. A pasta de trabalho precisa ser a raiz do projeto (é o padrão das IDEs), para o `credenciais.properties` ser encontrado.
4. Testes: botão direito em `src/test/java` → `Run As → JUnit Test`.

**Pelo terminal**
```bash
./mvnw test               # 19 testes unitários (não precisam de banco)  | Windows: mvnw.cmd test
./mvnw spring-boot:run    # sobe o Spring, conecta no Oracle e abre o menu | Windows: mvnw.cmd spring-boot:run
```

### Roteiro sugerido para a correção
1. Opção 2 logo no início: o sistema se cala, porque não há leituras dos últimos 60 minutos.
2. Opção **1** com **10 ciclos**: as leituras gravadas, as descartadas (valor inválido) e as falhas de sensor aparecem com mensagem clara.
3. Opção **2**: aparecem as duas previsões, com valor e significado, e os alertas explicados (sensor, valor previsto, limite estourado e o que fazer).
4. Opção **2** de novo: o mesmo alerta **não é gravado duas vezes**.
5. Opção **3**: histórico por sensor (com exportação CSV) e alertas emitidos.
6. Opção **4**:
   - altere a população (teste também um valor negativo);
   - altere os limiares com o código **`ZN-2026`** (teste também um código errado e limiares fora de ordem);
   - coloque um sensor em manutenção.
7. Opção **5**: status completo.
8. Saia com a opção **0**, rode de novo e veja que **o histórico continua lá**.
9. (Opcional) No SQL Developer, rode as consultas comentadas no fim de `banco/script-oracle.sql` para ver as medições e alertas gravados.

---

## 3. Estrutura do projeto

A estrutura sugerida no enunciado foi mantida (`dominio`, `excecao`, `persistencia`, `servico` e `SistemaMonitoramento`), dentro do pacote base `br.com.fiap.territory`, no mesmo padrão `br.com.<projeto>` das aulas. Os testes ficam nos mesmos pacotes das classes testadas.

```
territory-predictive-insights-fev/
├── pom.xml · mvnw · mvnw.cmd
├── README.md
├── credenciais.exemplo.properties   (modelo: copie para credenciais.properties e preencha RM/senha)
├── banco/script-oracle.sql          (criação das tabelas medicoes e alertas no Oracle)
├── src/main/java/br/com/fiap/territory/
│   ├── dominio/        ComponenteTerritorial, PontoMonitoramento,
│   │                   Sensor, SensorBase, SensorChuva, SensorNivelRio, SensorUmidadeSolo,
│   │                   DadosTerritorio, Limiar, NivelAlerta,
│   │                   EstrategiaPrevisao, PrevisaoMediaMovel, PrevisaoComparacao,
│   │                   Medicao, MedicaoBuilder, Alerta, AlertaBuilder
│   ├── excecao/        DadosInvalidosException, FonteIndisponivelException, AcessoNegadoException
│   ├── persistencia/   MedicaoRepository, AlertaRepository (Spring Data JPA)
│   ├── servico/        ServicoMonitoramento, FabricaPrevisao, ConfiguracaoSistema
│   └── SistemaMonitoramento.java   (@SpringBootApplication + CommandLineRunner: menu principal)
├── src/main/resources/application.properties      (conexão com o Oracle FIAP e configuração do JPA)
└── src/test/java/br/com/fiap/territory/
    ├── dominio/        MedicaoTest, PrevisaoMediaMovelTest
    └── servico/        ServicoMonitoramentoTest
```

---

## 4. Como cada requisito foi atendido

### 4.1 Domínio orientado a objetos
- **Classe abstrata `ComponenteTerritorial`** (`id`, `nome`, `regiao`, `status`), com o método abstrato `verificarStatus()`.
  - A filha concreta `PontoMonitoramento` calcula o status a partir dos sensores: OPERANTE, OPERAÇÃO PARCIAL ou INOPERANTE.
  - O ponto exige pelo menos um sensor e recusa o mesmo sensor duas vezes.
- **Interface `Sensor`**: tem só as assinaturas (ler valor, verificar funcionamento, tipo, código, unidade, valor máximo e ativar/desativar).
- **Classe abstrata `SensorBase`**: guarda o que é comum aos sensores (`codigo`, `ativo` e `random`).
  - O `lerValor()` dela faz as checagens (sensor em manutenção, falha de comunicação de 3%) e chama `simularValor()`, que é **abstrato**.
  - As 3 implementações (`SensorChuva` em mm/h, `SensorNivelRio` em m e `SensorUmidadeSolo` em %) só definem a própria faixa de valores, a mensagem de falha, o tipo e a unidade.
  - Cerca de 3% das leituras chegam corrompidas, de propósito, para exercitar a validação.
- **Encapsulamento:**
  - `DadosTerritorio` tem todos os atributos `private`, setters com validação e um **código de acesso** que autoriza a alteração de limiares.
  - `Limiar` tem `private final` para ATENÇÃO, ALERTA e CRÍTICO, valida no construtor e tem o método `classificar()`.
- **Níveis de alerta como constantes `String`** (`NivelAlerta.ATENCAO`, `ALERTA`, `CRITICO`, além de `NORMAL` para "sem alerta"), no mesmo estilo das categorias do `Produto` da Aula 12.
- **Polimorfismo:** a classe abstrata `EstrategiaPrevisao` tem as filhas `PrevisaoMediaMovel` e `PrevisaoComparacao`.
  - As duas sobrescrevem `prever()` e `explicarResultado()` e chamam `super(nome, janela)`.
  - `PrevisaoComparacao` também sobrescreve `getMinimoLeituras()`, usando `super.getMinimoLeituras() * 2`.
- **Escolha da estratégia sem `if tipo ==` / `instanceof`:** o serviço percorre a lista entregue pela `FabricaPrevisao` e chama `estrategia.prever(...)`.
- **JavaDoc** curto em `Sensor`, `prever()`, `explicarResultado()` e `verificarStatus()`.

### 4.2 Exceções customizadas
- `DadosInvalidosException`:
  - medição negativa ou que não é um número (validada em `Medicao`, ao ser criada pelo `MedicaoBuilder`);
  - campos obrigatórios ausentes (`MedicaoBuilder.build()`, `AlertaBuilder.build()`);
  - leitura fora da faixa do sensor (`ServicoMonitoramento.registrarLeitura`);
  - população negativa (setter `DadosTerritorio.setPopulacaoEstimada`);
  - limiar inválido ou fora de ordem (construtor de `Limiar`) e limiar acima do máximo do sensor;
  - histórico insuficiente, código de sensor vazio, sensor que não pertence ao ponto, opções inválidas no menu.
- `FonteIndisponivelException`: sensor que falha na comunicação ou sensor em manutenção.
- `AcessoNegadoException`: código de acesso errado ao alterar ou restaurar limiares.
- **No menu**, cada uma é capturada com uma mensagem clara: `[DADOS INVÁLIDOS]`, `[FONTE INDISPONÍVEL]`, `[ACESSO NEGADO]`, `[DESCARTADA]`, `[FALHA]`.
- No fim do loop há um `catch (RuntimeException e)` geral que mostra `[ERRO INESPERADO]` sem derrubar o programa (por exemplo, se o banco ficar indisponível).
- **Nenhum `catch` vazio.**

### 4.3 Persistência (Spring Boot + JPA)
- **Entidades no `dominio`:** `Medicao` (tabela `medicoes`) e `Alerta` (tabela `alertas`), com `@Entity`, `@Table`, `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` e `@Column`.
  - O construtor sem argumentos que o JPA exige é `protected`.
  - Fora do pacote, só se cria uma medição ou um alerta pelo Builder, que valida tudo.
- **Repositórios em `persistencia`:** `MedicaoRepository` e `AlertaRepository` estendem `JpaRepository` e usam **consultas derivadas do nome do método**, como na Aula 13:
  - `findBySensorCodigoOrderByIdAsc`: histórico do sensor;
  - `findBySensorCodigoAndDataHoraGreaterThanEqualOrderByIdAsc`: leituras dos últimos 60 minutos;
  - `findAllByOrderByIdDesc`: alertas emitidos, do mais recente para o mais antigo;
  - `existsBySensorCodigoAndNivelAndDataHoraGreaterThanEqual`: impede alerta duplicado.
- **Nenhum SQL escrito à mão nem concatenado.** O Spring gera as consultas, abre e fecha as conexões e gerencia as transações.
- Toda medição válida e todo alerta novo são gravados com `save()`. O menu lista o **histórico por sensor** e os **alertas já emitidos**.
- **Oracle da FIAP** (`application.properties`): os dados ficam no banco da FIAP e **sobrevivem ao reiniciar**.
  - `ddl-auto=none`: o Hibernate não cria nem altera tabelas. Elas são criadas pelo `banco/script-oracle.sql` (o enunciado aceita "script SQL do banco").
  - Credenciais fora do código: o `application.properties` lê `DB_USER`/`DB_PASSWORD` do arquivo local `credenciais.properties` (ignorado pelo Git) ou de variáveis de ambiente.
  - `PhysicalNamingStrategyStandardImpl`: o nome da coluna é **exatamente** o nome do atributo Java (`sensorCodigo`, `dataHora`...), por isso o script usa esses mesmos nomes.
  - `show-sql=false`: o SQL gerado pelo JPA não aparece no console, para não poluir o menu. Para ver o SQL durante a defesa, basta mudar para true.

### 4.4 Padrões criacionais

| Padrão | Classe | Por que **ali** e não em outro lugar |
|---|---|---|
| **Factory** | `FabricaPrevisao` (`@Component`) | As estratégias dependem da configuração (janela de cada uma e quais estão ativas). A fábrica cria as estratégias a partir do `ConfiguracaoSistema` e as guarda num `Map`, então escolher uma é uma consulta ao mapa, não uma cadeia de `if`. O serviço nunca faz `new PrevisaoX(...)`. |
| **Singleton** | `ConfiguracaoSistema` | Os limiares precisam ser uma **única fonte da verdade**: se o menu muda o limiar de chuva, o serviço de alertas tem que ver o mesmo valor. A estrutura do enunciado pede explicitamente `ConfiguracaoSistema (Singleton)`, e o grupo escolheu demonstrar a **versão clássica da Aula 14**: atributo `private static`, construtor `private` e `getInstancia()` `synchronized`, a forma que o professor lembra que "ainda cai em prova". Sabemos o custo apontado na própria Aula 14: em projeto Spring, o Singleton manual acopla e esconde estado global. Para reduzir esse custo, a classe guarda só configuração e oferece `restaurarLimiaresPadrao()`. É isso que permite aos **testes unitários**, que rodam fora do Spring, usar a mesma instância e voltá-la ao estado padrão antes e depois de cada teste (`@BeforeEach`/`@AfterEach`). Nas demais classes a instância única fica com o Spring (`@Service`, `@Component`, `@Repository`), que é "o Singleton na prática" da Aula 14. |
| **Builder** | `MedicaoBuilder` e `AlertaBuilder` | Medição e alerta têm muitos campos (o alerta tem 9). Com o Builder, o código monta passo a passo (`comSensor().comRegiao().comValor().build()`) e **toda a validação fica concentrada no `build()`**: um objeto inválido nunca chega a existir. Os construtores de `Medicao` e `Alerta` são de pacote, então fora do `dominio` só se cria por `build()`. Ao ler do banco, o JPA usa o construtor `protected` sem argumentos. |

O código **chama** os padrões em vez de duplicar a criação: o serviço usa `fabricaPrevisao.criarEstrategiasConfiguradas()`, quem precisa de limiar usa `ConfiguracaoSistema.getInstancia().getLimiar(tipo)`, e toda medição e todo alerta nascem de um Builder.

### 4.5 Previsões e alertas
- **Média móvel (janela 5):** média das últimas 5 medições, ou seja, o valor esperado se o ritmo recente continuar.
- **Comparação com o período anterior (janela 3):** média das últimas 3 somada à variação em relação às 3 anteriores, ou seja, se a situação tende a piorar ou melhorar. Precisa de 6 leituras.
- Só entram leituras dos **últimos 60 minutos**; cada estratégia usa as mais recentes.
- Cada previsão é classificada pelo `Limiar` do tipo de sensor (`classificar()`) em ATENÇÃO, ALERTA, CRÍTICO ou NORMAL.
- Para cada sensor, vale o nível mais grave entre as estratégias. Se um alerta do mesmo nível já foi emitido nos últimos 60 minutos, ele não é gravado de novo.
- Todo alerta explica **qual sensor, qual estratégia, o valor previsto e o limite estourado**, e sugere uma ação para hoje.

| Sensor | ATENÇÃO | ALERTA | CRÍTICO |
|---|---|---|---|
| Chuva (mm/h) | 10 | 25 | 50 |
| Nível do córrego (m) | 2,0 | 3,0 | 4,0 |
| Umidade do solo (%) | 75 | 85 | 95 |

### 4.6 Menu
É um loop `do-while` com `Scanner`, com as opções 1 (verificar sensores), 2 (rodar previsões), 3 (ver histórico), 4 (gerenciar dados do território), 5 (status completo) e 0 (sair). Todas as mensagens são exibidas com `System.out.printf`, e todo texto com valores é montado com `String.format`.

### 4.7 Testes unitários (JUnit 5 + Mockito)
São **19 testes**, todos no padrão **AAA** (prepara / executa / verifica). Nenhum usa banco ou o console.

| Classe | Testes |
|---|---|
| `MedicaoTest` (5) | Medição válida pelo Builder; chuva negativa, valor que não é número, código vazio e região ausente lançam `DadosInvalidosException` (`assertThrows`). |
| `PrevisaoMediaMovelTest` (4) | Média das últimas 5; ignora leituras antigas; histórico insuficiente lança exceção; explicação do resultado. |
| `ServicoMonitoramentoTest` (10) | **Mockito**, com `@Mock` em `MedicaoRepository` e `AlertaRepository`, `@Spy` na `FabricaPrevisao` real e `@InjectMocks` no serviço, como na Aula 15. Não sobe o Spring nem usa banco. Cobre: alerta CRÍTICO gravado; nível mais grave entre as estratégias; alerta recente não é duplicado; nada é gravado abaixo do limiar; o sistema se cala com menos de 5 leituras; leitura válida gravada com a região do ponto; leitura fora da faixa não é gravada; sensor em manutenção lança `FonteIndisponivelException`; código errado lança `AcessoNegadoException`; limiares fora de ordem são recusados. |

### 4.8 Bônus escolhido: exportação CSV
Na opção **3 → 1**, depois de listar o histórico de um sensor, o sistema oferece exportá-lo para `exportacoes/historico_<SENSOR>_<data>.csv`. O arquivo sai no formato do Excel em português:
- separador `;`;
- vírgula decimal;
- data `dd/MM/aaaa HH:mm:ss`;
- marca UTF-8, para os acentos aparecerem corretamente.

**Por que este bônus:** é o de menor risco. Não depende de internet (como os dados do INMET) e não mexe nas previsões (como a simulação de 24 h), além de ser útil para a associação levar o histórico a uma reunião.

---

## 5. Decisões da equipe
1. **Recorte:** clima local (chuva e alagamento), com 2 pontos de monitoramento e 5 sensores na bacia do Córrego Cabuçu de Baixo. Os dados descrevem regiões e equipamentos, **nunca pessoas**.
2. **Spring Boot + JPA com Oracle FIAP** (Aulas 12 a 15): o menu de console roda dentro do Spring Boot (`CommandLineRunner`), sem API REST, como o FAQ permite. As tabelas são criadas por script (`ddl-auto=none`).
3. **Silêncio estatístico:** a previsão usa só leituras dos últimos 60 minutos e exige um mínimo de leituras (5 para a média móvel e 6 para a comparação). Abaixo disso, o sistema mostra "SEM PREVISÃO" e não emite alerta.
4. **Alertas sem duplicação:** rodar a previsão várias vezes não grava o mesmo alerta de novo.
5. **A região vem do ponto:** a medição e o alerta herdam a região do ponto onde o sensor está instalado.
6. **Sensor em manutenção:** o status do ponto reflete o estado real dos sensores (ativo ou em manutenção), não um sorteio.
7. A **população estimada (95.000 pessoas)** é um valor ilustrativo da área de risco. Os limiares alterados no menu valem até o programa fechar. Medições e alertas ficam no banco.
8. **Código de acesso** para alterar limiares: `ZN-2026`, definido em `territorio.codigo-acesso` no `application.properties`. O `SistemaMonitoramento` o recebe com `@Value` e o entrega ao `DadosTerritorio`, que o guarda em um atributo `private final` **sem getter**: depois disso, nenhuma classe consegue ler o código, só pedir a validação com `autorizarAlteracaoDeLimiares()`.
9. **Credenciais fora do GitHub:** o RM e a senha ficam no `credenciais.properties`, que está no `.gitignore`. Quem for rodar (inclusive o professor) cria o seu a partir do `credenciais.exemplo.properties`.
10. **Console limpo:** `spring.main.banner-mode=off` e `logging.level.root=WARN` escondem o banner e os logs de inicialização do Spring, que poluíam o menu.

---

## 6. Guia para a defesa: recursos usados que não foram vistos em aula

Todo integrante deve saber explicar estes pontos:

| Recurso | Onde | O que faz |
|---|---|---|
| `CommandLineRunner` | `SistemaMonitoramento` | Faz o Spring chamar o método `run()` assim que a aplicação sobe. É ali que o menu de console começa. No fim, `contexto.close()` encerra o Spring e fecha o banco. |
| `existsBy...` (consulta derivada) | `AlertaRepository` | O Spring gera um `SELECT` que devolve `true` se já existe um alerta daquele sensor, com aquele nível, depois da data informada. É o que impede o alerta duplicado. |
| `spring.jpa.hibernate.ddl-auto=none` | `application.properties` | O Hibernate não mexe na estrutura do banco: as tabelas vêm do `banco/script-oracle.sql`. Se a tabela não existir, o erro aparece na primeira gravação. |
| `PhysicalNamingStrategyStandardImpl` | `application.properties` | Faz a coluna ter o mesmo nome do atributo (`dataHora` e não `data_hora`). Por isso o script usa os nomes dos atributos. |
| Construtor `protected` nas entidades | `Medicao`, `Alerta` | O JPA precisa de um construtor sem argumentos para montar o objeto ao ler do banco. Sendo `protected`, o resto do código não consegue criar uma entidade vazia. |
| `spring.config.import` e `${DB_USER:SEU_RM}` | `application.properties` | O `spring.config.import=optional:file:./credenciais.properties` faz o Spring ler também esse arquivo local (o `optional:` evita erro se ele não existir). O `${DB_USER:SEU_RM}` significa "use o valor de DB_USER; se não houver, use SEU_RM". |
| `@Value("${territorio.codigo-acesso}")` | `SistemaMonitoramento` | Injeta no atributo um valor do `application.properties`, do mesmo jeito que o `@Autowired` injeta um objeto. |
| `@Spy` | `ServicoMonitoramentoTest` | Usa a `FabricaPrevisao` **real** dentro do teste, enquanto os repositórios são falsos (`@Mock`). O `@InjectMocks` coloca os três dentro do serviço. |
| `Locale.forLanguageTag("pt-BR")` | `ServicoMonitoramento.exportarHistoricoCsv` | Faz o `String.format` escrever `12,5` (vírgula decimal) no CSV, como o Excel em português espera. |
| Marca `\uFEFF` (BOM) no início do CSV | `ServicoMonitoramento.exportarHistoricoCsv` | Caractere invisível que avisa o Excel que o arquivo está em UTF-8, para os acentos aparecerem corretamente. |

---

## 7. Reflexão crítica

**Quando o sistema deve se calar?** Com poucas leituras, uma "previsão" é só o último valor disfarçado de ciência. Por isso cada estratégia exige um mínimo de leituras no banco (5 para a média móvel e 6 para a comparação). Dados velhos também não servem: a chuva de ontem não diz nada sobre a enxurrada de hoje. Por isso o sistema só usa leituras dos últimos 60 minutos. Nesses casos ele avisa que se calou e **não emite alerta**. Preferimos o silêncio a um falso alarme, assim como preferimos não repetir o mesmo alerta a cada consulta. Alarmes falsos ou repetidos ensinam a comunidade a ignorar o sistema justamente no dia em que ele acertar. O silêncio também tem custo: nos primeiros minutos de uma tempestade o sistema ainda não "sabe" nada, e o olhar humano continua indispensável.

**O que os dados não representam?** Um pluviômetro mede a chuva *naquele ponto*. As chuvas de verão em São Paulo são muito localizadas, e pode cair uma tempestade a 1 km dali sem que o sensor registre nada. O nível do córrego em um trecho não mostra uma boca de lobo entupida na rua de cima, e uma sonda de umidade não representa a encosta inteira. Os dados também não mostram **quem** é mais vulnerável (idosos, pessoas com deficiência, casas sem segundo andar), nem o lixo acumulado ou as obras de drenagem. Além disso, nossos valores são simulados: servem para provar a arquitetura, não para calibrar limiares reais. Isso exigiria séries históricas oficiais (CGE, INMET) e validação com a Defesa Civil.

**Por que o alerta apoia a decisão em vez de decidir?** Porque quem conhece o território são as pessoas. O sistema enxerga cinco números, mas a agente comunitária sabe que a rua X alaga antes de todas e que a família Y precisa de ajuda para sair. Por isso o alerta **explica** o porquê (sensor, estratégia, valor previsto e limite estourado) e **sugere** uma ação, mas não dispara evacuação nem sirene sozinho. Um modelo simples erra, e decisões que mexem com a vida das pessoas precisam de responsabilidade humana, conferência no local e contexto. O papel do sistema é ganhar tempo e organizar a informação para que a decisão humana seja melhor e mais rápida.

---

## 8. Uso de IA

**Onde usamos:** usamos IA generativa (Claude, da Anthropic) para analisar o enunciado e montar a checklist de requisitos, sugerir a arquitetura a partir dos exemplos das Aulas 11 a 15, gerar uma primeira versão do código, dos testes e deste README, e aplicar as correções apontadas nas revisões.

**Como revisamos:** cada integrante revisou a parte do seu commit, rodou o projeto no IntelliJ conectado ao Oracle da FIAP e conferiu o funcionamento pelo menu: leituras gravadas, alertas, exportação do CSV e o SQL gerado pelo Hibernate no console (com show-sql=true). Quando não entendíamos um trecho, como o synchronized do Singleton, procurávamos o conceito nas aulas antes de manter o código.

- **Guilherme (domínio e exceções):** Revisei as classes de dominio/, rodei o MedicaoTest e o PrevisaoMediaMovelTest e conferi as contas da média móvel na mão. Revi as Aulas 8 e 9 para relembrar a diferença entre a interface Sensor e a classe abstrata SensorBase. Questionei o enum dos níveis de alerta, que não foi visto em aula, e o código repetido nos sensores, o que levou à criação da SensorBase.
- **Fernando (persistência e serviços):** Rodei o script-oracle.sql no Oracle da FIAP, configurei o application.properties com meu RM e confirmei que os alertas não se repetiam. Estudei como o Spring gera o SQL a partir do nome do método e para que serve o synchronized, que encontrei na Aula 15. Troquei o banco de H2 para o Oracle e questionei a justificativa do Singleton.
- **Raphael (menu e README):** Rodei o sistema no IntelliJ, passei pelas cinco opções do menu e testei entradas erradas para confirmar que o programa não fecha. Estudei como o CommandLineRunner inicia o menu depois que o Spring sobe. Pedi a troca das concatenações por String.format e printf e reescrevi o “Como revisamos”.

**Ajustes finais com IA (após a revisão):** pedimos uma revisão comparando o projeto com o enunciado. A partir dela: as credenciais saíram do `application.properties` para o `credenciais.properties` (fora do Git); o script SQL foi movido para `banco/` com instruções e consultas de conferência; o código de acesso deixou de ter getter público; e desligamos o banner e os logs do Spring no console.

**O que mudamos ou recusamos da IA:**
1. **Banco de dados:** a IA propôs H2 em arquivo. Trocamos para o **Oracle da FIAP**, com a configuração da Aula 13, o driver ojdbc11, ddl-auto=none e um script SQL próprio para criar as tabelas.
2. **Material das aulas como critério:** exigimos que o código usasse só o que foi visto em aula e, no resto, a forma mais básica (for, while, String.format, System.out.printf). Por isso trocamos o enum dos níveis de alerta por constantes String, como as categorias do Produto, e removemos um método static dentro de interface.
3. **Tecnologia:** testamos uma versão com JDBC puro e DAOs (Aula 12), mas voltamos para **Spring Boot + JPA** (Aulas 13 a 15), mantendo as pastas da estrutura sugerida no enunciado.
4. **Escopo:** recusamos classes extras que a IA tinha criado e que não eram pedidas, como um Builder de relatório e interfaces auxiliares. Ficaram só as que resolvem requisitos ou problemas reais.
5. **Justificativa do Singleton:** a IA justificou o getInstancia() dizendo que a fábrica e o menu não eram beans do Spring, o que estava errado. Corrigimos o README com o motivo verdadeiro: a estrutura pede o Singleton clássico da Aula 14.
6. **Problemas que encontramos na revisão e pedimos para corrigir:**
   - alertas duplicados quando a previsão rodava várias vezes;
   - alertas gerados a partir de leituras antigas;
   - status do ponto definido por sorteio;
   - código repetido nos três sensores (resolvido com a classe abstrata SensorBase);
   - método alterarLimiares com 6 parâmetros (passou a receber um objeto Limiar).

---

## 9. Divisão de tarefas

**Turma:** 2CCPO

| Integrante (RM) | Responsabilidade |
|---|---|
| Bento Donato Garcia — RM561621 | Domínio, exceções e testes do domínio |
| Enzo Ribeiro Domingues Piazentin — RM564216 | Persistência, serviços e testes do serviço |
| Guilherme Domingues Califoni — RM565157 | Menu, integração com Spring Boot e documentação |

Todos os integrantes fazem commits no GitHub, com mensagens claras.