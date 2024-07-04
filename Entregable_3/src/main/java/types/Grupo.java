package types;

import java.util.Objects;

public class Grupo {

	protected Integer id_Grupo;
	protected Integer mqtt_ch;
	
	public Integer getId_Grupo() {
		return id_Grupo;
	}
	public void setId_Grupo(Integer id_Grupo) {
		this.id_Grupo = id_Grupo;
	}
	
	public Integer getMqtt_ch() {
		return mqtt_ch;
	}
	public void setMqtt_ch(Integer mqtt_ch) {
		this.mqtt_ch = mqtt_ch;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id_Grupo, mqtt_ch);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grupo other = (Grupo) obj;
		return Objects.equals(id_Grupo, other.id_Grupo) && Objects.equals(mqtt_ch, other.mqtt_ch)
				;
	}
	@Override
	public String toString() {
		return "Grupo [id_Grupo=" + id_Grupo +  ", mqtt_ch=" + mqtt_ch + "]";
	}
	public Grupo(Integer id_Grupo, Integer mqtt_ch) {
		super();
		this.id_Grupo = id_Grupo;
		
		this.mqtt_ch = mqtt_ch;
	}
	public Grupo() {
		super();
		// TODO Auto-generated constructor stub
	}
}
	