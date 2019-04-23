package se.u3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.tools.javac.util.StringUtils;

/**
 * Servlet implementation class DemoServlet
 */

@WebServlet("/DemoServlet")
public class DemoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static int totalCars = 0;
	private static int totalStay = 0;
	private static float totalAmount = 0;

	/**
	 * @see HttpServlet#HttpServlet()
	 */

	public DemoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		if (request.getQueryString() == null || request.getQueryString().isEmpty()
				|| request.getQueryString().isBlank())
			return;
		String[] requestParamString = request.getQueryString().split("=");
		String command = requestParamString[0];
		String param = requestParamString[1];
		response.setContentType("text/html");
		if ("fun".equals(command) && "sum".equals(param)) {
			Float sum = getPersistentSum();

//			PrintWriter out = response.getWriter();
//			out.println(sum);
			response.getWriter().append(sum.toString());
			System.out.println("sum = " + sum);
		} else if (("fun".equals(command) && "average".equalsIgnoreCase(param))) {
			response.getWriter().append(" Fee:" + totalAmount / totalCars)
					.append("  Duration:" + totalStay / totalCars);
		} else {
			System.out.println("Invalid Command: " + request.getQueryString());
		}

		// response.getWriter().append("Sum: ").append(request.getContextPath());
	}

	private Float getPersistentSum() {

		Float sum;

		ServletContext application = getApplication();
		sum = (Float) application.getAttribute("sum");
		if (sum == null)
			sum = 0.0f;

		// application.setAttribute("sum", sum);
		return sum;
	}

	private ServletContext getApplication() {

		return getServletConfig().getServletContext();
	}

	private static String getBody(HttpServletRequest request) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Float sum = getPersistentSum();
		String body = getBody(request);
		System.out.println(body);
		String[] params = body.split(",");
		String event = params[0];

		String priceString = params[5];
		if (!"_".equals(priceString)) {
			// strip € in front, parse the number behind
			float price = Float.parseFloat(priceString.split(" ")[2]);
			sum += price;
			// store sum persistently in ServletContext
			getApplication().setAttribute("sum", sum);
			if ("leave".equalsIgnoreCase(event)) {
				totalCars++;
				totalAmount = totalAmount + price;
				totalStay = totalStay + Integer.parseInt(params[4]);

			}
			getApplication().setAttribute("avgAmount", totalAmount / totalCars);
			getApplication().setAttribute("avgStay", totalStay / totalCars);

		}
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(sum);
	}

}
