package types;

import java.util.Objects;

public class Placa {
	
	protected Integer id_Placa;
	protected Integer id_Grupo;
	protected String mqtt_ch;
	public Integer getId_Placa() {
		return id_Placa;
	}
	public void setId_Placa(Integer id_Placa) {
		this.id_Placa = id_Placa;
	}
	public Integer getId_Grupo() {
		return id_Grupo;
	}
	public void setId_Grupo(Integer id_Grupo) {
		this.id_Grupo = id_Grupo;
	}
	public String getMqtt_ch() {
		return mqtt_ch;
	}
	public void setMqtt_ch(String mqtt_ch) {
		this.mqtt_ch = mqtt_ch;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id_Grupo, mqtt_ch, id_Placa);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Placa other = (Placa) obj;
		return Objects.equals(id_Grupo, other.id_Grupo) && Objects.equals(mqtt_ch, other.mqtt_ch)
				&& Objects.equals(id_Placa, other.id_Placa);
	}
	@Override
	public String toString() {
		return "Placa [id_Placa" + id_Placa + ", id_Grupo " + id_Grupo + ", mqtt_ch=" + mqtt_ch + "]";
	}
	public Placa(Integer id_Placa, Integer id_Grupo, String mqtt_ch) {
		super();
		this.id_Placa = id_Placa;
		this.id_Grupo = id_Grupo;
		this.mqtt_ch = mqtt_ch;
	}
	public Placa() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
}
