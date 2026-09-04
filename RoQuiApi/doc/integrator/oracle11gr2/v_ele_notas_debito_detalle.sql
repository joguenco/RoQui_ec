--------------------------------------------------------
--  DDL for View V_ELE_NOTAS_DEBITO_DETALLE
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_notas_debito_detalle (
    "ID",
    "CODIGO",
    "NUMERO",
    "RAZON",
    "VALOR"
) AS
    SELECT
        d.num_documento                                                                            AS id,
        d.cod_documento                                                                            AS codigo,
        to_char(d.num_documento, 'fm000000000000000')                                              AS numero,
        d.detalle                                                                                  AS razon,
        round(d.valor_documento /((fun_get_porcentaje_iva / 100) + 1), 2)               AS valor
    FROM
        cxc_doc_cobrar d
    WHERE
            d.cod_documento = 'NDC'
        AND nvl(d.estado, 'G') <> 'A'
        AND d.num_documento > 1001000000000;