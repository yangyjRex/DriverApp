package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import http.HttpGetDemo;
import http.HttpPutDemo;
import model.RenewalNotices;
import response.JsonResponseList;

/**
 * Servlet implementation class OfficerUpdateServlet
 */
// manuallyUpdate
public class OfficerUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OfficerUpdateServlet() {
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
		Gson gson = new Gson();
		String address = request.getParameter("address").trim();
		String email = request.getParameter("email").trim();
		String status = request.getParameter("status").trim();
		String reviewResult = request.getParameter("reviewResult").trim();
		try {
			float amount = Float.parseFloat(request.getParameter("amount").trim());
		} catch (Exception e) {
			float amount = -1;
		}
		int nid = Integer.parseInt(request.getParameter("nid").trim());
		// get the notice
		String url = "http://localhost:8090/renewals/" + nid;
		HttpGetDemo restRequest = new HttpGetDemo("123456", url);
		String responseJson = restRequest.sendGetRequest();
		JsonResponseList<RenewalNotices> jsonObject = new Gson().fromJson(responseJson,
				new TypeToken<JsonResponseList<RenewalNotices>>() {
				}.getType());
		List<RenewalNotices> notices = jsonObject.getList();
		if (notices != null || notices.size() == 1) {
			RenewalNotices notice = notices.get(0);
			if (!address.equals(""))
				notice.setAddress(address);
			if (!email.equals(""))
				notice.setContact_email(email);
			notice.setStatus(status);
			notice.setReview_result(reviewResult);
			// send put to rest server
			try {
				url = "http://localhost:8090/renewals";
				HttpPutDemo putRequest = new HttpPutDemo(url, "123456", gson.toJson(notice));
				String putResponse = putRequest.sendPutRequest();
				jsonObject = gson.fromJson(putResponse, new TypeToken<JsonResponseList<RenewalNotices>>() {
				}.getType());
				if (jsonObject.getCode() == 200) {
					// update fee amount
					/*
					 * TODO update fee amount
					 */
					request.getRequestDispatcher("/renewalNoticeList").forward(request, response);

				} else {
					request.setAttribute("error", "Update Renewal Notice Failed. Code: " + jsonObject.getCode());
					request.getRequestDispatcher("/WEB-INF/jsp/ManualProcess.jsp").forward(request, response);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			request.setAttribute("error", "Renewal Notice Not Found");
			request.getRequestDispatcher("/WEB-INF/jsp/ManualProcess.jsp").forward(request, response);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}