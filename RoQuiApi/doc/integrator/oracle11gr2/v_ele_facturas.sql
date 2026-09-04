--------------------------------------------------------
--  DDL for View V_ELE_FACTURAS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_facturas (
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
    "ICE",
    "DESCUENTOS",
    "TOTAL",
    "TIPO_DOCUMENTO",
    "DOCUMENTO",
    "RAZON_SOCIAL",
    "DIRECCION",
    "GUIA_REMISION",
    "DIRECCION_ESTABLECIMIENTO"
) AS
    SELECT
        f.num_factura                                                                                                          AS id,
        f.cod_documento                                                                                                        AS codigo,
        to_char(f.num_factura, 'fm000000000000000')                                                                             AS numero,
        CAST('01' AS VARCHAR2(2))                                                                                            codigo_documento,
        substr(to_char(f.num_factura, 'fm000000000000000'), 1, 3)                                                                 establecimiento,
        substr(to_char(f.num_factura, 'fm000000000000000'), 4, 3)                                                                 punto_emision,
        to_char(substr(to_char(f.num_factura, 'fm000000000000000'), 7, 9))                                                        secuencial,
        trunc(f.fecha_factura)                                                                                                 AS fecha,
        round(f.total_sin_iva, 2) - pkg_info_descuento.fun_descuento_sin_iva('FAC', f.num_factura)                   AS total_sin_iva,
        round(f.total_con_iva, 2) - pkg_info_descuento.fun_descuento_iva('FAC', f.num_factura)                       AS total_con_iva,
        round(f.iva, 2)                                                                                                         AS iva,
        round(f.ice, 2)                                                                                                         AS ice,
        round(f.descuentos, 2)                                                                                                  AS descuentos,
        round(f.total_factura, 2)                                                                                               AS total,
        decode(c.cod_documento, 1, '04', 2, '05', 3, '06', 4, '07', '08')                                                                                                                 AS tipo_documento,
        c.documento,
        c.razon_social,
        c.direccion,
        pkg_info_factura.fun_numero_guia_remision(f.cod_documento, f.num_factura)                                    AS guia_remision,
        (
            SELECT
                e.direccion
            FROM
                v_ele_establecimientos e
            WHERE
                e.establecimiento = substr(to_char(f.num_factura, 'fm000000000000000'), 1, 3)
        )                                                                                                                      AS direccion_establecimiento
    FROM
             fac_factura_c f
        INNER JOIN v_cliente c ON f.cod_cliente = c.cod_cliente
    WHERE
            nvl(f.estado, 'G') <> 'A'
        AND f.num_factura > 1001000000000;