CREATE OR REPLACE PACKAGE pkg_roqui AS
    default_server CONSTANT VARCHAR2(100) := 'http://172.17.0.1:5276';
    FUNCTION fun_ping RETURN VARCHAR2;

    FUNCTION fun_version RETURN VARCHAR2;

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

END pkg_roqui;
/
