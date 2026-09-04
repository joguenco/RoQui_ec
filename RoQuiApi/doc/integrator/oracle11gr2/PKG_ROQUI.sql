CREATE OR REPLACE PACKAGE pkg_roqui AS
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

END pkg_roqui;
/


CREATE OR REPLACE PACKAGE BODY pkg_roqui AS

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

END pkg_roqui;
/
