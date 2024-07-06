--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

-- Started on 2024-07-05 19:19:01

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
-- TOC entry 3471 (class 1262 OID 16508)
-- Name: vma; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE vma WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Spanish_Peru.1252';


ALTER DATABASE vma OWNER TO postgres;

\connect vma

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
-- TOC entry 6 (class 2615 OID 16620)
-- Name: vma; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA vma;


ALTER SCHEMA vma OWNER TO postgres;

--
-- TOC entry 242 (class 1255 OID 16621)
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
-- TOC entry 215 (class 1259 OID 16622)
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
-- TOC entry 216 (class 1259 OID 16625)
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
-- TOC entry 3472 (class 0 OID 0)
-- Dependencies: 216
-- Name: alternativas_id_alternativa_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.alternativas_id_alternativa_seq OWNED BY vma.alternativas.id_alternativa;


--
-- TOC entry 217 (class 1259 OID 16626)
-- Name: archivos; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.archivos (
    id_archivo integer NOT NULL,
    nombre character varying(100) NOT NULL,
    ruta character varying(255) NOT NULL,
    fecha_creacion timestamp without time zone,
    fecha_actualizacion timestamp without time zone,
    username character varying(100),
    id_registro_vma integer
);


ALTER TABLE vma.archivos OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16629)
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
-- TOC entry 3473 (class 0 OID 0)
-- Dependencies: 218
-- Name: archivos_id_archivo_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.archivos_id_archivo_seq OWNED BY vma.archivos.id_archivo;


--
-- TOC entry 219 (class 1259 OID 16630)
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
-- TOC entry 220 (class 1259 OID 16635)
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
-- TOC entry 3474 (class 0 OID 0)
-- Dependencies: 220
-- Name: cuestionario_id_cuestionario_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.cuestionario_id_cuestionario_seq OWNED BY vma.cuestionarios.id_cuestionario;


--
-- TOC entry 221 (class 1259 OID 16636)
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
-- TOC entry 222 (class 1259 OID 16641)
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
-- TOC entry 3475 (class 0 OID 0)
-- Dependencies: 222
-- Name: empresa_id_empresa_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.empresa_id_empresa_seq OWNED BY vma.empresa.id_empresa;


--
-- TOC entry 223 (class 1259 OID 16642)
-- Name: ficha_registro; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.ficha_registro (
    id_ficha_registro integer NOT NULL,
    anio character varying(255) NOT NULL,
    fecha_fin date,
    fecha_inicio date NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    id_usuario_registro integer,
    id_usuario_actualizacion integer
);


ALTER TABLE vma.ficha_registro OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16645)
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
-- TOC entry 3476 (class 0 OID 0)
-- Dependencies: 224
-- Name: ficha_registro_id_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.ficha_registro_id_seq OWNED BY vma.ficha_registro.id_ficha_registro;


--
-- TOC entry 225 (class 1259 OID 16646)
-- Name: modulo; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.modulo (
    id_modulo integer NOT NULL,
    descripcion character varying
);


ALTER TABLE vma.modulo OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16651)
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
-- TOC entry 227 (class 1259 OID 16654)
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
-- TOC entry 3477 (class 0 OID 0)
-- Dependencies: 227
-- Name: opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq OWNED BY vma.opcion_tipo_desplegable.id_opcion_tipo_desplegable;


--
-- TOC entry 228 (class 1259 OID 16655)
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
-- TOC entry 229 (class 1259 OID 16661)
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
-- TOC entry 3478 (class 0 OID 0)
-- Dependencies: 229
-- Name: preguntas_id_pregunta_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.preguntas_id_pregunta_seq OWNED BY vma.preguntas.id_pregunta;


--
-- TOC entry 230 (class 1259 OID 16662)
-- Name: registro_vma; Type: TABLE; Schema: vma; Owner: postgres
--

CREATE TABLE vma.registro_vma (
    id_registro_vma integer NOT NULL,
    id_empresa integer,
    id_archivo integer,
    estado character varying(100) DEFAULT 'Sin registro'::character varying,
    fecha_creacion timestamp without time zone,
    fecha_actualizacion timestamp without time zone,
    id_ficha_registro integer,
    username character varying(100)
);


ALTER TABLE vma.registro_vma OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16666)
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
-- TOC entry 3479 (class 0 OID 0)
-- Dependencies: 231
-- Name: registro_vma_id_registro_vma_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.registro_vma_id_registro_vma_seq OWNED BY vma.registro_vma.id_registro_vma;


--
-- TOC entry 232 (class 1259 OID 16667)
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
-- TOC entry 233 (class 1259 OID 16671)
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
-- TOC entry 3480 (class 0 OID 0)
-- Dependencies: 233
-- Name: respuesta_vma_id_respuesta_vma_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.respuesta_vma_id_respuesta_vma_seq OWNED BY vma.respuesta_vma.id_respuesta_vma;


--
-- TOC entry 234 (class 1259 OID 16672)
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
-- TOC entry 235 (class 1259 OID 16675)
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
-- TOC entry 3481 (class 0 OID 0)
-- Dependencies: 235
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.roles_id_seq OWNED BY vma.roles.id_rol;


--
-- TOC entry 236 (class 1259 OID 16676)
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
-- TOC entry 237 (class 1259 OID 16681)
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
-- TOC entry 3482 (class 0 OID 0)
-- Dependencies: 237
-- Name: secciones_id_seccion_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.secciones_id_seccion_seq OWNED BY vma.secciones.id_seccion;


--
-- TOC entry 238 (class 1259 OID 16682)
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
-- TOC entry 239 (class 1259 OID 16685)
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
-- TOC entry 3483 (class 0 OID 0)
-- Dependencies: 239
-- Name: tipo_desplegable_id_tipo_desplegable_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.tipo_desplegable_id_tipo_desplegable_seq OWNED BY vma.tipo_desplegable.id_tipo_desplegable;


--
-- TOC entry 240 (class 1259 OID 16686)
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
-- TOC entry 241 (class 1259 OID 16691)
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
-- TOC entry 3484 (class 0 OID 0)
-- Dependencies: 241
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: vma; Owner: postgres
--

ALTER SEQUENCE vma.usuarios_id_seq OWNED BY vma.usuarios.id;


--
-- TOC entry 3239 (class 2604 OID 16692)
-- Name: alternativas id_alternativa; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.alternativas ALTER COLUMN id_alternativa SET DEFAULT nextval('vma.alternativas_id_alternativa_seq'::regclass);


--
-- TOC entry 3240 (class 2604 OID 16693)
-- Name: archivos id_archivo; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.archivos ALTER COLUMN id_archivo SET DEFAULT nextval('vma.archivos_id_archivo_seq'::regclass);


--
-- TOC entry 3241 (class 2604 OID 16694)
-- Name: cuestionarios id_cuestionario; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.cuestionarios ALTER COLUMN id_cuestionario SET DEFAULT nextval('vma.cuestionario_id_cuestionario_seq'::regclass);


--
-- TOC entry 3242 (class 2604 OID 16695)
-- Name: empresa id_empresa; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.empresa ALTER COLUMN id_empresa SET DEFAULT nextval('vma.empresa_id_empresa_seq'::regclass);


--
-- TOC entry 3245 (class 2604 OID 16696)
-- Name: ficha_registro id_ficha_registro; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.ficha_registro ALTER COLUMN id_ficha_registro SET DEFAULT nextval('vma.ficha_registro_id_seq'::regclass);


