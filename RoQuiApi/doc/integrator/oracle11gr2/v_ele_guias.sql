--------------------------------------------------------
--  DDL for View V_ELE_GUIAS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_guias (
    "ID",
    "CODIGO",
    "NUMERO",
    "CODIGO_DOCUMENTO",
    "ESTABLECIMIENTO",
    "PUNTO_EMISION",
    "SECUENCIAL",
    "FECHA",
    "FECHA_FIN",
    "DIRECCION_PARTIDA",
    "RAZON_SOCIAL_TRANSPORTISTA",
    "TIPO_DOCUMENTO",
    "DOCUMENTO",
    "PLACA",
    "DIRECCION_ESTABLECIMIENTO"
) AS
    SELECT
        p.num_despacho                                                                                                                                                            AS id,
        p.cod_documento                                                                                                                                                           AS codigo,
        to_char(p.num_despacho, 'fm000000000000000')                                                                                                                              AS numero,
        CAST('06' AS VARCHAR2(2))                                                                                                                                                 codigo_documento,
        decode(substr(to_char(p.num_despacho, 'fm000000000000000'), 1, 3), '000', '001', substr(to_char(p.num_despacho, 'fm000000000000000'),
        1, 3))                      establecimiento,
        decode(substr(to_char(p.num_despacho, 'fm000000000000000'), 4, 3), '000', '001', substr(to_char(p.num_despacho, 'fm000000000000000'),
        4, 3))                      punto_emision,
        to_char(substr(to_char(p.num_despacho, 'fm000000000000000'), 7, 9))                                                                                                       secuencial,
        trunc(p.fecha_despacho)                                                                                                                                                   AS fecha,
        trunc(p.fecha_despacho + 5)                                                                                                                                               AS fecha_fin,
        t.direccion                                                                                                                                                               AS direccion_partida,
        t.nombres                                                                                                                                                                 AS razon_social_transportista,
        decode(t.cedula, '9999999999999', '07', decode(length(t.cedula), 13, '04', 10, '05',
                                                       '06'))                                                                                                                                                                    AS tipo_documento,
        t.cedula                                                                                                                                                                  AS documento,
        t.comentario                                                                                                                                                              AS placa,
        (
            SELECT
                e.direccion
            FROM
                v_ele_establecimientos e
            WHERE
                e.establecimiento = substr(to_char(decode(substr(to_char(p.num_despacho, 'fm000000000000000'), 1, 3), '000', '001',
                substr(to_char(p.num_despacho, 'fm000000000000000'), 1, 3)),
                                                   'fm000000000000000'),
                                           1,
                                           3)
        )                                                                                                                                                                         AS direccion_establecimiento
    FROM
             ped_despacho_c p
        INNER JOIN ped_transportista t ON p.cod_transportista = t.cod_transportista
    WHERE
            p.estado = 'C'
        AND p.cod_documento = 'GUI';