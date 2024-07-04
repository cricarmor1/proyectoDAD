package types;

import java.util.Objects;

public class ValorHumedad {
	protected Integer id_ValorHumedad;
	protected Integer id_SensorHumedad;
	protected Long valor;
	protected Long timestamp;
	
	public Integer getId_ValorHumedad() {
		return id_ValorHumedad;
	}
	public void setId_ValorHumedad(Integer id_ValorHumedad) {
		this.id_ValorHumedad = id_ValorHumedad;
	}
	public Integer getId_SensorHumedad() {
		return id_SensorHumedad;
	}
	public void setId_SensorHumedad(Integer id_SensorHumedad) {
		this.id_SensorHumedad = id_SensorHumedad;
	}
	public Long getValor() {
		return valor;
	}
	public void setValor(Long valor) {
		this.valor = valor;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id_SensorHumedad, timestamp, valor, id_ValorHumedad);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValorHumedad other = (ValorHumedad) obj;
		return Objects.equals(id_SensorHumedad, other.id_SensorHumedad) && Objects.equals(timestamp, other.timestamp)
				&& Objects.equals(valor, other.valor) && Objects.equals(id_ValorHumedad, other.id_ValorHumedad);
	}
	@Override
	public String toString() {
		return "ValorHumedad [id_ValorHumedad" + id_ValorHumedad + ", id_SensorHumedad" + id_SensorHumedad
				+ ", valor=" + valor + ", timestamp=" + timestamp + "]";
	}
	public ValorHumedad(Integer id_ValorHumedad, Integer id_SensorHumedad, Long valor, Long timestamp) {
		super();
		this.id_ValorHumedad = id_ValorHumedad;
		this.id_SensorHumedad = id_SensorHumedad;
		this.valor = valor;
		this.timestamp = timestamp;
	}
	public ValorHumedad() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

}
