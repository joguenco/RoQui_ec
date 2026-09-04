--------------------------------------------------------
--  DDL for View V_ELE_PAGOS
--------------------------------------------------------
CREATE OR REPLACE FORCE VIEW v_ele_pagos (
    "ID",
    "CODIGO",
    "NUMERO",
    "FORMA_PAGO",
    "FORMA_PAGO_DESCRIPCION",
    "TOTAL",
    "PLAZO",
    "TIEMPO"
) AS
    WITH data AS (
        SELECT
            cod_documento                                                   AS codigo,
            to_char(num_pago, 'fm000000000000000')                          AS numero,
            CAST('01' AS VARCHAR2(2))                                       AS forma_pago,
            CAST('SIN UTILIZACION DEL SISTEMA FINANCIERO' AS VARCHAR2(100)) AS forma_pago_descripcion,
            nvl(efectivo, 0)                                                AS total,
            NULL                                                            plazo,
            NULL                                                            tiempo
        FROM
            cxc_pago_contado
        WHERE
                cod_documento = 'FAC'
            AND nvl(efectivo, 0) > 0
            AND num_pago > 1001000000000
        UNION
        SELECT
            cod_documento,
            to_char(num_pago, 'fm000000000000000'),
            '19',
            'TARJETA DE CREDITO',
            nvl(tarjeta, 0),
            NULL,
            NULL
        FROM
            cxc_pago_contado
        WHERE
                cod_documento = 'FAC'
            AND nvl(tarjeta, 0) > 0
            AND num_pago > 1001000000000
        UNION
        SELECT
            cod_documento,
            to_char(num_pago, 'fm000000000000000'),
            '20',
            'OTROS CON UTILIZACION DEL SISTEMA FINANCIERO',
            nvl(cheques, 0),
            NULL,
            NULL
        FROM
            cxc_pago_contado
        WHERE
                cod_documento = 'FAC'
            AND nvl(cheques, 0) > 0
            AND num_pago > 1001000000000
        UNION
        SELECT
            cod_documento,
            to_char(num_pago, 'fm000000000000000'),
            '20',
            'OTROS CON UTILIZACION DEL SISTEMA FINANCIERO',
            nvl(deposito, 0),
            NULL,
            NULL
        FROM
            cxc_pago_contado
        WHERE
                cod_documento = 'FAC'
            AND nvl(deposito, 0) > 0
            AND num_pago > 1001000000000
        UNION
        SELECT
            cod_documento,
            to_char(num_pago, 'fm000000000000000'),
            '20',
            'OTROS CON UTILIZACION DEL SISTEMA FINANCIERO',
            nvl(f.credito, 0),
            CAST((
                SELECT
                    MAX(round(cxc.dias_plazo, 0))
                FROM
                    cxc_doc_cobrar cxc
                WHERE
                        cxc.cod_documento = 'FAC'
                    AND cxc.num_documento = f.num_pago
            ) AS VARCHAR2(5)),
            'Dias'
        FROM
            cxc_pago_contado f
        WHERE
                f.cod_documento = 'FAC'
            AND nvl(f.credito, 0) > 0
            AND num_pago > 1001000000000
        UNION
        SELECT
            cod_documento,
            to_char(num_pago, 'fm000000000000000'),
            '20',
            'OTROS CON UTILIZACION DEL SISTEMA FINANCIERO',
            nvl(f.otros, 0),
            NULL,
            NULL
        FROM
            cxc_pago_contado f
        WHERE
                f.cod_documento = 'FAC'
            AND nvl(f.otros, 0) > 0
            AND num_pago > 1001000000000
        UNION ALL
  --liquidaciones
        SELECT
            CAST('LIQ' AS VARCHAR2(3))                                       AS codigo,
            replace(e.referencia, '-', '')                                   AS numero,
            CAST('01' AS VARCHAR2(2))                                        AS forma_pago,
            CAST('SIN UTILIZACION DEL SISTEMA FINANCIERO' AS VARCHAR2(100))  AS forma_pago_descripcion,
            round(e.base_imponible_cero + e.base_imponible + e.monto_iva, 2) AS total,
            CAST(NULL AS VARCHAR2(2))                                        AS plazo,
            CAST(NULL AS VARCHAR2(2))                                        AS tiempo
        FROM
            ban_egreso e
        WHERE
            e.cod_tipocomprobante = 3
        UNION ALL
        SELECT
            CAST('LIQ' AS VARCHAR2(3))                                            AS codigo,
            replace(e.referencia, '-', '')                                        AS numero,
            CAST('20' AS VARCHAR2(2))                                             AS forma_pago,
            CAST('OTROS CON UTILIZACION DEL SISTEMA FINANCIERO' AS VARCHAR2(100)) AS forma_pago_descripcion,
            round(e.base_imponible_cero + e.base_imponible + e.iva_documento, 2)  AS total,
            CAST(NULL AS VARCHAR2(2))                                             AS plazo,
            CAST(NULL AS VARCHAR2(2))                                             AS tiempo
        FROM
            cxp_doc_pagar e
        WHERE
            e.cod_tipocomprobante = 3
        UNION ALL
        SELECT
            CAST('LIQ' AS VARCHAR2(3))                                                                                        AS codigo
            ,
            replace(e.referencia, '-', '')                                                                                    AS numero
            ,
            decode(e.forma_pago, 0, '01', '20')                                                                               AS forma_pago
            ,
            decode(e.forma_pago, 0, 'SIN UTILIZACION DEL SISTEMA FINANCIERO', 'OTROS CON UTILIZACION DEL SISTEMA FINANCIERO') AS forma_pago_descripcion
            ,
            e.total_documento                                                                                                 AS total
            ,
            decode(e.dias_plazo, 0, NULL, e.dias_plazo)                                                                       AS plazo
            ,
            decode(e.dias_plazo, 0, NULL, 'Dias')                                                                             AS tiempo
        FROM
                 inv_movimiento_cab e
            INNER JOIN v_proveedor p ON e.cod_empresa = p.cod_empresa
                                                   AND e.cod_proveedor = p.cod_proveedor
        WHERE
                e.cod_documento = 'ENI'
            AND e.cod_movimiento = '10'
            AND e.cod_tipocomprobante = 3
/* Notas de débito */
        UNION ALL
        SELECT
            d.cod_documento                                                      AS codigo,
            to_char(d.num_documento, 'fm000000000000000')                        AS numero,
            CAST('20' AS VARCHAR2(2))                                            AS forma_pago,
            CAST('OTROS CON UTILIZACION DEL SISTEMA FINANCIERO' AS VARCHAR2(50)) AS forma_pago_descripcion,
            d.valor_documento                                                    AS total,
            CAST('8' AS VARCHAR2(1))                                             AS plazo,
            CAST('Días' AS VARCHAR2(4))                                          AS tiempo
        FROM
            cxc_doc_cobrar d
        WHERE
                d.cod_documento = 'NDC'
            AND nvl(d.estado, 'G') <> 'A'
            AND d.num_documento > 1001000000000
        UNION ALL
        SELECT
            CAST('RET' AS VARCHAR2(3))                                               AS codigo,
            to_char(r.num_retencion, 'fm000000000000000')                            AS numero,
            pkg_info_retencion.fun_pago(r.cod_documento, r.num_documento) AS forma_pago,
            NULL,
            0,
            NULL,
            NULL
        FROM
            ban_retencion_c r
        WHERE
            r.num_retencion > 1000000000000
    )
    SELECT
        ROWNUM id,
        codigo,
        numero,
        forma_pago,
        forma_pago_descripcion,
        total,
        plazo,
        tiempo
    FROM
        data;