--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

-- Started on 2024-07-12 23:54:46

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 16569)
-- Name: vma; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA vma;


ALTER SCHEMA vma OWNER TO postgres;

--
-- TOC entry 242 (class 1255 OID 16570)
-- Name: sp_filtro_vma(character varying, date, date, character varying, character varying); Type: PROCEDURE; Schema: vma; Owner: postgres
--

CREATE PROCEDURE vma.sp_filtro_vma(IN p_nombre character varying, IN p_fecha_desde date, IN p_fecha_hasta date, IN p_estado character varying, IN p_anio character varying)
    LANGUAGE plpgsql
    AS $_$
BEGIN
    EXECUTE
    'SELECT *
    FROM 
        vma.registro_vma r
    JOIN 
        vma.empresa e ON r.id_empresa = e.id_empresa
    JOIN 
        vma.ficha_registro fr ON r.id_ficha_registro = fr.id_ficha_registro
    WHERE 
        (e.nombre = $1 OR
        r.fecha_creacion BETWEEN $2 AND $3 OR 
        r.estado = $4 OR
        fr.anio = $5)'
    USING p_nombre, p_fecha_desde, p_fecha_hasta, p_estado, p_anio;
END;
$_$;


ALTER PROCEDURE vma.sp_filtro_vma(IN p_nombre character varying, IN p_fecha_desde date, IN p_fecha_hasta date, IN p_estado character varying, IN p_anio character varying) OWNER TO postgres;

--
-- TOC entry 254 (class 1255 OID 16571)
-- Name: usp_preguntas_list(integer); Type: FUNCTION; Schema: vma; Owner: postgres
--

CREATE FUNCTION vma.usp_preguntas_list(p_id_cuestionario integer) RETURNS refcursor
    LANGUAGE plpgsql
    AS $$
DECLARE
    list refcursor;
BEGIN 
    OPEN list FOR
		SELECT alt.id_alternativa,alt.id_pregunta,alt.id_seccion,alt.id_tipo_desplegable,
		alt.nombre_campo,alt.tipo_campo,alt.estado AS altestado,alt.id_cuestionario,pre.descripcion AS pregunta,
		sec.nombre AS nombseccion,tde.id_tipo_desplegable,tde.descripcion AS desctipodesplegable,
		CASE 
			WHEN alt.id_tipo_desplegable IS NOT NULL THEN
				(SELECT STRING_AGG(odp.descripcion, '-' ORDER BY odp.id_opcion_tipo_desplegable)
				 FROM vma.opcion_tipo_desplegable odp)
			ELSE NULL
		END AS opciones
		FROM vma.alternativas alt
		INNER JOIN vma.preguntas pre ON pre.id_pregunta = alt.id_pregunta
		INNER JOIN vma.secciones sec ON sec.id_seccion = alt.id_seccion
		LEFT JOIN vma.tipo_desplegable tde ON tde.id_tipo_desplegable = alt.id_tipo_desplegable
		WHERE alt.id_cuestionario = p_id_cuestionario;
	RETURN list;
END;
$$;


ALTER FUNCTION vma.usp_preguntas_list(p_id_cuestionario integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 215 (class 1259 OID 16572)
-- Name: alternativas; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.alternativas (
    id_alternativa integer NOT NULL,
    id_pregunta integer,
    id_tipo_desplegable integer,
    nombre_campo character varying(100),
    tipo_campo character varying(100),
    estado boolean,
    id_usuario_registro integer,
    fecha_registro timestamp without time zone,
    id_usuario_actualizacion integer,
    fecha_actualizacion timestamp without time zone,
    id_cuestionario integer
);


ALTER TABLE vma.alternativas OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16575)
-- Name: alternativas_id_alternativa_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.alternativas_id_alternativa_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.alternativas_id_alternativa_seq OWNER TO postgres;

--
-- TOC entry 3477 (class 0 OID 0)
-- Dependencies: 216
-- Name: alternativas_id_alternativa_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.alternativas_id_alternativa_seq OWNED BY vma.alternativas.id_alternativa;


--
-- TOC entry 217 (class 1259 OID 16576)
-- Name: archivos; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.archivos (
    id_archivo integer NOT NULL,
    nombre_archivo character varying(255) NOT NULL,
    id_alfresco character varying(255) NOT NULL,
    fecha_creacion timestamp without time zone,
    fecha_actualizacion timestamp without time zone,
    username character varying(100),
    id_registro_vma integer
);


ALTER TABLE vma.archivos OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16581)
-- Name: archivos_id_archivo_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.archivos_id_archivo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.archivos_id_archivo_seq OWNER TO postgres;

--
-- TOC entry 3478 (class 0 OID 0)
-- Dependencies: 218
-- Name: archivos_id_archivo_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.archivos_id_archivo_seq OWNED BY vma.archivos.id_archivo;


--
-- TOC entry 219 (class 1259 OID 16582)
-- Name: cuestionarios; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.cuestionarios (
    id_cuestionario integer NOT NULL,
    nombre character varying(100),
    descripcion character varying(500),
    estado character varying(2),
    id_usuario_creacion integer,
    fecha_creacion timestamp without time zone,
    id_usuario_actualizacion integer,
    fecha_actualizacion timestamp without time zone
);


ALTER TABLE vma.cuestionarios OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16587)
-- Name: cuestionario_id_cuestionario_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.cuestionario_id_cuestionario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.cuestionario_id_cuestionario_seq OWNER TO postgres;

--
-- TOC entry 3479 (class 0 OID 0)
-- Dependencies: 220
-- Name: cuestionario_id_cuestionario_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.cuestionario_id_cuestionario_seq OWNED BY vma.cuestionarios.id_cuestionario;


--
-- TOC entry 221 (class 1259 OID 16588)
-- Name: empresa; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.empresa (
    id_empresa integer NOT NULL,
    nombre character varying(200) NOT NULL,
    regimen character(50),
    tipo character varying(50) NOT NULL,
    estado boolean DEFAULT true NOT NULL,
    id_usuario_registro integer DEFAULT 1,
    created_at timestamp(6) without time zone,
    id_usuario_actualizacion integer,
    updated_at timestamp(6) without time zone
);


ALTER TABLE vma.empresa OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16593)
-- Name: empresa_id_empresa_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.empresa_id_empresa_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.empresa_id_empresa_seq OWNER TO postgres;

--
-- TOC entry 3480 (class 0 OID 0)
-- Dependencies: 222
-- Name: empresa_id_empresa_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.empresa_id_empresa_seq OWNED BY vma.empresa.id_empresa;


--
-- TOC entry 223 (class 1259 OID 16594)
-- Name: ficha_registro; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.ficha_registro (
    id_ficha_registro integer NOT NULL,
    anio character varying(255) NOT NULL,
    fecha_fin date NOT NULL,
    fecha_inicio date NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    id_usuario_registro integer,
    id_usuario_actualizacion integer
);


ALTER TABLE vma.ficha_registro OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16597)
-- Name: ficha_registro_id_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.ficha_registro_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.ficha_registro_id_seq OWNER TO postgres;

--
-- TOC entry 3481 (class 0 OID 0)
-- Dependencies: 224
-- Name: ficha_registro_id_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.ficha_registro_id_seq OWNED BY vma.ficha_registro.id_ficha_registro;


--
-- TOC entry 225 (class 1259 OID 16598)
-- Name: modulo; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.modulo (
    id_modulo integer NOT NULL,
    descripcion character varying
);


