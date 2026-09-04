--------------------------------------------------------
--  DDL for View V_ELE_ESTABLECIMIENTOS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW V_ELE_ESTABLECIMIENTOS (
    "ID",
    "RUC",
    "ESTABLECIMIENTO",
    "NOMBRE_COMERCIAL",
    "DIRECCION"
) AS
    SELECT
        id,
        (
            SELECT
                ruc
            FROM
                v_ele_contribuyentes
        )       AS ruc,
        codigo  AS establecimiento,
        nombre_comercial,
        direccion
    FROM
        sri_establecimientos
    WHERE
        estado = 'ACTIVO';