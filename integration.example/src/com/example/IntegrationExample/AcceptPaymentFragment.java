package com.example.IntegrationExample;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AcceptPaymentFragment extends Fragment {

	private static final boolean TEST_CUSTOM_FIELDS	= false;
	private static final boolean TEST_PURCHASES		= false;
	
	private String   mImagePath;
	
	private EditText edtAmount, edtDescription;
    private EditText edtHeader, edtFooter;
    private Button   btnSelectPhoto, btnCapturePhoto;
    private Button   btnAcceptPayment;
    private EditText txtResult;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_payment, container, false);
		
		edtDescription = (EditText)view.findViewById(R.id.edtDescription);
        edtAmount = (EditText)view.findViewById(R.id.edtAmount);
        edtHeader = (EditText)view.findViewById(R.id.edtHeader);
        edtFooter = (EditText)view.findViewById(R.id.edtFooter);        
        btnCapturePhoto = (Button)view.findViewById(R.id.btnCapturePhoto);
        btnSelectPhoto = (Button)view.findViewById(R.id.btnSelectPhoto);
        btnAcceptPayment = (Button)view.findViewById(R.id.btnAcceptPayment);
        txtResult = (EditText)view.findViewById(R.id.txtResult);
		
		
        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(getCameraImagePath());
                Uri outputFileUri = Uri.fromFile(file);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, 250);
            }
        });

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 251);

            }
        });

        btnAcceptPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double amount = Double.parseDouble(edtAmount.getText().toString());
                String description = edtDescription.getText().toString();

                acceptPayment(amount, description, mImagePath);

            }
        });
        		
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 250 && resultCode == Activity.RESULT_OK) {
            mImagePath = getCameraImagePath();
            return;
        }

        if (requestCode == 251 && resultCode == Activity.RESULT_OK) {
            mImagePath = getRealPathFromUri(data.getData());
            return;
        }

        if (requestCode == 500 && resultCode == Activity.RESULT_OK) {
            String strResult = "";

            if (data.getExtras().containsKey("TransactionId"))
                strResult += "Transaction ID : " + data.getExtras().getString("TransactionId") + "\n";

            if (data.getExtras().containsKey("Invoice"))
                strResult += "Invoice : " + data.getExtras().getString("Invoice") + "\n";

            if (data.getExtras().containsKey("ReceiptPhone"))
                strResult += "ReceiptPhone : " +  data.getExtras().getString("ReceiptPhone")+ "\n";

            if (data.getExtras().containsKey("ReceiptEmail"))
                strResult += "ReceiptEmail : " +  data.getExtras().getString("ReceiptEmail")+ "\n";

            if (data.getExtras().containsKey("PaymentType"))
                strResult += "PaymentType : " +  data.getExtras().getString("PaymentType")+ "\n";

            if (data.getExtras().containsKey("Amount"))
                strResult += "Amount : " +  data.getExtras().getDouble("Amount")+ "\n";

            if (data.getExtras().containsKey("PAN"))
                strResult += "PAN : " +  data.getExtras().getString("PAN")+ "\n";

            if (data.getExtras().containsKey("Created"))
                strResult += "Created : " +  data.getExtras().getLong("Created") + "\n";

            txtResult.setText(strResult);
        }

        if (requestCode == 500 && resultCode != Activity.RESULT_OK) {
        	if (data != null && data.getExtras().containsKey("ErrorMessage"))
        		txtResult.setText(data.getExtras().getString("ErrorMessage"));
        	else
        		txtResult.setText("Платеж не проведен!");
        }
	}
	
    private void acceptPayment(double amount, String description, String imagePath) {
        Intent intent = new Intent("ru.ibox.pro.acceptpayment");

        //CHIP&SIGN, CHIP&PIN
        //intent.putExtra("ReaderType", "CHIP&PIN");

        intent.putExtra("Amount", amount);
        intent.putExtra("PrinterHeader", edtHeader.getText().toString());
        intent.putExtra("PrinterFooter", edtFooter.getText().toString());
        
        // Простой платеж
        if (!TEST_CUSTOM_FIELDS && !TEST_PURCHASES) {
        	intent.putExtra("Email", getString(R.string.login));
            intent.putExtra("Password", getString(R.string.password));

            intent.putExtra("Description", description);
	        if (imagePath != null)
	            intent.putExtra("Image", imagePath);
        }

        //Платеж с использованием продуктов
        if (TEST_CUSTOM_FIELDS) {
            intent.putExtra("Email", getString(R.string.login));
            intent.putExtra("Password", getString(R.string.password));
            String product = "ST00012|Product=DELIVERY|_CLIENT_NAME_=Иванов|_CONTRACT_NO_=123456";
            intent.putExtra("Product", product);
        }

        // Простой платеж с произвольным фискальным чеком
        if (TEST_PURCHASES) {
        	try {
                intent.putExtra("Email", getString(R.string.login));
                intent.putExtra("Password", getString(R.string.password));

                String purchases =
                        "{"+
                        "    \"Purchases\": [{"+
                        "    \"Title\": \"Позиция 1\","+
                        "            \"Price\": 150.25,"+
                        "            \"Quantity\": 2,"+
                        "           \"TaxCode\": [\"VAT1800\"]"+
                        "}, {"+
                        "    \"Title\": \"Позиция 2\","+
                        "            \"Price\": 100.00,"+
                        "            \"Quantity\": 1,"+
                        "           \"TaxCode\": [\"VAT1000\"]"+
                        "}]"+
                        "}";
                intent.putExtra("Purchases", purchases);
	        	Log.i("TEST_PURCHASES", purchases);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        
        startActivityForResult(intent, 500);
    }
    
    private String getCameraImagePath() {
        return android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera.tmp";
    }

    private String getRealPathFromUri(Uri contentUri) {
        String[] projection = new String[] { android.provider.MediaStore.MediaColumns.DATA };
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(contentUri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(android.provider.MediaStore.MediaColumns.DATA);
            return cursor.getString(index);
        }
        return null;
    }
    
}
