package com.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beans.Transaction;
import com.operations.TransactionOperations;
import com.operations.impl.TransactionOperationsImpl;

/**
 * Servlet implementation class AddTransactionServlet
 */
@WebServlet("/addtransaction")
public class AddTransactionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddTransactionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		int transId= Integer.parseInt(request.getParameter("transId"));
		String security = request.getParameter("new_security");
		int quantity= Integer.parseInt(request.getParameter("new_quantity"));
		float price= Float.parseFloat(request.getParameter("new_price"));
		String buyer = request.getParameter("new_buyer");
		String seller = request.getParameter("new_seller");
		Transaction transaction = new Transaction(transId, buyer, security, seller, quantity, price);
		System.out.println(transaction.getTransId());
		TransactionOperations dao= new TransactionOperationsImpl();
		dao.addTransaction(transaction);
		List<Transaction> list = dao.findAll();
		request.setAttribute("transactions", list);
		RequestDispatcher dispatcher1 = request.getRequestDispatcher("admin.jsp");
		dispatcher1.forward(request, response);
	}

}
