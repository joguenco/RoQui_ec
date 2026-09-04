--------------------------------------------------------
--  DDL for View V_ELE_LIQUIDACIONES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_liquidaciones (
    "ID",
    "CODIGO",
    "NUMERO",
    "CODIGO_DOCUMENTO",
    "ESTABLECIMIENTO",
    "PUNTO_EMISION",
    "SECUENCIAL",
    "FECHA",
    "TOTAL_SIN_IVA",
    "TOTAL_CON_IVA",
    "IVA",
    "DESCUENTOS",
    "TOTAL",
    "TIPO_DOCUMENTO",
    "DOCUMENTO",
    "RAZON_SOCIAL",
    "DIRECCION",
    "DIRECCION_ESTABLECIMIENTO"
) AS
    WITH data AS (
        SELECT
            to_number(to_char(e.num_documento)
                      || '0')                                                                     AS id,
            CAST('LIQ' AS VARCHAR2(3))                                                  AS codigo,
            replace(e.referencia, '-', '')                                              AS numero,
            CAST('03' AS VARCHAR2(2))                                                   codigo_documento,
            substr(replace(e.referencia, '-', ''), 1, 3)                                establecimiento,
            substr(replace(e.referencia, '-', ''), 4, 3)                                punto_emision,
            substr(replace(e.referencia, '-', ''), 7, 9)                                secuencial,
            trunc(e.fecha_emision)                                                      AS fecha,
            round(e.base_imponible_cero, 2)                                             AS total_sin_iva,
            round(e.base_imponible, 2)                                                  AS total_con_iva,
            round(e.monto_iva, 2)                                                       AS iva,
            round(0, 2)                                                                 AS descuentos,
            round(e.base_imponible_cero + e.base_imponible + e.monto_iva, 2)            AS total,
            decode(p.cod_documento, 2, '05', '06')                                      AS tipo_documento,
            p.documento,
            p.razon_social,
            p.direccion,
            (
                SELECT
                    e.direccion
                FROM
                    v_ele_establecimientos e
                WHERE
                    e.establecimiento = substr(replace(e.referencia, '-', ''), 1, 3)
            )                                                                           AS direccion_establecimiento
        FROM
                 ban_egreso e
            INNER JOIN v_proveedor p ON e.cod_empresa = p.cod_empresa
                                                   AND e.cod_proveedor = p.cod_proveedor
        WHERE
            e.cod_tipocomprobante = 3
        UNION ALL
        SELECT
            to_number(to_char(e.num_documento)
                      || '1')                                                                         AS id,
            CAST('LIQ' AS VARCHAR2(3))                                                      AS codigo,
            replace(e.referencia, '-', '')                                                  AS numero,
            CAST('03' AS VARCHAR2(2))                                                       codigo_documento,
            substr(replace(e.referencia, '-', ''), 1, 3)                                    establecimiento,
            substr(replace(e.referencia, '-', ''), 4, 3)                                    punto_emision,
            substr(replace(e.referencia, '-', ''), 7, 9)                                    secuencial,
            trunc(e.fecha_emision_doc)                                                      AS fecha,
            round(e.base_imponible_cero, 2)                                                 AS total_sin_iva,
            round(e.base_imponible, 2)                                                      AS total_con_iva,
            round(e.iva_documento, 2)                                                       AS iva,
            round(0, 2)                                                                     AS descuentos,
            round(e.base_imponible_cero + e.base_imponible + e.iva_documento, 2)            AS total,
            decode(p.cod_documento, 2, '05', '06')                                          AS tipo_documento,
            p.documento,
            p.razon_social,
            p.direccion,
            (
                SELECT
                    e.direccion
                FROM
                    v_ele_establecimientos e
                WHERE
                    e.establecimiento = substr(replace(e.referencia, '-', ''), 1, 3)
            )                                                                               AS direccion_establecimiento
        FROM
                 cxp_doc_pagar e
            INNER JOIN v_proveedor p ON e.cod_empresa = p.cod_empresa
                                                   AND e.cod_proveedor = p.cod_proveedor
        WHERE
            e.cod_tipocomprobante = 3
        UNION ALL
        SELECT
            to_number(to_char(e.num_documento)
                      || '1')                                                   AS id,
            CAST('LIQ' AS VARCHAR2(3))                                AS codigo,
            replace(e.referencia, '-', '')                            AS numero,
            CAST('03' AS VARCHAR2(2))                                 codigo_documento,
            substr(replace(e.referencia, '-', ''), 1, 3)              establecimiento,
            substr(replace(e.referencia, '-', ''), 4, 3)              punto_emision,
            substr(replace(e.referencia, '-', ''), 7, 9)              secuencial,
            trunc(e.fech_emis_doc)                                    AS fecha,
            round(e.t_sin_iva, 2)                                     AS total_sin_iva,
            round(e.t_con_iva, 2)                                     AS total_con_iva,
            round(e.iva, 2)                                           AS iva,
            round(0, 2)                                               AS descuentos,
            round(e.t_sin_iva + e.t_con_iva + e.iva, 2)               AS total,
            decode(p.cod_documento, 2, '05', '06')                    AS tipo_documento,
            p.documento,
            p.razon_social,
            p.direccion,
            (
                SELECT
                    e.direccion
                FROM
                    v_ele_establecimientos e
                WHERE
                    e.establecimiento = substr(replace(e.referencia, '-', ''), 1, 3)
            )                                                         AS direccion_establecimiento
        FROM
                 inv_movimiento_cab e
            INNER JOIN v_proveedor p ON e.cod_empresa = p.cod_empresa
                                                   AND e.cod_proveedor = p.cod_proveedor
        WHERE
                e.cod_documento = 'ENI'
            AND e.cod_movimiento = '10'
            AND e.cod_tipocomprobante = 3
    )
    SELECT
        id,
        codigo,
        numero,
        codigo_documento,
        establecimiento,
        punto_emision,
        secuencial,
        fecha,
        total_sin_iva,
        total_con_iva,
        iva,
        descuentos,
        total,
        tipo_documento,
        documento,
        razon_social,
        direccion,
        direccion_establecimiento
    FROM
        data
    WHERE
        to_number(numero) > 1002000000000;