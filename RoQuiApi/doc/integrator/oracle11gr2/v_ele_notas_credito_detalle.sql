--------------------------------------------------------
--  DDL for View V_ELE_NOTAS_CREDITO_DETALLE
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW v_ele_notas_credito_detalle ( "ID",
"CODIGO",
"NUMERO",
"CODIGO_INTERNO",
"LINEA",
"DESCRIPCION",
"CANTIDAD",
"PRECIO_UNITARIO",
"DESCUENTO",
"PRECIO_TOTAL_SIN_IMPUESTO",
"CODIGO_PORCENTAJE",
"PORCENTAJE_IVA",
"VALOR_IVA" ) AS
    WITH data AS (
        SELECT
            dm.cod_documento             AS codigo,
            dm.num_devolucion            AS numero,
            a.cod_articulo               AS codigo_interno,
            dd.auxiliar                  AS linea,
            a.nombre_articulo            AS descripcion,
            round(dd.cantidad, 2)        AS cantidad,
            round(dd.precio_unitario, 2) AS precio_unitario,
            round(dd.precio_unitario * dd.cantidad, 2) - round(((dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
            )) -(nvl(dd.porc_desc_pago, 0) / 100) *(dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
            ))) -((nvl(dd.porc_desc_prom, 0) / 100) *((dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
            )) -(nvl(dd.porc_desc_pago, 0) / 100) *(dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
            )))),
                                                               2)                           AS descuento,
            greatest(0,
                     round((round(dd.cantidad, 2) * round(dd.precio_unitario, 2)) -(round(dd.precio_unitario * dd.cantidad, 2) - round
                     (((dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad)) -(nvl
                     (dd.porc_desc_pago, 0) / 100) *(dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
                     ))) -((nvl(dd.porc_desc_prom, 0) / 100) *((dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
                     )) -(nvl(dd.porc_desc_pago, 0) / 100) *(dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
                     )))),
                                                                                                                                 2)),
                           2))           AS precio_total_sin_impuesto,
            decode(dd.porcentaje_iva, 13, '10', 15, '4', 14, '3', 12, '2', 0, '0')               AS codigo_porcentaje,
            dd.porcentaje_iva,
            greatest(0,
                     round((dd.porcentaje_iva / 100) *((round(dd.cantidad, 2) * round(dd.precio_unitario, 2)) -(round(dd.precio_unitario * dd.cantidad
                     , 2) - round(((dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
                     )) -(nvl(dd.porc_desc_pago, 0) / 100) *(dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
                     ))) -((nvl(dd.porc_desc_prom, 0) / 100) *((dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
                     )) -(nvl(dd.porc_desc_pago, 0) / 100) *(dd.precio_unitario * dd.cantidad -((nvl(dd.porc_desc_vol, 0) / 100) * dd.precio_unitario * dd.cantidad
                     )))),
                                                                                                                                         2
                                                                                                                                         )
                                                                                                                                         )
                                                                                                                                         )
                                                                                                                                         ,
                           2))           AS valor_iva
        FROM
                 fac_devolucion_c dm
            INNER JOIN fac_devolucion_d dd ON dm.cod_documento = dd.cod_documento
                                                        AND dm.num_devolucion = dd.num_devolucion
            INNER JOIN inv_articulo     a ON dd.cod_articulo = a.cod_articulo
        WHERE
            nvl(dm.estado, 'G') <> 'A'
        UNION ALL
        SELECT
            nc.cod_documento         AS codigo,
            nc.num_abono             AS numero,
            TO_NUMBER(nc.cod_motivo) AS codigo_interno,
            1                        AS linea,
            m.descripcion_motivo     AS descripcion,
            1                        AS cantidad,
            round(SUM(dnc.capital - dnc.iva),
                  2)                 AS precio_unitario,
            0                        AS descuento,
            round(SUM(dnc.capital - dnc.iva),
                  2)                 AS precio_total_sin_impuesto,
            decode(MAX(dnc.porcentaje_iva), 13, '10', 15, '4', 14, '3', 12, '2', 0, '0')              AS codigo_porcentaje,
            MAX(dnc.porcentaje_iva)  AS porcentaje_iva,
            round(SUM(dnc.iva),
                  2)                 AS valor_iva
        FROM
                 cxc_abono_c nc
            INNER JOIN cxc_abono_d     dnc ON nc.cod_empresa = dnc.cod_empresa
                                                    AND nc.cod_documento = dnc.cod_documento
                                                    AND nc.num_abono = dnc.num_abono
            INNER JOIN cxc_tipo_motivo m ON nc.cod_motivo = m.cod_motivo
        WHERE
                nc.cod_documento = 'NCC'
            AND m.tipo_motivo = 'C'
        GROUP BY
            nc.cod_documento,
            nc.num_abono,
            TO_NUMBER(nc.cod_motivo),
            1,
            m.descripcion_motivo,
            1,
            0
    )
    SELECT
        ROWNUM                               AS id,
        codigo,
        to_char(numero, 'fm000000000000000') AS numero,
        to_char(codigo_interno)              AS codigo_interno,
        linea,
        descripcion,
        cantidad,
        precio_unitario,
        descuento,
        precio_total_sin_impuesto,
        codigo_porcentaje,
        porcentaje_iva,
        valor_iva
    FROM
        data;