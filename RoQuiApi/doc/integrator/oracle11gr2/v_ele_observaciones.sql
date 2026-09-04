--------------------------------------------------------
--  DDL for View V_ELE_OBSERVACIONES
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW v_ele_observaciones (
    "ID",
    "CODIGO",
    "NUMERO",
    "NOMBRE",
    "VALOR"
) AS
    WITH data AS (
        SELECT
            cod_documento                          AS codigo,
            to_char(num_pago, 'fm000000000000000') AS numero,
            CAST('Observación' AS VARCHAR2(36))    AS nombre,
            detalle                                AS valor
        FROM
            cxc_pago_contado
        WHERE
                num_pago > 1001000000000
            AND cod_documento = 'FAC'
            AND detalle IS NOT NULL
            AND detalle <> 'CONTADO'
        UNION ALL
        SELECT
            codigo,
            to_char(numero, 'fm000000000000000') AS numero,
            propiedad                            AS nombre,
            valor
        FROM
            v_fac_transportes
        WHERE
            numero >= 1002000000000
        UNION ALL
        SELECT
            codigo,
            to_char(numero, 'fm000000000000000') AS numero,
            propiedad                            AS nombre,
            valor
        FROM
            fac_informaciones
        WHERE
            numero >= 1002000000000
    )
    SELECT
        ROWNUM AS id,
        codigo,
        numero,
        nombre,
        valor
    FROM
        data;