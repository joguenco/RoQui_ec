--------------------------------------------------------
--  DDL for View V_ELE_REPORTE_LIQUIDACIONES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_reporte_liquidaciones (
    "ID",
    "CODIGO",
    "NUMERO",
    "ESTABLECIMIENTO",
    "PUNTO_EMISION",
    "SECUENCIAL",
    "FECHA",
    "TOTAL_SIN_IVA",
    "TOTAL_CON_IVA",
    "IVA",
    "DESCUENTOS",
    "TOTAL",
    "DOCUMENTO",
    "RAZON_SOCIAL",
    "CORREO_ELECTRONICO",
    "ESTADO"
) AS
    SELECT
        l.id,
        l.codigo,
        l.numero,
        l.establecimiento,
        l.punto_emision,
        l.secuencial,
        l.fecha,
        l.total_sin_iva,
        l.total_con_iva,
        l.iva,
        l.descuentos,
        l.total,
        l.documento,
        l.razon_social,
        (
            SELECT
                i.valor
            FROM
                v_ele_informaciones i
            WHERE
                    i.nombre = 'Email'
                AND i.documento = l.documento
                AND ROWNUM = 1
        )                   correo_electronico,
        nvl((
            SELECT
                e.estado
            FROM
                ele_documentos_electronicos e
            WHERE
                    e.codigo = l.codigo
                AND e.numero = l.numero
        ),
            'NO ENVIADO')   estado
    FROM
        v_ele_liquidaciones l
    WHERE
        to_number(to_char(l.fecha, 'rrrrmm')) >= 202106
    ORDER BY
        l.id DESC;