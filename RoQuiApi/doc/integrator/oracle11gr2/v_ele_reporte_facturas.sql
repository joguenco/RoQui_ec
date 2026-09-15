--------------------------------------------------------
--  DDL for View V_ELE_REPORTE_FACTURAS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_reporte_facturas (
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
    "ICE",
    "DESCUENTOS",
    "TOTAL",
    "DOCUMENTO",
    "RAZON_SOCIAL",
    "CORREO_ELECTRONICO",
    "ESTADO"
) AS
    SELECT
        f.id,
        f.codigo,
        f.numero,
        f.establecimiento,
        f.punto_emision,
        f.secuencial,
        f.fecha,
        f.total_sin_iva,
        f.total_con_iva,
        f.iva,
        f.ice,
        f.descuentos,
        f.total,
        f.documento,
        f.razon_social,
        (
            SELECT
                i.valor
            FROM
                v_ele_informaciones i
            WHERE
                    i.nombre = 'Email'
                AND i.documento = f.documento
                AND ROWNUM = 1
        )                   correo_electronico,
        nvl((
            SELECT
                e.estado
            FROM
                ele_documentos_electronicos e
            WHERE
                    e.codigo = f.codigo
                AND e.numero = f.numero
        ),
            'NO ENVIADO')   estado
    FROM
        v_ele_facturas f
    WHERE
        to_number(to_char(f.fecha, 'rrrrmm')) >= 202106
    ORDER BY
        f.id DESC;