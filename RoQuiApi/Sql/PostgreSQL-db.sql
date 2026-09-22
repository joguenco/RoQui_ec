CREATE TABLE IF NOT EXISTS v_assets (
    id character varying(255) NOT NULL,
    name character varying(255),
    value character varying(255)
);

DROP VIEW IF EXISTS v_ele_credit_notes cascade;
CREATE VIEW v_ele_credit_notes AS
    SELECT
        i.id::bigint id,
        i.code,
        i.number,
        '04' AS code_document,
        substr(i.number, 1, 3) AS establishment,
        substr(i.number, 4, 3) AS emission_point,
        substr(i.number, 7, 15) AS sequence,
        i.date,
        i.updated_code_document,
        i.updated_number_document,
        i.updated_date_document,
        sum(d.total_without_tax) AS total_without_taxes,
        sum(d.total) AS total,
        i.identification_type,
        i.identification,
        i.legal_name,
        i.address,
        i.reason,
        (
        SELECT e.address FROM establishments e
        WHERE e.code = substr(i.number, 1, 3)) AS establishment_address,
        i.access_key
    FROM documents i
    JOIN documents_detail d ON i.id = d.document_id
    -- El ERP usa dos codigos para la nota de credito: DVC devolucion de cliente
    -- y NCC nota de credito cliente. Los dos son codDoc 04 para el SRI.
    WHERE i.code IN ('DVC', 'NCC')
    GROUP BY
        i.id, i.code, i.number,
        substr(i.number, 1, 3), substr(i.number, 4, 3), substr(i.number, 7, 15),
        i.date, i.updated_code_document, i.updated_number_document, i.updated_date_document,
        i.identification_type, i.identification, i.legal_name,
        i.address, i.reason, i.access_key;


DROP VIEW IF EXISTS v_ele_credit_notes_detail;
CREATE VIEW v_ele_credit_notes_detail AS
    SELECT
        dd.id::bigint id,
        d.code,
        d.number,
        dd.product_code                AS principal_code,
        dd.line::bigint                AS line,
        dd.product_name                AS name,
        dd.quantity,
        dd.unit_price,
        dd.tax_code,
        dd.tax_iva,
        dd.value_iva,
        dd.discount,
        dd.total_without_tax           AS total_price_without_tax
    FROM documents_detail dd
    JOIN documents d ON d.id = dd.document_id;

DROP VIEW IF EXISTS v_ele_debit_notes cascade;
CREATE VIEW v_ele_debit_notes AS
    SELECT
        d.id::bigint id,
        d.code,
        d.number,
        '05' AS code_document,
        substr(d.number, 1, 3) AS establishment,
        substr(d.number, 4, 3) AS emission_point,
        substr(d.number, 7, 15) AS sequence,
        d.date,
        d.updated_code_document,
        d.updated_number_document,
        d.updated_date_document,
        -- el total sin impuestos es la suma de los motivos
        coalesce(r.total, 0) AS total_without_taxes,
        coalesce(r.total, 0) + coalesce(t.total, 0) AS total,
        d.identification_type,
        d.identification,
        d.legal_name,
        d.address,
        (
        SELECT e.address FROM establishments e
        WHERE e.code = substr(d.number, 1, 3)) AS establishment_address,
        d.access_key
    FROM documents d
    LEFT JOIN (SELECT document_id, sum(value) total
                 FROM debit_notes_reason GROUP BY document_id) r ON r.document_id = d.id
    LEFT JOIN (SELECT document_id, sum(value) total
                 FROM debit_notes_tax GROUP BY document_id) t ON t.document_id = d.id
    WHERE d.code = 'NDC';

DROP VIEW IF EXISTS v_ele_debit_notes_detail cascade;
CREATE VIEW v_ele_debit_notes_detail AS
    SELECT
        r.id::bigint id,
        d.code,
        d.number,
        r.line::bigint AS line,
        r.reason,
        r.value
    FROM debit_notes_reason r
    JOIN documents d ON d.id = r.document_id;

DROP VIEW IF EXISTS v_ele_delivery_notes cascade;
CREATE VIEW v_ele_delivery_notes AS
    SELECT
        g.id::bigint id,
        g.code,
        g.number,
        '06' AS code_document,
        substr(g.number, 1, 3) AS establishment,
        substr(g.number, 4, 3) AS emission_point,
        substr(g.number, 7, 15) AS sequence,
        g.date,
        g.date_start_transport,
        g.date_end_transport,
        g.address_start,
        g.carrier_identification_type,
        g.carrier_identification,
        g.carrier_legal_name,
        g.plate,
        g.observation,
        (
        SELECT e.address FROM establishments e
        WHERE e.code = substr(g.number, 1, 3)) AS establishment_address,
        g.access_key
    FROM delivery_notes g;

