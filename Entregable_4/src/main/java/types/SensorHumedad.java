package types;

import java.util.Objects;

public class SensorHumedad {
	
	protected Integer id_SensorHumedad;
	protected Integer id_Placa;
	
	public Integer getId_SensorHumedad() {
		return id_SensorHumedad;
	}
	public void setId_SensorHumedad(Integer id_SensorHumedad) {
		this.id_SensorHumedad = id_SensorHumedad;
	}
	public Integer getId_Placa() {
		return id_Placa;
	}
	public void setId_Placa(Integer id_Placa) {
		this.id_Placa = id_Placa;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id_Placa, id_SensorHumedad);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorHumedad other = (SensorHumedad) obj;
		return Objects.equals(id_Placa, other.id_Placa) && Objects.equals(id_SensorHumedad, other.id_SensorHumedad);
	}
	@Override
	public String toString() {
		return "SensorHumedad [id_SensorHumedad " + id_SensorHumedad + ", id_Placa" + id_Placa + "]";
	}
	public SensorHumedad(Integer id_SensorHumedad, Integer id_Placa) {
		super();
		this.id_SensorHumedad = id_SensorHumedad;
		this.id_Placa = id_Placa;
	}
	public SensorHumedad() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
