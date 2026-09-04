CREATE or REPLACE VIEW v_ele_retenciones_impuesto
AS WITH data AS (
    --NDP
    SELECT
        r.num_retencion numero,
        CAST('2' AS VARCHAR2(1)) codigo_impuesto, 
        CAST('0' AS VARCHAR2(4)) codigo_porcentaje,
        nvl(cxp.base_imponible_cero, 0) base_imponible,
        0 tarifa,
        0 valor
    FROM
             ban_retencion_c r
        INNER JOIN cxp_doc_pagar cxp ON r.cod_documento = cxp.cod_documento
                                                   AND cxp.num_documento = r.num_documento
    WHERE
        cxp.cod_documento = 'NDP'
        and nvl(cxp.base_imponible_cero, 0) > 0
    
    union all
     SELECT
        r.num_retencion numero,
        CAST('2' AS VARCHAR2(1)) codigo_impuesto, 
        CAST(decode(dr.tarifa_iva, 5, '5', 13, '10', 15, '4', 14, '3', 12, '2', -1) AS VARCHAR2(4)) codigo_porcentaje,
        nvl(dr.valor_base, 0) base_imponible,
        dr.tarifa_iva tarifa,
        round(nvl(nvl(dr.valor_base, 0) * dr.tarifa_iva / 100, 0), 2) valor
    FROM
             ban_retencion_c r
             INNER JOIN ban_retencion_d dr ON dr.num_retencion = r.num_retencion
             INNER JOIN ban_tipo_retencion tr ON dr.cod_retencion = tr.cod_retencion
        INNER JOIN cxp_doc_pagar cxp ON r.cod_documento = cxp.cod_documento
                                                   AND cxp.num_documento = r.num_documento
    WHERE
        cxp.cod_documento = 'NDP'
        and tr.tipo_impuesto = 'RENTA'
        and dr.tarifa_iva > 0
        and nvl(cxp.base_imponible, 0) > 0
    
    union all
    SELECT
        r.num_retencion numero,
        CAST('3' AS VARCHAR2(1)) codigo_impuesto, 
        cxp.codigo_ice  codigo_porcentaje,
        nvl(cxp.base_imponible, 0) base_imponible,
        (SELECT ice.porcentaje FROM inv_ice ice
            where ice.codigo = cxp.codigo_ice) tarifa,
        nvl(cxp.ice, 0) valor
    FROM
             ban_retencion_c r
        INNER JOIN cxp_doc_pagar cxp ON r.cod_documento = cxp.cod_documento
                                                   AND cxp.num_documento = r.num_documento
    WHERE
        cxp.cod_documento = 'NDP'
        and nvl(cxp.ice, 0) > 0
        
    union all
    -- Dividendos
    SELECT
        r.num_retencion numero,
        CAST('2' AS VARCHAR2(1)) codigo_impuesto, 
        CAST('6' AS VARCHAR2(4)) codigo_porcentaje,
        nvl(cxp.otros, 0) base_imponible,
        0 tarifa,
        0 valor
    FROM
             ban_retencion_c r
        INNER JOIN cxp_doc_pagar cxp ON r.cod_documento = cxp.cod_documento
                                                   AND cxp.num_documento = r.num_documento
    WHERE
        cxp.cod_documento = 'DGD'
        and nvl(cxp.otros, 0) > 0
    --ENI
    
    union all
    SELECT
        r.num_retencion                                                        numero,
        CAST('2' AS VARCHAR2(1))                                               codigo_impuesto,
        CAST('0' AS VARCHAR2(1))                                               codigo_porcentaje,
        round(SUM(decode(d.porcentaje_iva, 0, d.costo_unitario * d.aux_cantidad, 0)), 2) base_imponible,
        0                                                                      tarifa,
        0                                                                      valor
    FROM
             inv_movimiento_cab i
        INNER JOIN inv_movimiento_dtll        d ON i.cod_documento = d.cod_documento
                                            AND i.num_documento = d.num_documento
        INNER JOIN ban_retencion_c r ON r.cod_documento = i.cod_documento
                                                   AND i.num_documento = r.num_documento
    WHERE
            i.cod_documento = 'ENI'
        AND i.cod_movimiento = '10'
        AND nvl(i.estado, 'G') <> 'A'
        AND d.porcentaje_iva = 0
    GROUP BY
        r.num_retencion,
        CAST('2' AS VARCHAR2(1)),
        CAST('0' AS VARCHAR2(4))
    
    UNION ALL
    SELECT
        r.num_retencion                                                        numero,
        CAST('2' AS VARCHAR2(1))                                               codigo_impuesto,
        i.legal_code                                               codigo_porcentaje,
        round(SUM(DECODE(d.porcentaje_iva, 0, 0, d.costo_unitario * d.aux_cantidad)), 2) base_imponible,
        d.porcentaje_iva tarifa,
        round(SUM(DECODE(d.porcentaje_iva, 0, 0, d.costo_unitario * d.aux_cantidad)) * d.porcentaje_iva / 100, 2) valor
    FROM
             inv_movimiento_cab i
        INNER JOIN inv_movimiento_dtll        d ON i.cod_documento = d.cod_documento
                                            AND i.num_documento = d.num_documento
        INNER JOIN ban_retencion_c r ON r.cod_documento = i.cod_documento
                                                   AND i.num_documento = r.num_documento
        INNER JOIN inv_iva       i ON d.porcentaje_iva = i.valor                                                   
    WHERE
            i.cod_documento = 'ENI'
        AND i.cod_movimiento = '10'
        AND d.porcentaje_iva > 0
        and DECODE(d.porcentaje_iva, 0, 0, d.costo_unitario * d.aux_cantidad) > 0
        AND nvl(i.estado, 'G') <> 'A'
    GROUP BY
        r.num_retencion,
        CAST('2' AS VARCHAR2(1)),
        i.legal_code,
        d.porcentaje_iva,
        nvl(i.iva, 0)
        
    union all
    SELECT
        r.num_retencion numero,
        CAST('3' AS VARCHAR2(1)) codigo_impuesto, 
        i.codigo_ice  codigo_porcentaje,
        nvl(i.ice_base_imponible, 0) base_imponible,
        (SELECT ice.porcentaje FROM inv_ice ice
            where ice.codigo = i.codigo_ice) tarifa,
        nvl(i.ice, 0) valor
    FROM
             ban_retencion_c r
        INNER JOIN inv_movimiento_cab i ON r.cod_documento = i.cod_documento
                                                   AND i.num_documento = r.num_documento
    WHERE
        i.cod_documento = 'ENI'
        AND i.cod_movimiento = '10'
        and nvl(i.ice, 0) > 0     
    --CPA
    union all
    SELECT
        r.num_retencion numero,
        CAST('2' AS VARCHAR2(1)) codigo_impuesto, 
        CAST('0' AS VARCHAR2(4)) codigo_porcentaje,
        nvl(e.base_imponible_cero, 0) base_imponible,
        0 tarifa,
        0 valor
    FROM
             ban_retencion_c r
        INNER JOIN ban_egreso e ON r.cod_documento = e.cod_documento
                                                   AND e.num_documento = r.num_documento
    WHERE
        e.cod_documento = 'CPA'
        and nvl(e.base_imponible_cero, 0) > 0
    union all
    SELECT
        r.num_retencion numero,
        CAST('2' AS VARCHAR2(1)) codigo_impuesto, 
        CAST(decode(dr.tarifa_iva, 5, '5', 13, '10', 15, '4', 14, '3', 12, '2', -1) AS VARCHAR2(4)) codigo_porcentaje,
        nvl(dr.valor_base, 0) base_imponible,
        dr.tarifa_iva tarifa,
        round(nvl(nvl(dr.valor_base, 0) * dr.tarifa_iva / 100, 0), 2) valor
    FROM
             ban_retencion_c r
             INNER JOIN ban_retencion_d dr ON dr.num_retencion = r.num_retencion
             INNER JOIN ban_tipo_retencion tr ON dr.cod_retencion = tr.cod_retencion
        INNER JOIN ban_egreso e ON r.cod_documento = e.cod_documento
                                                   AND e.num_documento = r.num_documento
    WHERE
        e.cod_documento = 'CPA'
        and nvl(e.base_imponible, 0) > 0
        and dr.tarifa_iva > 0
    union all
    SELECT
        r.num_retencion numero,
        CAST('3' AS VARCHAR2(1)) codigo_impuesto, 
        e.codigo_ice  codigo_porcentaje,
        nvl(e.base_imponible, 0) base_imponible,
        (SELECT ice.porcentaje FROM inv_ice ice
            where ice.codigo = e.codigo_ice) tarifa,
        nvl(e.ice, 0) valor
    FROM
             ban_retencion_c r
        INNER JOIN ban_egreso e ON r.cod_documento = e.cod_documento
                                                   AND e.num_documento = r.num_documento
    WHERE
        e.cod_documento = 'CPA'
        and nvl(e.ice, 0) > 0 
        
)
SELECT
    ROWNUM                               AS id,
    CAST('RET' AS VARCHAR2(3))           AS codigo,
    to_char(numero, 'fm000000000000000') AS numero,    
    codigo_impuesto, 
    codigo_porcentaje,
    base_imponible,
    tarifa,
    valor
FROM
    data
order by numero desc;
