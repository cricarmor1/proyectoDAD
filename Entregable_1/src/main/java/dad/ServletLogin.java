package dad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletLogin extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6201150158950823811L;

	private Map<Integer, SensorTemp> memoria;

	public void init() throws ServletException {
		
		SensorTemp sensor = new SensorTemp(1,100);
		memoria = new HashMap<Integer, SensorTemp>();
		memoria.put(1, sensor);
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(req.getParameter("id_SensorTemp") == null) {
			resp.setContentType("text/html");
			String message2 = "Para usarme tienes que indicar id_SensorTemp como parámetro o bien realizar un Post";
			PrintWriter out = resp.getWriter();
			out.println("<body><h1>" + message2 + "</h1></body>");
		}
		else {
		Integer id_SensorTemp = Integer.parseInt(req.getParameter("id_SensorTemp"));
		Gson gson = new Gson();
		if (memoria.containsKey(id_SensorTemp)) {
			resp.getWriter().println(gson.toJson(memoria.get(id_SensorTemp)));
			resp.setStatus(201);
		} else {
			response(resp, "invalid id");
		}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
		SensorTemp sensor = gson.fromJson(reader, SensorTemp.class);
	
		if (sensor == null || sensor.getid_Placa() == null || sensor.getid_SensorTemp() == null) {
			resp.setStatus(300);
			response(resp, "Put some numbers?");
		}else{
			memoria.put(sensor.getid_SensorTemp(), sensor);
			resp.getWriter().println(gson.toJson(sensor));
			resp.setStatus(201);
		}
	   
	}
	
	/*@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
		User user = gson.fromJson(reader, User.class);
		if (!user.getPassword().equals("") && !user.getUser().equals("") 
				&& userPass.containsKey(user.getUser()) 
				&& userPass.get(user.getUser()).equals(user.getPassword())) {
			userPass.remove(user.getUser());
			resp.getWriter().println(gson.toJson(user));
			resp.setStatus(201);
		}else{
			resp.setStatus(300);
			response(resp, "Wrong user and password");
		}
	   
	}*/

	private void response(HttpServletResponse resp, String msg) throws IOException {
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<t1>" + msg + "</t1>");
		out.println("</body>");
		out.println("</html>");
	}
}