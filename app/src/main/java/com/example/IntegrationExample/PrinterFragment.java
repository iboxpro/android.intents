package com.example.IntegrationExample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class PrinterFragment extends Fragment {
	private EditText edtLogin, edtPassword, edtExtID;
	private Button btnAction, btnOpenShift, btnCloseShift, btnXReport, btnYReport, btnReport1, btnPrintText;
	private EditText txtResult;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_printer, container, false);

		edtLogin = (EditText)view.findViewById(R.id.edtLogin);
		edtPassword = (EditText)view.findViewById(R.id.edtPassword);
		edtExtID = (EditText)view.findViewById(R.id.edtExtId);
		btnAction = (Button)view.findViewById(R.id.btnFiscalAction);
		btnOpenShift = (Button)view.findViewById(R.id.btnOpenShift);
        btnCloseShift = (Button)view.findViewById(R.id.btnCloseShift);
		btnXReport = (Button)view.findViewById(R.id.btnXReport);
		btnYReport = (Button)view.findViewById(R.id.btnYReport);
		btnReport1 = (Button)view.findViewById(R.id.btnReport1);
		btnPrintText = (Button)view.findViewById(R.id.btnPrintText);
        txtResult = (EditText)view.findViewById(R.id.txtResult);

        btnOpenShift.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showOpenShiftDialog();
			}
		});
        btnCloseShift.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCloseShiftDialog();
			}
		});
		btnXReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				xReport();
			}
		});
		btnYReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				yReport();
			}
		});
		btnReport1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				report1();
			}
		});
		btnPrintText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPrintTextDialog();
			}
		});
		btnAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showFiscalDialog();
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

		if (requestCode == 505) {
			if (resultCode == Activity.RESULT_OK) {
				String strResult = "";
				if (data.getExtras().containsKey("FiscalPrinterSN"))
					strResult += "FiscalPrinterSN : " +  data.getExtras().getString("FiscalPrinterSN") + "\n";

                if (data.getExtras().containsKey("FiscalPrinterRN"))
                    strResult += "FiscalPrinterRN : " +  data.getExtras().getString("FiscalPrinterRN") + "\n";

				if (data.getExtras().containsKey("FiscalShift"))
					strResult += "FiscalShift : " +  data.getExtras().getString("FiscalShift") + "\n";

				if (data.getExtras().containsKey("FiscalCryptoVerifCode"))
					strResult += "FiscalCryptoVerifCode : " +  data.getExtras().getString("FiscalCryptoVerifCode") + "\n";

				if (data.getExtras().containsKey("FiscalDocSN"))
					strResult += "FiscalDocSN : " +  data.getExtras().getString("FiscalDocSN") + "\n";

				if (data.getExtras().containsKey("FiscalDocumentNumber"))
					strResult += "FiscalDocumentNumber : " +  data.getExtras().getString("FiscalDocumentNumber") + "\n";

				if (data.getExtras().containsKey("FiscalStorageNumber"))
					strResult += "FiscalStorageNumber : " +  data.getExtras().getString("FiscalStorageNumber") + "\n";

				if (data.getExtras().containsKey("FiscalMark"))
					strResult += "FiscalMark : " +  data.getExtras().getString("FiscalMark") + "\n";

				if (data.getExtras().containsKey("FiscalDatetime"))
					strResult += "FiscalDatetime : " +  data.getExtras().getString("FiscalDatetime") + "\n";

				txtResult.setText(strResult);
			} else {
				if (data != null && data.getExtras().containsKey("ErrorMessage"))
					txtResult.setText(data.getExtras().getString("ErrorMessage"));
				else
					txtResult.setText("Платеж не проведен!");
			}
		}

		if (requestCode == 506) {
			if (resultCode == Activity.RESULT_OK) {
				txtResult.setText("X-Report was printed");
			} else
				txtResult.setText("Failed to print X-Report");
		}

        if (requestCode == 507) {
            if (resultCode == Activity.RESULT_OK) {
                txtResult.setText("Y-Report was printed");
            } else
                txtResult.setText("Failed to print Y-Report");
        }

		if (requestCode == 508) {
			if (resultCode == Activity.RESULT_OK) {
				txtResult.setText("Report1 was printed");
			} else
				txtResult.setText("Failed to print Report1");
		}

		if (requestCode == 509) {
			if (resultCode == Activity.RESULT_OK) {
				txtResult.setText("Text was printed");
			} else
				txtResult.setText("Failed to print text");
		}
    }

    private void showOpenShiftDialog() {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_introduce, null);
		final EditText edtIntroduce = (EditText) view.findViewById(R.id.edtIntroduce);
		new AlertDialog.Builder(getContext())
				.setView(view)
				.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						Double introduce = null;
						try {
							introduce = Double.parseDouble(edtIntroduce.getText().toString());
						} catch (NumberFormatException e) { e.printStackTrace(); }
						openShift(introduce);
					}
				})
				.setNeutralButton("Без внесения", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						openShift(null);
					}
				})
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				}).create().show();
	}

    private void showCloseShiftDialog() {
		new AlertDialog.Builder(getContext())
				.setMessage("Получить значения регистров?")
				.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						closeShift(true);
					}
				})
				.setNeutralButton("Нет", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						closeShift(false);
					}
				})
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
		.create().show();
	}

	private void showPrintTextDialog() {
		final EditText edtPurchase = (EditText) LayoutInflater.from(getContext()).inflate(R.layout.dialog_purchase, null);
		edtPurchase.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit,\nsed do eiusmod tempor\nincididunt ut labore et dolore magna aliqua.");
		new AlertDialog.Builder(getContext())
				.setView(edtPurchase)
				.setPositiveButton("Печать", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						printText(edtPurchase.getText().toString());
					}
				})
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				}).create().show();
	}

	private void showFiscalDialog() {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom_fiscal, null);
		final RadioGroup rgInvoiceType = (RadioGroup)view.findViewById(R.id.rgInvoiceType),
						rgInputType = (RadioGroup)view.findViewById(R.id.rgInputType);
		final EditText edtDescription = (EditText)view.findViewById(R.id.edtDescription),
			edtPurchases = (EditText)view.findViewById(R.id.edtAux),
			edtCashAccepted = (EditText)view.findViewById(R.id.edtCashAccepted),
			edtEmail = (EditText)view.findViewById(R.id.edtReceiptEmail);
		StringBuilder purchases = new StringBuilder("{")
				.append("\"Purchases\":").append("[\n")
				.append(MainActivity.TEST_PURCHASE).append("\n]}");
		edtPurchases.setText(purchases.toString());
		new AlertDialog.Builder(getContext())
				.setView(view)
				.setPositiveButton("Фискализировать", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						String invoiceType = "Sale";
						if (rgInvoiceType.getCheckedRadioButtonId() != -1) {
							switch (rgInvoiceType.indexOfChild(rgInvoiceType.findViewById(rgInvoiceType.getCheckedRadioButtonId()))) {
								case 0:
									invoiceType = "Sale";
									break;
								case 1:
									invoiceType = "Return";
									break;
							}
						}
						String inputType = null;
						if (rgInputType.getCheckedRadioButtonId() != -1) {
							switch (rgInputType.indexOfChild(rgInputType.findViewById(rgInputType.getCheckedRadioButtonId()))) {
								case 0:
									inputType = "CASH";
									break;
								case 1:
									inputType = "CARD";
									break;
								case 2:
									inputType = "PREPAID";
									break;
							}
						}
						String purchases = edtPurchases.getText().toString();
						String email = edtEmail.getText().toString();
						String description = edtDescription.getText().toString();
						Double cashAccepted = null;
						try {
							cashAccepted = Double.parseDouble(edtCashAccepted.getText().toString());
						} catch (Exception e) { e.printStackTrace(); }

						fiscalAction(invoiceType, inputType, purchases, email, description, cashAccepted);
					}
				})
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				}).create().show();

	}

	private void setActionData(Intent intent) {
		intent.putExtra("Email", edtLogin.getText().toString());
		intent.putExtra("Password", edtPassword.getText().toString());
		intent.putExtra("ExtID", edtExtID.getText().toString());
	}

    private void openShift(Double introduce) {
    	Intent intent = new Intent("ru.ibox.pro.printer");
    	setActionData(intent);
        intent.putExtra("Action", "OpenShift");
		if (introduce != null)
			intent.putExtra("Cash", introduce);
        startActivityForResult(intent, 503);
    }
    
    private void closeShift(boolean readRegisters) {
    	Intent intent = new Intent("ru.ibox.pro.printer");
		setActionData(intent);
        intent.putExtra("Action", "CloseShift");
		intent.putExtra("ReadRegisters", readRegisters);
        startActivityForResult(intent, 504);
    }

	private void fiscalAction(String invoiceType, String inputType, String purchases, String email, String description, Double cashAccepted) {
		Intent intent = new Intent("ru.ibox.pro.printer");
		setActionData(intent);
		intent.putExtra("Action", invoiceType);
		intent.putExtra("ReceiptEmail", email);
		//intent.putExtra("ReceiptPhone", "+79161112233");
		if (inputType != null)
			intent.putExtra("InputType", inputType);
		intent.putExtra("Purchases", purchases);
		intent.putExtra("Description", description);
		if (cashAccepted != null)
			intent.putExtra("Cash", cashAccepted);
		startActivityForResult(intent, 505);
	}

	private void xReport() {
		Intent intent = new Intent("ru.ibox.pro.printer");
		setActionData(intent);
		intent.putExtra("Action", "XReport");
		startActivityForResult(intent, 506);
	}

	private void yReport() {
		Intent intent = new Intent("ru.ibox.pro.printer");
		setActionData(intent);
		intent.putExtra("Action", "YReport");
		startActivityForResult(intent, 507);
	}

	private void report1() {
		Intent intent = new Intent("ru.ibox.pro.printer");
		setActionData(intent);
		intent.putExtra("Action", "Report1");
		startActivityForResult(intent, 508);
	}

	private void printText(String text) {
		Intent intent = new Intent("ru.ibox.pro.printer");
		setActionData(intent);
		intent.putExtra("Action", "PrintText");
		intent.putExtra("Text", text);
		startActivityForResult(intent, 509);
	}
}
