package types;

import java.util.Objects;

public class Actuador {
	
	protected Integer id_Actuador;
	protected Integer id_Placa;
	
	
	public Integer getid_Actuador() {
		return id_Actuador;
	}
	public void setid_Actuador(Integer id_Actuador) {
		this.id_Actuador = id_Actuador;
	}
	public Integer getid_Placa() {
		return id_Placa;
	}
	public void setid_Placa(Integer id_Placa) {
		this.id_Placa = id_Placa;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id_Actuador, id_Placa);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actuador other = (Actuador) obj;
		return Objects.equals(id_Actuador, other.id_Actuador) && Objects.equals(id_Placa, other.id_Placa);
	}
	public Actuador(Integer id_Actuador, Integer id_Placa) {
		super();
		this.id_Actuador = id_Actuador;
		this.id_Placa = id_Placa;
	}
	public Actuador() {
		super();
		
	}
	@Override
	public String toString() {
		return "Actuador [id_Actuador=" + id_Actuador + ", id_Placa=" + id_Placa + "]";
	}
	
	
	
	
	
	
}