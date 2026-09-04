CREATE OR REPLACE FORCE VIEW v_ele_notas_credito (
    id,
    codigo,
    numero,
    codigo_documento,
    establecimiento,
    punto_emision,
    secuencial,
    fecha,
    tipo_documento,
    documento,
    razon_social,
    documento_modificado,
    modificado,
    fecha_modificado,
    total_sin_impuestos,
    total_modificado,
    total_sin_iva,
    total_con_iva,
    iva,
    ice,
    motivo,
    direccion_establecimiento
) AS
    WITH data AS (
        SELECT
            d.cod_documento                                                                 AS codigo,
            d.num_devolucion                                                                AS numero,
            '04'                                                                            codigo_documento,
            substr(
                to_char(d.num_devolucion, 'fm000000000000000'),
                1,
                3
            )                                                                               establecimiento,
            substr(
                to_char(d.num_devolucion, 'fm000000000000000'),
                4,
                3
            )                                                                               punto_emision,
            to_char(substr(
                to_char(d.num_devolucion, 'fm000000000000000'),
                7,
                9
            ))                                                                              secuencial,
            trunc(d.fecha_devolucion)                                                       AS fecha,
            decode(c.documento,
                   '9999999999999',
                   '07',
                   decode(
                length(c.documento),
                13,
                '04',
                10,
                '05',
                '06'
            ))                                                                              AS tipo_documento,
            c.razon_social,
            c.documento,
            '01'                                                                            AS documento_modificado,
            pkg_info_nota_credito.fun_numero_comprobante(d.cod_documento, d.num_devolucion) AS modificado,
            pkg_info_nota_credito.fun_fecha_comprobante(d.cod_documento, d.num_devolucion)  AS fecha_modificado,
            round(d.total_sin_iva + d.total_con_iva - d.descuentos, 2)                      AS total_sin_impuestos,
            round(d.total_devolucion, 2)                                                    AS total_modificado,
            round(d.total_sin_iva - pkg_info_descuento.fun_descuento_sin_iva('DVC', d.num_devolucion),
                  2)                                                                        total_sin_iva,
            round(d.total_con_iva - pkg_info_descuento.fun_descuento_iva('DVC', d.num_devolucion),
                  2)                                                                        total_con_iva,
            round(d.iva, 2)                                                                 iva,
            round(d.ice, 2)                                                                 ice,
            d.detalle                                                                       AS motivo,
            (
                SELECT
                    e.direccion
                FROM
                    v_ele_establecimientos e
                WHERE
                    e.establecimiento = substr(
                        to_char(d.num_devolucion, 'fm000000000000000'),
                        1,
                        3
                    )
            )                                                                               AS direccion_establecimiento
        FROM
                 fac_devolucion_c d
            INNER JOIN v_cliente c ON d.cod_cliente = c.cod_cliente
        WHERE
                d.num_devolucion > 1001000000000
            AND nvl(d.estado, 'G') <> 'A'
        UNION ALL
        SELECT
            nc.cod_documento                                                             AS codigo,
            nc.num_abono                                                                 AS numero,
            '04'                                                                         codigo_documento,
            substr(
                to_char(nc.num_abono, 'fm000000000000000'),
                1,
                3
            )                                                                            establecimiento,
            substr(
                to_char(nc.num_abono, 'fm000000000000000'),
                4,
                3
            )                                                                            punto_emision,
            to_char(substr(
                to_char(nc.num_abono, 'fm000000000000000'),
                7,
                9
            ))                                                                           secuencial,
            trunc(nc.fecha_abono)                                                        AS fecha,
            decode(c.documento,
                   '9999999999999',
                   '07',
                   decode(
                length(c.documento),
                13,
                '04',
                10,
                '05',
                '06'
            ))                                                                           AS tipo_documento,
            c.razon_social,
            c.documento,
            '01'                                                                         AS documento_modificado,
            pkg_info_nota_credito.fun_numero_comprobante(nc.cod_documento, nc.num_abono) AS modificado,
            pkg_info_nota_credito.fun_fecha_comprobante(nc.cod_documento, nc.num_abono)  AS fecha_modificado,
            round(
                sum(ncd.capital - ncd.iva),
                2
            )                                                                            AS total_sin_impuestos,
            ( nc.total_capital )                                                         AS total_modificado,
            CASE
                WHEN SUM(ncd.iva) > 0 THEN
                    0
                ELSE
                    round(
                        sum(ncd.capital - ncd.iva),
                        2
                    )
            END                                                                          AS total_sin_iva,
            CASE
                WHEN SUM(ncd.iva) = 0 THEN
                    0
                ELSE
                    round(
                        sum(ncd.capital - ncd.iva),
                        2
                    )
            END                                                                          AS total_con_iva,
            round(
                sum(ncd.iva),
                2
            )                                                                            AS iva,
            0                                                                            AS ice,
            nc.detalle                                                                   AS motivo,
            (
                SELECT
                    e.direccion
                FROM
                    v_ele_establecimientos e
                WHERE
                    e.establecimiento = substr(
                        to_char(nc.num_abono, 'fm000000000000000'),
                        1,
                        3
                    )
            )                                                                            AS direccion_establecimiento
        FROM
                 v_cliente c
            INNER JOIN cxc_abono_c nc ON nc.cod_cliente = c.cod_cliente
            INNER JOIN cxc_abono_d ncd ON ncd.cod_documento = nc.cod_documento
                                          AND ncd.num_abono = nc.num_abono
        WHERE
                nc.cod_documento = 'NCC'
            AND nc.num_abono > 1001000000000
            AND nvl(nc.estado, 'G') <> 'A'
        GROUP BY
            nc.cod_documento,
            nc.num_abono,
            '04',
            substr(
                to_char(nc.num_abono, 'fm000000000000000'),
                1,
                3
            ),
            substr(
                to_char(nc.num_abono, 'fm000000000000000'),
                4,
                3
            ),
            to_char(substr(
                to_char(nc.num_abono, 'fm000000000000000'),
                7,
                9
            )),
            trunc(nc.fecha_abono),
            decode(c.documento,
                   '9999999999999',
                   '07',
                   decode(
                length(c.documento),
                13,
                '04',
                10,
                '05',
                '06'
            )),
            c.razon_social,
            c.documento,
            '01',
            pkg_info_nota_credito.fun_numero_comprobante(nc.cod_documento, nc.num_abono),
            pkg_info_nota_credito.fun_fecha_comprobante(nc.cod_documento, nc.num_abono),
            (
                nc.total_capital
            ),
            0,
            nc.detalle,
            ''
    )
    SELECT
        numero                                    AS id,
        codigo,
        to_char(numero, 'fm000000000000000')      AS numero,
        CAST(codigo_documento AS VARCHAR2(2))     AS codigo_documento,
        establecimiento,
        punto_emision,
        secuencial,
        fecha,
        tipo_documento,
        documento,
        razon_social,
        CAST(documento_modificado AS VARCHAR2(2)) AS documento_modificado,
        modificado,
        fecha_modificado,
        total_sin_impuestos,
        total_modificado,
        total_sin_iva,
        total_con_iva,
        iva,
        ice,
        motivo,
        direccion_establecimiento
    FROM
        data