ALTER TABLE vma.modulo OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16603)
-- Name: opcion_tipo_desplegable; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.opcion_tipo_desplegable (
    id_opcion_tipo_desplegable integer NOT NULL,
    id_tipo_desplegable integer,
    descripcion character varying(100),
    estado character varying(2),
    id_usuario_registro integer,
    fecha_registro timestamp without time zone,
    id_usuario_actualizacion integer,
    fecha_actualizacion timestamp without time zone
);


ALTER TABLE vma.opcion_tipo_desplegable OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16606)
-- Name: opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq OWNER TO postgres;

--
-- TOC entry 3482 (class 0 OID 0)
-- Dependencies: 227
-- Name: opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq OWNED BY vma.opcion_tipo_desplegable.id_opcion_tipo_desplegable;


--
-- TOC entry 228 (class 1259 OID 16607)
-- Name: preguntas; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.preguntas (
    id_pregunta integer NOT NULL,
    descripcion character varying(5000),
    estado boolean DEFAULT true,
    id_usuario_registro integer,
    fecha_registro timestamp without time zone,
    id_usuario_actualizacion integer,
    fecha_actualizacion timestamp without time zone,
    orden integer,
    id_seccion integer,
    requerido boolean,
    tipo_pregunta character varying
);


ALTER TABLE vma.preguntas OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16613)
-- Name: preguntas_id_pregunta_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.preguntas_id_pregunta_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.preguntas_id_pregunta_seq OWNER TO postgres;

--
-- TOC entry 3483 (class 0 OID 0)
-- Dependencies: 229
-- Name: preguntas_id_pregunta_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.preguntas_id_pregunta_seq OWNED BY vma.preguntas.id_pregunta;


--
-- TOC entry 230 (class 1259 OID 16614)
-- Name: registro_vma; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.registro_vma (
    id_registro_vma integer NOT NULL,
    id_empresa integer,
    estado character varying(100) DEFAULT 'Sin registro'::character varying,
    fecha_creacion timestamp without time zone,
    fecha_actualizacion timestamp without time zone,
    id_ficha_registro integer,
    username character varying(100)
);


ALTER TABLE vma.registro_vma OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16618)
-- Name: registro_vma_id_registro_vma_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.registro_vma_id_registro_vma_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.registro_vma_id_registro_vma_seq OWNER TO postgres;

--
-- TOC entry 3484 (class 0 OID 0)
-- Dependencies: 231
-- Name: registro_vma_id_registro_vma_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.registro_vma_id_registro_vma_seq OWNED BY vma.registro_vma.id_registro_vma;


--
-- TOC entry 232 (class 1259 OID 16619)
-- Name: respuesta_vma; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.respuesta_vma (
    id_respuesta_vma integer NOT NULL,
    id_alternativa integer,
    id_periodo integer,
    respuesta character varying(200),
    estado boolean DEFAULT true,
    id_usuario_registro integer,
    fecha_registro timestamp without time zone,
    id_usuario_actualizacion integer,
    fecha_actualizacion timestamp without time zone,
    id_registro_vma integer NOT NULL,
    id_pregunta integer
);


ALTER TABLE vma.respuesta_vma OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 16623)
-- Name: respuesta_vma_id_respuesta_vma_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.respuesta_vma_id_respuesta_vma_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.respuesta_vma_id_respuesta_vma_seq OWNER TO postgres;

--
-- TOC entry 3485 (class 0 OID 0)
-- Dependencies: 233
-- Name: respuesta_vma_id_respuesta_vma_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.respuesta_vma_id_respuesta_vma_seq OWNED BY vma.respuesta_vma.id_respuesta_vma;


--
-- TOC entry 234 (class 1259 OID 16624)
-- Name: roles; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.roles (
    id_rol integer NOT NULL,
    nombre character varying(30) NOT NULL,
    auth character varying(30),
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE vma.roles OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 16627)
-- Name: roles_id_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.roles_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.roles_id_seq OWNER TO postgres;

--
-- TOC entry 3486 (class 0 OID 0)
-- Dependencies: 235
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.roles_id_seq OWNED BY vma.roles.id_rol;


--
-- TOC entry 236 (class 1259 OID 16628)
-- Name: secciones; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.secciones (
    id_seccion integer NOT NULL,
    nombre character varying(1000),
    orden integer,
    estado boolean,
    id_usuario_registro integer,
    fecha_creacion timestamp without time zone,
    id_usuario_actualizacion integer,
    fecha_actualizacion timestamp without time zone,
    id_cuestionario integer
);


ALTER TABLE vma.secciones OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 16633)
-- Name: secciones_id_seccion_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.secciones_id_seccion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.secciones_id_seccion_seq OWNER TO postgres;

--
-- TOC entry 3487 (class 0 OID 0)
-- Dependencies: 237
-- Name: secciones_id_seccion_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.secciones_id_seccion_seq OWNED BY vma.secciones.id_seccion;


--
-- TOC entry 238 (class 1259 OID 16634)
-- Name: tipo_desplegable; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.tipo_desplegable (
    id_tipo_desplegable integer NOT NULL,
    descripcion character varying(50),
    estado boolean,
    id_usuario_registro integer,
    fecha_registro timestamp without time zone,
    id_usuario_actualizacion integer,
    fecha_actualizacion timestamp without time zone
);


ALTER TABLE vma.tipo_desplegable OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 16637)
-- Name: tipo_desplegable_id_tipo_desplegable_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.tipo_desplegable_id_tipo_desplegable_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.tipo_desplegable_id_tipo_desplegable_seq OWNER TO postgres;

--
-- TOC entry 3488 (class 0 OID 0)
-- Dependencies: 239
-- Name: tipo_desplegable_id_tipo_desplegable_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.tipo_desplegable_id_tipo_desplegable_seq OWNED BY vma.tipo_desplegable.id_tipo_desplegable;


--
-- TOC entry 240 (class 1259 OID 16638)
-- Name: usuarios; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.usuarios (
    id integer NOT NULL,
    username character varying(30) NOT NULL,
    password character varying(100),
    apellidos character varying(30),
    nombres character varying(30),
    correo character varying(100) NOT NULL,
    telefono character varying(255),
    tipo character varying(255) NOT NULL,
    unidad_organica character varying(60),
    eps character varying(255),
    rol_id integer NOT NULL,
    estado boolean NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    id_empresa integer NOT NULL
);


ALTER TABLE vma.usuarios OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 16643)
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: vma; Owner: postgres
--

CREATE SEQUENCE vma.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vma.usuarios_id_seq OWNER TO postgres;

--
-- TOC entry 3489 (class 0 OID 0)
-- Dependencies: 241
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.usuarios_id_seq OWNED BY vma.usuarios.id;


--
-- TOC entry 3240 (class 2604 OID 16644)
-- Name: alternativas id_alternativa; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.alternativas ALTER COLUMN id_alternativa SET DEFAULT nextval('vma.alternativas_id_alternativa_seq'::regclass);


--
-- TOC entry 3241 (class 2604 OID 16645)
-- Name: archivos id_archivo; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.archivos ALTER COLUMN id_archivo SET DEFAULT nextval('vma.archivos_id_archivo_seq'::regclass);


--
-- TOC entry 3242 (class 2604 OID 16646)
-- Name: cuestionarios id_cuestionario; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.cuestionarios ALTER COLUMN id_cuestionario SET DEFAULT nextval('vma.cuestionario_id_cuestionario_seq'::regclass);


