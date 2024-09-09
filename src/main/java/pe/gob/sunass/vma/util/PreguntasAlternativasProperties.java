package pe.gob.sunass.vma.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:preguntas-alternativas.properties")
public class PreguntasAlternativasProperties {

	@Value("${cuestionario.id_pregunta_si_no}")
	private int id_pregunta_si_no;
	
	@Value("${cuestionario.id_pregunta_numero_trabajadores_empresa_prestadora}")
	private int id_pregunta_nro_trabajadores_eps;
	
	@Value("${cuestionario.id_alternativa_und_identificados_parcial}")
	private int id_alternativa_nro_und_identificados_parcial;
	
	@Value("${cuestionario.id_alternativa_und_inspeccionados_parcial}")
	private int id_alternativa_nro_und_inspeccionados_parcial;
	
	@Value("${cuestionario.id_alternativa_solicitaron_diagrama_flujo}")
	private int id_alternativa_solicitaron_diagrama_flujo;
	
	@Value("${cuestionario.id_alternativa_presentaron_diagrama_flujo}")
	private int id_alternativa_presentaron_diagrama_flujo;
	
	@Value("${cuestionario.id_alternativa_und_inscritos}")
	private int id_alternativa_und_inscritos;
	
	@Value("${cuestionario.id_pregunta_und_caja_registro}")
	private int id_pregunta_nro_und_caja_registro;
	
	@Value("${cuestionario.id_pregunta_und_toma_muestra_inopinada}")
	private int id_pregunta_und_toma_muestra_inopinada;

	@Value("${cuestionario.id_pregunta_total_muestras_inopinadas}")
	private int id_pregunta_total_muestras_inopinadas;
	
	@Value("${cuestionario.id_alternativa_und_facturaron_pago_adicional}")
	private int id_alternativa_und_facturaron_pago_adicional;
	
	@Value("${cuestionario.id_alternativa_und_realizaron_pago_adicional}")
	private int id_alternativa_und_realizaron_pago_adicional;
	
	@Value("${cuestionario.id_alternativa_und_sobrepasan_parametro_anexo1}")
	private int id_alternativa_und_sobrepasan_parametro_anexo1;
	
	@Value("${cuestionario.id_alternativa_und_sobrepasan_parametro_anexo2}")
	private int id_alternativa_und_sobrepasan_parametro_anexo2;
	
	@Value("${cuestionario.id_alternativa_und_otorgado_plazo_adicional}")
	private int id_alternativa_und_otorgado_plazo_adicional;
	
	@Value("${cuestionario.id_alternativa_und_suscrito_plazo_otorgado}")
	private int id_alternativa_und_suscrito_plazo_otorgado;
	
	@Value("${cuestionario.id_pregunta_cantidad_reclamos_recibidos_vma}")
	private int id_pregunta_nro_reclamos_recibidos;
	
	@Value("${cuestionario.id_pregunta_cantidad_reclamos_fundados_vma}")
	private int id_pregunta_nro_reclamos_fundados;
	
	@Value("${cuestionario.id_pregunta_costo_total_anual_und}")
	private int id_pregunta_costo_total_anual_und;
	
	@Value("${cuestionario.id_pregunta_costo_total_anual_muestras_inopinadas}")
	private int id_pregunta_costot_anual_muestras_inopinadas;
	
	@Value("${cuestionario.id_pregunta_otros_gastos_implementacion}")
	private int id_pregunta_otros_gastos_implementacion;
	
	@Value("${cuestionario.id_pregunta_ingresos_facturados_vma}")
	private int id_pregunta_ingresos_facturados;
	
	@Value("${cuestionario.id_pregunta_remitio_informe_tecnico}")
	private int id_pregunta_remitio_informe_tecnico;

	public int getId_pregunta_si_no() {
		return id_pregunta_si_no;
	}

	public void setId_pregunta_si_no(int id_pregunta_si_no) {
		this.id_pregunta_si_no = id_pregunta_si_no;
	}

	public int getId_pregunta_nro_trabajadores_eps() {
		return id_pregunta_nro_trabajadores_eps;
	}

