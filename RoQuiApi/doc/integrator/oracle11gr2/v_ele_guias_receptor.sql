--------------------------------------------------------
--  DDL for View V_ELE_GUIAS_RECEPTOR
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_guias_receptor (
    "ID",
    "CODIGO",
    "NUMERO",
    "DOCUMENTO",
    "RAZON_SOCIAL",
    "DIRECCION",
    "MOTIVO_TRASLADO",
    "CODIGO_DOCUMENTO",
    "NUMERO_DOCUMENTO",
    "AUTORIZACION_DOCUMENTO",
    "FECHA_DOCUMENTO"
) AS
    SELECT
        ROWNUM                                                                                AS id,
        dd.cod_documento                                                                      AS codigo,
        to_char(dd.num_despacho, 'fm000000000000000')                                          AS numero,
        c.documento,
        c.razon_social                                                                        AS razon_social,
        c.direccion,
        CAST('Venta de productos' AS VARCHAR2(18))                                            AS motivo_traslado,
        CAST('01' AS VARCHAR2(2))                                                           AS codigo_documento,
        decode(substr(to_char(f.num_factura, 'fm000000000000000'), 1, 3), '000', '001', substr(to_char(f.num_factura, 'fm000000000000000'),
        1, 3))
        || '-'
        || decode(substr(to_char(f.num_factura, 'fm000000000000000'), 4, 3), '000', '001', substr(to_char(f.num_factura, 'fm000000000000000'),
        4, 3))
        || '-'
        || to_char(substr(to_char(f.num_factura, 'fm000000000000000'), 7, 9))                AS numero_documento,
        (
            SELECT
                e.numero_autorizacion
            FROM
                ele_documentos_electronicos e
            WHERE
                    e.codigo = 'FAC'
                AND e.numero = to_char(f.num_factura, 'fm000000000000000')
        )                                                                                     AS autorizacion_documento,
        f.fecha_factura                                                                       AS fecha_documento
    FROM
             ped_despacho_d dd
        INNER JOIN fac_factura_c    f ON dd.cod_factura = f.cod_documento
                                                 AND dd.num_factura = f.num_factura
        INNER JOIN v_cliente        c ON f.cod_cliente = c.cod_cliente
    WHERE
        dd.cod_documento = 'GUI';