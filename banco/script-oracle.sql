-- =====================================================================
--  Territory Predictive Insights - GS 2º/2026 - POO
--  Script de criação das tabelas no ORACLE
-- =====================================================================
--  Como usar:
--   1) Conecte no Oracle (ex.: SQL Developer, host oracle.fiap.com.br,
--      porta 1521, SID ORCL) com o MESMO usuário configurado no projeto.
--   2) Execute este script inteiro (no SQL Developer: tecla F5 - Run Script).
--   3) Rode o sistema: as medições e os alertas passam a ser gravados aqui.
--
--  Observações:
--   - Os nomes das colunas são iguais aos atributos das classes Java
--     (Medicao e Alerta), por causa da PhysicalNamingStrategyStandardImpl
--     configurada no application.properties.
--   - O ID é gerado pelo próprio Oracle (IDENTITY), o mesmo que o
--     @GeneratedValue(strategy = GenerationType.IDENTITY) das entidades.
--   - CREATE TABLE não precisa de COMMIT.
-- =====================================================================

-- ---------------------------------------------------------------------
--  (OPCIONAL) Recomeçar do zero: apaga as tabelas e TODOS os dados.
--  Tire os comentários das duas linhas abaixo somente se quiser isso.
-- ---------------------------------------------------------------------
-- DROP TABLE alertas;
-- DROP TABLE medicoes;

-- ---------------------------------------------------------------------
--  MEDICOES: cada leitura válida de sensor (classe Medicao)
-- ---------------------------------------------------------------------
CREATE TABLE medicoes (
    id           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sensorCodigo VARCHAR2(20 CHAR)  NOT NULL,
    tipoSensor   VARCHAR2(20 CHAR)  NOT NULL,
    regiao       VARCHAR2(80 CHAR)  NOT NULL,
    valor        BINARY_DOUBLE      NOT NULL,
    unidade      VARCHAR2(10 CHAR)  NOT NULL,
    dataHora     TIMESTAMP          NOT NULL
);

-- ---------------------------------------------------------------------
--  ALERTAS: cada alerta emitido (classe Alerta)
-- ---------------------------------------------------------------------
CREATE TABLE alertas (
    id                 NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sensorCodigo       VARCHAR2(20 CHAR) NOT NULL,
    regiao             VARCHAR2(80 CHAR) NOT NULL,
    nivel              VARCHAR2(10 CHAR) NOT NULL,
    valorPrevisto      BINARY_DOUBLE     NOT NULL,
    limiteUltrapassado BINARY_DOUBLE     NOT NULL,
    unidade            VARCHAR2(10 CHAR) NOT NULL,
    estrategia         VARCHAR2(60 CHAR) NOT NULL,
    dataHora           TIMESTAMP         NOT NULL
);

-- ---------------------------------------------------------------------
--  CONSULTAS PARA CONFERIR (rode depois de usar o sistema)
-- ---------------------------------------------------------------------
-- Tabelas criadas no seu usuário:
--   SELECT table_name FROM user_tables WHERE table_name IN ('MEDICOES', 'ALERTAS');
--
-- Quantas medições por sensor:
--   SELECT sensorCodigo, COUNT(*) AS total FROM medicoes GROUP BY sensorCodigo ORDER BY sensorCodigo;
--
-- Últimos alertas emitidos:
--   SELECT nivel, sensorCodigo, valorPrevisto, limiteUltrapassado, dataHora
--     FROM alertas ORDER BY id DESC;
