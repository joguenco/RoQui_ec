--------------------------------------------------------
--  DDL for View V_ELE_INFORMACIONES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW v_ele_informaciones (
    "ID",
    "DOCUMENTO",
    "NOMBRE",
    "VALOR"
) AS
    WITH data AS (
        SELECT
            to_number(to_char(d.cod_persona)
                      || to_char(1))       AS id,
            d.documento,
            'Dirección'          AS nombre,
            d.direccion          AS valor
        FROM
            gnr_persona d
        WHERE
            d.direccion IS NOT NULL
            AND length(d.direccion) = (
                SELECT
                    MAX(length(f.direccion))
                FROM
                    gnr_persona f
                WHERE
                    f.direccion IS NOT NULL
                    AND f.documento = d.documento
            )
        UNION
        SELECT
            to_number(to_char(d.cod_persona)
                      || to_char(2)),
            d.documento,
            'Teléfono'    AS nombre,
            d.telefono    AS valor
        FROM
            gnr_persona d
        WHERE
            d.telefono IS NOT NULL
        UNION
        SELECT
            to_number(to_char(d.cod_persona)
                      || to_char(3)),
            d.documento,
            'Email'                                                                                                                                       AS nombre,
            decode(substr(d.mail, 0, instr(d.mail, ',') - 1), NULL, d.mail, substr(d.mail, 0, instr(d.mail, ',') - 1))                                    AS valor
        FROM
            gnr_persona d
        WHERE
            d.mail IS NOT NULL
        UNION
        SELECT
            to_number(to_char(p.cod_persona)
                      || to_char(4)),
            p.documento                                                 documento,
            'Email'                                                     AS nombre,
            substr(p.mail, instr(p.mail || ',', ',') + 1)               valor
        FROM
            gnr_persona p
        WHERE
            substr(p.mail, instr(p.mail || ',', ',') + 1) IS NOT NULL
    )
    SELECT
        id,
        documento,
        nombre,
        valor
    FROM
        data
    WHERE
        documento IS NOT NULL;