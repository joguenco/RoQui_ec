--------------------------------------------------------
--  DDL for View V_ELE_REPORTE_NOTAS_DEBITO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_reporte_notas_debito (
    "ID",
    "CODIGO",
    "NUMERO",
    "ESTABLECIMIENTO",
    "PUNTO_EMISION",
    "SECUENCIAL",
    "FECHA",
    "DOCUMENTO",
    "RAZON_SOCIAL",
    "DOCUMENTO_MODIFICADO",
    "TOTAL",
    "CORREO_ELECTRONICO",
    "ESTADO"
) AS
    SELECT
        nd.id,
        nd.codigo,
        nd.numero,
        nd.establecimiento,
        nd.punto_emision,
        nd.secuencial,
        nd.fecha,
        nd.documento,
        nd.razon_social,
        nd.documento_modificado,
        nd.total_sin_impuestos    AS total,
        (
            SELECT
                i.valor
            FROM
                v_ele_informaciones i
            WHERE
                    i.nombre = 'Email'
                AND i.documento = nd.documento
                AND ROWNUM = 1
        )                         correo_electronico,
        nvl((
            SELECT
                e.estado
            FROM
                ele_documentos_electronicos e
            WHERE
                    e.codigo = nd.codigo
                AND e.numero = nd.numero
        ),
            'NO ENVIADO')         estado
    FROM
        v_ele_notas_debito nd
    WHERE
        nd.id >= 1001000000001
    ORDER BY
        nd.id DESC;