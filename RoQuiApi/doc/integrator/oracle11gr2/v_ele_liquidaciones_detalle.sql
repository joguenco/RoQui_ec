--------------------------------------------------------
--  DDL for View V_ELE_LIQUIDACIONES_DETALLE
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_liquidaciones_detalle (
    "ID",
    "ID_CONTRIBUYENTE",
    "CODIGO",
    "NUMERO",
    "CODIGO_PRINCIPAL",
    "DESCRIPCION",
    "CANTIDAD",
    "UNIDAD",
    "PRECIO_UNITARIO",
    "CODIGO_PORCENTAJE",
    "PORCENTAJE_IVA",
    "VALOR_IVA",
    "DESCUENTO",
    "PRECIO_TOTAL_SIN_IMPUESTO"
) AS
    WITH data AS (
        SELECT
            to_number(to_char(e.num_documento)
                      || '0')                                                                                         AS id,
            1                                                                                               AS id_contribuyente,
            CAST('LIQ' AS VARCHAR2(3))                                                                      AS codigo,
            replace(e.referencia, '-', '')                                                                  AS numero,
            CAST(e.num_documento AS VARCHAR2(10))                                                           AS codigo_principal,
            e.detalle                                                                                       AS descripcion,
            1                                                                                               AS cantidad,
            null    as unidad,
            round(e.base_imponible_cero + e.base_imponible, 2)                                              AS precio_unitario,
            decode(round(decode(e.base_imponible, 0, 0, e.monto_iva * 100 / e.base_imponible), 0), 
            5, '5', 13, '10', 15, '4', 14, '3', 12, '2', -1)                                                                                  AS codigo_porcentaje,
            round(decode(e.base_imponible, 0, 0, e.monto_iva * 100 / e.base_imponible), 0)                  AS porcentaje_iva,
            round(e.monto_iva, 2)                                                                           AS valor_iva,
            0                                                                                               AS descuento,
            round(e.base_imponible_cero + e.base_imponible, 2)                                              AS precio_total_sin_impuesto
        FROM
                 ban_egreso e
            INNER JOIN v_proveedor p ON e.cod_empresa = p.cod_empresa
                                                   AND e.cod_proveedor = p.cod_proveedor
        WHERE
            e.cod_tipocomprobante = 3
        UNION ALL
        SELECT
            to_number(to_char(e.num_documento)
                      || '1')                                                                                             AS id,
            1                                                                                                   AS id_contribuyente,
            CAST('LIQ' AS VARCHAR2(3))                                                                          AS codigo,
            replace(e.referencia, '-', '')                                                                      AS numero,
            CAST(e.num_documento AS VARCHAR2(10))                                                               AS codigo_principal,
            e.detalle                                                                                           AS descripcion,
            1                                                                                                   AS cantidad,
            null    as unidad,
            round(e.base_imponible_cero + e.base_imponible, 2)                                                  AS precio_unitario,
            decode(round(decode(e.base_imponible, 0, 0, e.iva_documento * 100 / e.base_imponible), 0), 
            5, '5', 13, '10', 15, '4', 14, '3', 12, '2', -1)                                                                                         AS codigo_porcentaje,
            round(decode(e.base_imponible, 0, 0, e.iva_documento * 100 / e.base_imponible), 0)                  AS porcentaje_iva,
            round(e.iva_documento, 2)                                                                           AS valor_iva,
            0                                                                                                   AS descuento,
            round(e.base_imponible_cero + e.base_imponible, 2)                                                  AS precio_total_sin_impuesto
        FROM
                 cxp_doc_pagar e
            INNER JOIN v_proveedor p ON e.cod_empresa = p.cod_empresa
                                                   AND e.cod_proveedor = p.cod_proveedor
        WHERE
            e.cod_tipocomprobante = 3
        UNION ALL
        SELECT
            to_number(to_char(l.num_documento)
                      || '2'
                      || ld.cod_articulo)                                             AS id,
            1                                                               AS id_contribuyente,
            CAST('LIQ' AS VARCHAR2(3))                                      AS codigo,
            replace(l.referencia, '-', '')                                  AS numero,
            CAST(ld.cod_articulo AS VARCHAR2(10))                           AS codigo_principal,
            a.nombre_articulo                                               AS descripcion,
            ld.cantidad                                                     AS cantidad,
            ld.cod_unidad                                                   as unidad,
            round(ld.costo_unitario, 2)                                     AS precio_unitario,
            decode(ld.porcentaje_iva, 5, '5', 13, '10', 15, '4', 14, '3', 12, '2', -1)                                                  AS codigo_porcentaje,
            ld.porcentaje_iva,
            round(ld.costo_total *(ld.porcentaje_iva / 100), 2)             AS valor_iva,
            0                                                               AS descuento,
            ld.valor_compra                                                 AS precio_total_sin_impuesto
        FROM
                 inv_movimiento_dtll ld
            INNER JOIN inv_movimiento_cab    l ON l.cod_empresa = ld.cod_empresa
                                                          AND l.cod_documento = ld.cod_documento
                                                          AND l.num_documento = ld.num_documento
            INNER JOIN inv_articulo          a ON ld.cod_empresa = a.cod_empresa
                                                    AND ld.cod_articulo = a.cod_articulo
        WHERE
                ld.cod_empresa = '01'
            AND ld.cod_documento = 'ENI'
            AND l.cod_tipocomprobante = 3
    )
    SELECT
        rownum as id,
        id_contribuyente,
        codigo,
        numero,
        codigo_principal,
        descripcion,
        cantidad,
        unidad,
        precio_unitario,
        codigo_porcentaje,
        porcentaje_iva,
        valor_iva,
        descuento,
        precio_total_sin_impuesto
    FROM
        data;