--
-- TOC entry 3243 (class 2604 OID 16647)
-- Name: empresa id_empresa; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.empresa ALTER COLUMN id_empresa SET DEFAULT nextval('vma.empresa_id_empresa_seq'::regclass);


--
-- TOC entry 3246 (class 2604 OID 16648)
-- Name: ficha_registro id_ficha_registro; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.ficha_registro ALTER COLUMN id_ficha_registro SET DEFAULT nextval('vma.ficha_registro_id_seq'::regclass);


--
-- TOC entry 3247 (class 2604 OID 16649)
-- Name: opcion_tipo_desplegable id_opcion_tipo_desplegable; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.opcion_tipo_desplegable ALTER COLUMN id_opcion_tipo_desplegable SET DEFAULT nextval('vma.opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq'::regclass);


--
-- TOC entry 3248 (class 2604 OID 16650)
-- Name: preguntas id_pregunta; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.preguntas ALTER COLUMN id_pregunta SET DEFAULT nextval('vma.preguntas_id_pregunta_seq'::regclass);


--
-- TOC entry 3250 (class 2604 OID 16651)
-- Name: registro_vma id_registro_vma; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma ALTER COLUMN id_registro_vma SET DEFAULT nextval('vma.registro_vma_id_registro_vma_seq'::regclass);


--
-- TOC entry 3252 (class 2604 OID 16652)
-- Name: respuesta_vma id_respuesta_vma; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.respuesta_vma ALTER COLUMN id_respuesta_vma SET DEFAULT nextval('vma.respuesta_vma_id_respuesta_vma_seq'::regclass);


--
-- TOC entry 3254 (class 2604 OID 16653)
-- Name: roles id_rol; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.roles ALTER COLUMN id_rol SET DEFAULT nextval('vma.roles_id_seq'::regclass);


--
-- TOC entry 3255 (class 2604 OID 16654)
-- Name: secciones id_seccion; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.secciones ALTER COLUMN id_seccion SET DEFAULT nextval('vma.secciones_id_seccion_seq'::regclass);


--
-- TOC entry 3256 (class 2604 OID 16655)
-- Name: tipo_desplegable id_tipo_desplegable; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.tipo_desplegable ALTER COLUMN id_tipo_desplegable SET DEFAULT nextval('vma.tipo_desplegable_id_tipo_desplegable_seq'::regclass);


--
-- TOC entry 3257 (class 2604 OID 16656)
-- Name: usuarios id; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.usuarios ALTER COLUMN id SET DEFAULT nextval('vma.usuarios_id_seq'::regclass);