	public void setId_pregunta_nro_trabajadores_eps(int id_pregunta_nro_trabajadores_eps) {
		this.id_pregunta_nro_trabajadores_eps = id_pregunta_nro_trabajadores_eps;
	}

	public int getId_alternativa_nro_und_identificados_parcial() {
		return id_alternativa_nro_und_identificados_parcial;
	}

	public void setId_alternativa_nro_und_identificados_parcial(int id_alternativa_nro_und_identificados_parcial) {
		this.id_alternativa_nro_und_identificados_parcial = id_alternativa_nro_und_identificados_parcial;
	}

	public int getId_alternativa_nro_und_inspeccionados_parcial() {
		return id_alternativa_nro_und_inspeccionados_parcial;
	}

	public void setId_alternativa_nro_und_inspeccionados_parcial(int id_alternativa_nro_und_inspeccionados_parcial) {
		this.id_alternativa_nro_und_inspeccionados_parcial = id_alternativa_nro_und_inspeccionados_parcial;
	}

	public int getId_alternativa_solicitaron_diagrama_flujo() {
		return id_alternativa_solicitaron_diagrama_flujo;
	}

	public void setId_alternativa_solicitaron_diagrama_flujo(int id_alternativa_solicitaron_diagrama_flujo) {
		this.id_alternativa_solicitaron_diagrama_flujo = id_alternativa_solicitaron_diagrama_flujo;
	}

	public int getId_alternativa_presentaron_diagrama_flujo() {
		return id_alternativa_presentaron_diagrama_flujo;
	}

	public void setId_alternativa_presentaron_diagrama_flujo(int id_alternativa_presentaron_diagrama_flujo) {
		this.id_alternativa_presentaron_diagrama_flujo = id_alternativa_presentaron_diagrama_flujo;
	}

	public int getId_alternativa_und_inscritos() {
		return id_alternativa_und_inscritos;
	}

	public void setId_alternativa_und_inscritos(int id_alternativa_und_inscritos) {
		this.id_alternativa_und_inscritos = id_alternativa_und_inscritos;
	}

	public int getId_pregunta_nro_und_caja_registro() {
		return id_pregunta_nro_und_caja_registro;
	}

	public void setId_pregunta_nro_und_caja_registro(int id_pregunta_nro_und_caja_registro) {
		this.id_pregunta_nro_und_caja_registro = id_pregunta_nro_und_caja_registro;
	}

	public int getId_pregunta_und_toma_muestra_inopinada() {
		return id_pregunta_und_toma_muestra_inopinada;
	}

	public void setId_pregunta_und_toma_muestra_inopinada(int id_pregunta_und_toma_muestra_inopinada) {
		this.id_pregunta_und_toma_muestra_inopinada = id_pregunta_und_toma_muestra_inopinada;
	}

	public int getId_pregunta_total_muestras_inopinadas() {
		return id_pregunta_total_muestras_inopinadas;
	}

	public void setId_pregunta_total_muestras_inopinadas(int id_pregunta_total_muestras_inopinadas) {
		this.id_pregunta_total_muestras_inopinadas = id_pregunta_total_muestras_inopinadas;
	}

	public int getId_alternativa_und_facturaron_pago_adicional() {
		return id_alternativa_und_facturaron_pago_adicional;
	}

	public void setId_alternativa_und_facturaron_pago_adicional(int id_alternativa_und_facturaron_pago_adicional) {
		this.id_alternativa_und_facturaron_pago_adicional = id_alternativa_und_facturaron_pago_adicional;
	}

	public int getId_alternativa_und_realizaron_pago_adicional() {
		return id_alternativa_und_realizaron_pago_adicional;
	}

	public void setId_alternativa_und_realizaron_pago_adicional(int id_alternativa_und_realizaron_pago_adicional) {
		this.id_alternativa_und_realizaron_pago_adicional = id_alternativa_und_realizaron_pago_adicional;
	}

	public int getId_alternativa_und_sobrepasan_parametro_anexo1() {
		return id_alternativa_und_sobrepasan_parametro_anexo1;
	}

