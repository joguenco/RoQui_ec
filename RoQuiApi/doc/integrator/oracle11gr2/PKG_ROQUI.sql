CREATE OR REPLACE PACKAGE            pkg_roqui AS
    default_server CONSTANT VARCHAR2(100) := 'http://172.17.0.1:5276';

    TYPE type_taxpayer IS RECORD (
            identification        VARCHAR2(180),
            legal_name        VARCHAR2(180),
            forced_accounting VARCHAR2(9),
            special_taxpayer  VARCHAR2(9),
            rimpe             VARCHAR2(180),
            retention_agent   VARCHAR2(9)
    );
    FUNCTION fun_ping RETURN VARCHAR2;

    FUNCTION fun_version RETURN VARCHAR2;

    function fun_taxpayer return varchar2;

    FUNCTION fun_invoice (
        p_number IN VARCHAR2
    ) RETURN VARCHAR2;

    FUNCTION fun_credit_note (
        p_number IN VARCHAR2
    ) RETURN VARCHAR2;

    FUNCTION fun_debit_note (
        p_number IN VARCHAR2
    ) RETURN VARCHAR2;

    FUNCTION fun_liquidation (
        p_number IN VARCHAR2
    ) RETURN VARCHAR2;

END pkg_roqui;

/


CREATE OR REPLACE PACKAGE BODY            pkg_roqui AS

    FUNCTION fun_ping RETURN VARCHAR2 AS
        v_result CLOB;
    BEGIN
        dbms_output.put_line('url='
                             || default_server
                             || '/ping');
        v_result := apex_web_service.make_rest_request(
            p_url         => default_server || '/ping',
            p_http_method => 'GET'
        );

        dbms_output.put_line('status=' || apex_web_service.g_status_code);
        dbms_output.put_line('v_result=' || v_result);
        apex_json.parse(v_result);
        RETURN apex_json.get_varchar2(p_path => 'message');
    END fun_ping;

    FUNCTION fun_version RETURN VARCHAR2 AS
        l_clob CLOB;
    BEGIN
        l_clob := apex_web_service.make_rest_request(
            p_url         => default_server || '/version',
            p_http_method => 'GET'
        );

        dbms_output.put_line('status=' || apex_web_service.g_status_code);
        dbms_output.put_line('l_clob=' || l_clob);
        apex_json.parse(l_clob);
        RETURN apex_json.get_varchar2(p_path => 'version');
    END fun_version;

    FUNCTION fun_taxpayer RETURN VARCHAR2 AS

        l_response   CLOB;
        l_body       CLOB;
        rec_taxpayer type_taxpayer;
        CURSOR cur_establishment IS
        SELECT
            codigo           AS code,
            nombre_comercial AS business_name,
            direccion        AS address,
            principal        principal
        FROM
            sri_establecimientos
        WHERE
            estado = 'ACTIVO';

    BEGIN
        SELECT
            ruc,
            razon_social,
            obligado_contabilidad,
            contribuyente_especial,
            contribuyente_rimpe,
            agente_retencion
        INTO
            rec_taxpayer.identification,
            rec_taxpayer.legal_name,
            rec_taxpayer.forced_accounting,
            rec_taxpayer.special_taxpayer,
            rec_taxpayer.rimpe,
            rec_taxpayer.retention_agent
        FROM
            v_ele_contribuyentes;

        apex_json.initialize_clob_output;
        apex_json.open_object;
        apex_json.write('identification', rec_taxpayer.identification);
        apex_json.write('legalName', rec_taxpayer.legal_name);
        apex_json.write('forcedAccounting', rec_taxpayer.forced_accounting);
        apex_json.write('specialTaxpayer', rec_taxpayer.special_taxpayer);
        apex_json.write('rimpe', rec_taxpayer.rimpe);
        apex_json.write('retentionAgent', rec_taxpayer.retention_agent);
        apex_json.open_array('establishments');
        FOR e IN cur_establishment LOOP
            dbms_output.put_line('business_name=' || e.business_name);
            apex_json.open_object;
            apex_json.write('code', e.code);
            apex_json.write('businessName', e.business_name);
            apex_json.write('address', e.address);
            IF e.principal = 'Principal' THEN
                apex_json.write('isPrincipal', TRUE);
            ELSE
                apex_json.write('isPrincipal', FALSE);
            END IF;

            apex_json.close_object;
        END LOOP;

        apex_json.close_array;
        apex_json.close_object;
        l_body := apex_json.get_clob_output;
        dbms_output.put_line('l_body=' || l_body);
        apex_json.free_output;
        apex_web_service.g_request_headers.delete();
        apex_web_service.g_request_headers(1).name := 'Content-Type';
        apex_web_service.g_request_headers(1).value := 'application/json';
        l_response := apex_web_service.make_rest_request(
            p_url         => default_server || '/taxpayer/rest/v1/taxpayer',
            p_http_method => 'POST',
            p_body        => l_body
        );

        dbms_output.put_line('status=' || apex_web_service.g_status_code);
        dbms_output.put_line('l_response=' || l_response);
        apex_json.parse(l_response);
        RETURN apex_json.get_varchar2(p_path => 'title');
    END fun_taxpayer;

    FUNCTION fun_invoice (
        p_number IN VARCHAR2
    ) RETURN VARCHAR2 AS

        l_response   CLOB;
        l_body       CLOB;
        rec_header   v_ele_facturas%ROWTYPE;
        v_deadline   NUMBER;
        v_access_key VARCHAR2(100);

        CURSOR cur_detail IS
        SELECT
            linea,
            codigo_principal,
            descripcion,
            cantidad,
            unidad,
            precio_unitario,
            codigo_porcentaje,
            porcentaje_iva,
            valor_iva,
            descuento,
            precio_total_sin_impuesto
        FROM
            v_ele_facturas_detalle
        WHERE
                codigo = 'FAC'
            AND numero = p_number
        ORDER BY
            linea;

        CURSOR cur_tax (
            p_line IN NUMBER
        ) IS
        SELECT
            codigo_impuesto,
            codigo_porcentaje,
            base_imponible,
            tarifa,
            valor
        FROM
            v_ele_impuestos_detalle
        WHERE
                codigo = 'FAC'
            AND numero = p_number
            AND linea = p_line;

        CURSOR cur_payment IS
        SELECT
            forma_pago,
            total,
            plazo,
            tiempo
        FROM
            v_ele_pagos
        WHERE
            numero = p_number;

    BEGIN
        SELECT
            *
        INTO rec_header
        FROM
            v_ele_facturas
        WHERE
            numero = p_number;

        -- Pendiente: falta la columna clave_acceso en la vista de Oracle
        v_access_key := fun_clave_acceso(replace(rec_header.fecha, '-'), 'FAC', rec_header.numero);

        apex_json.initialize_clob_output;
        apex_json.open_object;
        apex_json.write('code', rec_header.codigo);
        apex_json.write('number', rec_header.numero);
        apex_json.write('date', to_char(rec_header.fecha, 'yyyy-mm-dd"T"hh24:mi:ss"Z"'));
        apex_json.write('identificationType', rec_header.tipo_documento);
        apex_json.write('identification', rec_header.documento);
        apex_json.write('legalName', rec_header.razon_social);
        apex_json.write('address', rec_header.direccion);
        apex_json.write('deliveryNote', rec_header.guia_remision);
        apex_json.write('accessKey', v_access_key);

        apex_json.open_array('invoiceDetails');
        FOR d IN cur_detail LOOP
            apex_json.open_object;
            apex_json.write('line', d.linea);
            apex_json.write('productCode', d.codigo_principal);
            apex_json.write('productName', d.descripcion);
            apex_json.write('quantity', d.cantidad);
            apex_json.write('unit', d.unidad);
            apex_json.write('unitPrice', d.precio_unitario);
            apex_json.write('taxCode', d.codigo_porcentaje);
            apex_json.write('taxIva', d.porcentaje_iva);
            apex_json.write('valueIva', d.valor_iva);
            apex_json.write('discount', d.descuento);
            apex_json.write('totalWithoutTax', d.precio_total_sin_impuesto);
            -- El detalle de Oracle no trae el total con impuestos, se calcula
            apex_json.write('total', round(nvl(d.precio_total_sin_impuesto, 0) + nvl(d.valor_iva, 0), 2));

            apex_json.open_array('invoiceDetailTaxes');
            FOR t IN cur_tax(d.linea) LOOP
                apex_json.open_object;
                apex_json.write('taxCode', t.codigo_impuesto);
                apex_json.write('taxCodePercentage', t.codigo_porcentaje);
                apex_json.write('taxValue', t.tarifa);
                apex_json.write('base', t.base_imponible);
                apex_json.write('value', t.valor);
                apex_json.close_object;
            END LOOP;

            apex_json.close_array;
            apex_json.close_object;
        END LOOP;

        apex_json.close_array;

        apex_json.open_array('payments');
        FOR p IN cur_payment LOOP
            -- En Oracle el plazo es VARCHAR2 aunque guarde numeros
            
            apex_json.open_object;
            apex_json.write('code', p.forma_pago);
            apex_json.write('total', p.total);
            apex_json.write('deadline', p.plazo);
            apex_json.write('unitTime', p.tiempo);
            apex_json.close_object;
        END LOOP;

        apex_json.close_array;
        apex_json.close_object;

        l_body := apex_json.get_clob_output;
        dbms_output.put_line('l_body=' || l_body);
        apex_json.free_output;

        apex_web_service.g_request_headers.delete();
        apex_web_service.g_request_headers(1).name := 'Content-Type';
        apex_web_service.g_request_headers(1).value := 'application/json';
        l_response := apex_web_service.make_rest_request(
            p_url         => default_server || '/invoice/rest/v1/invoice',
            p_http_method => 'POST',
            p_body        => l_body
        );

        dbms_output.put_line('status=' || apex_web_service.g_status_code);
        dbms_output.put_line('l_response=' || l_response);
        apex_json.parse(l_response);
        RETURN apex_json.get_varchar2(p_path => 'title');
    EXCEPTION
        WHEN no_data_found THEN
            RETURN 'Invoice '
                   || p_number
                   || ' was not found in v_ele_facturas';
    END fun_invoice;

    FUNCTION fun_credit_note (
        p_number IN VARCHAR2
    ) RETURN VARCHAR2 AS

        l_response   CLOB;
        l_body       CLOB;
        rec_header   v_ele_notas_credito%ROWTYPE;
        v_access_key VARCHAR2(100);

        CURSOR cur_detail IS
        SELECT
            linea,
            codigo_interno,
            descripcion,
            cantidad,
            precio_unitario,
            codigo_porcentaje,
            porcentaje_iva,
            valor_iva,
            descuento,
            precio_total_sin_impuesto
        FROM
            v_ele_notas_credito_detalle
        WHERE
            numero = p_number
        ORDER BY
            linea;

        CURSOR cur_tax (
            p_code IN VARCHAR2,
            p_line IN NUMBER
        ) IS
        SELECT
            codigo_impuesto,
            codigo_porcentaje,
            base_imponible,
            tarifa,
            valor
        FROM
            v_ele_impuestos_detalle
        WHERE
                codigo = p_code
            AND numero = p_number
            AND linea = p_line;

    BEGIN
        SELECT
            *
        INTO rec_header
        FROM
            v_ele_notas_credito
        WHERE
            numero = p_number;

        v_access_key := fun_clave_acceso(replace(rec_header.fecha, '-'), rec_header.codigo, rec_header.numero);

        apex_json.initialize_clob_output;
        apex_json.open_object;
        apex_json.write('code', rec_header.codigo);
        apex_json.write('number', rec_header.numero);
        apex_json.write('date', to_char(rec_header.fecha, 'yyyy-mm-dd"T"hh24:mi:ss"Z"'));
        apex_json.write('identificationType', rec_header.tipo_documento);
        apex_json.write('identification', rec_header.documento);
        apex_json.write('legalName', rec_header.razon_social);
        apex_json.write('accessKey', v_access_key);
        apex_json.write('modifiedDocumentType', rec_header.documento_modificado);
        apex_json.write('modifiedDocument', rec_header.modificado);
        -- En esta vista la fecha viene como texto dd/mm/yyyy
        apex_json.write('modifiedDate',
                        to_char(to_date(rec_header.fecha_modificado, 'dd/mm/yyyy'), 'yyyy-mm-dd"T"hh24:mi:ss"Z"'));
        apex_json.write('reason', rec_header.motivo);
        apex_json.write('totalWithoutTaxes', rec_header.total_sin_impuestos);
        apex_json.write('modifiedTotal', rec_header.total_modificado);

        apex_json.open_array('creditNoteDetails');
        FOR d IN cur_detail LOOP
            apex_json.open_object;
            apex_json.write('line', d.linea);
            apex_json.write('productCode', d.codigo_interno);
            apex_json.write('productName', d.descripcion);
            apex_json.write('quantity', d.cantidad);
            apex_json.write('unitPrice', d.precio_unitario);
            apex_json.write('taxCode', d.codigo_porcentaje);
            apex_json.write('taxIva', d.porcentaje_iva);
            apex_json.write('valueIva', d.valor_iva);
            apex_json.write('discount', d.descuento);
            apex_json.write('totalWithoutTax', d.precio_total_sin_impuesto);
            apex_json.write('total', round(nvl(d.precio_total_sin_impuesto, 0) + nvl(d.valor_iva, 0), 2));

            apex_json.open_array('creditNoteDetailTaxes');
            FOR t IN cur_tax(rec_header.codigo, d.linea) LOOP
                apex_json.open_object;
                apex_json.write('taxCode', t.codigo_impuesto);
                apex_json.write('taxCodePercentage', t.codigo_porcentaje);
                apex_json.write('taxValue', t.tarifa);
                apex_json.write('base', t.base_imponible);
                apex_json.write('value', t.valor);
                apex_json.close_object;
            END LOOP;

            apex_json.close_array;
            apex_json.close_object;
        END LOOP;

        apex_json.close_array;
        apex_json.close_object;

        l_body := apex_json.get_clob_output;
        dbms_output.put_line('l_body=' || l_body);
        apex_json.free_output;
        apex_web_service.g_request_headers.delete();
        apex_web_service.g_request_headers(1).name := 'Content-Type';
        apex_web_service.g_request_headers(1).value := 'application/json';
        l_response := apex_web_service.make_rest_request(
            p_url         => default_server || '/creditnote/rest/v1/creditnote',
            p_http_method => 'POST',
            p_body        => l_body
        );

        dbms_output.put_line('status=' || apex_web_service.g_status_code);
        dbms_output.put_line('l_response=' || l_response);
        apex_json.parse(l_response);
        RETURN apex_json.get_varchar2(p_path => 'title');
    EXCEPTION
        WHEN no_data_found THEN
            RETURN 'Credit note '
                   || p_number
                   || ' was not found in v_ele_notas_credito';
    END fun_credit_note;

    FUNCTION fun_debit_note (
        p_number IN VARCHAR2
    ) RETURN VARCHAR2 AS

        l_response   CLOB;
        l_body       CLOB;
        rec_header   v_ele_notas_debito%ROWTYPE;
        v_access_key VARCHAR2(100);
        v_line       NUMBER := 0;

        CURSOR cur_detail IS
        SELECT
            razon,
            valor
        FROM
            v_ele_notas_debito_detalle
        WHERE
            numero = p_number;

        CURSOR cur_tax IS
        SELECT
            codigo_impuesto,
            codigo_porcentaje,
            base_imponible,
            tarifa,
            valor
        FROM
            v_ele_impuestos_detalle
        WHERE
                codigo = rec_header.codigo
            AND numero = p_number;

    BEGIN
        SELECT
            *
        INTO rec_header
        FROM
            v_ele_notas_debito
        WHERE
            numero = p_number;

        -- Pendiente: falta la columna clave_acceso en la vista de Oracle
        v_access_key := NULL;

        apex_json.initialize_clob_output;
        apex_json.open_object;
        apex_json.write('code', rec_header.codigo);
        apex_json.write('number', rec_header.numero);
        apex_json.write('date', to_char(rec_header.fecha, 'yyyy-mm-dd"T"hh24:mi:ss"Z"'));
        apex_json.write('identificationType', rec_header.tipo_documento);
        apex_json.write('identification', rec_header.documento);
        apex_json.write('legalName', rec_header.razon_social);
        apex_json.write('accessKey', v_access_key);
        apex_json.write('modifiedDocumentType', rec_header.documento_modificado);
        apex_json.write('modifiedDocument', rec_header.modificado);
        apex_json.write('modifiedDate', to_char(rec_header.fecha_modificado, 'yyyy-mm-dd"T"hh24:mi:ss"Z"'));
        apex_json.write('totalWithoutTaxes', rec_header.total_sin_impuestos);

        -- La nota de debito no lleva productos, lleva motivos de cobro
        apex_json.open_array('debitNoteDetails');
        FOR d IN cur_detail LOOP
            apex_json.open_object;
            apex_json.write('reason', d.razon);
            apex_json.write('value', d.valor);
            apex_json.close_object;
        END LOOP;

        apex_json.close_array;

        -- Sus impuestos van en la cabecera, no por linea
        apex_json.open_array('debitNoteTaxes');
        FOR t IN cur_tax LOOP
            apex_json.open_object;
            apex_json.write('taxCode', t.codigo_impuesto);
            apex_json.write('taxCodePercentage', t.codigo_porcentaje);
            apex_json.write('taxValue', t.tarifa);
            apex_json.write('base', t.base_imponible);
            apex_json.write('value', t.valor);
            apex_json.close_object;
        END LOOP;

        apex_json.close_array;
        apex_json.close_object;

        l_body := apex_json.get_clob_output;
        dbms_output.put_line('l_body=' || l_body);
        apex_json.free_output;
        apex_web_service.g_request_headers.delete();
        apex_web_service.g_request_headers(1).name := 'Content-Type';
        apex_web_service.g_request_headers(1).value := 'application/json';
        l_response := apex_web_service.make_rest_request(
            p_url         => default_server || '/debitnote/rest/v1/debitnote',
            p_http_method => 'POST',
            p_body        => l_body
        );

        dbms_output.put_line('status=' || apex_web_service.g_status_code);
        dbms_output.put_line('l_response=' || l_response);
        apex_json.parse(l_response);
        RETURN apex_json.get_varchar2(p_path => 'title');
    EXCEPTION
        WHEN no_data_found THEN
            RETURN 'Debit note '
                   || p_number
                   || ' was not found in v_ele_notas_debito';
    END fun_debit_note;

    FUNCTION fun_liquidation (
        p_number IN VARCHAR2
    ) RETURN VARCHAR2 AS

        l_response   CLOB;
        l_body       CLOB;
        rec_header   v_ele_liquidaciones%ROWTYPE;
        v_access_key VARCHAR2(100);

        -- La vista de Oracle no trae LINEA, se genera con row_number
        CURSOR cur_detail IS
        SELECT
            ROW_NUMBER() OVER(
                ORDER BY
                    codigo_principal
            ) AS linea,
            codigo_principal,
            descripcion,
            cantidad,
            unidad,
            precio_unitario,
            codigo_porcentaje,
            porcentaje_iva,
            valor_iva,
            descuento,
            precio_total_sin_impuesto
        FROM
            v_ele_liquidaciones_detalle
        WHERE
            numero = p_number;

        CURSOR cur_tax (
            p_line IN NUMBER
        ) IS
        SELECT
            codigo_impuesto,
            codigo_porcentaje,
            base_imponible,
            tarifa,
            valor
        FROM
            v_ele_impuestos_detalle
        WHERE
                codigo = rec_header.codigo
            AND numero = p_number
            AND linea = p_line;

    BEGIN
        SELECT
            *
        INTO rec_header
        FROM
            v_ele_liquidaciones
        WHERE
            numero = p_number;

        -- Pendiente: falta la columna clave_acceso en la vista de Oracle
        v_access_key := NULL;

        apex_json.initialize_clob_output;
        apex_json.open_object;
        apex_json.write('code', rec_header.codigo);
        apex_json.write('number', rec_header.numero);
        apex_json.write('date', to_char(rec_header.fecha, 'yyyy-mm-dd"T"hh24:mi:ss"Z"'));
        apex_json.write('identificationType', rec_header.tipo_documento);
        apex_json.write('identification', rec_header.documento);
        apex_json.write('legalName', rec_header.razon_social);
        apex_json.write('address', rec_header.direccion);
        apex_json.write('accessKey', v_access_key);

        apex_json.open_array('liquidationDetails');
        FOR d IN cur_detail LOOP
            apex_json.open_object;
            apex_json.write('line', d.linea);
            apex_json.write('productCode', d.codigo_principal);
            apex_json.write('productName', d.descripcion);
            apex_json.write('quantity', d.cantidad);
            apex_json.write('unit', d.unidad);
            apex_json.write('unitPrice', d.precio_unitario);
            apex_json.write('taxCode', d.codigo_porcentaje);
            apex_json.write('taxIva', d.porcentaje_iva);
            apex_json.write('valueIva', d.valor_iva);
            apex_json.write('discount', d.descuento);
            apex_json.write('totalWithoutTax', d.precio_total_sin_impuesto);
            apex_json.write('total', round(nvl(d.precio_total_sin_impuesto, 0) + nvl(d.valor_iva, 0), 2));

            apex_json.open_array('liquidationDetailTaxes');
            FOR t IN cur_tax(d.linea) LOOP
                apex_json.open_object;
                apex_json.write('taxCode', t.codigo_impuesto);
                apex_json.write('taxCodePercentage', t.codigo_porcentaje);
                apex_json.write('taxValue', t.tarifa);
                apex_json.write('base', t.base_imponible);
                apex_json.write('value', t.valor);
                apex_json.close_object;
            END LOOP;

            apex_json.close_array;
            apex_json.close_object;
        END LOOP;

        apex_json.close_array;
        apex_json.close_object;

        l_body := apex_json.get_clob_output;
        dbms_output.put_line('l_body=' || l_body);
        apex_json.free_output;
        apex_web_service.g_request_headers.delete();
        apex_web_service.g_request_headers(1).name := 'Content-Type';
        apex_web_service.g_request_headers(1).value := 'application/json';
        l_response := apex_web_service.make_rest_request(
            p_url         => default_server || '/liquidation/rest/v1/liquidation',
            p_http_method => 'POST',
            p_body        => l_body
        );

        dbms_output.put_line('status=' || apex_web_service.g_status_code);
        dbms_output.put_line('l_response=' || l_response);
        apex_json.parse(l_response);
        RETURN apex_json.get_varchar2(p_path => 'title');
    EXCEPTION
        WHEN no_data_found THEN
            RETURN 'Liquidation '
                   || p_number
                   || ' was not found in v_ele_liquidaciones';
    END fun_liquidation;

END pkg_roqui;
/
