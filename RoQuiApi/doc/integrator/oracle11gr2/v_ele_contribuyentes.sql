--------------------------------------------------------
--  DDL for View V_ELE_CONTRIBUYENTES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_contribuyentes (
    "ID",
    "RAZON_SOCIAL",
    "NOMBRE_COMERCIAL",
    "RUC",
    "DIRECCION_MATRIZ",
    "OBLIGADO_CONTABILIDAD",
    "CONTRIBUYENTE_ESPECIAL",
    "CONTRIBUYENTE_RIMPE",
    "AGENTE_RETENCION"
) AS
    SELECT
        ROWNUM     AS id,
        titulo5    AS razon_social,
        titulo1    AS nombre_comercial,
        ruc,
        direccion  AS direccion_matriz,
        titulo2    AS obligado_contabilidad,
        titulo6    AS contribuyente_especial,
        CONTRIBUYENTE_RIMPE,
        agente_retencion
    FROM
        gnr_datos_generales
    WHERE
        ROWNUM = 1;