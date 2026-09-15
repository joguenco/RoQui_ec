CREATE TABLE ele_documentos_electronicos (
    id                  NUMBER NOT NULL ENABLE,
    codigo              VARCHAR2(20) NOT NULL ENABLE,
    numero              VARCHAR2(20) NOT NULL ENABLE,
    numero_autorizacion VARCHAR2(100),
    fecha_autorizacion  DATE,
    observacion         VARCHAR2(4000),
    estado              VARCHAR2(20),
    CONSTRAINT pk_ele_documentos_electronicos PRIMARY KEY ( id ),
    CONSTRAINT uk_ele_documentos UNIQUE ( codigo,
                                          numero )
);

CREATE SEQUENCE seq_ele_documentos INCREMENT BY 1 NOCACHE;

CREATE OR REPLACE TRIGGER tr_ele_documentos_on_in BEFORE
    INSERT ON ele_documentos_electronicos
    FOR EACH ROW
BEGIN
    SELECT
        seq_ele_documentos.NEXTVAL
    INTO :new.id
    FROM
        dual;

END;
/
ALTER TRIGGER tr_ele_documentos_on_in ENABLE;