DROP VIEW IF EXISTS v_ele_delivery_notes_receiver cascade;
CREATE VIEW v_ele_delivery_notes_receiver AS
    SELECT
        r.id::bigint id,
        g.code,
        g.number,
        r.line::bigint AS line,
        r.identification_type,
        r.identification,
        r.legal_name,
        r.address,
        r.transfer_reason,
        r.code_document_support,
        r.number_document_support,
        r.authorization_document_support,
        r.date_document_support
    FROM delivery_notes_receiver r
    JOIN delivery_notes g ON g.id = r.delivery_note_id;

DROP VIEW IF EXISTS v_ele_delivery_notes_receiver_detail;
CREATE VIEW v_ele_delivery_notes_receiver_detail AS
    SELECT
        d.id::bigint id,
        g.code,
        g.number,
        -- RoQui busca el detalle por la linea del DESTINATARIO, no por la suya
        r.line::bigint AS line,
        d.principal_code,
        d.name,
        d.quantity
    FROM delivery_notes_receiver_detail d
    JOIN delivery_notes_receiver r ON r.id = d.delivery_note_receiver_id
    JOIN delivery_notes g ON g.id = r.delivery_note_id;

DROP VIEW IF EXISTS v_ele_establishments;
CREATE VIEW v_ele_establishments AS
    SELECT 
        e.id::bigint as id,
        t.identification,
        e.code, 
        e.business_name, 
        e.address,
        CASE 
	        WHEN e.is_principal = true THEN 'Principal'
	        ELSE 'BranchOffice'
    	END AS principal        
    FROM establishments e join taxpayers t
	on t.id = e.taxpayer_id;

DROP VIEW IF EXISTS v_ele_general_informations cascade;
CREATE VIEW v_ele_general_informations AS
    SELECT
        p.id::integer AS id,
        p.name        AS name,
        p.value       AS value
    FROM ele_parameters p
    -- El SRI pide el RUC del proveedor del sistema en todo comprobante (Anexo 26).
    -- El valor se edita desde la pantalla de Parametros, no hace falta tocar el SQL.
    -- El status apaga el campo sin borrar el parametro.
    WHERE p.name = 'RUC Proveedor'
      AND p.status = true;

DROP VIEW IF EXISTS v_ele_information cascade;
CREATE VIEW v_ele_information AS
    -- RoQui pide esta informacion por identificacion del cliente, no por documento,
    -- y cada factura guarda su propia copia. Sin el DISTINCT ON se repetirian los
    -- campos una vez por cada factura que tenga ese cliente.
    -- Gana el valor del documento mas reciente.
    SELECT DISTINCT ON (d.identification, di.name)
        di.id::integer   AS id,
        d.identification AS identification,
        di.name          AS name,
        di.value         AS value
    FROM documents_information di
    JOIN documents d ON d.id = di.document_id
    ORDER BY d.identification, di.name, di.id DESC;

DROP VIEW IF EXISTS v_ele_invoices cascade;
CREATE VIEW v_ele_invoices AS
    select
        i.id::bigint id,
        i.code,
        i.number,
        '01' as code_document,
        substr(i.number, 1, 3) as establishment,
        substr(i.number, 4, 3) as emission_point,
        substr(i.number, 7, 15) as sequence,
        i.date,
        sum(d.total_without_tax) as total_without_taxes,
        sum(d.discount) discount,
        sum(d.total) total,
        0::numeric tip,
        i.identification_type,
        i.identification,
        i.legal_name,
        i.address,
        i.delivery_note,
        (
        select
            e.address
        from
            establishments e
        where
            e.code = substr(i.number, 1, 3)) establishment_address,
        i.access_key
    from
        documents i
    join documents_detail d
    on
        i.id = d.document_id
    group by
        i.id,
        i.code,
        i.number,
        substr(i.number, 1, 3),
        substr(i.number, 4, 3),
        substr(i.number, 7, 15),
        i.date,
        i.identification_type,
        i.identification,
        i.legal_name,
        i.address,
        i.delivery_note,
        i.access_key;

