package com.operations.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.beans.Transaction;
import com.connections.MyConnection;
import com.operations.TransactionOperations;

public class TransactionOperationsImpl implements TransactionOperations {

	@Override
	public boolean Login(String username, String password) {
		// TODO Auto-generated method stub
		String FIND = "select * from Login";
		try(Connection con =MyConnection.openConnection();) {
			PreparedStatement ps= con.prepareStatement(FIND);
			ResultSet set = ps.executeQuery();
			while(set.next())
			{
				String compName = set.getString("companyName");
				String pass=set.getString("password");
				System.out.println(pass);
				if((compName.equals(username) && pass.equals(password))||(username.equals("Admin") && password.equals("Admin123"))){
					return true;
				}
				
			}
			
			
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean NettingFunds() {
		// TODO Auto-generated method stub
		boolean isUpdated= false;
		String stat1="SELECT buyerCompanyId,sum(quantity*price) as payFund FROM Transactions GROUP BY buyerCompanyId";
		String stat2="SELECT sellerCompanyId,sum(quantity*price) as getFund FROM Transactions GROUP BY sellerCompanyId";
		
		try(Connection con =MyConnection.openConnection();) {
			PreparedStatement ps= con.prepareStatement(stat1);
            PreparedStatement ps1= con.prepareStatement(stat2);
			ResultSet set = ps.executeQuery();
			
			while(set.next())
			{
				String buyerId = set.getString("buyerCompanyId");				
				ResultSet set1 = ps1.executeQuery();
				while(set1.next())
				{
					isUpdated=false;
				String sellerId = set1.getString("sellerCompanyId");
				if(buyerId.equals(sellerId)) {
				float payFund = set.getFloat(2);
				float getFund = set1.getFloat(2);
				float nettedfunds=getFund-payFund;
				String stat3="UPDATE Company SET nettedFunds=? WHERE companyId=?";
				
				PreparedStatement ps3= con.prepareStatement(stat3);
				ps3.setFloat(1, (float)nettedfunds);
				ps3.setString(2, buyerId);
				int rows=ps3.executeUpdate();
				if(rows>0)
				{
					isUpdated=true;
				}
				}
				}
				//float payFund=set.getFloat("payFund");
				//float getFund=set1.getFloat("getFund");
			}
	}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isUpdated;
	}

	@Override
	public boolean NettingShares() {
		// TODO Auto-generated method stub
		boolean isUpdated = false;
		String stat1 = "SELECT buyerCompanyId,securityId,sum(quantity) from Transactions GROUP BY securityId,buyerCompanyId";
		String stat2 = "SELECT sellerCompanyId,securityId,sum(quantity) from Transactions GROUP BY securityId,sellerCompanyId";
		
		try(Connection con =MyConnection.openConnection();) {
			PreparedStatement ps= con.prepareStatement(stat1);
            PreparedStatement ps1= con.prepareStatement(stat2);
			ResultSet set = ps.executeQuery();
			
			while(set.next())
			{
				ResultSet set1 = ps1.executeQuery();
				String buyerId = set.getString("buyerCompanyId");
				String securityId1 = set.getString("securityId");
				int buySecurities = set.getInt(3);
				//System.out.println("b"+buyerId);
				//System.out.println("b"+securityId1);
				while(set1.next())
				{
					isUpdated=false;
				String sellerId = set1.getString("sellerCompanyId");
				String securityId2 = set1.getString("securityId");
				
				if(buyerId.equals(sellerId)&&(securityId1.equals(securityId2))) {
					//System.out.println("s"+sellerId);
					//System.out.println("s"+securityId2);
					int sellSecurities = set1.getInt(3);
					
					int netSecurities = buySecurities-sellSecurities;
					//System.out.println("s"+netSecurities);
					String stat3="UPDATE Manages SET nettedSecurities=? WHERE companyId=? and securityId=?";
				
					PreparedStatement ps3= con.prepareStatement(stat3);
					ps3.setInt(1, (int)netSecurities);
					ps3.setString(2, buyerId);
					ps3.setString(3, securityId1);
					int rows=ps3.executeUpdate();
					if(rows>0)
					{
						isUpdated=true;
						break;
					}
					else
						isUpdated=false;
				}	
				}
				if(isUpdated==false)
				{
					int netSecurities = buySecurities;
					String stat3="UPDATE Manages SET nettedSecurities=? WHERE companyId=? and securityId=?";
					
					PreparedStatement ps3= con.prepareStatement(stat3);
					ps3.setInt(1, (int)netSecurities);
					ps3.setString(2, buyerId);
					ps3.setString(3, securityId1);
					int rows=ps3.executeUpdate();
				}
				//float payFund=set.getFloat("payFund");
				//float getFund=set1.getFloat("getFund");
			}
			
			
			ResultSet set3 = ps1.executeQuery();
			while(set3.next())
			{
				String sellerId = set3.getString("sellerCompanyId");
				String securityId2 = set3.getString("securityId");
				int sellSecurities = set3.getInt(3);
				ResultSet set2 = ps.executeQuery();
				System.out.println("s"+sellerId);
				System.out.println("s"+securityId2);
				while(set2.next())
				{
					isUpdated=false;
				String buyerId = set2.getString("buyerCompanyId");
				String securityId1 = set2.getString("securityId");
				
				if(buyerId.equals(sellerId)&&(securityId1.equals(securityId2))) {
					int buySecurities = set2.getInt(3);
					//System.out.println("b"+buyerId);
					//System.out.println("b"+securityId1);
					int netSecurities = buySecurities-sellSecurities;
					System.out.println("s"+netSecurities);
					String stat3="UPDATE Manages SET nettedSecurities=? WHERE companyId=? and securityId=?";
					
					PreparedStatement ps3= con.prepareStatement(stat3);
					ps3.setInt(1, (int)netSecurities);
					ps3.setString(2, sellerId);
					ps3.setString(3, securityId2);
					int rows=ps3.executeUpdate();
					if(rows>0)
					{
						isUpdated=true;
						break;
					}
					else
						isUpdated=false;
				}	
				}
				if(isUpdated==false)
				{
					
					int netSecurities = -sellSecurities;
                    String stat3="UPDATE Manages SET nettedSecurities=? WHERE companyId=? and securityId=?";
					
					PreparedStatement ps3= con.prepareStatement(stat3);
					ps3.setInt(1, (int)netSecurities);
					ps3.setString(2, sellerId);
					ps3.setString(3, securityId2);
					int rows=ps3.executeUpdate();
				}
			}
			
		} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return isUpdated;
	}
	@Override
	public List<Transaction> findAll() {
		// TODO Auto-generated method stub
		 List<Transaction> transactions= new ArrayList<>();
			
			String FIND_ALL = "select * from Transactions";
			//String FIND="select companyName from Company where companyId=?";
			try(Connection con =MyConnection.openConnection();) {
				PreparedStatement ps= con.prepareStatement(FIND_ALL);
				ResultSet set = ps.executeQuery();
				while(set.next())
				{
					int transactionId= set.getInt(1);
					String buyerCompanyId=set.getString(2);
					//PreparedStatement ps1= con.prepareStatement(FIND);
					//ResultSet set1 = ps1.executeQuery();
					//ps1.setString(1,buyerCompanyId);
					//String buyerCompanyName=set1.getString(1);
					String securityId = set.getString(3);
					String sellerCompanyId = set.getString(4);
					
					int quantity = set.getInt(5);
					float price =set.getFloat(6);
					Transaction transaction = new Transaction(transactionId,buyerCompanyId,securityId,sellerCompanyId,quantity,price);
					transactions.add(transaction);
				}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return transactions;
		
	}
	
	@Override
	public List<Transaction> filterprice() {
		// TODO Auto-generated method stub
		 List<Transaction> transactions= new ArrayList<>();
			
			String FIND_ALL = "select * from Transactions order by Price";
			//String FIND="select companyName from Company where companyId=?";
			try(Connection con =MyConnection.openConnection();) {
				PreparedStatement ps= con.prepareStatement(FIND_ALL);
				ResultSet set = ps.executeQuery();
				while(set.next())
				{
					int transactionId= set.getInt(1);
					String buyerCompanyId=set.getString(2);
					//PreparedStatement ps1= con.prepareStatement(FIND);
					//ResultSet set1 = ps1.executeQuery();
					//ps1.setString(1,buyerCompanyId);
					//String buyerCompanyName=set1.getString(1);
					String securityId = set.getString(3);
					String sellerCompanyId = set.getString(4);
					
					int quantity = set.getInt(5);
					float price =set.getFloat(6);
					Transaction transaction = new Transaction(transactionId,buyerCompanyId,securityId,sellerCompanyId,quantity,price);
					transactions.add(transaction);
				}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return transactions;
	}
	
	@Override
	public List<Transaction> filterquantity() {
		// TODO Auto-generated method stub
		 List<Transaction> transactions= new ArrayList<>();
			
			String FIND_ALL = "select * from Transactions order by Quantity";
			//String FIND="select companyName from Company where companyId=?";
			try(Connection con =MyConnection.openConnection();) {
				PreparedStatement ps= con.prepareStatement(FIND_ALL);
				ResultSet set = ps.executeQuery();
				while(set.next())
				{
					int transactionId= set.getInt(1);
					String buyerCompanyId=set.getString(2);
					//PreparedStatement ps1= con.prepareStatement(FIND);
					//ResultSet set1 = ps1.executeQuery();
					//ps1.setString(1,buyerCompanyId);
					//String buyerCompanyName=set1.getString(1);
					String securityId = set.getString(3);
					String sellerCompanyId = set.getString(4);
					
					int quantity = set.getInt(5);
					float price =set.getFloat(6);
					Transaction transaction = new Transaction(transactionId,buyerCompanyId,securityId,sellerCompanyId,quantity,price);
					transactions.add(transaction);
				}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return transactions;
	}
	public List<Transaction> findTransactionBySecurity(String securityId) {
		// TODO Auto-generated method stub
		List<Transaction> transactions= new ArrayList<>();
		String FIND_BY_SECURITY = "select * from Transactions where securityId=?";
		try(Connection con =MyConnection.openConnection();) {
			PreparedStatement ps= con.prepareStatement(FIND_BY_SECURITY);
			ps.setString(1, securityId);
			ResultSet set = ps.executeQuery();
			while(set.next())
			{
				int transactionId= set.getInt(1);
				String buyerCompanyId=set.getString(2);
				String securityid = set.getString(3);
				String sellerCompanyId = set.getString(4);		
				int quantity = set.getInt(5);
				float price =set.getFloat(6);
				Transaction transaction = new Transaction(transactionId,buyerCompanyId,securityid,sellerCompanyId,quantity,price);
				transactions.add(transaction);
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return transactions;
	}

	@Override
	public List<Transaction> findTransactionByBuyer(String buyerCompanyId) {
		// TODO Auto-generated method stub
		List<Transaction> transactions= new ArrayList<>();
		String FIND_BY_SECURITY = "select * from Transactions where buyerCompanyId=?";
		try(Connection con =MyConnection.openConnection();) {
			PreparedStatement ps= con.prepareStatement(FIND_BY_SECURITY);
			ps.setString(1, buyerCompanyId);
			ResultSet set = ps.executeQuery();
			while(set.next())
			{
				int transactionId= set.getInt(1);
				String buyerCompId=set.getString(2);
				String securityid = set.getString(3);
				String sellerCompanyId = set.getString(4);		
				int quantity = set.getInt(5);
				float price =set.getFloat(6);
				Transaction transaction = new Transaction(transactionId,buyerCompId,securityid,sellerCompanyId,quantity,price);
				transactions.add(transaction);
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return transactions;
		
		
	}

	@Override
	public List<Transaction> findTransactionBySeller(String sellerCompanyId) {
		// TODO Auto-generated method stub
		List<Transaction> transactions= new ArrayList<>();
		String FIND_BY_SECURITY = "select * from Transactions where sellerCompanyId=?";
		try(Connection con =MyConnection.openConnection();) {
			PreparedStatement ps= con.prepareStatement(FIND_BY_SECURITY);
			ps.setString(1, sellerCompanyId);
			ResultSet set = ps.executeQuery();
			while(set.next())
			{
				int transactionId= set.getInt(1);
				String buyerCompId=set.getString(2);
				String securityid = set.getString(3);
				String sellerCompId = set.getString(4);		
				int quantity = set.getInt(5);
				float price =set.getFloat(6);
				Transaction transaction = new Transaction(transactionId,buyerCompId,securityid,sellerCompId,quantity,price);
				transactions.add(transaction);
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return transactions;
		
	}
	
	@Override
	public void addTransaction(Transaction transaction) {
		// TODO Auto-generated method stub
		String LAST ="select top 1 transactionId from Transactions order by transactionId desc";
		
		int transactionId=0;
		String ADDTRANSACTION = "Insert into TRANSACTIONS values(?,?,?,?,?,?)";

		try {
			Connection con = MyConnection.openConnection();
			PreparedStatement ps1 = con.prepareStatement(LAST);
			ResultSet set = ps1.executeQuery();
			while(set.next())
			{
				transactionId= set.getInt(1);
			}
			transactionId=transactionId+1;
			System.out.println(transactionId);
			PreparedStatement ps = con.prepareStatement(ADDTRANSACTION);
			ps.setInt(1, transactionId);
			ps.setString(3, transaction.getSecurityId());
			ps.setInt(5, transaction.getQuantity());
			ps.setFloat(6, transaction.getPrice());
			ps.setString(2, transaction.getBuyerCompId());
			ps.setString(4, transaction.getSellerCompId());
			 ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();

		}
		
		
	}

	@Override
	public boolean deleteTransaction(int TransId) {
		// TODO Auto-generated method stub
		String DELETETRANSACTION="DELETE FROM TRANSACTIONS WHERE transactionId=?";
		
		Connection con= MyConnection.openConnection();
		try {
			PreparedStatement ps= con.prepareStatement(DELETETRANSACTION);
			ps.setInt(1, TransId);
			 ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public Transaction findTransactionByID(int TransID) {
		// TODO Auto-generated method stub
	String FINDBYID="select * from transactions where transactionId=?";
	Transaction transaction = new Transaction();
	Connection con= MyConnection.openConnection();
	try {
		PreparedStatement ps= con.prepareStatement(FINDBYID);
		ps.setInt(1, TransID);
		ResultSet set=ps.executeQuery();
		while(set.next())
		{
		int transId= set.getInt(1);
		String buyerId= set.getString(2);
		String security= set.getString(3);
		String sellerId= set.getString(4);
		int quantity= set.getInt(5);
		float price= set.getFloat(6);
		 transaction= new Transaction(transId, buyerId, security, sellerId,quantity, price);
		}
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return transaction;
	}
	@Override
	public
    void dropTransaction() {
		String DELETE="delete from Transactions";
		
		Connection con= MyConnection.openConnection();
		try {
			PreparedStatement ps= con.prepareStatement(DELETE);
			
			ps.executeUpdate();
    }
		catch (SQLException e) {
			e.printStackTrace();

		}
		
		
	}
	@Override
	public void updateTransaction(Transaction transaction) {
		// TODO Auto-generated method stub
		String UPDATETRANSACTION = "update TRANSACTIONS set buyerCompanyId=?,securityId=?,sellerCompanyId=?,quantity=?,price=? where transactionId=?";

		try {
			Connection con = MyConnection.openConnection();

			PreparedStatement ps = con.prepareStatement(UPDATETRANSACTION);
			ps.setInt(6, transaction.getTransId());
			ps.setString(2, transaction.getSecurityId());
			ps.setInt(4, transaction.getQuantity());
			ps.setFloat(5, transaction.getPrice());
			ps.setString(1, transaction.getBuyerCompId());
			ps.setString(3, transaction.getSellerCompId());
			 ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();

		}
		
		
	}


	@Override
	public List<Integer> findAllGroupCompany() {
		// TODO Auto-generated method stub
		 List<Integer> transactions= new ArrayList<>();
			
			String FIND_ALL = "select count(transactionId) , buyerCompanyId from Transactions Group By buyerCompanyId";
			//String FIND="select companyName from Company where companyId=?";
			try(Connection con =MyConnection.openConnection();) {
				PreparedStatement ps= con.prepareStatement(FIND_ALL);
				ResultSet set = ps.executeQuery();
				while(set.next())
				{
					int price =set.getInt(1);
					transactions.add(price);
				}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return transactions;
		
			 
	}

	@Override
	public List<Integer> findAllGroupCompany1() {
		// TODO Auto-generated method stub
		 List<Integer> transactions= new ArrayList<>();
			
			String FIND_ALL = "select count(transactionId) , sellerCompanyId from Transactions Group By sellerCompanyId";
			//String FIND="select companyName from Company where companyId=?";
			try(Connection con =MyConnection.openConnection();) {
				PreparedStatement ps= con.prepareStatement(FIND_ALL);
				ResultSet set = ps.executeQuery();
				while(set.next())
				{
					int price =set.getInt(1);
					transactions.add(price);
				}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return transactions;
	}
	
@Override
	public List<Float> findTransactionByBuyerSec(String buyerCompanyId) {
		// TODO Auto-generated method stub
		List<Float> transactions= new ArrayList<>();
		System.out.println(buyerCompanyId);
		String FIND_BY_SECURITY = "select sum(quantity*price)from Transactions where buyerCompanyId=? GROUP BY securityId";
		try(Connection con =MyConnection.openConnection();) {
			PreparedStatement ps= con.prepareStatement(FIND_BY_SECURITY);
			ps.setString(1, buyerCompanyId);
			ResultSet set = ps.executeQuery();
			
			while(set.next())
			{   
				float price =set.getFloat(1);
				
				transactions.add(price);
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return transactions;
	}

	@Override
	public List<Float> findTransactionBySellerSec(String sellerCompanyId) {
		// TODO Auto-generated method stub
		List<Float> transactions= new ArrayList<>();
		String FIND_BY_SECURITY = "select sum(quantity*price)from Transactions where sellerCompanyId=? GROUP BY securityId";
		try(Connection con =MyConnection.openConnection();) {
			PreparedStatement ps= con.prepareStatement(FIND_BY_SECURITY);
			ps.setString(1, sellerCompanyId);
			ResultSet set = ps.executeQuery();
			
			while(set.next())
			{   
				float price =set.getFloat(1);
				
				transactions.add(price);
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return transactions;
	}

	@Override
	public List<Integer> findAllGroupSecurity() {
		// TODO Auto-generated method stub
		 List<Integer> transactions= new ArrayList<>();
			
			String FIND_ALL = "select count(transactionId) , securityId from Transactions Group By securityId";
			//String FIND="select companyName from Company where companyId=?";
			try(Connection con =MyConnection.openConnection();) {
				PreparedStatement ps= con.prepareStatement(FIND_ALL);
				ResultSet set = ps.executeQuery();
				while(set.next())
				{
					int price =set.getInt(1);
					transactions.add(price);
				}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return transactions;
	}



	

}
