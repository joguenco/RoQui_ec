CREATE TABLE roqui_parameters (
    id          NUMBER NOT NULL,
    url         VARCHAR2(900) NOT NULL,
    key         VARCHAR2(900) NOT NULL,
    environment VARCHAR2(90) NOT NULL,
    status      VARCHAR2(9) DEFAULT 'Activo' NOT NULL
);

COMMENT ON COLUMN roqui_parameters.environment IS
    'Pruebas, Producción';

COMMENT ON COLUMN roqui_parameters.status IS
    'Activo, Inactivo';

INSERT INTO roqui_parameters (
    id,
    url,
    key,
    environment,
    status
) VALUES ( 1,
           'http://172.17.0.1:5276',
           'api_abcdef',
           'Pruebas',
           'Activo' );
           
commit;           