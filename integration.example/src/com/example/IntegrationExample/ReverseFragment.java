package com.example.IntegrationExample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ReverseFragment extends Fragment {

	private EditText edtTrID;
    private EditText edtHeader, edtFooter;
    private Button   btnReturn, btnCancel;
    private EditText txtResult;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reverse, container, false);
		
		edtTrID = (EditText)view.findViewById(R.id.edtTrId);
		edtHeader = (EditText)view.findViewById(R.id.edtHeader);
        edtFooter = (EditText)view.findViewById(R.id.edtFooter);        
        btnReturn = (Button)view.findViewById(R.id.btnReturn);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        txtResult = (EditText)view.findViewById(R.id.txtResult);
        
        btnReturn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				returnPayment();
			}
		});
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelPayment();
			}
		});
		
		return view;
	}
	
	private void returnPayment() {
		Intent intent = new Intent("ru.ibox.pro.reversepayment");
    	intent.putExtra("Email", getString(R.string.login));
        intent.putExtra("Password", getString(R.string.password));
        intent.putExtra("Action", "Return");
        intent.putExtra("TrID", edtTrID.getText().toString());
        intent.putExtra("PrinterHeader", edtHeader.getText().toString());
        intent.putExtra("PrinterFooter", edtFooter.getText().toString());
        
        startActivityForResult(intent, 501);
	}
	
	private void cancelPayment() {
		Intent intent = new Intent("ru.ibox.pro.reversepayment");
    	intent.putExtra("Email", getString(R.string.login));
        intent.putExtra("Password", getString(R.string.password));
        intent.putExtra("Action", "Cancel");
        intent.putExtra("TrID", edtTrID.getText().toString());
        intent.putExtra("PrinterHeader", edtHeader.getText().toString());
        intent.putExtra("PrinterFooter", edtFooter.getText().toString());
        
        startActivityForResult(intent, 502);
	}

	private String getResult(Intent data) {
		String result = "";
		
		if (data.getExtras().containsKey("TransactionId"))
			result += "Transaction ID : " + data.getExtras().getString("TransactionId") + "\n";

        if (data.getExtras().containsKey("Invoice"))
        	result += "Invoice : " + data.getExtras().getString("Invoice") + "\n";

        if (data.getExtras().containsKey("ReceiptPhone"))
        	result += "ReceiptPhone : " +  data.getExtras().getString("ReceiptPhone")+ "\n";

        if (data.getExtras().containsKey("ReceiptEmail"))
        	result += "ReceiptEmail : " +  data.getExtras().getString("ReceiptEmail")+ "\n";

        if (data.getExtras().containsKey("Amount"))
        	result += "Amount : " +  data.getExtras().getDouble("Amount")+ "\n";

        if (data.getExtras().containsKey("PAN"))
        	result += "PAN : " +  data.getExtras().getString("PAN")+ "\n";

        if (data.getExtras().containsKey("Created"))
        	result += "Created : " +  data.getExtras().getLong("Created") + "\n";
        
        return result;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		 if (requestCode == 501) {
	        	if (resultCode == Activity.RESULT_OK) {
	                txtResult.setText(getResult(data));
	        	} else {
	        		if (data != null && data.getExtras().containsKey("ErrorMessage"))
	            		txtResult.setText(data.getExtras().getString("ErrorMessage"));
	            	else
	            		txtResult.setText("Payment return error");
	        	}
	        }
	        
	        if (requestCode == 502) {
	        	if (resultCode == Activity.RESULT_OK) {
	                txtResult.setText(getResult(data));
	        	} else {
	        		if (data != null && data.getExtras().containsKey("ErrorMessage"))
	            		txtResult.setText(data.getExtras().getString("ErrorMessage"));
	            	else
	            		txtResult.setText("Payment cancel error");
	        	}
	        }
	}
	
}
