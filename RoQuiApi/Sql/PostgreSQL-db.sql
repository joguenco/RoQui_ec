CREATE TABLE IF NOT EXISTS v_assets (
    id character varying(255) NOT NULL,
    name character varying(255),
    value character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_credit_notes (
    total numeric(38,2),
    total_without_taxes numeric(38,2),
    date date,
    updated_date_document date,
    id uuid NOT NULL,
    access_key character varying(255),
    address character varying(255),
    code character varying(255),
    code_document character varying(255),
    emission_point character varying(255),
    establishment character varying(255),
    establishment_address character varying(255),
    identification character varying(255),
    identification_type character varying(255),
    legal_name character varying(255),
    number character varying(255),
    reason character varying(255),
    sequence character varying(255),
    updated_code_document character varying(255),
    updated_number_document character varying(255)
);


CREATE TABLE IF NOT EXISTS v_ele_credit_notes_detail (
    discount numeric(38,2),
    quantity numeric(38,2),
    tax_iva numeric(38,2),
    total_price_without_tax numeric(38,2),
    unit_price numeric(38,2),
    value_iva numeric(38,2),
    line bigint,
    id uuid NOT NULL,
    code character varying(255),
    name character varying(255),
    number character varying(255),
    principal_code character varying(255),
    tax_code character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_debit_notes (
    total numeric(38,2),
    total_without_taxes numeric(38,2),
    date date,
    updated_date_document date,
    id uuid NOT NULL,
    access_key character varying(255),
    address character varying(255),
    code character varying(255),
    code_document character varying(255),
    emission_point character varying(255),
    establishment character varying(255),
    establishment_address character varying(255),
    identification character varying(255),
    identification_type character varying(255),
    legal_name character varying(255),
    number character varying(255),
    sequence character varying(255),
    updated_code_document character varying(255),
    updated_number_document character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_debit_notes_detail (
    value numeric(38,2),
    line bigint,
    id uuid NOT NULL,
    code character varying(255),
    number character varying(255),
    reason character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_delivery_notes (
    date date,
    date_end_transport date,
    date_start_transport date,
    id uuid NOT NULL,
    access_key character varying(255),
    address_start character varying(255),
    carrier_identification character varying(255),
    carrier_identification_type character varying(255),
    carrier_legal_name character varying(255),
    code character varying(255),
    code_document character varying(255),
    emission_point character varying(255),
    establishment character varying(255),
    establishment_address character varying(255),
    number character varying(255),
    observation character varying(255),
    plate character varying(255),
    sequence character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_delivery_notes_receiver (
    date_document_support date,
    line bigint,
    id uuid NOT NULL,
    address character varying(255),
    authorization_document_support character varying(255),
    code character varying(255),
    code_document_support character varying(255),
    identification character varying(255),
    identification_type character varying(255),
    legal_name character varying(255),
    number character varying(255),
    number_document_support character varying(255),
    transfer_reason character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_delivery_notes_receiver_detail (
    quantity numeric(38,2),
    line bigint,
    id uuid NOT NULL,
    code character varying(255),
    name character varying(255),
    number character varying(255),
    principal_code character varying(255)
);

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

CREATE TABLE IF NOT EXISTS v_ele_general_observations (
    id integer NOT NULL,
    name character varying(255),
    value character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_information (
    id integer NOT NULL,
    identification character varying(255),
    name character varying(255),
    value character varying(255)
);

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
        d.id::bigint id,
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

CREATE TABLE IF NOT EXISTS v_ele_liquidations (
    discount numeric(38,2),
    total numeric(38,2),
    total_without_taxes numeric(38,2),
    date date,
    id uuid NOT NULL,
    access_key character varying(255),
    address character varying(255),
    code character varying(255),
    code_document character varying(255),
    emission_point character varying(255),
    establishment character varying(255),
    establishment_address character varying(255),
    identification character varying(255),
    identification_type character varying(255),
    legal_name character varying(255),
    number character varying(255),
    sequence character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_liquidations_detail (
    discount numeric(38,2),
    quantity numeric(38,2),
    tax_iva numeric(38,2),
    total_price_without_tax numeric(38,2),
    unit_price numeric(38,2),
    value_iva numeric(38,2),
    line bigint,
    id uuid NOT NULL,
    code character varying(255),
    name character varying(255),
    number character varying(255),
    principal_code character varying(255),
    tax_code character varying(255),
    unit character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_liquidations_taxes (
    tax_base numeric(38,2),
    tax_iva numeric(38,2),
    value numeric(38,2),
    line bigint,
    id uuid NOT NULL,
    code character varying(255),
    number character varying(255),
    percentage_code character varying(255),
    principal_code character varying(255),
    tax_code character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_payments (
    payment_deadline numeric(38,2),
    total numeric(38,2),
    id uuid NOT NULL,
    code character varying(255),
    name character varying(255),
    number character varying(255),
    unit_time character varying(255),
    way_pay character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_report_credit_notes (
    total numeric(38,2),
    date date,
    id bigint NOT NULL,
    access_key character varying(255),
    code character varying(255),
    email character varying(255),
    identification character varying(255),
    legal_name character varying(255),
    number character varying(255),
    status character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_report_debit_notes (
    total numeric(38,2),
    date date,
    id bigint NOT NULL,
    access_key character varying(255),
    code character varying(255),
    email character varying(255),
    identification character varying(255),
    legal_name character varying(255),
    number character varying(255),
    status character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_report_delivery_notes (
    total numeric(38,2),
    date date,
    id bigint NOT NULL,
    access_key character varying(255),
    code character varying(255),
    email character varying(255),
    identification character varying(255),
    legal_name character varying(255),
    number character varying(255),
    status character varying(255)
);

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

CREATE TABLE IF NOT EXISTS v_ele_report_liquidations (
    total numeric(38,2),
    date date,
    id bigint NOT NULL,
    access_key character varying(255),
    code character varying(255),
    email character varying(255),
    identification character varying(255),
    legal_name character varying(255),
    number character varying(255),
    status character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_report_withholds (
    total numeric(38,2),
    date date,
    id bigint NOT NULL,
    access_key character varying(255),
    code character varying(255),
    email character varying(255),
    identification character varying(255),
    legal_name character varying(255),
    number character varying(255),
    status character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_taxes_detail (
    tax_base numeric(38,2),
    tax_iva numeric(38,2),
    value numeric(38,2),
    line bigint,
    id uuid NOT NULL,
    code character varying(255),
    number character varying(255),
    percentage_code character varying(255),
    principal_code character varying(255),
    tax_code character varying(255)
);

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

CREATE TABLE IF NOT EXISTS v_ele_withholds (
    date date,
    id uuid NOT NULL,
    access_key character varying(255),
    code character varying(255),
    code_document character varying(255),
    emission_point character varying(255),
    establishment character varying(255),
    establishment_address character varying(255),
    fiscal_period character varying(255),
    identification character varying(255),
    identification_type character varying(255),
    legal_name character varying(255),
    number character varying(255),
    related character varying(255),
    sequence character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_withholds_detail (
    base_value numeric(38,2),
    percentage numeric(38,2),
    withholded_value numeric(38,2),
    line bigint,
    id uuid NOT NULL,
    code character varying(255),
    code_support character varying(255),
    number character varying(255),
    tax_code character varying(255),
    withhold_code character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_withholds_document_taxes (
    tax_base numeric(38,2),
    tax_iva numeric(38,2),
    value numeric(38,2),
    id uuid NOT NULL,
    code character varying(255),
    code_support character varying(255),
    number character varying(255),
    percentage_code character varying(255),
    tax_code character varying(255)
);

CREATE TABLE IF NOT EXISTS v_ele_withholds_support (
    total numeric(38,2),
    total_without_taxes numeric(38,2),
    date_document_support date,
    id uuid NOT NULL,
    authorization_document_support character varying(255),
    code character varying(255),
    code_document_support character varying(255),
    code_support character varying(255),
    number character varying(255),
    number_document_support character varying(255)
);

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