--
-- TOC entry 3445 (class 0 OID 16572)
-- Dependencies: 215
-- Data for Name: alternativas; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.alternativas (id_alternativa, id_pregunta, id_tipo_desplegable, nombre_campo, tipo_campo, estado, id_usuario_registro, fecha_registro, id_usuario_actualizacion, fecha_actualizacion, id_cuestionario) FROM stdin;
11	29	\N	\N	textbox	t	\N	2024-06-03 12:20:00	\N	\N	1
12	1	1	Sí	radio	t	\N	2024-05-30 16:19:00	\N	\N	1
13	1	\N	No	textbox	t	\N	2024-05-30 16:25:00	\N	\N	1
1	13	\N	Parcial(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
2	13	\N	Acumulado(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
3	14	\N	Parcial(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
4	14	\N	Acumulado(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
5	26	\N	Parcial(*)	textbox	t	\N	2024-06-03 11:59:00	\N	\N	1
6	26	\N	Acumulado(*)	textbox	t	\N	2024-06-03 11:59:00	\N	\N	1
7	27	\N	Parcial(*)	textbox	t	\N	2024-06-03 11:59:00	\N	\N	1
8	27	\N	Acumulado(*)	textbox	t	\N	2024-06-03 11:59:00	\N	\N	1
9	28	\N	Parcial(*)	textbox	t	\N	2024-06-03 12:20:00	\N	\N	1
10	28	\N	Acumulado(*)	textbox	t	\N	2024-06-03 12:20:00	\N	\N	1
14	7	\N	Parcial(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
15	7	\N	Acumulado(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
16	4	\N	Parcial(*)	textbox	t	\N	2024-05-30 16:28:00	\N	\N	1
17	4	\N	Acumulado(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
18	8	\N	Parcial(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
19	8	\N	Acumulado(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
20	9	\N	Parcial(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
21	9	\N	Acumulado(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
22	5	\N	Parcial(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
23	5	\N	Acumulado(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
24	6	\N	Parcial(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
25	6	\N	Acumulado(*)	textbox	t	\N	2024-06-03 08:56:00	\N	\N	1
26	16	\N	Parcial(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
27	16	\N	Acumulado(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
28	17	\N	Parcial(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
29	17	\N	Acumulado(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
30	18	\N	Parcial(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
31	18	\N	Acumulado(*)	textbox	t	\N	2024-06-03 10:20:00	\N	\N	1
33	25	\N	Parcial(*)	textbox	t	\N	2024-07-12 10:20:00	\N	\N	1
34	25	\N	Acumulado(*)	textbox	t	\N	2024-07-12 10:20:00	\N	\N	1
\.


--
-- TOC entry 3447 (class 0 OID 16576)
-- Dependencies: 217
-- Data for Name: archivos; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.archivos (id_archivo, nombre_archivo, id_alfresco, fecha_creacion, fecha_actualizacion, username, id_registro_vma) FROM stdin;
1	d9a7e837-ee00-44ca-8875-c4ae2fee647d.pdf	1178951f-e8c1-440e-9033-0bc73940c8df	\N	\N	\N	\N
2	prueba-alfresco.pdf	75ba6e6e-d377-4efb-bdc2-1897bb49fa15	\N	\N	\N	\N
3	acta207.pdf	75027fa7-2cd9-4a62-88a2-9eae1f6533b7	2024-07-12 18:12:53.506	\N	\N	\N
4	Reporte_EXP1720197108103.xlsx	4d4f3fb6-f8b2-478d-b48c-86f93b0c452b	2024-07-12 18:20:54.237	\N	\N	\N
5	5. Instructivo configuraciones del entorno de programacion v3.docx	6c983602-8bd3-46f4-b149-008a183a091e	2024-07-12 18:39:10.452	\N	\N	\N
6	Reporte_EXP1720197075776.xlsx	e925ee2f-1dde-4050-a002-5dfdd2ef9b18	2024-07-12 18:47:41.414	\N	\N	\N
\.


--
-- TOC entry 3449 (class 0 OID 16582)
-- Dependencies: 219
-- Data for Name: cuestionarios; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.cuestionarios (id_cuestionario, nombre, descripcion, estado, id_usuario_creacion, fecha_creacion, id_usuario_actualizacion, fecha_actualizacion) FROM stdin;
1	Cuestionario Inicial	Cuestionario Inicial para las preguntas de las secciones de VMA	1	\N	2024-05-30 16:21:00	\N	\N
\.


--
-- TOC entry 3451 (class 0 OID 16588)
-- Dependencies: 221
-- Data for Name: empresa; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.empresa (id_empresa, nombre, regimen, tipo, estado, id_usuario_registro, created_at, id_usuario_actualizacion, updated_at) FROM stdin;
9	EMAPA PASCO  S.A.	NO RAT                                            	PEQUEÑA	t	\N	2024-06-03 11:50:04.899	\N	\N
15	pacasymayo	RAT                                               	PEQUEÑA	t	\N	2024-06-13 17:51:44.812	\N	\N
2	EMAPACOP	NO RAT                                            	PEQUEÑA	t	\N	2024-05-29 10:45:00.155	\N	2024-06-14 10:06:12.908
3	EMSAJUNIN	RAT                                               	PEQUEÑA	t	\N	2024-05-30 11:57:04.899	\N	2024-06-14 10:06:29.415
20	pasco	RAT                                               	PEQUEÑA	t	\N	2024-06-14 10:07:55.257	\N	\N
24	empresa 19-06	RAT                                               	PEQUEÑA	t	\N	2024-06-19 15:11:39.165	\N	\N
17	EPS VIRU	RAT                                               	PEQUEÑA	t	\N	2024-06-14 01:35:16.974	\N	2024-06-14 09:20:11.659
19	EPS VIRU2	RAT                                               	PEQUEÑA	t	\N	2024-06-14 09:15:27.572	\N	2024-06-14 09:22:21.344
10	EPS MOQUEGUA S.A.	NO RAT                                            	MEDIANA	t	\N	2024-06-03 11:55:04.899	\N	\N
11	EPS AGUAS DE LIMA NORTE S.A.	NO RAT                                            	MEDIANA	t	\N	2024-06-03 11:56:04.899	\N	\N
4	EMSAPUNO	RAT                                               	MEDIANA	t	\N	2024-05-30 11:57:04.899	\N	2024-06-14 09:38:15.021
6	EMAPAVIGS S.A.	RAT                                               	MEDIANA	t	\N	2024-06-01 15:57:04.899	\N	2024-06-14 10:07:27.637
21	pacasmayo2	RAT                                               	MEDIANA	t	\N	2024-06-14 10:08:45.117	\N	\N
23	PRUEBA PEQUEÑA RAT	NO RAT                                            	MEDIANA	t	\N	2024-06-17 16:00:40.429	\N	2024-06-17 16:03:04.551
7	SEDA AYACUCHO S.A.	NO RAT                                            	GRANDE	t	\N	2024-06-01 15:57:04.899	\N	\N
8	EMAPA SAN MARTÍN S.A.	RAT                                               	GRANDE	t	\N	2024-06-01 15:57:04.899	\N	\N
12	EPS EMAPICA S.A.	RAT                                               	GRANDE	t	\N	2024-06-03 11:57:04.899	\N	\N
18	sider peru	RAT                                               	GRANDE	t	\N	2024-06-14 08:38:47.504	\N	2024-06-14 08:39:06.741
5	EPS SEDACHIMBOTE S.A.	NO RAT                                            	GRANDE	t	\N	2024-05-31 11:57:04.899	\N	2024-06-14 09:38:24.98
22	EPS SEDAJULIACA S.A.	NO RAT                                            	GRANDE	t	\N	2024-06-17 15:26:32.072	\N	\N
25	Sedapal	RAT                                               	SEDAPAL	t	\N	2024-06-21 09:15:07.627	\N	\N
13	EPS LIBERTAD	RAT                                               	PEQUEÑA	t	\N	2024-06-13 17:32:12.366	\N	\N
1	SUNASS	NINGUNO                                           	NINGUNO	t	\N	2024-05-29 10:45:00.155	\N	2024-06-21 09:36:38.727
\.


--
-- TOC entry 3453 (class 0 OID 16594)
-- Dependencies: 223
-- Data for Name: ficha_registro; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.ficha_registro (id_ficha_registro, anio, fecha_fin, fecha_inicio, created_at, updated_at, id_usuario_registro, id_usuario_actualizacion) FROM stdin;
2	2024	2024-05-01	2024-03-01	2024-06-14 16:36:06.98	\N	1	\N
1	2024	2024-03-01	2024-02-01	\N	2024-06-14 16:38:08.363	\N	\N
5	2024	2024-02-04	2024-03-06	2024-06-17 16:05:15.681	\N	1	\N
3	2024	2024-10-05	2024-08-10	2024-06-14 16:36:32.514	\N	1	\N
4	2024	2024-06-23	2024-06-19	2024-06-15 19:36:56.206	2024-06-15 19:45:57.137	1	\N
8	2024	2024-06-17	2024-06-11	2024-06-20 08:47:22.445	\N	1	\N
7	2024	2024-08-30	2024-08-01	2024-06-19 17:05:16.861	\N	1	\N
9	2024	2024-07-31	2024-06-24	2024-06-20 09:36:16.919	\N	1	\N
\.


--
-- TOC entry 3455 (class 0 OID 16598)
-- Dependencies: 225
-- Data for Name: modulo; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.modulo (id_modulo, descripcion) FROM stdin;
\.


--
-- TOC entry 3456 (class 0 OID 16603)
-- Dependencies: 226
-- Data for Name: opcion_tipo_desplegable; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.opcion_tipo_desplegable (id_opcion_tipo_desplegable, id_tipo_desplegable, descripcion, estado, id_usuario_registro, fecha_registro, id_usuario_actualizacion, fecha_actualizacion) FROM stdin;
3	1	Si	1	\N	2024-05-30 14:17:00	\N	\N
4	1	No	1	\N	2024-05-30 14:17:00	\N	\N
\.


--
-- TOC entry 3458 (class 0 OID 16607)
-- Dependencies: 228
-- Data for Name: preguntas; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.preguntas (id_pregunta, descripcion, estado, id_usuario_registro, fecha_registro, id_usuario_actualizacion, fecha_actualizacion, orden, id_seccion, requerido, tipo_pregunta) FROM stdin;
2	Consignar el nombre del área. (Nombre de área.)	t	\N	2024-05-30 16:23:00	\N	\N	2	1	t	TEXTO
3	Número total de trabajadores de la empresa prestadora dedicadas exclusivamente al cumplimiento de la normativa VMA. (Considerar la cantidad de trabajadores que realizan labor a tiempo completo o parcial durante el año anterior)	t	\N	2024-05-30 16:28:00	\N	\N	3	1	t	NUMERICO
4	Número total de UND identificados. (PARCIAL: Considerar la cantidad de UND identificados entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND identificados hasta el cierre del año en curso (incluye a los identificados en años anteriores).)	t	\N	2024-06-03 08:30:00	\N	\N	1	2	t	NUMERICO
6	Número total de UND a los que se le ha solicitado la presentación del diagrama de flujo y balance hídrico. (PARCIAL Considerar la cantidad de UND a los que se ha solicitado por escrito, entre enero a diciembre en el año en curso. ACUMULADO: Considerar la cantidad de UND a los que se ha solicitado por escrito hasta el cierre del año en curso. (incluye a los que se les solicitó en años anteriores).)	t	\N	2024-06-03 08:30:00	\N	\N	3	2	t	NUMERICO
7	Número total de UND que han presentado el diagrama de flujo y balance hídrico. (PARCIAL: Considerar la cantidad de UND que han presentado dichos documentos entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND que han presentado dichos documentos hasta el cierre del año del año en curso. (incluye a los que presentaron en años anteriores).)	t	\N	2024-06-03 08:30:00	\N	\N	4	2	t	NUMERICO
8	Número total de UND inscritos en el Registro de UND. (PARCIAL: Considerar la cantidad de UND inscritos entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND inscritos hasta el cierre del año en curso. (incluye a los inscritos en años anteriores).)	t	\N	2024-06-03 08:30:00	\N	\N	5	2	t	NUMERICO
9	Número total de UND inscritos en el Registro de UND que cuentan con resultados de las caracterizaciones de las aguas residuales no domésticas.  PARCIAL: Considerar la cantidad de UND con caracterizaciones de sus aguas residuales no domésticas que fueron inscritos entre enero a diciembre del año en curso ACUMULADO: Considerar la cantidad de UND con caracterizaciones de sus aguas residuales no domésticas que fueron inscritos hasta el cierre del año en curso. (incluye a los inscritos en años anteriores).)\r\n	t	\N	2024-06-03 08:30:00	\N	\N	6	2	t	NUMERICO
10	Número total de UND que cuentan con caja de registro o dispositivo similar en la parte externa de su predio. (Considerar la cantidad de UND con caja de registro o dispositivo similar en la parte externa de su predio hasta el cierre del año en curso. (incluye a los UND con caja de registros identificados de años anteriores).)	t	\N	2024-06-03 08:30:00	\N	\N	7	2	t	NUMERICO
11	Número total de UND a los que se ha realizado la toma de muestra inopinada. (Considerar la cantidad de UND a los que se realizó la toma de muestra inopinada entre enero y diciembre del año en curso.)	t	\N	2024-06-03 10:00:00	\N	\N	1	3	t	NUMERICO
12	Número total de tomas de muestras inopinadas. (Considerar el número de veces que se realizó la toma de muestra inopinada entre enero y diciembre del año en curso)	t	\N	2024-06-03 10:00:00	\N	\N	2	3	t	NUMERICO
13	Número de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 1 del Reglamento de VMA. (PARCIAL: Considerar la cantidad de UND que registraron exceso de parámetros del Anexo N° 1 del Reglamento de VMA, entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND que registraron exceso de parámetros del Anexo N° 1 del Reglamento de VMA, hasta el cierre del año en curso. (incluye los de años anteriores).)	t	\N	2024-06-03 10:00:00	\N	\N	1	4	t	NUMERICO
14	Número de UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración. (PARCIAL: Considerar la cantidad de UND a los que se le inició la facturación por el concepto de Pago adicional entre enero a diciembre del año en curso ACUMULADO: Considerar la cantidad total de UND a los que se facturó por concepto de Pago adicional hasta el cierre del año en curso (incluye los de años anteriores).)	t	\N	2024-06-03 10:00:00	\N	\N	2	4	t	NUMERICO
15	Número de UND que realizaron el Pago adicional por exceso de concentración. (Considerar la cantidad de  UND a los que se cobro por concepto de pago adicional durante el año  anterior.)	t	\N	2024-06-03 10:00:00	\N	\N	3	4	t	NUMERICO
16	Número de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 2 del Reglamento de VMA. (PARCIAL: Considerar la cantidad de UND que registraron exceso de parámetros del Anexo N° 2 del Reglamento de VMA entre enero a diciembre del año en curso.  ACUMULADO: Considerar la cantidad de UND que registraron exceso de parámetros del Anexo N° 2 del Reglamento de VMA hasta el cierre del año en curso. (incluye los de años anteriores).)	t	\N	2024-06-03 10:00:00	\N	\N	1	5	t	NUMERICO
17	Número de UND a los que les ha otorgado un plazo adicional (hasta 18 meses) con el fin de implementar las acciones de mejora y acreditar el cumplimiento de los VMA. (PARCIAL: Considerar la cantidad de UND a los que se otorgó un plazo adicional de hasta 18 meses, entre enero a diciembre del año en curso ACUMULADO: Considerar la cantidad de UND a los que se otorgó un plazo adicional de hasta 18 meses hasta el cierre del año en curso. (incluye los de años anteriores).)	t	\N	2024-06-03 10:00:00	\N	\N	2	5	t	NUMERICO
18	Número de UND que han suscrito un acuerdo en el que se establece un plazo otorgado, por única vez, a fin de ejecutar las acciones de mejora y acreditar el cumplimiento de los VMA. (PARCIAL: Considerar la cantidad de UND que suscribieron un acuerdo con la empresa prestadora, entre enero a diciembre del año en curso ACUMULADO: Considerar la cantidad de UND que suscribieron un acuerdo con la empresa prestadora hasta el cierre del año del año en curso. (incluye los de años anteriores).)	t	\N	2024-06-03 10:40:00	\N	\N	3	5	t	NUMERICO
19	Número de reclamos recibidos por VMA.( Considerar la cantidad de reclamos recibidos por la empresa prestadora en primera instancia durante el del año en curso.)	t	\N	2024-06-03 10:45:00	\N	\N	1	6	t	NUMERICO
20	Número de reclamos por VMA resueltos fundados. (Considerar la cantidad de reclamos recibidos por la empresa prestadora resueltos en\r\nprimera instancia a favor del UND durante el del año en curso. )	t	\N	2024-06-03 10:45:00	\N	\N	1	6	t	NUMERICO
22	Ingresos cobrados por concepto de VMA (Exceso de concentración de parámetros que superen los VMA e importe la toma de muestra inopinada y análisis de laboratorio) (*)	t	\N	2024-06-03 10:58:00	\N	\N	3	7	t	NUMERICO
23	Costos total anual incurrido en la identificación, inspección e inscripción de los UND en el Registro de UND.( Considerar los costos (en soles) en la identificación, inspección e inscripción en el Registro de UND, durante el del año en curso.) (*)	t	\N	2024-06-03 10:58:00	\N	\N	3	7	t	NUMERICO
5	Número total de UND inspeccionados. (PARCIAL: Considerar la cantidad de UND inspeccionados entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND inspeccionados hasta al cierre del año en curso. (incluye a los inspeccionados en años anteriores).)	t	\N	2024-06-03 08:30:00	\N	\N	2	2	t	NUMERICO
1	¿Cuenta con un área dedicada al monitoreo y control de los VMA? (Área dependiente o independiente de alguna gerencia o jefatura.)(*)	t	\N	2024-05-30 14:13:00	\N	\N	1	1	t	RADIO
21	Ingresos facturados por concepto de VMA (Considerar los importes facturados por exceso de concentración de VMA y otros referidos al monitoreo durante el del año en curso.) (*)	t	\N	2024-06-03 10:58:00	\N	\N	2	7	t	NUMERICO
24	Costo total anual incurrido por realizar las tomas de muestras Inopinadas (considerar todos los costos (en soles) que son asumidos por la empresa prestadora para realizar la toma de muestra inopinada tales como los costos del análisis y la toma de muestra, movilidad, traslado, entre otros, durante el del año en curso.) (*)	t	\N	2024-06-03 10:58:00	\N	\N	4	7	t	NUMERICO
25	Recursos recaudados por concepto de Pago adicional por exceso de concentración destinados al financiamiento de los costos de inversión de las redes de alcantarillado sanitario. (Considerar el monto total (en soles) al cierre del del año en curso, señalando el proyecto de inversión financiado. PARCIAL: Considerar el monto destinado (en soles) entre enero a diciembre del año en curso\r\nACUMULADO: Considerar el monto destinado (en soles) hasta el cierre del año del año en curso (incluye lo destinado en años anteriores).) (*)	t	\N	2024-06-03 10:58:00	\N	\N	5	7	t	NUMERICO
26	Recursos recaudados por concepto de Pago adicional por exceso de concentración destinados al financiamiento de los costos de operación y mantenimiento de las redes de alcantarillado sanitario. (Considerar el monto total (en soles) al cierre del del año en curso. PARCIAL: Considerar el monto destinado (en soles) entre enero a diciembre del año en curso ACUMULADO: Considerar el monto destinado (en soles) hasta el cierre del año del año en curso (incluye lo destinado en años anteriores).) (*)	t	\N	2024-06-03 10:58:00	\N	\N	6	7	t	NUMERICO
28	Recursos recaudados por concepto de Pago adicional por exceso de concentración destinados al financiamiento de los costos de operación y mantenimiento del servicio de tratamiento de aguas residuales. (Considerar el monto total (en soles) al cierre del del año en curso. PARCIAL: Considerar el monto destinado (en soles) entre enero a diciembre del año en curso ACUMULADO: Considerar el monto destinado (en soles) hasta el cierre del año del año en curso (incluye lo destinado en años anteriores).) (*)	t	\N	2024-06-03 11:46:00	\N	\N	8	7	t	NUMERICO
29	Costo total anual por otros gastos incurridos en la implementación de los VMA (Considerar otros gastos (en soles) no señalados en los ítems anteriores tales como las relacionadas a las inspecciones para la toma de muestra de parte, verificación de la implementación de acciones de mejoras, entre otros, incurridos durante el del año en curso.) (*)	t	\N	2024-06-03 11:46:00	\N	\N	9	7	t	NUMERICO
27	Recursos recaudados por concepto de Pago adicional por exceso de concentración destinados al financiamiento de los costos de inversión del servicio de tratamiento de aguas residuales. (Considerar el monto total (en soles) al cierre del del año en curso, señalando el proyecto de inversión financiado. PARCIAL: Considerar el monto destinado (en soles) entre enero a diciembre del año en curso ACUMULADO: Considerar el monto destinado (en soles) hasta el cierre del año del año en curso (incluye lo destinado en años anteriores).)	t	\N	2024-06-03 11:46:00	\N	\N	7	7	t	NUMERICO
\.


--
-- TOC entry 3460 (class 0 OID 16614)
-- Dependencies: 230
-- Data for Name: registro_vma; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.registro_vma (id_registro_vma, id_empresa, estado, fecha_creacion, fecha_actualizacion, id_ficha_registro, username) FROM stdin;
1	12	INCOMPLETO	2024-07-10 11:44:11.723	\N	9	mquipas
\.


--
-- TOC entry 3462 (class 0 OID 16619)
-- Dependencies: 232
-- Data for Name: respuesta_vma; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.respuesta_vma (id_respuesta_vma, id_alternativa, id_periodo, respuesta, estado, id_usuario_registro, fecha_registro, id_usuario_actualizacion, fecha_actualizacion, id_registro_vma, id_pregunta) FROM stdin;
1	\N	\N	Sí	t	\N	\N	\N	\N	1	1
2	\N	\N	prueba mquipas	t	\N	\N	\N	\N	1	2
3	\N	\N	2	t	\N	\N	\N	\N	1	3
4	16	\N	1	t	\N	\N	\N	\N	1	4
5	22	\N	1	t	\N	\N	\N	\N	1	5
6	24	\N	1	t	\N	\N	\N	\N	1	6
7	\N	\N	1	t	\N	\N	\N	\N	1	11
8	\N	\N	1	t	\N	\N	\N	\N	1	12
9	1	\N	1	t	\N	\N	\N	\N	1	13
10	3	\N	1	t	\N	\N	\N	\N	1	14
11	\N	\N	1	t	\N	\N	\N	\N	1	15
12	30	\N	1	t	\N	\N	\N	\N	1	18
\.


--
-- TOC entry 3464 (class 0 OID 16624)
-- Dependencies: 234
-- Data for Name: roles; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.roles (id_rol, nombre, auth, created_at, updated_at) FROM stdin;
1	Administrador OTI	Administrador OTI	2024-05-29 10:45:00.155582	\N
2	Administrador DAP	Administrador DAP	2024-05-29 10:45:00.155582	\N
4	Consultor	Consultor	2024-05-29 10:45:00.155582	\N
3	Registrador	Registrador	2024-05-29 10:45:00.155582	\N
\.


--
-- TOC entry 3466 (class 0 OID 16628)
-- Dependencies: 236
-- Data for Name: secciones; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.secciones (id_seccion, nombre, orden, estado, id_usuario_registro, fecha_creacion, id_usuario_actualizacion, fecha_actualizacion, id_cuestionario) FROM stdin;
1	I. Organización	1	t	\N	2024-05-30 14:15:00	\N	\N	1
2	II. Identificación, inspección y registro de Usuarios No Domésticos	2	t	\N	2024-06-03 08:49:00	\N	\N	1
3	III. Toma de muestra inopinada	3	t	\N	2024-06-03 08:49:00	\N	\N	1
4	IV. Evaluación de los VMA del Anexo N° 1 del Reglamento de VMA	4	t	\N	2024-06-03 08:49:00	\N	\N	1
5	V. Evaluación de los VMA del Anexo N° 2 del Reglamento de VMA	5	t	\N	2024-06-03 08:49:00	\N	\N	1
6	VI. Atención de reclamos referidos a VMA	6	t	\N	2024-06-03 08:49:00	\N	\N	1
7	VII. Costos de implementación de la normativa VMA	7	t	\N	2024-06-03 08:49:00	\N	\N	1
\.


--
-- TOC entry 3468 (class 0 OID 16634)
-- Dependencies: 238
-- Data for Name: tipo_desplegable; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.tipo_desplegable (id_tipo_desplegable, descripcion, estado, id_usuario_registro, fecha_registro, id_usuario_actualizacion, fecha_actualizacion) FROM stdin;
1	radio	t	\N	2024-05-30 14:16:00	\N	\N
\.


--
-- TOC entry 3470 (class 0 OID 16638)
-- Dependencies: 240
-- Data for Name: usuarios; Type: TABLE DATA; Schema: vma; Owner: postgres
--

COPY vma.usuarios (id, username, password, apellidos, nombres, correo, telefono, tipo, unidad_organica, eps, rol_id, estado, created_at, updated_at, id_empresa) FROM stdin;
24	mcuellar		CUELLAR SANCHEZ	MARIELA	mcuellar@sunass.gob.pe	\N	SUNASS	DU	\N	2	t	2024-06-17 12:27:35.313	\N	1
18	audiencia-maranon		MARAÑON	AUDIENCIA	audiencia-maranon@sunass.gob.pe	32322	SUNASS	\N	Sunass	1	t	2024-06-13 00:37:08.161	2024-06-13 00:39:02.01	1
1	dhuaman		Huaman Ricse	Daniel	dhuaman@sunass.gob.pe	043316589	SUNASS	OFICINA DE TECNOLOGIAS DE INFORMACION	Sunass	1	t	2024-05-29 10:45:00.155582	2024-06-12 22:24:29.057	1
19	scardo		CARDÓ URRUNAGA	SILVIA	scardo@sunass.gob.pe	043316589	SUNASS	DU	Sunass	3	t	2024-06-13 08:32:50.855	\N	1
30	lespinal		ESPINAL VÁSQUEZ	LUIS	lespinal@sunass.gob.pe	\N	SUNASS	TRASS	Sunass	2	t	2024-06-17 18:02:22.427	\N	1
41	jsuarezz	$2a$10$h6T/TXR3/drYvkQ9VEnZRuE4aaalekop6QbqazWNDuAK/tcJDofzS	Suarezz	Jorge	jsuarez@gmail.com	043316589	EPS		JRM SUAREZ ASOCIADOS	4	t	2024-06-21 09:46:08.794	\N	1
16	fmendez3	$2a$10$z/YUvBj95QfOTSrmMBWQCuHCid7kFDK5kI79czW6LEKT2B1oT5TfS	Mendez	Fabian3	fmendez3@xxx.com	5558759563	EPS		Sunass	1	t	2024-06-11 21:20:00.372	\N	1
6	aapumayta	\N	Apumayta	Alan	aapumayta@sunass.gob.pe	\N	SUNASS	\N	Sunass	2	t	2024-05-30 23:00:00	2024-06-13 10:10:03.091	1
32	rcruz		CRUZ TORIBIO	ROBERTO MANUEL	rcruz@sunass.gob.pe	\N	SUNASS	DRT	Sunass	2	t	2024-06-19 13:15:34.285	\N	1
11	jchunga	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	chunga	julio	jchunga@yahoo.com	3105578	EPS	\N	SEDACHIMBOTE	3	t	2024-06-05 10:45:00.155	\N	7
46	mquipas2	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	quipas	mirtha	mquipas2@gmail.com	316589	EPS		\N	3	t	2024-07-01 12:28:31.993	\N	4
45	mquipas	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	quipas blz	mirtha	mquipas@gmail.com	316589	EPS		\N	3	t	2024-07-01 12:25:30.772	\N	4
15	fmendez2	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	Mendez	Fabian2	fmendez2@xxx.com	5558759563	EPS		Sunass	4	t	2024-06-11 21:15:40.156	\N	5
5	aortega2	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	Ortega Velez	adrian jesuss	aortega1@gmail.com	045316589	EPS		PIURA	2	t	2024-05-29 10:45:00.155582	2024-06-14 10:09:41.5	11
34	rsipan		SIPAN MONROY	ROSA LUCRECIA	rsipan@sunass.gob.pe	\N	SUNASS	OAF-URH	Sunass	2	f	2024-06-19 15:02:22.602	\N	1
2	jperez	$2a$10$VIqBuVmNshV.r/zQcQbWnObHreQVTZ8VeQ1E5fjoX4.ehGolR8pOm	perez soto12	juan viliz	jperez@gmail.com	043316588	SUNASS		Sunass	4	t	2024-05-29 10:45:00.155582	2024-07-08 16:22:42.091	2
7	ccastro1	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	castro	carlos	ccastro@gmail.com	45781236	SUNASS	OAJ	SEDACHIMBOTE	1	t	2024-05-29 10:45:00.155	\N	5
35	dprutsky		PRUTSKY LOPEZ	DEBORA	dprutsky@sunass.gob.pe	\N	SUNASS	TRASS	Sunass	2	t	2024-06-19 15:05:08.483	\N	1
10	cquezada	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	quezada	carmen	cquezada@hotmail.com	3105578	EPS	\N	SEDACHIMBOTE	3	t	2024-06-05 10:45:00.155	\N	5
8	pbuitron	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	buitron	peregrina	pbuitron@eps.gob.pe	45781236	EPS	 	SEDAPAL	3	t	2024-06-01 10:45:00.155	\N	25
9	ediaz	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	diaz barthez	eliana	ediaz@gmail.com	045316589	EPS	\N	SEDAPAL	3	t	2024-06-03 10:45:00.155	2024-06-19 15:09:59.9	25
13	jloja	$2a$10$rgfq8GJPXPwQcgiHzHq5fu1/GtULLXyEhiP6vOohYLM7dGKq9OnjS	loja	luis	lloja@gmail.com	043316589	EPS		SAN MARTIN	3	t	2024-06-11 10:10:08.168	\N	7
23	nricse	$2a$10$VzHVi/S4LOqqcAfwKRAOmetnfAU0Mbia5nwPZQPVi63rc5QNiCzFq	ricse	norma esther	nricse@gmail.com	316589	EPS		VILLA MARIA	3	t	2024-06-13 17:48:48.809	\N	8
26	jcarrera	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	Carrera	Juan	jcarrera@gmail.com	986574789	EPS		EPS DE PRUEBA	3	t	2024-06-17 15:45:01.33	\N	3
36	jriverao		RIVERA OBREGÓN	JORGE CHRISTIAN	jriverao@sunass.gob.pe	\N	SUNASS	TRASS	Sunass	4	t	2024-06-19 15:07:53.814	\N	1
43	gporras	$2a$10$enOpsBBPOl6Q0DzC5fXwV.6BU4wn1z3c8aFeG1usEeep7ID8loF9K	porras	goyo	gporras@gmail.com	316589	EPS		PIURA2	3	f	2024-06-27 10:09:22.247	2024-06-27 10:10:24.572	12
21	jdomingo	$2a$10$mUyhU2eQe1k8Qj.0DLYHyeEXokbrQY6bkkJhHpxHzm./by92MjX3y	Domingo	Jose	jdomingo@sedapal.com	985647587	EPS		Sedapal	3	t	2024-06-13 09:57:13.231	2024-06-13 17:19:30.921	25
14	fmendez	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	Mendez	Fabian	fmendez@xxx.com	5558759563	EPS		LORETO	3	t	2024-06-11 21:13:50.465	\N	13
17	jsuarez	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	suarez	jose	jsuarez4@xxx.com	5558759563	SUNASS		Sunass	4	t	2024-06-11 21:29:10.358	\N	1
4	aortega11	$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG	Ortega chavez	adrian	aortega2@gmail.com	045316589	EPS		PIURA	3	t	2024-05-29 10:45:00.155582	2024-06-13 18:18:49.903	11
\.


--
-- TOC entry 3490 (class 0 OID 0)
-- Dependencies: 216
-- Name: alternativas_id_alternativa_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.alternativas_id_alternativa_seq', 34, true);


--
-- TOC entry 3491 (class 0 OID 0)
-- Dependencies: 218
-- Name: archivos_id_archivo_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.archivos_id_archivo_seq', 6, true);


--
-- TOC entry 3492 (class 0 OID 0)
-- Dependencies: 220
-- Name: cuestionario_id_cuestionario_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.cuestionario_id_cuestionario_seq', 1, true);


--
-- TOC entry 3493 (class 0 OID 0)
-- Dependencies: 222
-- Name: empresa_id_empresa_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.empresa_id_empresa_seq', 26, true);


--
-- TOC entry 3494 (class 0 OID 0)
-- Dependencies: 224
-- Name: ficha_registro_id_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.ficha_registro_id_seq', 23, true);


--
-- TOC entry 3495 (class 0 OID 0)
-- Dependencies: 227
-- Name: opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq', 4, true);


--
-- TOC entry 3496 (class 0 OID 0)
-- Dependencies: 229
-- Name: preguntas_id_pregunta_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.preguntas_id_pregunta_seq', 29, true);


--
-- TOC entry 3497 (class 0 OID 0)
-- Dependencies: 231
-- Name: registro_vma_id_registro_vma_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.registro_vma_id_registro_vma_seq', 1, true);


--
-- TOC entry 3498 (class 0 OID 0)
-- Dependencies: 233
-- Name: respuesta_vma_id_respuesta_vma_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.respuesta_vma_id_respuesta_vma_seq', 12, true);


--
-- TOC entry 3499 (class 0 OID 0)
-- Dependencies: 235
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.roles_id_seq', 4, false);


--
-- TOC entry 3500 (class 0 OID 0)
-- Dependencies: 237
-- Name: secciones_id_seccion_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.secciones_id_seccion_seq', 7, true);


--
-- TOC entry 3501 (class 0 OID 0)
-- Dependencies: 239
-- Name: tipo_desplegable_id_tipo_desplegable_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.tipo_desplegable_id_tipo_desplegable_seq', 1, true);


--
-- TOC entry 3502 (class 0 OID 0)
-- Dependencies: 241
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.usuarios_id_seq', 46, true);


--
-- TOC entry 3259 (class 2606 OID 16658)
-- Name: alternativas alternativas_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.alternativas
    ADD CONSTRAINT alternativas_pkey PRIMARY KEY (id_alternativa);


--
-- TOC entry 3261 (class 2606 OID 16660)
-- Name: archivos archivos_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.archivos
    ADD CONSTRAINT archivos_pkey PRIMARY KEY (id_archivo);


--
-- TOC entry 3263 (class 2606 OID 16662)
-- Name: cuestionarios cuestionario_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.cuestionarios
    ADD CONSTRAINT cuestionario_pkey PRIMARY KEY (id_cuestionario);


--
-- TOC entry 3265 (class 2606 OID 16664)
-- Name: empresa empresa_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.empresa
    ADD CONSTRAINT empresa_pkey PRIMARY KEY (id_empresa);


--
-- TOC entry 3268 (class 2606 OID 16666)
-- Name: ficha_registro ficha_registro_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.ficha_registro
    ADD CONSTRAINT ficha_registro_pkey PRIMARY KEY (id_ficha_registro);


--
-- TOC entry 3274 (class 2606 OID 16668)
-- Name: modulo modulo_pk; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.modulo
    ADD CONSTRAINT modulo_pk PRIMARY KEY (id_modulo);


--
-- TOC entry 3276 (class 2606 OID 16670)
-- Name: opcion_tipo_desplegable opcion_tipo_desplegable_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.opcion_tipo_desplegable
    ADD CONSTRAINT opcion_tipo_desplegable_pkey PRIMARY KEY (id_opcion_tipo_desplegable);


--
-- TOC entry 3278 (class 2606 OID 16672)
-- Name: preguntas preguntas_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.preguntas
    ADD CONSTRAINT preguntas_pkey PRIMARY KEY (id_pregunta);


--
-- TOC entry 3280 (class 2606 OID 16674)
-- Name: registro_vma registro_vma_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma
    ADD CONSTRAINT registro_vma_pkey PRIMARY KEY (id_registro_vma);


--
-- TOC entry 3282 (class 2606 OID 16676)
-- Name: respuesta_vma respuesta_vma_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.respuesta_vma
    ADD CONSTRAINT respuesta_vma_pkey PRIMARY KEY (id_respuesta_vma);


--
-- TOC entry 3284 (class 2606 OID 16678)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id_rol);


--
-- TOC entry 3286 (class 2606 OID 16680)
-- Name: secciones secciones_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.secciones
    ADD CONSTRAINT secciones_pkey PRIMARY KEY (id_seccion);


--
-- TOC entry 3288 (class 2606 OID 16682)
-- Name: tipo_desplegable tipo_desplegable_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.tipo_desplegable
    ADD CONSTRAINT tipo_desplegable_pkey PRIMARY KEY (id_tipo_desplegable);


--
-- TOC entry 3270 (class 2606 OID 16684)
-- Name: ficha_registro unique_fecha_fin; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.ficha_registro
    ADD CONSTRAINT unique_fecha_fin UNIQUE (fecha_fin);


--
-- TOC entry 3272 (class 2606 OID 16686)
-- Name: ficha_registro unique_fecha_inicio; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.ficha_registro
    ADD CONSTRAINT unique_fecha_inicio UNIQUE (fecha_inicio);


--
-- TOC entry 3291 (class 2606 OID 16688)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 3266 (class 1259 OID 16689)
-- Name: idx_nombre_eps; Type: INDEX; Schema: vma; Owner: postgres
--

CREATE UNIQUE INDEX idx_nombre_eps ON vma.empresa USING btree (nombre);


--
-- TOC entry 3289 (class 1259 OID 16690)
-- Name: idx_username; Type: INDEX; Schema: vma; Owner: postgres
--

CREATE UNIQUE INDEX idx_username ON vma.usuarios USING btree (username);


--
-- TOC entry 3298 (class 2606 OID 16691)
-- Name: respuesta_vma fk_alternativa; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.respuesta_vma
    ADD CONSTRAINT fk_alternativa FOREIGN KEY (id_alternativa) REFERENCES vma.alternativas(id_alternativa);


--
-- TOC entry 3300 (class 2606 OID 16696)
-- Name: secciones fk_cuestionario; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.secciones
    ADD CONSTRAINT fk_cuestionario FOREIGN KEY (id_cuestionario) REFERENCES vma.cuestionarios(id_cuestionario);


--
-- TOC entry 3301 (class 2606 OID 16701)
-- Name: usuarios fk_empresa; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.usuarios
    ADD CONSTRAINT fk_empresa FOREIGN KEY (id_empresa) REFERENCES vma.empresa(id_empresa);


--
-- TOC entry 3296 (class 2606 OID 16706)
-- Name: registro_vma fk_ficha_registro; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma
    ADD CONSTRAINT fk_ficha_registro FOREIGN KEY (id_ficha_registro) REFERENCES vma.ficha_registro(id_ficha_registro);


--
-- TOC entry 3297 (class 2606 OID 16711)
-- Name: registro_vma fk_id_empresa; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma
    ADD CONSTRAINT fk_id_empresa FOREIGN KEY (id_empresa) REFERENCES vma.empresa(id_empresa);


--
-- TOC entry 3292 (class 2606 OID 16716)
-- Name: alternativas fk_id_pregunta; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.alternativas
    ADD CONSTRAINT fk_id_pregunta FOREIGN KEY (id_pregunta) REFERENCES vma.preguntas(id_pregunta);


--
-- TOC entry 3299 (class 2606 OID 16721)
-- Name: respuesta_vma fk_registro_vma; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.respuesta_vma
    ADD CONSTRAINT fk_registro_vma FOREIGN KEY (id_registro_vma) REFERENCES vma.registro_vma(id_registro_vma);


--
-- TOC entry 3295 (class 2606 OID 16726)
-- Name: preguntas fk_seccion; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.preguntas
    ADD CONSTRAINT fk_seccion FOREIGN KEY (id_seccion) REFERENCES vma.secciones(id_seccion);


--
-- TOC entry 3294 (class 2606 OID 16731)
-- Name: opcion_tipo_desplegable fk_tipo_desplegable; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.opcion_tipo_desplegable
    ADD CONSTRAINT fk_tipo_desplegable FOREIGN KEY (id_tipo_desplegable) REFERENCES vma.tipo_desplegable(id_tipo_desplegable);


--
-- TOC entry 3293 (class 2606 OID 16736)
-- Name: alternativas fk_tipo_desplegable; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.alternativas
    ADD CONSTRAINT fk_tipo_desplegable FOREIGN KEY (id_tipo_desplegable) REFERENCES vma.tipo_desplegable(id_tipo_desplegable);


--
-- TOC entry 3302 (class 2606 OID 16741)
-- Name: usuarios fkqf5elo4jcq7qrt83oi0qmenjo; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.usuarios
    ADD CONSTRAINT fkqf5elo4jcq7qrt83oi0qmenjo FOREIGN KEY (rol_id) REFERENCES vma.roles(id_rol);


-- Completed on 2024-07-12 23:54:47

--
-- PostgreSQL database dump complete
--