--
-- TOC entry 3246 (class 2604 OID 16697)
-- Name: opcion_tipo_desplegable id_opcion_tipo_desplegable; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.opcion_tipo_desplegable ALTER COLUMN id_opcion_tipo_desplegable SET DEFAULT nextval('vma.opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq'::regclass);


--
-- TOC entry 3247 (class 2604 OID 16698)
-- Name: preguntas id_pregunta; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.preguntas ALTER COLUMN id_pregunta SET DEFAULT nextval('vma.preguntas_id_pregunta_seq'::regclass);


--
-- TOC entry 3249 (class 2604 OID 16699)
-- Name: registro_vma id_registro_vma; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma ALTER COLUMN id_registro_vma SET DEFAULT nextval('vma.registro_vma_id_registro_vma_seq'::regclass);


--
-- TOC entry 3251 (class 2604 OID 16700)
-- Name: respuesta_vma id_respuesta_vma; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.respuesta_vma ALTER COLUMN id_respuesta_vma SET DEFAULT nextval('vma.respuesta_vma_id_respuesta_vma_seq'::regclass);


--
-- TOC entry 3253 (class 2604 OID 16701)
-- Name: roles id_rol; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.roles ALTER COLUMN id_rol SET DEFAULT nextval('vma.roles_id_seq'::regclass);


--
-- TOC entry 3254 (class 2604 OID 16702)
-- Name: secciones id_seccion; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.secciones ALTER COLUMN id_seccion SET DEFAULT nextval('vma.secciones_id_seccion_seq'::regclass);


--
-- TOC entry 3255 (class 2604 OID 16703)
-- Name: tipo_desplegable id_tipo_desplegable; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.tipo_desplegable ALTER COLUMN id_tipo_desplegable SET DEFAULT nextval('vma.tipo_desplegable_id_tipo_desplegable_seq'::regclass);


--
-- TOC entry 3256 (class 2604 OID 16704)
-- Name: usuarios id; Type: DEFAULT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.usuarios ALTER COLUMN id SET DEFAULT nextval('vma.usuarios_id_seq'::regclass);


