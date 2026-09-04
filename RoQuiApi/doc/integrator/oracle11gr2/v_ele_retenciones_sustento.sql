CREATE or REPLACE VIEW v_ele_retenciones_sustento
AS WITH data AS (
    --NDP
    SELECT
        r.num_retencion numero,
        cxp.cod_sustento sustento,
        to_char(cxp.cod_tipocomprobante, 'fm00') tipo_documento,
        replace(cxp.referencia, '-', '') documento,
        to_char(cxp.fecha_emision_doc, 'dd/mm/rrrr') fecha,
        cxp.numero_autorizacion_doc autorizacion,
        nvl(cxp.base_imponible_cero, 0) + nvl(cxp.base_imponible, 0) + nvl(cxp.ice, 0) + nvl(cxp.otros, 0) total_sin_impuestos,
        nvl(cxp.base_imponible_cero, 0) + nvl(cxp.base_imponible, 0) + nvl(cxp.ice, 0) + nvl(cxp.otros, 0) + nvl(cxp.iva_documento, 0) total
    FROM
             ban_retencion_c r
        INNER JOIN cxp_doc_pagar cxp ON r.cod_documento = cxp.cod_documento
                                                   AND cxp.num_documento = r.num_documento
    WHERE
        cxp.cod_documento in ('NDP', 'DGD')
    union all
    --ENI
    SELECT
        r.num_retencion                        numero,
        nvl(i.cod_sustento, '01')              sustento,
        to_char(i.cod_tipocomprobante, 'fm00') tipo_documento,
        replace(i.referencia, '-', '')         documento,
        to_char(i.fech_emis_doc, 'dd/mm/rrrr') fecha,
        i.auto_cont_impr_doc                   autorizacion,
        i.subtotal                               AS total_sin_impuestos,
        i.subtotal + i.iva                               AS total
    FROM
         inv_movimiento_cab i
    INNER JOIN ban_retencion_c r ON r.cod_documento = i.cod_documento
                                               AND i.num_documento = r.num_documento
    WHERE i.cod_documento = 'ENI'
    AND i.cod_movimiento = '10'
    AND nvl(i.estado, 'G') <> 'A'
    union all
    --CPA
    SELECT
        r.num_retencion numero,
        e.cod_sustento sustento,
        to_char(e.cod_tipocomprobante, 'fm00') tipo_documento,
        replace(e.referencia, '-', '') documento,
        to_char(e.fecha_emision, 'dd/mm/rrrr') fecha,
        e.autorizacion,
        nvl(e.base_imponible_cero, 0) + nvl(e.base_imponible, 0) + nvl(e.ice, 0) + nvl(e.otros, 0) total_sin_impuestos,
        nvl(e.base_imponible_cero, 0) + nvl(e.base_imponible, 0) + nvl(e.ice, 0) + nvl(e.otros, 0) + nvl(e.monto_iva, 0) total
    FROM
             ban_retencion_c r
        INNER JOIN ban_egreso e ON r.cod_documento = e.cod_documento
                                                   AND e.num_documento = r.num_documento
    WHERE
        e.cod_documento = 'CPA'
)
SELECT
    ROWNUM as id,
    CAST('RET' AS VARCHAR2(3)) as codigo,
    to_char(numero, 'fm000000000000000') as numero,
    sustento,
    tipo_documento,
    documento,
    fecha,
    autorizacion,
    total_sin_impuestos,
    total
FROM
    data;
