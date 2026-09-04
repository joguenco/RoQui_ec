--------------------------------------------------------
--  DDL for View V_ELE_FACTURAS_DETALLE
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW v_ele_facturas_detalle (
    id,
    codigo,
    numero,
    codigo_principal,
    codigo_auxiliar,
    linea,
    descripcion,
    cantidad,
    unidad,
    precio_unitario,
    codigo_porcentaje,
    porcentaje_iva,
    valor_iva,
    descuento,
    precio_total_sin_impuesto
) AS
    WITH data AS (
    /*IVA  Factura Bienes*/
        SELECT
            TO_NUMBER(to_char(f.num_factura)
                      || to_char(fd.auxiliar))                    AS id,
            f.cod_documento                             AS codigo,
            to_char(f.num_factura, 'fm000000000000000') AS numero,
            CAST(a.cod_articulo AS VARCHAR2(20))        AS codigo_principal,
            CAST(a.codigo_fabrica AS VARCHAR2(20))      AS codigo_auxiliar,
            fd.auxiliar                                 AS linea,
            a.nombre_articulo || ' ' || fd.detalle      AS descripcion,
            fd.cantidad                                 AS cantidad,
            fd.cod_unidad                               AS unidad,
            round((fd.precio_unitario), 4)              AS precio_unitario,
            i.legal_code                                AS codigo_porcentaje,
            fd.porcentaje_iva,
            round((((fd.cantidad *(fd.precio_unitario)) -((fd.precio_unitario * fd.cantidad) -(((fd.precio_unitario * fd.cantidad -((
            nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad))) -((nvl(fd.porc_desc_prom, 0) / 100) *((fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad))))))) * fd.porcentaje_iva / 100),
                  2)                                    AS valor_iva,
            round((fd.precio_unitario * fd.cantidad) -(((fd.precio_unitario * fd.cantidad -((nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad
            )) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -((nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad
            ))) -((nvl(fd.porc_desc_prom, 0) / 100) *((fd.precio_unitario * fd.cantidad -((nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad
            )) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -((nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad
            ))))),
                  2)                                    descuento,
            round((fd.precio_unitario * fd.cantidad) -((fd.precio_unitario * fd.cantidad) -(((fd.precio_unitario * fd.cantidad -((nvl
            (fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad))) -((nvl(fd.porc_desc_prom, 0) / 100) *((fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)))))),
                  2)                                    AS precio_total_sin_impuesto
        FROM
                 fac_factura_c f
            INNER JOIN fac_factura_d fd ON f.cod_documento = fd.cod_documento
                                           AND f.num_factura = fd.num_factura
            INNER JOIN inv_articulo  a ON fd.cod_articulo = a.cod_articulo
            INNER JOIN inv_iva       i ON fd.cod_iva = i.cod_iva
        WHERE
                f.num_factura > 1001000000000
            AND f.cod_documento = 'FAC'
            AND fd.cod_bodega IS NOT NULL
        UNION ALL
        /*IVA  Factura Servicios*/
        SELECT
            TO_NUMBER(to_char(f.num_factura)
                      || to_char(fd.auxiliar))                    AS id,
            f.cod_documento                             AS codigo,
            to_char(f.num_factura, 'fm000000000000000') AS numero,
            a.cod_producto                              AS codigo_principal,
            CAST(a.auxiliary_code AS VARCHAR2(20))      AS codigo_auxiliar,
            fd.auxiliar                                 AS linea,
            a.desc_producto
            || ' '
            || fd.detalle                               AS descripcion,
            fd.cantidad                                 AS cantidad,
            NULL                                        AS unidad,
            round((fd.precio_unitario), 6)              AS precio_unitario,
            i.legal_code                                AS codigo_porcentaje,
            fd.porcentaje_iva,
            round((((fd.cantidad *(fd.precio_unitario)) -((fd.precio_unitario * fd.cantidad) -(((fd.precio_unitario * fd.cantidad -((
            nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad))) -((nvl(fd.porc_desc_prom, 0) / 100) *((fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad))))))) * fd.porcentaje_iva / 100),
                  2)                                    AS valor_iva,
            round((fd.precio_unitario * fd.cantidad) -(((fd.precio_unitario * fd.cantidad -((nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad
            )) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -((nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad
            ))) -((nvl(fd.porc_desc_prom, 0) / 100) *((fd.precio_unitario * fd.cantidad -((nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad
            )) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -((nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad
            ))))),
                  2)                                    descuento,
            round((fd.precio_unitario * fd.cantidad) -((fd.precio_unitario * fd.cantidad) -(((fd.precio_unitario * fd.cantidad -((nvl
            (fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad))) -((nvl(fd.porc_desc_prom, 0) / 100) *((fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)) -(nvl(fd.porc_desc_pago, 0) / 100) *(fd.precio_unitario * fd.cantidad -(
            (nvl(fd.porc_desc_vol, 0) / 100) * fd.precio_unitario * fd.cantidad)))))),
                  2)                                    AS precio_total_sin_impuesto
        FROM
                 fac_factura_c f
            INNER JOIN fac_factura_d fd ON f.cod_documento = fd.cod_documento
                                           AND f.num_factura = fd.num_factura
            INNER JOIN inv_productos a ON fd.cod_articulo = a.cod_producto
            INNER JOIN inv_iva       i ON fd.cod_iva = i.cod_iva
        WHERE
                f.num_factura > 1001000000000
            AND f.cod_documento = 'FAC'
            AND a.tipo_producto = 'V'
    )
    SELECT
        id,
        codigo,
        numero,
        codigo_principal,
        codigo_auxiliar,
        linea,
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
        data
    ORDER BY
        linea;
