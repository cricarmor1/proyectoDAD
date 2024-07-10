package types;

import java.util.Objects;

public class EstadoActuador {
	
	protected Integer id_EstadoActuador;
	protected Integer id_Actuador;
	protected Float estado;
	protected Long timestamp;
	
	public Integer getId_EstadoActuador() {
		return id_EstadoActuador;
	}
	public void setId_EstadoActuador(Integer id_EstadoActuador) {
		this.id_EstadoActuador = id_EstadoActuador;
	}
	public Integer getId_Actuador() {
		return id_Actuador;
	}
	public void setId_Actuador(Integer id_Actuador) {
		this.id_Actuador = id_Actuador;
	}
	public Float getEstado() {
		return estado;
	}
	public void setEstado(Float estado) {
		this.estado = estado;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id_Actuador, timestamp, estado, id_EstadoActuador);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EstadoActuador other = (EstadoActuador) obj;
		return Objects.equals(id_Actuador, other.id_Actuador) && Objects.equals(timestamp, other.timestamp)
				&& Objects.equals(estado, other.estado) && Objects.equals(id_EstadoActuador, other.id_EstadoActuador);
	}
	@Override
	public String toString() {
		return "EstadoActuador [id_EstadoActuador" + id_EstadoActuador + ", id_Actuador" + id_Actuador + ", estado="
				+ estado + ", timestamp=" + timestamp + "]";
	}
	public EstadoActuador(Integer id_EstadoActuador, Integer id_Actuador, Float estado, Long timestamp) {
		super();
		this.id_EstadoActuador = id_EstadoActuador;
		this.id_Actuador = id_Actuador;
		this.estado = estado;
		this.timestamp = timestamp;
	}
	public EstadoActuador() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
