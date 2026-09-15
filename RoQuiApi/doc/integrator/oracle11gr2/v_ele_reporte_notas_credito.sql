--------------------------------------------------------
--  DDL for View V_ELE_REPORTE_NOTAS_CREDITO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_reporte_notas_credito (
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
    "TOTAL_SIN_IMPUESTOS",
    "TOTAL_SIN_IVA",
    "TOTAL_CON_IVA",
    "IVA",
    "ICE",
    "CORREO_ELECTRONICO",
    "ESTADO"
) AS
    SELECT
        nc.id,
        nc.codigo,
        nc.numero,
        nc.establecimiento,
        nc.punto_emision,
        nc.secuencial,
        nc.fecha,
        nc.documento,
        nc.razon_social,
        nc.documento_modificado,
        nc.total_sin_impuestos,
        nc.total_sin_iva,
        nc.total_con_iva,
        nc.iva,
        nc.ice,
        (
            SELECT
                i.valor
            FROM
                v_ele_informaciones i
            WHERE
                    i.nombre = 'Email'
                AND i.documento = nc.documento
                AND ROWNUM = 1
        )                   correo_electronico,
        nvl((
            SELECT
                e.estado
            FROM
                ele_documentos_electronicos e
            WHERE
                    e.codigo = nc.codigo
                AND e.numero = nc.numero
        ),
            'NO ENVIADO')   estado
    FROM
        v_ele_notas_credito nc
    WHERE
        to_number(to_char(nc.fecha, 'rrrrmm')) >= 202106
    ORDER BY
        nc.id DESC;