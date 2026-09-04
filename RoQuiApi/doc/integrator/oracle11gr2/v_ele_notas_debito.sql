--------------------------------------------------------
--  DDL for View V_ELE_NOTAS_DEBITO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_notas_debito (
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
    "DOCUMENTO_MODIFICADO",
    "MODIFICADO",
    "FECHA_MODIFICADO",
    "TOTAL_SIN_IMPUESTOS",
    "TOTAL_SIN_IVA",
    "TOTAL_CON_IVA",
    "IVA",
    "DIRECCION_ESTABLECIMIENTO"
) AS
    SELECT
        d.num_documento                                                                                                              AS id,
        d.cod_documento                                                                                                              AS codigo,
        to_char(d.num_documento, 'fm000000000000000')                                                                                  AS numero,
        CAST('05' AS VARCHAR2(2))                                                                                                    AS codigo_documento,
        substr(to_char(d.num_documento, 'fm000000000000000'), 1, 3)                                                                  AS establecimiento,
        substr(to_char(d.num_documento, 'fm000000000000000'), 4, 3)                                                                  AS punto_emision,
        to_char(substr(to_char(d.num_documento, 'fm000000000000000'), 7, 9))                                                         AS secuencial,
        trunc(d.fecha_emision)                                                                                                       AS fecha,
        decode(c.cod_documento, 1, '04', 2, '05', 3, '06', 4, '07', '08')                                                                                                                        AS tipo_documento,
        c.documento,
        c.razon_social,
        CAST('01' AS VARCHAR2(2))                                                                                                    AS documento_modificado,
        m.numero                                                                                                                     AS modificado,
        (
            SELECT
                f.fecha_factura
            FROM
                fac_factura_c f
            WHERE
                    f.cod_documento = 'FAC'
                AND f.num_factura = m.numero_modificado
        )                                                                                                                            AS fecha_modificado,
        round(d.valor_documento /((fun_get_porcentaje_iva / 100) + 1), 2)                                                 AS total_sin_impuestos,
        0                                                                                                                            AS total_sin_iva,
        round(d.valor_documento /((fun_get_porcentaje_iva / 100) + 1), 2)                                                 AS total_con_iva,
        round((d.valor_documento * fun_get_porcentaje_iva) /(fun_get_porcentaje_iva + 100), 2)                 AS iva,
        (
            SELECT
                e.direccion
            FROM
                v_ele_establecimientos e
            WHERE
                e.establecimiento = substr(to_char(d.num_documento, 'fm000000000000000'), 1, 3)
        )                                                                                                                            AS direccion_establecimiento
    FROM
             cxc_doc_cobrar d
        INNER JOIN v_cliente                     c ON d.cod_cliente = c.cod_cliente
        INNER JOIN cxc_documentos_modificados    m ON m.codigo_documento = d.cod_documento
                                                              AND m.numero_documento = d.num_documento
    WHERE
            d.cod_documento = 'NDC'
        AND nvl(d.estado, 'G') <> 'A'
        AND d.num_documento > 1001000000000;
