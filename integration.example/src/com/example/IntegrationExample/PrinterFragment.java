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

public class PrinterFragment extends Fragment {

	private EditText edtIntroduce;
	private Button btnOpenShift, btnCloseShift;
	private EditText txtResult;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_printer, container, false);

		edtIntroduce = (EditText)view.findViewById(R.id.edtIntroduce);
		btnOpenShift = (Button)view.findViewById(R.id.btnOpenShift);
        btnCloseShift = (Button)view.findViewById(R.id.btnCloseShift);
        txtResult = (EditText)view.findViewById(R.id.txtResult);
        
        btnOpenShift.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openShift();
			}
		});

        btnCloseShift.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeShift();
			}
		});
        
		return view;
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 503) {
        	if (resultCode == Activity.RESULT_OK) {
        		txtResult.setText("Shift was opened");
        	} else 
        		txtResult.setText("Cant open shift");
        }
        
        if (requestCode == 504) {
        	if (resultCode == Activity.RESULT_OK) {
        		if (data.getExtras().containsKey("Registers"))
                    txtResult.setText("Registers :\n" + data.getExtras().getString("Registers"));
        	} else 
        		txtResult.setText("Cant close shift");
        }
    }
	
    private void openShift() {
    	Intent intent = new Intent("ru.ibox.pro.printer");
    	intent.putExtra("Email", getString(R.string.login));
        intent.putExtra("Password", getString(R.string.password));
        intent.putExtra("Action", "OpenShift");

		try {
			double introduce = Double.parseDouble(edtIntroduce.getText().toString());
			if (introduce > 0d) {
				intent.putExtra("Cash", introduce);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
        startActivityForResult(intent, 503);
    }
    
    private void closeShift() {
    	Intent intent = new Intent("ru.ibox.pro.printer");
    	intent.putExtra("Email", getString(R.string.login));
        intent.putExtra("Password", getString(R.string.password));
        intent.putExtra("Action", "CloseShift");
        startActivityForResult(intent, 504);
    }


    
}
