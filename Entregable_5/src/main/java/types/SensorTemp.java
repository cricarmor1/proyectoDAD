package types;

import java.util.Objects;

public class SensorTemp {
	
	protected Integer id_SensorTemp;
	protected Integer id_Placa;
	
	public SensorTemp() {
		super();
	}

	public SensorTemp(Integer id_SensorTemp, Integer id_Placa) {
		super();
		this.id_SensorTemp = id_SensorTemp;
		this.id_Placa = id_Placa;
	}

	public Integer getId_SensorTemp() {
		return id_SensorTemp;
	}

	public void setId_SensorTemp(Integer id_SensorTemp) {
		this.id_SensorTemp = id_SensorTemp;
	}

	public Integer getId_Placa() {
		return id_Placa;
	}

	public void setId_Placa(Integer id_Placa) {
		this.id_Placa = id_Placa;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id_SensorTemp, id_Placa);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorTemp other = (SensorTemp) obj;
		return Objects.equals(id_Placa, other.id_Placa) && Objects.equals(id_SensorTemp, other.id_SensorTemp);
				
	}

	
	
	
}