DROP VIEW IF EXISTS v_ele_invoices_detail;
CREATE VIEW v_ele_invoices_detail AS
    SELECT
        dd.id::bigint id,
        d.code,
        d.number,
        dd.product_code                AS principal_code,
        dd.line::bigint                AS line,
        dd.product_name                AS name,
        dd.quantity,
        dd.unit,
        dd.unit_price,
        dd.tax_code,
        dd.tax_iva,
        dd.value_iva,
        dd.discount,
        dd.total_without_tax           AS total_price_without_tax
    FROM documents_detail dd
    JOIN documents d ON d.id = dd.document_id;        

DROP VIEW IF EXISTS v_ele_liquidations cascade;
CREATE VIEW v_ele_liquidations AS
    SELECT
        i.id::bigint id,
        i.code,
        i.number,
        '03' AS code_document,
        substr(i.number, 1, 3) AS establishment,
        substr(i.number, 4, 3) AS emission_point,
        substr(i.number, 7, 15) AS sequence,
        i.date,
        sum(d.total_without_tax) AS total_without_taxes,
        sum(d.discount) AS discount,
        sum(d.total) AS total,
        i.identification_type,
        i.identification,
        i.legal_name,
        i.address,
        (
        SELECT e.address FROM establishments e
        WHERE e.code = substr(i.number, 1, 3)) AS establishment_address,
        i.access_key
    FROM documents i
    JOIN documents_detail d ON i.id = d.document_id
    GROUP BY
        i.id, i.code, i.number,
        substr(i.number, 1, 3), substr(i.number, 4, 3), substr(i.number, 7, 15),
        i.date, i.identification_type, i.identification, i.legal_name,
        i.address, i.access_key;

DROP VIEW IF EXISTS v_ele_liquidations_detail;
CREATE VIEW v_ele_liquidations_detail AS
    SELECT
        dd.id::bigint id,
        d.code,
        d.number,
        dd.product_code                AS principal_code,
        dd.line::bigint                AS line,
        dd.product_name                AS name,
        dd.quantity,
        dd.unit,
        dd.unit_price,
        dd.tax_code,
        dd.tax_iva,
        dd.value_iva,
        dd.discount,
        dd.total_without_tax           AS total_price_without_tax
    FROM documents_detail dd
    JOIN documents d ON d.id = dd.document_id;

DROP VIEW IF EXISTS v_ele_liquidations_taxes;
CREATE VIEW v_ele_liquidations_taxes AS
    SELECT
        t.id::bigint id,
        d.code,
        d.number,
        dd.product_code                AS principal_code,
        dd.line::bigint                AS line,
        t.tax_code,
        t.tax_code_percentage          AS percentage_code,
        t.base                         AS tax_base,
        t.tax_value                    AS tax_iva,
        t.value
    FROM documents_detail_taxes t
    JOIN documents_detail dd ON dd.id = t.document_detail_id
    JOIN documents d ON d.id = dd.document_id;

DROP VIEW IF EXISTS v_ele_payments;
CREATE VIEW v_ele_payments AS
    SELECT
        p.id::bigint id,
        d.code,
        d.number,
        p.code                         AS way_pay,
        CASE p.code
            WHEN '01' THEN 'SIN UTILIZACION DEL SISTEMA FINANCIERO'
            ELSE 'OTROS CON UTILIZACION DEL SISTEMA FINANCIERO'
        END                            AS name,
        p.total,
        p.deadline                     AS payment_deadline,
        p.unit_time
    FROM documents_payment p
    JOIN documents d ON d.id = p.document_id;

DROP VIEW IF EXISTS v_ele_report_credit_notes;
CREATE VIEW v_ele_report_credit_notes AS
SELECT
    j.id::bigint AS id,
    j.code AS code,
    j.number AS number,
    j.access_key AS access_key,
    j.date AS date,
    j.total AS total,
    j.identification AS identification,
    j.legal_name AS legal_name,
    (SELECT i.value
     FROM v_ele_information i
     WHERE i.name = 'Email'
       AND i.identification = j.identification
     LIMIT 1) AS email,
    COALESCE(
        (SELECT e.status
         FROM ele_documents e
         WHERE e.code = j.code
           AND e.number = j.number
        ),
        'NO ENVIADO'
    ) AS status
FROM
    v_ele_credit_notes j
ORDER BY
    j.number DESC;

