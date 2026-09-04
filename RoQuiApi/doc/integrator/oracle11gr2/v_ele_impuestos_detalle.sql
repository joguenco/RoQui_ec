--------------------------------------------------------
--  DDL for View V_ELE_IMPUESTOS_DETALLE
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW v_ele_impuestos_detalle (
    "ID",
    "CODIGO",
    "NUMERO",
    "CODIGO_PRINCIPAL",
    "LINEA",
    "CODIGO_IMPUESTO",
    "CODIGO_PORCENTAJE",
    "BASE_IMPONIBLE",
    "TARIFA",
    "VALOR"
) AS
    WITH data AS (
        /*IVA  Factura Bienes*/
        SELECT DISTINCT
            TO_NUMBER(to_char(f.num_factura)
                      || to_char(d.auxiliar)
                      || '02')                                               AS id,
            f.cod_documento                                        AS codigo,
            to_char(f.num_factura, 'fm000000000000000')            AS numero,
            CAST(d.cod_articulo AS VARCHAR2(20))                   AS codigo_principal,
            d.auxiliar                                             AS linea,
            CAST('2' AS VARCHAR2(1))                               AS codigo_impuesto,
            i.legal_code                                           AS codigo_porcentaje,
            round(d.total_reg, 2)                                  AS base_imponible,
            d.porcentaje_iva                                       AS tarifa,
            decode(d.porcentaje_iva,
                   0,
                   0,
                   round(d.total_reg * d.porcentaje_iva / 100, 2)) AS valor
        FROM
                 fac_factura_c f
            INNER JOIN fac_factura_d d ON f.cod_documento = d.cod_documento
                                          AND f.num_factura = d.num_factura
            INNER JOIN inv_articulo  a ON d.cod_articulo = a.cod_articulo
            INNER JOIN inv_iva       i ON d.cod_iva = i.cod_iva
        WHERE
                nvl(f.estado, 'G') <> 'A'
            AND d.cod_bodega IS NOT NULL
            AND f.num_factura > 1001000000000
        UNION ALL
        /*IVA  Factura Servicios*/
        SELECT DISTINCT
            TO_NUMBER(to_char(f.num_factura)
                      || to_char(d.auxiliar)
                      || '02')                                               AS id,
            f.cod_documento                                        AS codigo,
            to_char(f.num_factura, 'fm000000000000000')            AS numero,
            CAST(d.cod_articulo AS VARCHAR2(20))                   AS codigo_principal,
            d.auxiliar                                             AS linea,
            CAST('2' AS VARCHAR2(1))                               AS codigo_impuesto,
            i.legal_code                                           AS codigo_porcentaje,
            round(d.total_reg, 2)                                  AS base_imponible,
            d.porcentaje_iva                                       AS tarifa,
            decode(d.porcentaje_iva,
                   0,
                   0,
                   round(d.total_reg * d.porcentaje_iva / 100, 2)) AS valor
        FROM
                 fac_factura_c f
            INNER JOIN fac_factura_d d ON f.cod_documento = d.cod_documento
                                          AND f.num_factura = d.num_factura
            INNER JOIN inv_productos a ON d.cod_articulo = a.cod_producto
            INNER JOIN inv_iva       i ON d.cod_iva = i.cod_iva
        WHERE
                nvl(f.estado, 'G') <> 'A'
            AND f.num_factura > 1001000000000
        /*ICE  Factura*/
        UNION ALL
        SELECT DISTINCT
            TO_NUMBER(to_char(f.num_factura)
                      || to_char(d.auxiliar)
                      || '03')                                    AS id,
            f.cod_documento                             AS codigo,
            to_char(f.num_factura, 'fm000000000000000') AS numero,
            CAST(a.cod_articulo AS VARCHAR2(20))        AS codigo_principal,
            d.auxiliar                                  AS linea,
            CAST('3' AS VARCHAR2(1))                    AS codigo_impuesto,
            ice.codigo                                  codigo_porcentaje,
            decode(d.porcentaje_ice, 0, 0, d.total_reg) AS base_imponible,
            d.porcentaje_ice                            AS tarifa,
            d.valor_ice                                 AS valor
        FROM
                 fac_factura_c f
            INNER JOIN fac_factura_d d ON f.cod_documento = d.cod_documento
                                          AND f.num_factura = d.num_factura
            INNER JOIN inv_articulo  a ON d.cod_articulo = a.cod_articulo
            INNER JOIN inv_ice       ice ON a.cod_ice = ice.codigo
        WHERE
                nvl(f.estado, 'G') <> 'A'
            AND f.num_factura > 1001000000000
        UNION ALL
        /*IVA  Devolución*/
        SELECT DISTINCT
            TO_NUMBER(to_char(d.num_devolucion)
                      || to_char(dd.auxiliar)
                      || '12')                                       AS id,
            d.cod_documento                                AS codigo,
            to_char(d.num_devolucion, 'fm000000000000000') AS numero,
            CAST(dd.cod_articulo AS VARCHAR2(20))          AS codigo_principal,
            dd.auxiliar                                    AS linea,
            CAST('2' AS VARCHAR2(1))                       AS codigo_impuesto,
            decode(dd.porcentaje_iva, 13, '10', 15, '4',
                   14, '3', 12, '2', 0,
                   '0', -1)                                AS codigo_porcentaje,
            round(dd.cantidad * dd.precio_unitario, 2) - round(
                pkg_info_descuento.fun_descuento('DVC', d.num_devolucion, dd.cod_articulo, dd.auxiliar),
                2
            )                                              AS base_imponible,
            dd.porcentaje_iva                              AS tarifa,
            decode(dd.porcentaje_iva,
                   0,
                   0,
                   round((dd.cantidad * dd.precio_unitario - pkg_info_descuento.fun_descuento('DVC', d.num_devolucion, dd.cod_articulo
                   , dd.auxiliar)) * dd.porcentaje_iva / 100,
                         2))                               AS valor
        FROM
                 fac_devolucion_c d
            INNER JOIN fac_devolucion_d dd ON d.cod_documento = dd.cod_documento
                                              AND d.num_devolucion = dd.num_devolucion
            INNER JOIN inv_articulo     a ON dd.cod_articulo = a.cod_articulo
            INNER JOIN inv_iva          i ON a.cod_iva = i.cod_iva
        WHERE
            d.num_devolucion > 1001000000000
        UNION ALL
        /*ICE Devolución*/
        SELECT DISTINCT
            TO_NUMBER(to_char(d.num_devolucion)
                      || to_char(dd.auxiliar)
                      || '13')                                       AS id,
            d.cod_documento                                AS codigo,
            to_char(d.num_devolucion, 'fm000000000000000') AS numero,
            CAST(dd.cod_articulo AS VARCHAR2(20))          AS codigo_principal,
            dd.auxiliar                                    AS linea,
            CAST('3' AS VARCHAR2(1))                       AS codigo_impuesto,
            ice.codigo                                     AS codigo_porcentaje,
            decode(dd.porcentaje_ice, 0, 0, NULL)          AS base_imponible,
            dd.porcentaje_ice                              AS tarifa,
            round(dd.valor_ice, 2)                         AS valor
        FROM
                 fac_devolucion_c d
            INNER JOIN fac_devolucion_d dd ON d.cod_documento = dd.cod_documento
                                              AND d.num_devolucion = dd.num_devolucion
            INNER JOIN inv_articulo     a ON dd.cod_articulo = a.cod_articulo
            INNER JOIN inv_ice          ice ON a.cod_ice = ice.codigo
        WHERE
            d.num_devolucion > 1001000000000
        UNION ALL
            /*IVA  Nota de Crédito Descuentos*/
        SELECT
            nc.num_abono                               AS id,
            nc.cod_documento                           AS codigo,
            to_char(nc.num_abono, 'fm000000000000000') AS numero,
            to_char(TO_NUMBER(nc.cod_motivo))          AS codigo_principal,
            1                                          AS linea,
            CAST('2' AS VARCHAR2(1))                   AS codigo_impuesto,
            decode(
                max(dnc.porcentaje_iva),
                15,
                '4',
                5,
                '5',
                14,
                '3',
                12,
                '2',
                0,
                '0'
            )                                          AS codigo_porcentaje,
            round(
                sum(dnc.capital - dnc.iva),
                2
            )                                          AS base_imponible,
            MAX(dnc.porcentaje_iva)                    AS tarifa,
            round(
                sum(dnc.iva),
                2
            )                                          AS valor
        FROM
                 cxc_abono_c nc
            INNER JOIN cxc_abono_d     dnc ON nc.cod_documento = dnc.cod_documento
                                          AND nc.num_abono = dnc.num_abono
            INNER JOIN cxc_tipo_motivo m ON nc.cod_motivo = m.cod_motivo
        WHERE
                nc.cod_documento = 'NCC'
            AND m.tipo_motivo = 'C'
        GROUP BY
            nc.num_abono,
            nc.cod_documento,
            to_char(nc.num_abono, 'fm000000000000000'),
            nc.cod_motivo,
            CAST('2' AS VARCHAR2(1)),
            CAST('2' AS VARCHAR2(1))
    )
    SELECT
        id,
        codigo,
        numero,
        codigo_principal,
        linea,
        codigo_impuesto,
        codigo_porcentaje,
        base_imponible,
        tarifa,
        valor
    FROM
        data;