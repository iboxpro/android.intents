package com.example.IntegrationExample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

public class ReverseFragment extends Fragment {
	private EditText edtLogin, edtPassword, edtExtID, edtAmount, edtReceiptEmail, edtReceiptPhone;
	private EditText edtTrID;
    private EditText edtHeader, edtFooter;
	private CheckBox cbAmount, cbAux, cbReaderType, cbPrintCopy;
    private Button   btnReturn, btnCancel;
    private EditText txtResult;

	private String readerType;
	private ArrayAdapter<String> purchasesAdapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		readerType = null;
		purchasesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.test_list_item, new ArrayList<String>());
	}

	@Override
	public void onResume() {
		super.onResume();
		cbAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				edtAmount.setEnabled(isChecked);
			}
		});
		cbAux.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				if (checked)
					showPurchasesDialog();
				else
					purchasesAdapter.clear();
			}
		});
		cbReaderType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					showReaderTypeDialog();
				else
					readerType = null;
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		cbAmount.setOnCheckedChangeListener(null);
		cbAux.setOnCheckedChangeListener(null);
		cbReaderType.setOnCheckedChangeListener(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reverse, container, false);

		edtLogin = (EditText)view.findViewById(R.id.edtLogin);
		edtPassword = (EditText)view.findViewById(R.id.edtPassword);
		edtExtID = (EditText)view.findViewById(R.id.edtExtId);
		edtAmount = (EditText)view.findViewById(R.id.edtAmount);
		edtReceiptEmail = (EditText)view.findViewById(R.id.edtReceiptEmail);
		edtReceiptPhone = (EditText)view.findViewById(R.id.edtReceiptPhone);
		edtHeader = (EditText)view.findViewById(R.id.edtHeader);
		edtFooter = (EditText)view.findViewById(R.id.edtFooter);
		cbAmount = (CheckBox)view.findViewById(R.id.cbAmount);
		cbAux = (CheckBox)view.findViewById(R.id.cbAux);
		cbReaderType = (CheckBox)view.findViewById(R.id.cbReaderType);
		cbPrintCopy = (CheckBox)view.findViewById(R.id.cbPrintCopy);
		edtTrID = (EditText)view.findViewById(R.id.edtTrId);
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

	private void setActionData(Intent intent) {
		intent.putExtra("Email", edtLogin.getText().toString());
		intent.putExtra("Password", edtPassword.getText().toString());
		intent.putExtra("ExtID", edtExtID.getText().toString());
		intent.putExtra("ReceiptEmail", edtReceiptEmail.getText().toString());
		intent.putExtra("ReceiptPhone", edtReceiptPhone.getText().toString());
		intent.putExtra("PrinterHeader", edtHeader.getText().toString());
		intent.putExtra("PrinterFooter", edtFooter.getText().toString());
		intent.putExtra("TrID", edtTrID.getText().toString());
		if (cbAmount.isChecked()) {
			Double amount = null;
			try {
				amount = Double.parseDouble(edtAmount.getText().toString());
			} catch (Exception e) { e.printStackTrace(); }
			if (amount != null)
				intent.putExtra("Amount", amount);
		}
		if (cbAux.isChecked()) {
			StringBuilder purchases = new StringBuilder("{")
					.append("\"Purchases\":").append("[");
			for (int i = 0; i < purchasesAdapter.getCount(); i++) {
				purchases.append(purchasesAdapter.getItem(i));
				if (i != purchasesAdapter.getCount() - 1)
					purchases.append(",");
			}
			purchases.append("]}");
			intent.putExtra("Purchases", purchases.toString());
		}
		if (cbReaderType.isChecked())
			intent.putExtra("ReaderType", readerType);
		if (cbPrintCopy.isChecked())
			intent.putExtra("ReceiptCopy", true);
	}

	private void returnPayment() {
		Intent intent = new Intent("ru.ibox.pro.reversepayment");
        intent.putExtra("Action", "Return");
        setActionData(intent);
        startActivityForResult(intent, 501);
	}
	
	private void cancelPayment() {
		Intent intent = new Intent("ru.ibox.pro.reversepayment");
        intent.putExtra("Action", "Cancel");
		setActionData(intent);
        startActivityForResult(intent, 502);
	}

	private String getResult(Intent data) {
		String result = "";
		
		if (data.getExtras().containsKey("TransactionId"))
			result += "Transaction ID : " + data.getExtras().getString("TransactionId") + "\n";

        if (data.getExtras().containsKey("Invoice"))
        	result += "Invoice : " + data.getExtras().getString("Invoice") + "\n";

		if (data.getExtras().containsKey("RRN"))
			result += "RRN : " + data.getExtras().getString("RRN") + "\n";

        if (data.getExtras().containsKey("ReceiptPhone"))
        	result += "ReceiptPhone : " +  data.getExtras().getString("ReceiptPhone")+ "\n";

        if (data.getExtras().containsKey("ReceiptEmail"))
        	result += "ReceiptEmail : " +  data.getExtras().getString("ReceiptEmail")+ "\n";

        if (data.getExtras().containsKey("Amount"))
        	result += "Amount : " +  data.getExtras().getDouble("Amount")+ "\n";

        if (data.getExtras().containsKey("PAN"))
        	result += "PAN : " +  data.getExtras().getString("PAN")+ "\n";

		if (data.getExtras().containsKey("IIN"))
			result += "IIN : " +  data.getExtras().getString("IIN")+ "\n";

        if (data.getExtras().containsKey("Created"))
        	result += "Created : " +  data.getExtras().getLong("Created") + "\n";

		if (data.getExtras().containsKey("ExtID"))
			result += "Ext ID : " + data.getExtras().getString("ExtID") + "\n";

		if (data.getExtras().containsKey("FiscalPrinterSN"))
			result += "FiscalPrinterSN : " +  data.getExtras().getString("FiscalPrinterSN") + "\n";

		if (data.getExtras().containsKey("FiscalPrinterRN"))
			result += "FiscalPrinterRN : " +  data.getExtras().getString("FiscalPrinterRN") + "\n";

		if (data.getExtras().containsKey("FiscalShift"))
			result += "FiscalShift : " +  data.getExtras().getString("FiscalShift") + "\n";

		if (data.getExtras().containsKey("FiscalCryptoVerifCode"))
			result += "FiscalCryptoVerifCode : " +  data.getExtras().getString("FiscalCryptoVerifCode") + "\n";

		if (data.getExtras().containsKey("FiscalDocSN"))
			result += "FiscalDocSN : " +  data.getExtras().getString("FiscalDocSN") + "\n";

		if (data.getExtras().containsKey("FiscalDocumentNumber"))
			result += "FiscalDocumentNumber : " +  data.getExtras().getString("FiscalDocumentNumber") + "\n";

		if (data.getExtras().containsKey("FiscalStorageNumber"))
			result += "FiscalStorageNumber : " +  data.getExtras().getString("FiscalStorageNumber") + "\n";

		if (data.getExtras().containsKey("FiscalMark"))
			result += "FiscalMark : " +  data.getExtras().getString("FiscalMark") + "\n";

		if (data.getExtras().containsKey("FiscalDocSN"))
			result += "FiscalDatetime : " +  data.getExtras().getString("FiscalDatetime") + "\n";

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
	            	else {
						txtResult.setText("Payment return error");
					}
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

	private void showPurchasesDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setNeutralButton("Добавить", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				showAddPurchaseDialog((AlertDialog) dialogInterface);
			}
		}).setAdapter(purchasesAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface parent, final int i) {
				new AlertDialog.Builder(getContext())
						.setMessage("Удалить продукт " + purchasesAdapter.getItem(i) + "?")
						.setPositiveButton("Да", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int j) {
								dialogInterface.dismiss();
								purchasesAdapter.remove(purchasesAdapter.getItem(i));
								purchasesAdapter.notifyDataSetChanged();
								((AlertDialog) parent).show();
							}
						})
						.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int j) {
								dialogInterface.dismiss();
								((AlertDialog) parent).show();
							}
						})
						.create().show();
			}
		})
				.create().show();
	}

	private void showAddPurchaseDialog(final Dialog parent) {
		final EditText edtPurchase = (EditText) LayoutInflater.from(getContext()).inflate(R.layout.dialog_purchase, null);
		edtPurchase.setText(MainActivity.TEST_PURCHASE);
		new AlertDialog.Builder(getContext())
				.setView(edtPurchase)
				.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						try {
							new JSONObject(edtPurchase.getText().toString());
							purchasesAdapter.add(edtPurchase.getText().toString());
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(getContext(), "Ошибка!", Toast.LENGTH_LONG).show();
						}
						parent.show();
					}
				})
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						parent.show();
					}
				}).create().show();
	}

	private void showReaderTypeDialog() {
		new AlertDialog.Builder(getContext())
				.setSingleChoiceItems(R.array.reader_types, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						readerType = getResources().getStringArray(R.array.reader_types)[which];
						dialog.dismiss();
					}
				})
				.create().show();
	}
}