--
-- TOC entry 3439 (class 0 OID 16622)
-- Dependencies: 215
-- Data for Name: alternativas; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.alternativas VALUES (1, 13, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (2, 13, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (3, 14, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (4, 14, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (5, 26, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 11:59:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (6, 26, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 11:59:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (7, 27, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 11:59:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (8, 27, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 11:59:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (9, 28, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 12:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (10, 28, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 12:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (11, 29, NULL, NULL, 'textbox', true, NULL, '2024-06-03 12:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (12, 1, 1, 'Sí', 'radio', true, NULL, '2024-05-30 16:19:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (13, 1, NULL, 'No', 'textbox', true, NULL, '2024-05-30 16:25:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (14, 7, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (15, 7, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (16, 4, NULL, 'Parcial', 'textbox', true, NULL, '2024-05-30 16:28:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (17, 4, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (18, 8, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (19, 8, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (20, 9, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (21, 9, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (22, 5, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (23, 5, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (24, 6, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (25, 6, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 08:56:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (26, 16, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (27, 16, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (28, 17, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (29, 17, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (30, 18, NULL, 'Parcial', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);
INSERT INTO vma.alternativas VALUES (31, 18, NULL, 'Acumulado', 'textbox', true, NULL, '2024-06-03 10:20:00', NULL, NULL, 1);


--
-- TOC entry 3441 (class 0 OID 16626)
-- Dependencies: 217
-- Data for Name: archivos; Type: TABLE DATA; Schema: vma; Owner: postgres
--



--
-- TOC entry 3443 (class 0 OID 16630)
-- Dependencies: 219
-- Data for Name: cuestionarios; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.cuestionarios VALUES (1, 'Cuestionario Inicial', 'Cuestionario Inicial para las preguntas de las secciones de VMA', '1', NULL, '2024-05-30 16:21:00', NULL, NULL);


--
-- TOC entry 3445 (class 0 OID 16636)
-- Dependencies: 221
-- Data for Name: empresa; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.empresa VALUES (7, 'SEDA AYACUCHO S.A.', 'NO RAT                                            ', 'Grande', true, NULL, '2024-06-01 15:57:04.899', NULL, NULL);
INSERT INTO vma.empresa VALUES (8, 'EMAPA SAN MARTÍN S.A.', 'RAT                                               ', 'Grande', true, NULL, '2024-06-01 15:57:04.899', NULL, NULL);
INSERT INTO vma.empresa VALUES (9, 'EMAPA PASCO  S.A.', 'NO RAT                                            ', 'Pequeña', true, NULL, '2024-06-03 11:50:04.899', NULL, NULL);
INSERT INTO vma.empresa VALUES (10, 'EPS MOQUEGUA S.A.', 'NO RAT                                            ', 'Mediana', true, NULL, '2024-06-03 11:55:04.899', NULL, NULL);
INSERT INTO vma.empresa VALUES (11, 'EPS AGUAS DE LIMA NORTE S.A.', 'NO RAT                                            ', 'Mediana', true, NULL, '2024-06-03 11:56:04.899', NULL, NULL);
INSERT INTO vma.empresa VALUES (12, 'EPS EMAPICA S.A.', 'RAT                                               ', 'Grande', true, NULL, '2024-06-03 11:57:04.899', NULL, NULL);
INSERT INTO vma.empresa VALUES (13, 'LIBERTAD', 'RAT                                               ', 'Pequeña', true, NULL, '2024-06-13 17:32:12.366', NULL, NULL);
INSERT INTO vma.empresa VALUES (14, 'Libertad', 'RAT                                               ', 'Mediana', true, NULL, '2024-06-13 17:49:36.34', NULL, NULL);
INSERT INTO vma.empresa VALUES (15, 'pacasymayo', 'RAT                                               ', 'Pequeña', true, NULL, '2024-06-13 17:51:44.812', NULL, NULL);
INSERT INTO vma.empresa VALUES (16, 'prueba eps', 'RAT                                               ', 'Pequeña', true, NULL, '2024-06-13 21:49:21.401', NULL, NULL);
INSERT INTO vma.empresa VALUES (18, 'sider peru', 'RAT                                               ', 'Grande', true, NULL, '2024-06-14 08:38:47.504', NULL, '2024-06-14 08:39:06.741');
INSERT INTO vma.empresa VALUES (17, 'Emp Fabo x12', 'RAT                                               ', 'Pequeña', true, NULL, '2024-06-14 01:35:16.974', NULL, '2024-06-14 09:20:11.659');
INSERT INTO vma.empresa VALUES (19, 'Empresa Fab x34', 'RAT                                               ', 'Pequeña', true, NULL, '2024-06-14 09:15:27.572', NULL, '2024-06-14 09:22:21.344');
INSERT INTO vma.empresa VALUES (4, 'EMSAPUNO', 'RAT                                               ', 'Mediana', true, NULL, '2024-05-30 11:57:04.899', NULL, '2024-06-14 09:38:15.021');
INSERT INTO vma.empresa VALUES (5, 'EPS SEDACHIMBOTE S.A.', 'NO RAT                                            ', 'Grande', true, NULL, '2024-05-31 11:57:04.899', NULL, '2024-06-14 09:38:24.98');
INSERT INTO vma.empresa VALUES (2, 'EMAPACOP', 'NO RAT                                            ', 'Pequeña', true, NULL, '2024-05-29 10:45:00.155', NULL, '2024-06-14 10:06:12.908');
INSERT INTO vma.empresa VALUES (3, 'EMSAJUNIN', 'RAT                                               ', 'Pequeña', true, NULL, '2024-05-30 11:57:04.899', NULL, '2024-06-14 10:06:29.415');
INSERT INTO vma.empresa VALUES (6, 'EMAPAVIGS S.A.', 'RAT                                               ', 'Mediana', true, NULL, '2024-06-01 15:57:04.899', NULL, '2024-06-14 10:07:27.637');
INSERT INTO vma.empresa VALUES (20, 'pasco', 'RAT                                               ', 'Pequeña', true, NULL, '2024-06-14 10:07:55.257', NULL, NULL);
INSERT INTO vma.empresa VALUES (21, 'pacasmayo2', 'RAT                                               ', 'Mediana', true, NULL, '2024-06-14 10:08:45.117', NULL, NULL);
INSERT INTO vma.empresa VALUES (22, 'EPS SEDAJULIACA S.A.', 'NO RAT                                            ', 'Grande', true, NULL, '2024-06-17 15:26:32.072', NULL, NULL);
INSERT INTO vma.empresa VALUES (23, 'PRUEBA PEQUEÑA RAT', 'NO RAT                                            ', 'Mediana', true, NULL, '2024-06-17 16:00:40.429', NULL, '2024-06-17 16:03:04.551');
INSERT INTO vma.empresa VALUES (24, 'empresa 19-06', 'RAT                                               ', 'Pequeña', true, NULL, '2024-06-19 15:11:39.165', NULL, NULL);
INSERT INTO vma.empresa VALUES (25, 'Sedapal', 'RAT                                               ', 'Sedapal', true, NULL, '2024-06-21 09:15:07.627', NULL, NULL);
INSERT INTO vma.empresa VALUES (26, 'sedalib', 'NO RAT                                            ', 'Pequeña', true, NULL, '2024-06-21 09:15:29.662', NULL, NULL);
INSERT INTO vma.empresa VALUES (1, 'EMASUPP', 'NO RAT                                            ', 'Grande', true, NULL, '2024-05-29 10:45:00.155', NULL, '2024-06-21 09:36:38.727');


--
-- TOC entry 3447 (class 0 OID 16642)
-- Dependencies: 223
-- Data for Name: ficha_registro; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.ficha_registro VALUES (2, '2024', '2024-05-01', '2024-03-01', '2024-06-14 16:36:06.98', NULL, 1, NULL);
INSERT INTO vma.ficha_registro VALUES (3, '2024', '2024-10-11', '2024-07-10', '2024-06-14 16:36:32.514', NULL, 1, NULL);
INSERT INTO vma.ficha_registro VALUES (1, '2024', '2024-03-01', '2024-02-01', NULL, '2024-06-14 16:38:08.363', NULL, NULL);
INSERT INTO vma.ficha_registro VALUES (4, '2024', '2024-06-30', '2024-06-19', '2024-06-15 19:36:56.206', '2024-06-15 19:45:57.137', 1, NULL);
INSERT INTO vma.ficha_registro VALUES (5, '2024', '2025-02-04', '2024-03-06', '2024-06-17 16:05:15.681', NULL, 1, NULL);
INSERT INTO vma.ficha_registro VALUES (6, '2024', '2024-06-19', '2024-06-18', '2024-06-19 17:04:06.848', NULL, 1, NULL);
INSERT INTO vma.ficha_registro VALUES (7, '2024', '2024-06-22', '2024-06-20', '2024-06-19 17:05:16.861', NULL, 1, NULL);
INSERT INTO vma.ficha_registro VALUES (8, '2024', '2024-06-26', '2024-06-23', '2024-06-20 08:47:22.445', NULL, 1, NULL);
INSERT INTO vma.ficha_registro VALUES (9, '2024', '2024-07-08', '2024-06-24', '2024-06-20 09:36:16.919', NULL, 1, NULL);
INSERT INTO vma.ficha_registro VALUES (11, '2024', '2024-07-02', '2024-07-01', '2024-06-24 15:09:39.867', '2024-06-24 15:13:03.556', 1, NULL);
INSERT INTO vma.ficha_registro VALUES (10, '2024', '2024-07-31', '2024-07-29', '2024-06-24 14:57:21.159', '2024-06-24 15:21:37.03', 1, NULL);


--
-- TOC entry 3449 (class 0 OID 16646)
-- Dependencies: 225
-- Data for Name: modulo; Type: TABLE DATA; Schema: vma; Owner: postgres
--



--
-- TOC entry 3450 (class 0 OID 16651)
-- Dependencies: 226
-- Data for Name: opcion_tipo_desplegable; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.opcion_tipo_desplegable VALUES (3, 1, 'Si', '1', NULL, '2024-05-30 14:17:00', NULL, NULL);
INSERT INTO vma.opcion_tipo_desplegable VALUES (4, 1, 'No', '1', NULL, '2024-05-30 14:17:00', NULL, NULL);


--
-- TOC entry 3452 (class 0 OID 16655)
-- Dependencies: 228
-- Data for Name: preguntas; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.preguntas VALUES (1, '¿Cuenta con un área dedicada al monitoreo y control de los VMA? (Área dependiente o independiente de alguna gerencia o jefatura.)', true, NULL, '2024-05-30 14:13:00', NULL, NULL, 1, 1, true, 'RADIO');
INSERT INTO vma.preguntas VALUES (2, 'Consignar el nombre del área. (Nombre de área.)', true, NULL, '2024-05-30 16:23:00', NULL, NULL, 2, 1, true, 'TEXTO');
INSERT INTO vma.preguntas VALUES (3, 'Número total de trabajadores de la empresa prestadora dedicadas exclusivamente al cumplimiento de la normativa VMA. (Considerar la cantidad de trabajadores que realizan labor a tiempo completo o parcial durante el año anterior)', true, NULL, '2024-05-30 16:28:00', NULL, NULL, 3, 1, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (4, 'Número total de UND identificados. (PARCIAL: Considerar la cantidad de UND identificados entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND identificados hasta el cierre del año en curso (incluye a los identificados en años anteriores).)', true, NULL, '2024-06-03 08:30:00', NULL, NULL, 1, 2, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (6, 'Número total de UND a los que se le ha solicitado la presentación del diagrama de flujo y balance hídrico. (PARCIAL Considerar la cantidad de UND a los que se ha solicitado por escrito, entre enero a diciembre en el año en curso. ACUMULADO: Considerar la cantidad de UND a los que se ha solicitado por escrito hasta el cierre del año en curso. (incluye a los que se les solicitó en años anteriores).)', true, NULL, '2024-06-03 08:30:00', NULL, NULL, 3, 2, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (7, 'Número total de UND que han presentado el diagrama de flujo y balance hídrico. (PARCIAL: Considerar la cantidad de UND que han presentado dichos documentos entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND que han presentado dichos documentos hasta el cierre del año del año en curso. (incluye a los que presentaron en años anteriores).)', true, NULL, '2024-06-03 08:30:00', NULL, NULL, 4, 2, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (8, 'Número total de UND inscritos en el Registro de UND. (PARCIAL: Considerar la cantidad de UND inscritos entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND inscritos hasta el cierre del año en curso. (incluye a los inscritos en años anteriores).)', true, NULL, '2024-06-03 08:30:00', NULL, NULL, 5, 2, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (9, 'Número total de UND inscritos en el Registro de UND que cuentan con resultados de las caracterizaciones de las aguas residuales no domésticas.  PARCIAL: Considerar la cantidad de UND con caracterizaciones de sus aguas residuales no domésticas que fueron inscritos entre enero a diciembre del año en curso ACUMULADO: Considerar la cantidad de UND con caracterizaciones de sus aguas residuales no domésticas que fueron inscritos hasta el cierre del año en curso. (incluye a los inscritos en años anteriores).)
', true, NULL, '2024-06-03 08:30:00', NULL, NULL, 6, 2, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (10, 'Número total de UND que cuentan con caja de registro o dispositivo similar en la parte externa de su predio. (Considerar la cantidad de UND con caja de registro o dispositivo similar en la parte externa de su predio hasta el cierre del año en curso. (incluye a los UND con caja de registros identificados de años anteriores).)', true, NULL, '2024-06-03 08:30:00', NULL, NULL, 7, 2, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (11, 'Número total de UND a los que se ha realizado la toma de muestra inopinada. (Considerar la cantidad de UND a los que se realizó la toma de muestra inopinada entre enero y diciembre del año en curso.)', true, NULL, '2024-06-03 10:00:00', NULL, NULL, 1, 3, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (12, 'Número total de tomas de muestras inopinadas. (Considerar el número de veces que se realizó la toma de muestra inopinada entre enero y diciembre del año en curso)', true, NULL, '2024-06-03 10:00:00', NULL, NULL, 2, 3, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (13, 'Número de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 1 del Reglamento de VMA. (PARCIAL: Considerar la cantidad de UND que registraron exceso de parámetros del Anexo N° 1 del Reglamento de VMA, entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND que registraron exceso de parámetros del Anexo N° 1 del Reglamento de VMA, hasta el cierre del año en curso. (incluye los de años anteriores).)', true, NULL, '2024-06-03 10:00:00', NULL, NULL, 1, 4, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (14, 'Número de UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración. (PARCIAL: Considerar la cantidad de UND a los que se le inició la facturación por el concepto de Pago adicional entre enero a diciembre del año en curso ACUMULADO: Considerar la cantidad total de UND a los que se facturó por concepto de Pago adicional hasta el cierre del año en curso (incluye los de años anteriores).)', true, NULL, '2024-06-03 10:00:00', NULL, NULL, 2, 4, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (15, 'Número de UND que realizaron el Pago adicional por exceso de concentración. (Considerar la cantidad de  UND a los que se cobro por concepto de pago adicional durante el año  anterior.)', true, NULL, '2024-06-03 10:00:00', NULL, NULL, 3, 4, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (16, 'Número de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 2 del Reglamento de VMA. (PARCIAL: Considerar la cantidad de UND que registraron exceso de parámetros del Anexo N° 2 del Reglamento de VMA entre enero a diciembre del año en curso.  ACUMULADO: Considerar la cantidad de UND que registraron exceso de parámetros del Anexo N° 2 del Reglamento de VMA hasta el cierre del año en curso. (incluye los de años anteriores).)', true, NULL, '2024-06-03 10:00:00', NULL, NULL, 1, 5, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (17, 'Número de UND a los que les ha otorgado un plazo adicional (hasta 18 meses) con el fin de implementar las acciones de mejora y acreditar el cumplimiento de los VMA. (PARCIAL: Considerar la cantidad de UND a los que se otorgó un plazo adicional de hasta 18 meses, entre enero a diciembre del año en curso ACUMULADO: Considerar la cantidad de UND a los que se otorgó un plazo adicional de hasta 18 meses hasta el cierre del año en curso. (incluye los de años anteriores).)', true, NULL, '2024-06-03 10:00:00', NULL, NULL, 2, 5, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (18, 'Número de UND que han suscrito un acuerdo en el que se establece un plazo otorgado, por única vez, a fin de ejecutar las acciones de mejora y acreditar el cumplimiento de los VMA. (PARCIAL: Considerar la cantidad de UND que suscribieron un acuerdo con la empresa prestadora, entre enero a diciembre del año en curso ACUMULADO: Considerar la cantidad de UND que suscribieron un acuerdo con la empresa prestadora hasta el cierre del año del año en curso. (incluye los de años anteriores).)', true, NULL, '2024-06-03 10:40:00', NULL, NULL, 3, 5, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (19, 'Número de reclamos recibidos por VMA.( Considerar la cantidad de reclamos recibidos por la empresa prestadora en primera instancia durante el del año en curso.)', true, NULL, '2024-06-03 10:45:00', NULL, NULL, 1, 6, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (20, 'Número de reclamos por VMA resueltos fundados. (Considerar la cantidad de reclamos recibidos por la empresa prestadora resueltos en
primera instancia a favor del UND durante el del año en curso. )', true, NULL, '2024-06-03 10:45:00', NULL, NULL, 1, 6, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (21, 'Ingresos facturados por concepto de VMA (Considerar los importes facturados por exceso de concentración de VMA y otros referidos al monitoreo durante el del año en curso.)', true, NULL, '2024-06-03 10:58:00', NULL, NULL, 2, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (22, 'Ingresos cobrados por concepto de VMA (Exceso de concentración de parámetros que superen los VMA e importe la toma de muestra inopinada y análisis de laboratorio)', true, NULL, '2024-06-03 10:58:00', NULL, NULL, 3, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (23, 'Costos total anual incurrido en la identificación, inspección e inscripción de los UND en el Registro de UND.( Considerar los costos (en soles) en la identificación, inspección e inscripción en el Registro de UND, durante el del año en curso.)', true, NULL, '2024-06-03 10:58:00', NULL, NULL, 3, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (24, 'Costo total anual incurrido por realizar las tomas de muestras Inopinadas (considerar todos los costos (en soles) que son asumidos por la empresa prestadora para realizar la toma de muestra inopinada tales como los costos del análisis y la toma de muestra, movilidad, traslado, entre otros, durante el del año en curso.)', true, NULL, '2024-06-03 10:58:00', NULL, NULL, 4, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (25, 'Recursos recaudados por concepto de Pago adicional por exceso de concentración destinados al financiamiento de los costos de inversión de las redes de alcantarillado sanitario. (Considerar el monto total (en soles) al cierre del del año en curso, señalando el proyecto de inversión financiado. PARCIAL: Considerar el monto destinado (en soles) entre enero a diciembre del año en curso
ACUMULADO: Considerar el monto destinado (en soles) hasta el cierre del año del año en curso (incluye lo destinado en años anteriores).)', true, NULL, '2024-06-03 10:58:00', NULL, NULL, 5, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (26, 'Recursos recaudados por concepto de Pago adicional por exceso de concentración destinados al financiamiento de los costos de operación y mantenimiento de las redes de alcantarillado sanitario. (Considerar el monto total (en soles) al cierre del del año en curso. PARCIAL: Considerar el monto destinado (en soles) entre enero a diciembre del año en curso ACUMULADO: Considerar el monto destinado (en soles) hasta el cierre del año del año en curso (incluye lo destinado en años anteriores).)', true, NULL, '2024-06-03 10:58:00', NULL, NULL, 6, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (27, 'Recursos recaudados por concepto de Pago adicional por exceso de concentración destinados al financiamiento de los costos de inversión del servicio de tratamiento de aguas residuales. (Considerar el monto total (en soles) al cierre del del año en curso, señalando el proyecto de inversión financiado. PARCIAL: Considerar el monto destinado (en soles) entre enero a diciembre del año en curso ACUMULADO: Considerar el monto destinado (en soles) hasta el cierre del año del año en curso (incluye lo destinado en años anteriores).)', true, NULL, '2024-06-03 11:46:00', NULL, NULL, 7, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (28, 'Recursos recaudados por concepto de Pago adicional por exceso de concentración destinados al financiamiento de los costos de operación y mantenimiento del servicio de tratamiento de aguas residuales. (Considerar el monto total (en soles) al cierre del del año en curso. PARCIAL: Considerar el monto destinado (en soles) entre enero a diciembre del año en curso ACUMULADO: Considerar el monto destinado (en soles) hasta el cierre del año del año en curso (incluye lo destinado en años anteriores).)', true, NULL, '2024-06-03 11:46:00', NULL, NULL, 8, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (29, 'Costo total anual por otros gastos incurridos en la implementación de los VMA (Considerar otros gastos (en soles) no señalados en los ítems anteriores tales como las relacionadas a las inspecciones para la toma de muestra de parte, verificación de la implementación de acciones de mejoras, entre otros, incurridos durante el del año en curso.)', true, NULL, '2024-06-03 11:46:00', NULL, NULL, 9, 7, true, 'NUMERICO');
INSERT INTO vma.preguntas VALUES (5, 'Número total de UND inspeccionados. (PARCIAL: Considerar la cantidad de UND inspeccionados entre enero a diciembre del año en curso. ACUMULADO: Considerar la cantidad de UND inspeccionados hasta al cierre del año en curso. (incluye a los inspeccionados en años anteriores).)', true, NULL, '2024-06-03 08:30:00', NULL, NULL, 2, 2, true, 'NUMERICO');


--
-- TOC entry 3454 (class 0 OID 16662)
-- Dependencies: 230
-- Data for Name: registro_vma; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.registro_vma VALUES (5, 6, NULL, 'INCOMPLETO', '2024-07-03 10:49:35.774', NULL, 1, 'mquipas');
INSERT INTO vma.registro_vma VALUES (8, 3, NULL, 'INCOMPLETO', '2024-07-03 15:52:25.145', NULL, NULL, 'fmendez');
INSERT INTO vma.registro_vma VALUES (1, 1, NULL, 'INCOMPLETO', '2024-06-25 12:35:35', '2024-06-26 12:35:35', 1, 'aortega11');
INSERT INTO vma.registro_vma VALUES (7, 1, NULL, 'INCOMPLETO', '2024-07-03 15:11:17.262', '2024-07-03 18:01:16.361', NULL, 'mquipas2');


--
-- TOC entry 3456 (class 0 OID 16667)
-- Dependencies: 232
-- Data for Name: respuesta_vma; Type: TABLE DATA; Schema: vma; Owner: postgres
--



--
-- TOC entry 3458 (class 0 OID 16672)
-- Dependencies: 234
-- Data for Name: roles; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.roles VALUES (1, 'Administrador OTI', 'Administrador OTI', '2024-05-29 10:45:00.155582', NULL);
INSERT INTO vma.roles VALUES (2, 'Administrador DAP', 'Administrador DAP', '2024-05-29 10:45:00.155582', NULL);
INSERT INTO vma.roles VALUES (4, 'Consultor', 'Consultor', '2024-05-29 10:45:00.155582', NULL);
INSERT INTO vma.roles VALUES (3, 'Registrador', 'Registrador', '2024-05-29 10:45:00.155582', NULL);


--
-- TOC entry 3460 (class 0 OID 16676)
-- Dependencies: 236
-- Data for Name: secciones; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.secciones VALUES (1, 'I. Organización', 1, true, NULL, '2024-05-30 14:15:00', NULL, NULL, 1);
INSERT INTO vma.secciones VALUES (2, 'II. Identificación, inspección y registro de Usuarios No Domésticos', 2, true, NULL, '2024-06-03 08:49:00', NULL, NULL, 1);
INSERT INTO vma.secciones VALUES (3, 'III. Toma de muestra inopinada', 3, true, NULL, '2024-06-03 08:49:00', NULL, NULL, 1);
INSERT INTO vma.secciones VALUES (4, 'IV. Evaluación de los VMA del Anexo N° 1 del Reglamento de VMA', 4, true, NULL, '2024-06-03 08:49:00', NULL, NULL, 1);
INSERT INTO vma.secciones VALUES (5, 'V. Evaluación de los VMA del Anexo N° 2 del Reglamento de VMA', 5, true, NULL, '2024-06-03 08:49:00', NULL, NULL, 1);
INSERT INTO vma.secciones VALUES (6, 'VI. Atención de reclamos referidos a VMA', 6, true, NULL, '2024-06-03 08:49:00', NULL, NULL, 1);
INSERT INTO vma.secciones VALUES (7, 'VII. Costos de implementación de la normativa VMA', 7, true, NULL, '2024-06-03 08:49:00', NULL, NULL, 1);


--
-- TOC entry 3462 (class 0 OID 16682)
-- Dependencies: 238
-- Data for Name: tipo_desplegable; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.tipo_desplegable VALUES (1, 'radio', true, NULL, '2024-05-30 14:16:00', NULL, NULL);


--
-- TOC entry 3464 (class 0 OID 16686)
-- Dependencies: 240
-- Data for Name: usuarios; Type: TABLE DATA; Schema: vma; Owner: postgres
--

INSERT INTO vma.usuarios VALUES (24, 'mcuellar', '', 'CUELLAR SANCHEZ', 'MARIELA', 'mcuellar@sunass.gob.pe', NULL, 'SUNASS', 'DU', NULL, 2, true, '2024-06-17 12:27:35.313', NULL, 1);
INSERT INTO vma.usuarios VALUES (12, 'igalvan', NULL, 'GALVÁN VIDALON', 'IVÁN', 'igalvan@sunass.gob.pe', '987456123', 'SUNASS', 'OTI', 'Sunass', 1, true, '2024-06-05 11:45:00.155', '2024-06-13 10:15:35.785', 1);
INSERT INTO vma.usuarios VALUES (27, 'ntp', '$2a$10$DDgMolG/g7VW1Q5eBNYWpumB6iAHxOyUd2oIVu1n9KOhG/M64sCR2', 'OLIVAREZ OLANO', 'JOHN ALBERT', 'jolivarezo@sunass.gob.pe', '654987321', 'SUNASS', '', 'asd', 3, true, '2024-06-17 15:45:35.863', '2024-06-17 15:47:09.39', 1);
INSERT INTO vma.usuarios VALUES (4, 'aortega11', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'Ortega chavez', 'adrian', 'aortega2@gmail.com', '045316589', 'EPS', '', 'PIURA', 4, true, '2024-05-29 10:45:00.155582', '2024-06-13 18:18:49.903', 1);
INSERT INTO vma.usuarios VALUES (25, 'jcabrera', '', 'CABRERA JAUREGUI', 'JANETH INGEBORE', 'jcabrera@sunass.gob.pe', '956356672', 'SUNASS', 'OTI', 'Sunass', 1, true, '2024-06-17 15:41:29.345', '2024-06-17 15:49:53.58', 1);
INSERT INTO vma.usuarios VALUES (21, 'jdomingo', '$2a$10$mUyhU2eQe1k8Qj.0DLYHyeEXokbrQY6bkkJhHpxHzm./by92MjX3y', 'Domingo', 'Jose', 'jdomingo@sedapal.com', '985647587', 'EPS', '', 'Sedapal', 4, true, '2024-06-13 09:57:13.231', '2024-06-13 17:19:30.921', 1);
INSERT INTO vma.usuarios VALUES (3, 'lportocarrero', '', 'Portocarrero', 'Luis', 'lportocarrero@sunass.gob.pe', '', 'SUNASS', 'OFICINA DE TECNOLOGIAS DE INFORMACION', 'Sunass', 1, true, '2024-05-29 10:45:00.155582', NULL, 1);
INSERT INTO vma.usuarios VALUES (18, 'audiencia-maranon', '', 'MARAÑON', 'AUDIENCIA', 'audiencia-maranon@sunass.gob.pe', '32322', 'SUNASS', NULL, 'Sunass', 1, true, '2024-06-13 00:37:08.161', '2024-06-13 00:39:02.01', 1);
INSERT INTO vma.usuarios VALUES (22, 'yvertiz', '', 'VERTIZ BARRANTES', 'YURI ALEXANDER', 'yvertiz@sunass.gob.pe', '043316581', 'SUNASS', 'OTI', 'Sunass', 2, true, '2024-06-13 17:47:48.289', '2024-06-13 17:48:07.135', 1);
INSERT INTO vma.usuarios VALUES (1, 'dhuaman', '', 'Huaman Ricse', 'Daniel', 'dhuaman@sunass.gob.pe', '043316589', 'SUNASS', 'OFICINA DE TECNOLOGIAS DE INFORMACION', 'Sunass', 1, true, '2024-05-29 10:45:00.155582', '2024-06-12 22:24:29.057', 1);
INSERT INTO vma.usuarios VALUES (28, 'reg', '$2a$10$6bXc0u9gMRpI8rnMKX38oO9rOxCB7t.g2ZUMJYLNhb2IKW7C09FGy', 'reg', 'reg', 'ld@gmail.com', '968457874', 'EPS', '', 'empresa', 3, true, '2024-06-17 17:23:46.748', NULL, 1);
INSERT INTO vma.usuarios VALUES (7, 'ccastro1', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'castro', 'carlos', 'ccastro@gmail.com', '45781236', 'SUNASS', 'OAJ', 'LORETO', 1, true, '2024-05-29 10:45:00.155', NULL, 1);
INSERT INTO vma.usuarios VALUES (8, 'pbuitron', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'buitron', 'peregrina', 'pbuitron@eps.gob.pe', '45781236', 'EPS', ' ', 'JUNIN', 3, true, '2024-06-01 10:45:00.155', NULL, 1);
INSERT INTO vma.usuarios VALUES (10, 'cquezada', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'quezada', 'carmen', 'cquezada@hotmail.com', '3105578', 'EPS', NULL, 'SEDACHIMBOTE', 3, true, '2024-06-05 10:45:00.155', NULL, 1);
INSERT INTO vma.usuarios VALUES (11, 'jchunga', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'chunga', 'julio', 'jchunga@yahoo.com', '3105578', 'EPS', NULL, 'SEDACHIMBOTE', 3, true, '2024-06-05 10:45:00.155', NULL, 1);
INSERT INTO vma.usuarios VALUES (13, 'jloja', '$2a$10$rgfq8GJPXPwQcgiHzHq5fu1/GtULLXyEhiP6vOohYLM7dGKq9OnjS', 'loja', 'luis', 'lloja@gmail.com', '043316589', 'EPS', '', 'SAN MARTIN', 3, true, '2024-06-11 10:10:08.168', NULL, 1);
INSERT INTO vma.usuarios VALUES (23, 'nricse', '$2a$10$VzHVi/S4LOqqcAfwKRAOmetnfAU0Mbia5nwPZQPVi63rc5QNiCzFq', 'ricse', 'norma esther', 'nricse@gmail.com', '316589', 'EPS', '', 'VILLA MARIA', 3, true, '2024-06-13 17:48:48.809', NULL, 1);
INSERT INTO vma.usuarios VALUES (29, 'wer', '$2a$10$PECVOqlX1ulcrF1wpxyFoeY1a8Jw4CCnXxQpIOWeZYlJlaLrmK70y', 'COn', 'Consultor', 'cas@gmail.com', '986547878', 'EPS', '', 'asd', 4, true, '2024-06-17 17:26:38.985', NULL, 1);
INSERT INTO vma.usuarios VALUES (46, 'mquipas2', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'quipas', 'mirtha', 'mquipas2@gmail.com', '316589', 'EPS', '', NULL, 3, true, '2024-07-01 12:28:31.993', NULL, 12);
INSERT INTO vma.usuarios VALUES (37, 'mcuellar', '', 'CUELLAR SANCHEZ', 'MARIELA', 'mcuellar@sunass.gob.pe', NULL, 'SUNASS', 'DU', 'Sunass', 1, true, '2024-06-21 08:58:54.511', NULL, 1);
INSERT INTO vma.usuarios VALUES (19, 'scardo', '', 'CARDÓ URRUNAGA', 'SILVIA', 'scardo@sunass.gob.pe', '043316589', 'SUNASS', 'DU', 'Sunass', 3, true, '2024-06-13 08:32:50.855', NULL, 1);
INSERT INTO vma.usuarios VALUES (30, 'lespinal', '', 'ESPINAL VÁSQUEZ', 'LUIS', 'lespinal@sunass.gob.pe', NULL, 'SUNASS', 'TRASS', 'Sunass', 2, true, '2024-06-17 18:02:22.427', NULL, 1);
INSERT INTO vma.usuarios VALUES (31, 'dsfsdfsfsd', '$2a$10$87Xh.m4A1/YrU0Um7MhUHuWrYzENES7pSwmCijK8lGO/XqKDu4boa', 'sdfdsfds', 'dsfdsfsd', 'dsfds@sdssf', '42454532453', 'EPS', '', 'dsfsdff', 4, true, '2024-06-17 19:01:39.502', NULL, 1);
INSERT INTO vma.usuarios VALUES (20, 'ltenorio', '', 'TENORIO MOSQUERA', 'LIZANDRA', 'ltenorio@sunass.gob.pe', '043316589', 'SUNASS', 'OTI', 'Sunass', 3, true, '2024-06-13 08:34:24.629', '2024-06-13 08:37:04.512', 1);
INSERT INTO vma.usuarios VALUES (26, 'jcarrera', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'Carrera', 'Juan', 'jcarrera@gmail.com', '986574789', 'EPS', '', 'EPS DE PRUEBA', 3, true, '2024-06-17 15:45:01.33', NULL, 1);
INSERT INTO vma.usuarios VALUES (14, 'fmendez', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'Mendez', 'Fabian', 'fmendez@xxx.com', '5558759563', 'EPS', '', 'Sunass', 3, true, '2024-06-11 21:13:50.465', NULL, 3);
INSERT INTO vma.usuarios VALUES (41, 'jsuarezz', '$2a$10$h6T/TXR3/drYvkQ9VEnZRuE4aaalekop6QbqazWNDuAK/tcJDofzS', 'Suarezz', 'Jorge', 'jsuarez@gmail.com', '043316589', 'EPS', '', 'JRM SUAREZ ASOCIADOS', 4, true, '2024-06-21 09:46:08.794', NULL, 1);
INSERT INTO vma.usuarios VALUES (15, 'fmendez2', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'Mendez', 'Fabian2', 'fmendez2@xxx.com', '5558759563', 'EPS', '', 'Sunass', 3, true, '2024-06-11 21:15:40.156', NULL, 5);
INSERT INTO vma.usuarios VALUES (5, 'aortega2', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'Ortega Velez', 'adrian jesuss', 'aortega1@gmail.com', '045316589', 'EPS', '', 'PIURA', 2, true, '2024-05-29 10:45:00.155582', '2024-06-14 10:09:41.5', 1);
INSERT INTO vma.usuarios VALUES (16, 'fmendez3', '$2a$10$z/YUvBj95QfOTSrmMBWQCuHCid7kFDK5kI79czW6LEKT2B1oT5TfS', 'Mendez', 'Fabian3', 'fmendez3@xxx.com', '5558759563', 'EPS', '', 'Sunass', 1, true, '2024-06-11 21:20:00.372', NULL, 1);
INSERT INTO vma.usuarios VALUES (17, 'fmendez4', '$2a$10$4VKoEP4sB868byuRjTCoKu20P68Algw5MKtouiSNP8hTUwj5tQiJC', 'Mendez', 'Fabian4', 'fmendez4@xxx.com', '5558759563', 'EPS', '', 'Sunass', 4, true, '2024-06-11 21:29:10.358', NULL, 1);
INSERT INTO vma.usuarios VALUES (6, 'aapumayta', NULL, 'Apumayta', 'Alan', 'aapumayta@sunass.gob.pe', NULL, 'SUNASS', NULL, 'Sunass', 2, true, '2024-05-30 23:00:00', '2024-06-13 10:10:03.091', 1);
INSERT INTO vma.usuarios VALUES (42, 'reg1', '$2a$10$6CaNomGLqIYJamRMW6IvbO8mN3asQKYaqFUUK5zUsF8HL4l9n5eTW', 'pORTO', 'REGISTRADOR 1', 'LASD@GMAIL.COM', '965874588', 'EPS', '', NULL, 3, true, '2024-06-25 10:23:15.748', NULL, 1);
INSERT INTO vma.usuarios VALUES (32, 'rcruz', '', 'CRUZ TORIBIO', 'ROBERTO MANUEL', 'rcruz@sunass.gob.pe', NULL, 'SUNASS', 'DRT', 'Sunass', 2, true, '2024-06-19 13:15:34.285', NULL, 1);
INSERT INTO vma.usuarios VALUES (2, 'jperez', '$2a$10$x9Y8t7jNUeGDxo8u1cF76eOriwYWOd8QRRd0l.2B7cgMawRysl5tq', 'perez soto12', 'juan vilizxxz', 'jperez@gmail.com', '043316589', 'EPS', '', 'JRM PEREZ ASOCIADOS', 4, false, '2024-05-29 10:45:00.155582', '2024-06-28 15:52:41.829', 1);
INSERT INTO vma.usuarios VALUES (33, 'prueba19', '$2a$10$6Yi2n5ZhbQotdJjVOlYEgODe/df1ChYypsWZXBFOG5wSEYdvZLQOi', 'prueba19', 'prueba19', 'prueba19@gmail.com', '316589', 'EPS', '', 'ninguno', 3, true, '2024-06-19 14:38:25.372', NULL, 1);
INSERT INTO vma.usuarios VALUES (34, 'rsipan', '', 'SIPAN MONROY', 'ROSA LUCRECIA', 'rsipan@sunass.gob.pe', NULL, 'SUNASS', 'OAF-URH', 'Sunass', 2, true, '2024-06-19 15:02:22.602', NULL, 1);
INSERT INTO vma.usuarios VALUES (35, 'dprutsky', '', 'PRUTSKY LOPEZ', 'DEBORA', 'dprutsky@sunass.gob.pe', NULL, 'SUNASS', 'TRASS', 'Sunass', 3, true, '2024-06-19 15:05:08.483', NULL, 1);
INSERT INTO vma.usuarios VALUES (36, 'jriverao', '', 'RIVERA OBREGÓN', 'JORGE CHRISTIAN', 'jriverao@sunass.gob.pe', NULL, 'SUNASS', 'TRASS', 'Sunass', 3, true, '2024-06-19 15:07:53.814', NULL, 1);
INSERT INTO vma.usuarios VALUES (43, 'gporras', '$2a$10$enOpsBBPOl6Q0DzC5fXwV.6BU4wn1z3c8aFeG1usEeep7ID8loF9K', 'porras', 'goyo', 'gporras@gmail.com', '316589', 'EPS', '', 'PIURA2', 4, false, '2024-06-27 10:09:22.247', '2024-06-27 10:10:24.572', 1);
INSERT INTO vma.usuarios VALUES (9, 'ediaz', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'diaz barthez', 'eliana', 'ediaz@gmail.com', '045316589', 'EPS', NULL, 'SEDAPAL', 3, true, '2024-06-03 10:45:00.155', '2024-06-19 15:09:59.9', 4);
INSERT INTO vma.usuarios VALUES (45, 'mquipas', '$2a$10$p0MpFaD9EBH4RGP94DSKbeu5OBcGEnV7f5naC0YwX1XwmntByzbSG', 'quipas blz', 'mirtha', 'mquipas@gmail.com', '316589', 'EPS', '', NULL, 3, true, '2024-07-01 12:25:30.772', NULL, 1);


--
-- TOC entry 3485 (class 0 OID 0)
-- Dependencies: 216
-- Name: alternativas_id_alternativa_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.alternativas_id_alternativa_seq', 32, true);


--
-- TOC entry 3486 (class 0 OID 0)
-- Dependencies: 218
-- Name: archivos_id_archivo_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.archivos_id_archivo_seq', 1, false);


--
-- TOC entry 3487 (class 0 OID 0)
-- Dependencies: 220
-- Name: cuestionario_id_cuestionario_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.cuestionario_id_cuestionario_seq', 1, true);


--
-- TOC entry 3488 (class 0 OID 0)
-- Dependencies: 222
-- Name: empresa_id_empresa_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.empresa_id_empresa_seq', 26, true);


--
-- TOC entry 3489 (class 0 OID 0)
-- Dependencies: 224
-- Name: ficha_registro_id_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.ficha_registro_id_seq', 11, true);


--
-- TOC entry 3490 (class 0 OID 0)
-- Dependencies: 227
-- Name: opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.opcion_tipo_desplegable_id_opcion_tipo_desplegable_seq', 4, true);


--
-- TOC entry 3491 (class 0 OID 0)
-- Dependencies: 229
-- Name: preguntas_id_pregunta_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.preguntas_id_pregunta_seq', 29, true);


--
-- TOC entry 3492 (class 0 OID 0)
-- Dependencies: 231
-- Name: registro_vma_id_registro_vma_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.registro_vma_id_registro_vma_seq', 8, true);


--
-- TOC entry 3493 (class 0 OID 0)
-- Dependencies: 233
-- Name: respuesta_vma_id_respuesta_vma_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.respuesta_vma_id_respuesta_vma_seq', 1, false);


--
-- TOC entry 3494 (class 0 OID 0)
-- Dependencies: 235
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.roles_id_seq', 4, false);


--
-- TOC entry 3495 (class 0 OID 0)
-- Dependencies: 237
-- Name: secciones_id_seccion_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.secciones_id_seccion_seq', 7, true);


--
-- TOC entry 3496 (class 0 OID 0)
-- Dependencies: 239
-- Name: tipo_desplegable_id_tipo_desplegable_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.tipo_desplegable_id_tipo_desplegable_seq', 1, true);


--
-- TOC entry 3497 (class 0 OID 0)
-- Dependencies: 241
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: vma; Owner: postgres
--

SELECT pg_catalog.setval('vma.usuarios_id_seq', 46, true);


--
-- TOC entry 3258 (class 2606 OID 16706)
-- Name: alternativas alternativas_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.alternativas
    ADD CONSTRAINT alternativas_pkey PRIMARY KEY (id_alternativa);


--
-- TOC entry 3260 (class 2606 OID 16708)
-- Name: archivos archivos_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.archivos
    ADD CONSTRAINT archivos_pkey PRIMARY KEY (id_archivo);


--
-- TOC entry 3262 (class 2606 OID 16710)
-- Name: cuestionarios cuestionario_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.cuestionarios
    ADD CONSTRAINT cuestionario_pkey PRIMARY KEY (id_cuestionario);


--
-- TOC entry 3264 (class 2606 OID 16712)
-- Name: empresa empresa_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.empresa
    ADD CONSTRAINT empresa_pkey PRIMARY KEY (id_empresa);


--
-- TOC entry 3266 (class 2606 OID 16714)
-- Name: ficha_registro ficha_registro_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.ficha_registro
    ADD CONSTRAINT ficha_registro_pkey PRIMARY KEY (id_ficha_registro);


--
-- TOC entry 3268 (class 2606 OID 16716)
-- Name: modulo modulo_pk; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.modulo
    ADD CONSTRAINT modulo_pk PRIMARY KEY (id_modulo);


--
-- TOC entry 3270 (class 2606 OID 16718)
-- Name: opcion_tipo_desplegable opcion_tipo_desplegable_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.opcion_tipo_desplegable
    ADD CONSTRAINT opcion_tipo_desplegable_pkey PRIMARY KEY (id_opcion_tipo_desplegable);


--
-- TOC entry 3272 (class 2606 OID 16720)
-- Name: preguntas preguntas_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.preguntas
    ADD CONSTRAINT preguntas_pkey PRIMARY KEY (id_pregunta);


--
-- TOC entry 3274 (class 2606 OID 16722)
-- Name: registro_vma registro_vma_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma
    ADD CONSTRAINT registro_vma_pkey PRIMARY KEY (id_registro_vma);


--
-- TOC entry 3276 (class 2606 OID 16724)
-- Name: respuesta_vma respuesta_vma_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.respuesta_vma
    ADD CONSTRAINT respuesta_vma_pkey PRIMARY KEY (id_respuesta_vma);


--
-- TOC entry 3278 (class 2606 OID 16726)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id_rol);


--
-- TOC entry 3280 (class 2606 OID 16728)
-- Name: secciones secciones_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.secciones
    ADD CONSTRAINT secciones_pkey PRIMARY KEY (id_seccion);


--
-- TOC entry 3282 (class 2606 OID 16730)
-- Name: tipo_desplegable tipo_desplegable_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.tipo_desplegable
    ADD CONSTRAINT tipo_desplegable_pkey PRIMARY KEY (id_tipo_desplegable);


--
-- TOC entry 3284 (class 2606 OID 16732)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 3292 (class 2606 OID 16733)
-- Name: respuesta_vma fk_alternativa; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.respuesta_vma
    ADD CONSTRAINT fk_alternativa FOREIGN KEY (id_alternativa) REFERENCES vma.alternativas(id_alternativa);


--
-- TOC entry 3294 (class 2606 OID 16738)
-- Name: secciones fk_cuestionario; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.secciones
    ADD CONSTRAINT fk_cuestionario FOREIGN KEY (id_cuestionario) REFERENCES vma.cuestionarios(id_cuestionario);


--
-- TOC entry 3295 (class 2606 OID 16789)
-- Name: usuarios fk_empresa; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.usuarios
    ADD CONSTRAINT fk_empresa FOREIGN KEY (id_empresa) REFERENCES vma.empresa(id_empresa);


--
-- TOC entry 3289 (class 2606 OID 16801)
-- Name: registro_vma fk_ficha_registro; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma
    ADD CONSTRAINT fk_ficha_registro FOREIGN KEY (id_ficha_registro) REFERENCES vma.ficha_registro(id_ficha_registro);


--
-- TOC entry 3290 (class 2606 OID 16743)
-- Name: registro_vma fk_id_archivo; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma
    ADD CONSTRAINT fk_id_archivo FOREIGN KEY (id_archivo) REFERENCES vma.archivos(id_archivo);


--
-- TOC entry 3291 (class 2606 OID 16748)
-- Name: registro_vma fk_id_empresa; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.registro_vma
    ADD CONSTRAINT fk_id_empresa FOREIGN KEY (id_empresa) REFERENCES vma.empresa(id_empresa);


--
-- TOC entry 3285 (class 2606 OID 16758)
-- Name: alternativas fk_id_pregunta; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.alternativas
    ADD CONSTRAINT fk_id_pregunta FOREIGN KEY (id_pregunta) REFERENCES vma.preguntas(id_pregunta);


--
-- TOC entry 3293 (class 2606 OID 16763)
-- Name: respuesta_vma fk_registro_vma; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.respuesta_vma
    ADD CONSTRAINT fk_registro_vma FOREIGN KEY (id_registro_vma) REFERENCES vma.registro_vma(id_registro_vma);


--
-- TOC entry 3288 (class 2606 OID 16768)
-- Name: preguntas fk_seccion; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.preguntas
    ADD CONSTRAINT fk_seccion FOREIGN KEY (id_seccion) REFERENCES vma.secciones(id_seccion);


--
-- TOC entry 3287 (class 2606 OID 16773)
-- Name: opcion_tipo_desplegable fk_tipo_desplegable; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.opcion_tipo_desplegable
    ADD CONSTRAINT fk_tipo_desplegable FOREIGN KEY (id_tipo_desplegable) REFERENCES vma.tipo_desplegable(id_tipo_desplegable);


--
-- TOC entry 3286 (class 2606 OID 16778)
-- Name: alternativas fk_tipo_desplegable; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.alternativas
    ADD CONSTRAINT fk_tipo_desplegable FOREIGN KEY (id_tipo_desplegable) REFERENCES vma.tipo_desplegable(id_tipo_desplegable);


--
-- TOC entry 3296 (class 2606 OID 16783)
-- Name: usuarios fkqf5elo4jcq7qrt83oi0qmenjo; Type: FK CONSTRAINT; Schema: vma; Owner: postgres
--

ALTER TABLE ONLY vma.usuarios
    ADD CONSTRAINT fkqf5elo4jcq7qrt83oi0qmenjo FOREIGN KEY (rol_id) REFERENCES vma.roles(id_rol);


-- Completed on 2024-07-05 19:19:01

--
-- PostgreSQL database dump complete
--

