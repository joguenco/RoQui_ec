DECLARE
    v_principal VARCHAR2(20) := 'APEX_200200';
    v_server    VARCHAR2(20) := '172.17.0.1';
BEGIN
    dbms_network_acl_admin.create_acl(
        acl         => 'server_roqui_api_acl.xml',
        description => 'An ACL for the RoQui API Server',
        principal   => v_principal,
        is_grant    => TRUE,
        privilege   => 'connect',
        start_date  => systimestamp,
        end_date    => NULL
    );

    dbms_network_acl_admin.assign_acl(
        acl        => 'server_roqui_api_acl.xml',
        host       => v_server,
        lower_port => 5276,
        upper_port => 5276
    );

    COMMIT;
END;
/