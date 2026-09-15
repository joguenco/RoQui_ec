--------------------------------------------------------
--  DDL for View V_ELE_REPORTE_GUIAS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_reporte_guias (
    "ID",
    "CODIGO",
    "NUMERO",
    "ESTABLECIMIENTO",
    "PUNTO_EMISION",
    "SECUENCIAL",
    "FECHA",
    "RAZON_SOCIAL_TRANSPORTISTA",
    "DOCUMENTO",
    "PLACA",
    "ESTADO"
) AS
    SELECT
        g.id,
        g.codigo,
        g.numero,
        g.establecimiento,
        g.punto_emision,
        g.secuencial,
        g.fecha,
        g.razon_social_transportista,
        g.documento,
        g.placa,
        nvl((
            SELECT
                e.estado
            FROM
                ele_documentos_electronicos e
            WHERE
                    e.codigo = g.codigo
                AND e.numero = g.numero
        ),
            'NO ENVIADO') estado
    FROM
        v_ele_guias g
    WHERE
        to_number(to_char(g.fecha, 'rrrrmm')) >= 202106
    ORDER BY
        g.id DESC;