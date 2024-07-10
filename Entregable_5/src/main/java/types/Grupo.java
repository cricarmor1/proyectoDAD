package types;

import java.util.Objects;

public class Grupo {

	protected Integer id_Grupo;
	protected String mqtt_ch;
	protected Long timestampTemp;
	protected Long timestampHum;
	
	public Integer getId_Grupo() {
		return id_Grupo;
	}
	public void setId_Grupo(Integer id_Grupo) {
		this.id_Grupo = id_Grupo;
	}
	
	public Long getTimestampTemp() {
		return timestampTemp;
	}
	public void setTimestampTemp(Long timestampTemp) {
		this.timestampTemp = timestampTemp;
	}
	
	public Long getTimestampHum() {
		return timestampHum;
	}
	public void setTimestampHum(Long timestampHum) {
		this.timestampHum = timestampHum;
	}
	
	public String getMqtt_ch() {
		return mqtt_ch;
	}
	public void setMqtt_ch(String mqtt_ch) {
		this.mqtt_ch = mqtt_ch;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id_Grupo, mqtt_ch,timestampTemp, timestampHum);
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
		return Objects.equals(id_Grupo, other.id_Grupo) && Objects.equals(mqtt_ch, other.mqtt_ch) && Objects.equals(timestampTemp, other.timestampTemp) && Objects.equals(timestampHum, other.timestampHum)
				;
	}
	@Override
	public String toString() {
		return "Grupo [id_Grupo=" + id_Grupo +  ", mqtt_ch=" + mqtt_ch + ",timestampTemp="+ timestampTemp +",timestampHum ="+ timestampHum+"]";
	}
	public Grupo(Integer id_Grupo, String mqtt_ch, Long timestampTemp, Long timestampHum) {
		super();
		this.id_Grupo = id_Grupo;
		
		this.mqtt_ch = mqtt_ch;
		this.timestampTemp = timestampTemp;
		this.timestampHum = timestampHum;
	}
	public Grupo() {
		super();
		// TODO Auto-generated constructor stub
	}
}
	