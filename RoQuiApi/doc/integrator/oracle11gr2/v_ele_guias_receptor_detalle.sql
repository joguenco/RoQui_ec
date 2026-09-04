--------------------------------------------------------
--  DDL for View V_ELE_GUIAS_RECEPTOR_DETALLE
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_guias_receptor_detalle (
    "ID",
    "CODIGO",
    "NUMERO",
    "DOCUMENTO",
    "CODIGO_ARTICULO",
    "NOMBRE_ARTICULO",
    "CANTIDAD"
) AS
    SELECT
        ROWNUM                                               AS id,
        dd.cod_documento                                     AS codigo,
        to_char(dd.num_despacho, 'fm000000000000000')         AS numero,
        c.documento,
        to_char(fd.cod_articulo)                             AS codigo_articulo,
        a.nombre_articulo,
        SUM(fd.aux_cantidad)                                 AS cantidad
    FROM
             ped_despacho_d dd
        INNER JOIN fac_factura_c    fc ON dd.cod_factura = fc.cod_documento
                                                  AND dd.num_factura = fc.num_factura
        INNER JOIN fac_factura_d    fd ON fc.cod_empresa = fd.cod_empresa
                                                  AND fc.cod_documento = fd.cod_documento
                                                  AND fc.num_factura = fd.num_factura
        INNER JOIN inv_articulo     a ON fd.cod_articulo = a.cod_articulo
        INNER JOIN v_cliente        c ON c.cod_cliente = fc.cod_cliente
    WHERE
        dd.cod_documento = 'GUI'
    GROUP BY
        ROWNUM,
        dd.cod_documento,
        dd.num_despacho,
        c.documento,
        fd.cod_articulo,
        a.nombre_articulo;