	public void setId_alternativa_und_sobrepasan_parametro_anexo1(int id_alternativa_und_sobrepasan_parametro_anexo1) {
		this.id_alternativa_und_sobrepasan_parametro_anexo1 = id_alternativa_und_sobrepasan_parametro_anexo1;
	}

	public int getId_alternativa_und_sobrepasan_parametro_anexo2() {
		return id_alternativa_und_sobrepasan_parametro_anexo2;
	}

	public void setId_alternativa_und_sobrepasan_parametro_anexo2(int id_alternativa_und_sobrepasan_parametro_anexo2) {
		this.id_alternativa_und_sobrepasan_parametro_anexo2 = id_alternativa_und_sobrepasan_parametro_anexo2;
	}

	public int getId_alternativa_und_otorgado_plazo_adicional() {
		return id_alternativa_und_otorgado_plazo_adicional;
	}

	public void setId_alternativa_und_otorgado_plazo_adicional(int id_alternativa_und_otorgado_plazo_adicional) {
		this.id_alternativa_und_otorgado_plazo_adicional = id_alternativa_und_otorgado_plazo_adicional;
	}

	public int getId_alternativa_und_suscrito_plazo_otorgado() {
		return id_alternativa_und_suscrito_plazo_otorgado;
	}

	public void setId_alternativa_und_suscrito_plazo_otorgado(int id_alternativa_und_suscrito_plazo_otorgado) {
		this.id_alternativa_und_suscrito_plazo_otorgado = id_alternativa_und_suscrito_plazo_otorgado;
	}

	public int getId_pregunta_nro_reclamos_recibidos() {
		return id_pregunta_nro_reclamos_recibidos;
	}

	public void setId_pregunta_nro_reclamos_recibidos(int id_pregunta_nro_reclamos_recibidos) {
		this.id_pregunta_nro_reclamos_recibidos = id_pregunta_nro_reclamos_recibidos;
	}

	public int getId_pregunta_nro_reclamos_fundados() {
		return id_pregunta_nro_reclamos_fundados;
	}

	public void setId_pregunta_nro_reclamos_fundados(int id_pregunta_nro_reclamos_fundados) {
		this.id_pregunta_nro_reclamos_fundados = id_pregunta_nro_reclamos_fundados;
	}

	public int getId_pregunta_costo_total_anual_und() {
		return id_pregunta_costo_total_anual_und;
	}

	public void setId_pregunta_costo_total_anual_und(int id_pregunta_costo_total_anual_und) {
		this.id_pregunta_costo_total_anual_und = id_pregunta_costo_total_anual_und;
	}

	public int getId_pregunta_costot_anual_muestras_inopinadas() {
		return id_pregunta_costot_anual_muestras_inopinadas;
	}

	public void setId_pregunta_costot_anual_muestras_inopinadas(int id_pregunta_costot_anual_muestras_inopinadas) {
		this.id_pregunta_costot_anual_muestras_inopinadas = id_pregunta_costot_anual_muestras_inopinadas;
	}

	public int getId_pregunta_otros_gastos_implementacion() {
		return id_pregunta_otros_gastos_implementacion;
	}

	public void setId_pregunta_otros_gastos_implementacion(int id_pregunta_otros_gastos_implementacion) {
		this.id_pregunta_otros_gastos_implementacion = id_pregunta_otros_gastos_implementacion;
	}

	public int getId_pregunta_ingresos_facturados() {
		return id_pregunta_ingresos_facturados;
	}

	public void setId_pregunta_ingresos_facturados(int id_pregunta_ingresos_facturados) {
		this.id_pregunta_ingresos_facturados = id_pregunta_ingresos_facturados;
	}

	public int getId_pregunta_remitio_informe_tecnico() {
		return id_pregunta_remitio_informe_tecnico;
	}

	public void setId_pregunta_remitio_informe_tecnico(int id_pregunta_remitio_informe_tecnico) {
		this.id_pregunta_remitio_informe_tecnico = id_pregunta_remitio_informe_tecnico;
	}

	
	

}


