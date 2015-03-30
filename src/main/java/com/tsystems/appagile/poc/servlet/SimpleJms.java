package com.tsystems.appagile.poc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SimpleJms
 */
public class SimpleJms extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SimpleJms() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			sendAndReceive(request, response);
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

	private void sendAndReceive(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("SendAndReceive Started");
		Context ctx = new InitialContext();
		QueueConnectionFactory cf1 = (QueueConnectionFactory) ctx
				.lookup("connectionFactory");
		Queue queue = (Queue) ctx.lookup("MyQueue");
		out.println("QCF and Queue lookup completed !!");
		QueueConnection con = cf1.createQueueConnection();
		// start the connection to receive message
		con.start();
		// create a queue session to send a message
		QueueSession sessionSender = con.createQueueSession(false,
				javax.jms.Session.AUTO_ACKNOWLEDGE);
		QueueSender send = sessionSender.createSender(queue);
		
		// send a sample message
		TextMessage txtMsg = sessionSender.createTextMessage("Liberty Sample Message");
		txtMsg.setJMSCorrelationID("corrId-" + (new Date()).getTime());
		send.send(txtMsg);
		out.println("Message sent successfully     :" + txtMsg);
		// create a queue receiver object
		QueueReceiver rec = sessionSender.createReceiver(queue);
		// receive message from Queue
		TextMessage msg = (TextMessage) rec.receive();
		out.println("Received Message Successfully :" + msg);
		if (con != null)
			con.close();
		out.println("SendAndReceive Completed");
	}
}