DROP VIEW IF EXISTS v_ele_report_debit_notes cascade;
CREATE VIEW v_ele_report_debit_notes AS
SELECT
    j.id::bigint AS id,
    j.code AS code,
    j.number AS number,
    j.access_key AS access_key,
    j.date AS date,
    j.total AS total,
    j.identification AS identification,
    j.legal_name AS legal_name,
    (SELECT i.value
     FROM v_ele_information i
     WHERE i.name = 'Email'
       AND i.identification = j.identification
     LIMIT 1) AS email,
    COALESCE(
        (SELECT e.status
         FROM ele_documents e
         WHERE e.code = j.code
           AND e.number = j.number
        ),
        'NO ENVIADO'
    ) AS status
FROM
    v_ele_debit_notes j
ORDER BY
    j.number DESC;

DROP VIEW IF EXISTS v_ele_report_delivery_notes;
CREATE VIEW v_ele_report_delivery_notes AS
SELECT
    g.id::bigint AS id,
    g.code AS code,
    g.number AS number,
    g.access_key AS access_key,
    g.date AS date,
    -- la guia no mueve plata, solo mercaderia
    0::numeric AS total,
    -- en el reporte va el primer destinatario, que es a quien se le entrega
    (SELECT r.identification FROM delivery_notes_receiver r
     WHERE r.delivery_note_id = g.id ORDER BY r.line LIMIT 1) AS identification,
    (SELECT r.legal_name FROM delivery_notes_receiver r
     WHERE r.delivery_note_id = g.id ORDER BY r.line LIMIT 1) AS legal_name,
    (SELECT i.value
     FROM v_ele_information i
     WHERE i.name = 'Email'
       AND i.identification = (SELECT r.identification FROM delivery_notes_receiver r
                               WHERE r.delivery_note_id = g.id ORDER BY r.line LIMIT 1)
     LIMIT 1) AS email,
    COALESCE(
        (SELECT e.status
         FROM ele_documents e
         WHERE e.code = g.code
           AND e.number = g.number
        ),
        'NO ENVIADO'
    ) AS status
FROM
    delivery_notes g
ORDER BY
    g.number DESC;

DROP VIEW IF EXISTS v_ele_report_invoices;
CREATE VIEW v_ele_report_invoices AS
SELECT 
    j.id::bigint AS id,
    j.code AS code,
    j.number AS number,
    j.access_key AS access_key,
    j.date AS date,
    j.total AS total,
    j.identification AS identification,
    j.legal_name AS legal_name,
    (SELECT i.value
     FROM v_ele_information i
     WHERE i.name = 'Email'
       AND i.identification = j.identification
     LIMIT 1) AS email,
    COALESCE(
        (SELECT e.status 
         FROM ele_documents e 
         WHERE e.code = j.code 
           AND e.number = j.number
        ), 
        'NO ENVIADO'
    ) AS status
FROM 
    v_ele_invoices j
ORDER BY 
    j.number DESC;

DROP VIEW IF EXISTS v_ele_report_liquidations;
CREATE VIEW v_ele_report_liquidations AS
SELECT
    j.id::bigint AS id,
    j.code AS code,
    j.number AS number,
    j.access_key AS access_key,
    j.date AS date,
    j.total AS total,
    j.identification AS identification,
    j.legal_name AS legal_name,
    (SELECT i.value
     FROM v_ele_information i
     WHERE i.name = 'Email'
       AND i.identification = j.identification
     LIMIT 1) AS email,
    COALESCE(
        (SELECT e.status
         FROM ele_documents e
         WHERE e.code = j.code
           AND e.number = j.number
        ),
        'NO ENVIADO'
    ) AS status
FROM
    v_ele_liquidations j
-- La tabla documents guarda facturas y liquidaciones juntas,
-- el codigo del ERP es lo unico que las distingue
WHERE
    j.code = 'LIQ';

DROP VIEW IF EXISTS v_ele_report_withholds;
CREATE VIEW v_ele_report_withholds AS
SELECT
    w.id::bigint AS id,
    w.code AS code,
    w.number AS number,
    w.access_key AS access_key,
    w.date AS date,
    COALESCE(
        (SELECT sum(d.withholded_value)
         FROM withholds_detail d
         JOIN withholds_support s ON s.id = d.withhold_support_id
         WHERE s.withhold_id = w.id
        ), 0) AS total,
    w.identification AS identification,
    w.legal_name AS legal_name,
    (SELECT i.value
     FROM v_ele_information i
     WHERE i.name = 'Email'
       AND i.identification = w.identification
     LIMIT 1) AS email,
    COALESCE(
        (SELECT e.status
         FROM ele_documents e
         WHERE e.code = w.code
           AND e.number = w.number
        ),
        'NO ENVIADO'
    ) AS status
