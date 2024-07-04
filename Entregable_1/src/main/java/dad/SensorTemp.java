package dad;

import java.util.Objects;

public class SensorTemp {
	
	private Integer id_SensorTemp;
	private Integer id_Placa;
	
	public SensorTemp() {
		super();
	}

	public SensorTemp(Integer id_SensorTemp, Integer id_Placa) {
		super();
		this.id_SensorTemp = id_SensorTemp;
		this.id_Placa = id_Placa;
	}

	public Integer getid_SensorTemp() {
		return id_SensorTemp;
	}

	public void setid_SensorTemp(Integer id_SensorTemp) {
		this.id_SensorTemp = id_SensorTemp;
	}

	public Integer getid_Placa() {
		return id_Placa;
	}

	public void setid_Placa(Integer id_Placa) {
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
