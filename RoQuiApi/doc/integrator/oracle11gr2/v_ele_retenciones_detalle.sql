--------------------------------------------------------
--  DDL for View V_ELE_RETENCIONES_DETALLE
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_retenciones_detalle (
    "ID",
    "CODIGO",
    "NUMERO",
    "TIPO",
    "CODIGO_RETENCION",
    "BASE_IMPONIBLE",
    "PORCENTAJE",
    "VALOR_RETENIDO"
) AS
    SELECT
        ROWNUM                                                                             AS id,
        CAST('RET' AS VARCHAR2(3))                                                       AS codigo,
        to_char(dr.num_retencion, 'fm000000000000000')                                      AS numero,
        CAST(decode(tr.tipo_impuesto, 'RENTA', 1, 'IVA', 2,
                    0) AS VARCHAR2(4))                                                                AS tipo,
        decode(tr.tipo_impuesto, 'RENTA', tr.cod_sri, 'IVA', decode(porc_retencion, 10, '9', 20, '10',
                                                                    30, '1', 70, '2', 100,
                                                                    '3', '0'), '0')                                                                      AS codigo_sri,
        round(dr.valor_base, 2)                                                                      AS base_imponible,
        dr.porc_retencion                                                                  AS porcentaje,
        dr.valor_retenido
    FROM
             ban_retencion_d dr
        INNER JOIN ban_tipo_retencion tr ON dr.cod_retencion = tr.cod_retencion;