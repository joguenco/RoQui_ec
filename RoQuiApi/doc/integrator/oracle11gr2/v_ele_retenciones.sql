--------------------------------------------------------
--  DDL for View V_ELE_RETENCIONES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_retenciones (
    "ID",
    "CODIGO",
    "NUMERO",
    "CODIGO_DOCUMENTO",
    "ESTABLECIMIENTO",
    "PUNTO_EMISION",
    "SECUENCIAL",
    "FECHA",
    "TIPO_DOCUMENTO",
    "DOCUMENTO",
    "RAZON_SOCIAL",
    "TIPO_SUJETO",
    "RELACIONADO",
    "PERIODO_FISCAL",
    "DIRECCION_ESTABLECIMIENTO"
) AS
    SELECT
        r.num_retencion                                                                     AS id,
        CAST('RET' AS VARCHAR2(3))                                                        AS codigo,
        to_char(r.num_retencion, 'fm000000000000000')                                        AS numero,
        CAST('07' AS VARCHAR2(2))                                                         codigo_documento,
        substr(to_char(r.num_retencion, 'fm000000000000000'), 1, 3)                         establecimiento,
        substr(to_char(r.num_retencion, 'fm000000000000000'), 4, 3)                         punto_emision,
        to_char(substr(to_char(r.num_retencion, 'fm000000000000000'), 7, 9))                secuencial,
        trunc(r.fecha_retencion)                                                            AS fecha,
        decode(p.documento, '9999999999999', '07', decode(length(p.documento), 13, '04', 10, '05',
                                                          '06'))                                                                              AS tipo_documento,
        p.documento,
        p.razon_social,
        decode(p.tipo_persona, 'N', '01', '02') as tipo_sujeto,
        CAST(p.relacionada AS VARCHAR2(2)) as relacionado,
        to_char(r.fiscal_period, 'mm/rrrr')                                               AS periodo_fiscal,
        (
            SELECT
                e.direccion
            FROM
                v_ele_establecimientos e
            WHERE
                e.establecimiento = substr(to_char(r.num_retencion, 'fm000000000000000'), 1, 3)
        )                                                                                    AS direccion_establecimiento
    FROM
             ban_retencion_c r
        INNER JOIN v_proveedor p ON r.cod_proveedor = p.cod_proveedor
    WHERE
        r.num_retencion > 1000000000000;