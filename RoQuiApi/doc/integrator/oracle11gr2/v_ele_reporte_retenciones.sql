--------------------------------------------------------
--  DDL for View V_ELE_REPORTE_RETENCIONES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_reporte_retenciones (
    "ID",
    "CODIGO",
    "NUMERO",
    "ESTABLECIMIENTO",
    "PUNTO_EMISION",
    "SECUENCIAL",
    "FECHA",
    "DOCUMENTO",
    "RAZON_SOCIAL",
    "CORREO_ELECTRONICO",
    "ESTADO"
) AS
    SELECT
        r.id,
        r.codigo,
        r.numero,
        r.establecimiento,
        r.punto_emision,
        r.secuencial,
        r.fecha,
        r.documento,
        r.razon_social,
        (
            SELECT
                i.valor
            FROM
                v_ele_informaciones i
            WHERE
                    i.nombre = 'Email'
                AND i.documento = r.documento
                AND ROWNUM = 1
        )                   correo_electronico,
        nvl((
            SELECT
                e.estado
            FROM
                ele_documentos_electronicos e
            WHERE
                    e.codigo = r.codigo
                AND e.numero = r.numero
        ),
            'NO ENVIADO')   estado
    FROM
        v_ele_retenciones r
    WHERE
        to_number(to_char(r.fecha, 'rrrrmm')) >= 201806
    ORDER BY
        r.id DESC;