FROM
    withholds w
ORDER BY
    w.number DESC;

DROP VIEW IF EXISTS v_ele_taxes_detail;
CREATE VIEW v_ele_taxes_detail AS
    SELECT
        t.id::bigint id,
        d.code,
        d.number,
        dd.product_code                AS principal_code,
        dd.line::bigint                AS line,
        t.tax_code,
        t.tax_code_percentage          AS percentage_code,
        t.base                         AS tax_base,
        t.tax_value                    AS tax_iva,
        t.value
    FROM documents_detail_taxes t
    JOIN documents_detail dd ON dd.id = t.document_detail_id
    JOIN documents d ON d.id = dd.document_id
    UNION ALL
    -- La nota de debito lleva los impuestos en la cabecera, no por linea, pero
    -- RoQui los busca todos aqui. Le sumo un millon al id para que no choquen.
    SELECT
        (t.id + 1000000)::bigint id,
        d.code,
        d.number,
        NULL                           AS principal_code,
        1::bigint                      AS line,
        t.tax_code,
        t.tax_code_percentage          AS percentage_code,
        t.base                         AS tax_base,
        t.tax_value                    AS tax_iva,
        t.value
    FROM debit_notes_tax t
    JOIN documents d ON d.id = t.document_id;

CREATE OR REPLACE VIEW v_ele_taxpayer AS
    SELECT 
        id, 
        identification, 
        legal_name, 
        forced_accounting, 
        special_taxpayer, 
        retention_agent, 
        rimpe as regime
    FROM taxpayers;

DROP VIEW IF EXISTS v_ele_withholds cascade;
CREATE VIEW v_ele_withholds AS
    SELECT
        w.id::bigint id,
        w.code,
        w.number,
        '07' AS code_document,
        substr(w.number, 1, 3) AS establishment,
        substr(w.number, 4, 3) AS emission_point,
        substr(w.number, 7, 15) AS sequence,
        w.date,
        w.fiscal_period,
        w.identification_type,
        w.identification,
        w.legal_name,
        w.related,
        (
        SELECT e.address FROM establishments e
        WHERE e.code = substr(w.number, 1, 3)) AS establishment_address,
        w.access_key
    FROM withholds w;

DROP VIEW IF EXISTS v_ele_withholds_detail;
CREATE VIEW v_ele_withholds_detail AS
    SELECT
        d.id::bigint id,
        w.code,
        w.number,
        s.code_support,
        d.line::bigint AS line,
        d.tax_code,
        d.withhold_code,
        d.base_value,
        d.percentage,
        d.withholded_value
    FROM withholds_detail d
    JOIN withholds_support s ON s.id = d.withhold_support_id
    JOIN withholds w ON w.id = s.withhold_id;

DROP VIEW IF EXISTS v_ele_withholds_document_taxes;
CREATE VIEW v_ele_withholds_document_taxes AS
    SELECT
        t.id::bigint id,
        w.code,
        w.number,
        s.code_support,
        t.tax_code,
        t.percentage_code,
        t.tax_base,
        t.tax_iva,
        t.value
    FROM withholds_document_taxes t
    JOIN withholds_support s ON s.id = t.withhold_support_id
    JOIN withholds w ON w.id = s.withhold_id;

DROP VIEW IF EXISTS v_ele_withholds_support;
CREATE VIEW v_ele_withholds_support AS
    SELECT
        s.id::bigint id,
        w.code,
        w.number,
        s.code_support,
        s.code_document_support,
        s.number_document_support,
        s.date_document_support,
        s.authorization_document_support,
        s.total_without_taxes,
        s.total
    FROM withholds_support s
    JOIN withholds w ON w.id = s.withhold_id;

CREATE TABLE IF NOT EXISTS v_users (
    id integer NOT NULL,
    status boolean,
    password character varying(255),
    role character varying(255),
    username character varying(255),
    CONSTRAINT v_users_pkey PRIMARY KEY (id)
);

CREATE OR REPLACE VIEW v_version AS 
    select 1 as id,
    version() as version_database;

INSERT INTO v_users (id,username,password,role,status) 
VALUES (1,'Administrator','sha1:86F7E437FAA5A7FCE15D1DDCB9EAEAEA377667B8','Administrator',true) ON CONFLICT (ID) DO NOTHING;
