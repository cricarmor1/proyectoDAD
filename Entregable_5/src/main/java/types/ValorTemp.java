package types;

import java.util.Objects;

public class ValorTemp {
	protected Integer id_ValorTemp;
	protected Integer id_SensorTemp;
	protected Float valor;
	protected Long timestamp;
	
	public Integer getId_ValorTemp() {
		return id_ValorTemp;
	}
	public void setId_ValorTemp(Integer id_ValorTemp) {
		this.id_ValorTemp = id_ValorTemp;
	}
	public Integer getId_SensorTemp() {
		return id_SensorTemp;
	}
	public void setId_SensorTemp(Integer id_SensorTemp) {
		this.id_SensorTemp = id_SensorTemp;
	}
	public Float getValor() {
		return valor;
	}
	public void setValor(Float valor) {
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
		return Objects.hash(id_SensorTemp, timestamp, valor, id_ValorTemp);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValorTemp other = (ValorTemp) obj;
		return Objects.equals(id_SensorTemp, other.id_SensorTemp) && Objects.equals(timestamp, other.timestamp)
				&& Objects.equals(valor, other.valor) && Objects.equals(id_ValorTemp, other.id_ValorTemp);
	}
	@Override
	public String toString() {
		return "ValorTemp [id_ValorTemp" + id_ValorTemp + ", id_SensorTemp" + id_SensorTemp
				+ ", valor=" + valor + ", timestamp=" + timestamp + "]";
	}
	public ValorTemp(Integer id_ValorTemp, Integer id_SensorTemp, Float valor, Long timestamp) {
		super();
		this.id_ValorTemp = id_ValorTemp;
		this.id_SensorTemp = id_SensorTemp;
		this.valor = valor;
		this.timestamp = timestamp;
	}
	public ValorTemp() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

}
