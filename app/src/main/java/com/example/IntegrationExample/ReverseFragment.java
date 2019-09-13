package com.example.IntegrationExample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class ReverseFragment extends Fragment {
	private EditText edtLogin, edtPassword, edtExtID, edtExtTID, edtAmount, edtReceiptEmail, edtReceiptPhone;
	private EditText edtTrID;
    private EditText edtHeader, edtFooter;
	private CheckBox cbCreditVoucher, cbAmount, cbSkipReceipt, cbAux, cbAuxTags, cbReaderType, cbReaderID, cbPrintCopy, cbSkipFiscalRequest;
    private Button   btnReturn, btnCancel;
    private EditText txtResult;

	private String readerType, readerID, auxTags, skipReceiptMode;
	private ArrayAdapter<String> purchasesAdapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		readerType = null;
		auxTags = null;
		purchasesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.test_list_item, new ArrayList<String>());
	}

	@Override
	public void onResume() {
		super.onResume();
		cbCreditVoucher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				edtTrID.setEnabled(!isChecked);
			}
		});
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
				else {
					purchasesAdapter.clear();
					cbAuxTags.setChecked(false);
				}
			}
		});
		cbAuxTags.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
				if (checked)
					showTagsDialog();
				else
					auxTags = null;
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
		cbReaderID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					showDevicesDialog();
				else
					readerID = null;
			}
		});
		cbSkipReceipt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					showSkipReceiptDialog();
				else
					skipReceiptMode = null;
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		cbCreditVoucher.setOnCheckedChangeListener(null);
		cbAmount.setOnCheckedChangeListener(null);
		cbAux.setOnCheckedChangeListener(null);
		cbReaderType.setOnCheckedChangeListener(null);
		cbReaderID.setOnCheckedChangeListener(null);
		cbSkipReceipt.setOnCheckedChangeListener(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reverse, container, false);

		edtLogin = (EditText)view.findViewById(R.id.edtLogin);
		edtPassword = (EditText)view.findViewById(R.id.edtPassword);
		edtExtID = (EditText)view.findViewById(R.id.edtExtId);
		edtExtTID = (EditText)view.findViewById(R.id.edtExtTid);
		edtAmount = (EditText)view.findViewById(R.id.edtAmount);
		edtReceiptEmail = (EditText)view.findViewById(R.id.edtReceiptEmail);
		edtReceiptPhone = (EditText)view.findViewById(R.id.edtReceiptPhone);
		cbSkipReceipt = (CheckBox)view.findViewById(R.id.cbSkipReceipt);
		edtHeader = (EditText)view.findViewById(R.id.edtHeader);
		edtFooter = (EditText)view.findViewById(R.id.edtFooter);
		cbCreditVoucher = (CheckBox)view.findViewById(R.id.cbCreditVoucher);
		cbAmount = (CheckBox)view.findViewById(R.id.cbAmount);
		cbAux = (CheckBox)view.findViewById(R.id.cbAux);
		cbAuxTags = (CheckBox)view.findViewById(R.id.cbAuxTags);
		cbReaderType = (CheckBox)view.findViewById(R.id.cbReaderType);
		cbReaderID = (CheckBox)view.findViewById(R.id.cbReaderId);
		cbPrintCopy = (CheckBox)view.findViewById(R.id.cbPrintCopy);
		cbSkipFiscalRequest = (CheckBox)view.findViewById(R.id.cbSkipFiscalRequest);
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
		intent.putExtra("ExternalTerminalID", edtExtTID.getText().toString());
		intent.putExtra("ReceiptEmail", edtReceiptEmail.getText().toString());
		intent.putExtra("ReceiptPhone", edtReceiptPhone.getText().toString());
		intent.putExtra("SkipReceiptScr", skipReceiptMode);
		intent.putExtra("PrinterHeader", edtHeader.getText().toString());
		intent.putExtra("PrinterFooter", edtFooter.getText().toString());
		intent.putExtra("FiscalResultSkip", cbSkipFiscalRequest.isChecked());
		if (!cbCreditVoucher.isChecked())
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
			purchases.append("]");
			if (cbAuxTags.isChecked()) {
				purchases.append(",")
						.append("\"Tags\":").append(auxTags);
			}
			purchases.append("}");
			intent.putExtra("Purchases", purchases.toString());
		} else if (cbAuxTags.isChecked()) {
			StringBuilder tags = new StringBuilder()
					.append("{")
					.append("\"Tags\":").append(auxTags)
					.append("}");
			intent.putExtra("Purchases", tags.toString());
		}

		if (cbReaderType.isChecked())
			intent.putExtra("ReaderType", readerType);
		if (cbReaderID.isChecked())
			intent.putExtra("ReaderID", readerID);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		 if (requestCode == 501 || requestCode == 502) {
	        	if (resultCode == Activity.RESULT_OK) {
	                txtResult.setText(AcceptPaymentFragment.getResult(data));
	        	} else {
					StringBuilder strResult = new StringBuilder();

					if (data != null && data.getExtras() != null) {
						if (data.getExtras().containsKey("ErrorCode"))
							strResult.append("Код ошибки: ").append(data.getExtras().getInt("ErrorCode", 0)).append("\n");

						if (data.getExtras().containsKey("ErrorMessage"))
							strResult.append(data.getExtras().getString("ErrorMessage")).append("\n");
						else
							strResult.append(requestCode == 501 ? "Возврат  не проведен!" : "Отмена  не проведена!").append("\n");
					} else
						strResult.append(requestCode == 501 ? "Возврат  не проведен!" : "Отмена  не проведена!").append("\n");
					txtResult.setText(strResult.toString());
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

	private void showTagsDialog() {
		final EditText edtTags = (EditText) LayoutInflater.from(getContext()).inflate(R.layout.dialog_purchase, null);
		edtTags.setText(MainActivity.TEST_TAGS);
		new AlertDialog.Builder(getContext())
				.setView(edtTags)
				.setPositiveButton("Применить", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						try {
							new JSONObject(edtTags.getText().toString());
							auxTags = edtTags.getText().toString();
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(getContext(), "Ошибка!", Toast.LENGTH_LONG).show();
						}
					}
				})
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
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

	private void showDevicesDialog() {
		final List<String> devices = new ArrayList<>();
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		if (bt != null) {
			Set<BluetoothDevice> bonded = bt.getBondedDevices();
			if (bonded != null)
				for (BluetoothDevice device : bonded)
					devices.add(String.format("%s\n%s", device.getName(), device.getAddress()));
		}

		new AlertDialog.Builder(getContext())
				.setSingleChoiceItems(devices.toArray(new String[] {}), 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						readerID = devices.get(which).split("\\n", 2)[1];
						dialog.dismiss();
					}
				})
				.create().show();
	}

	private void showSkipReceiptDialog() {
		final LinkedHashMap<String, String> skipModes = new LinkedHashMap<>();
		skipModes.put("Всегда пропускать", "true");
		skipModes.put("Никогда не пропускать", "false");
		skipModes.put("Пропускать при наличии адреса/телефона", "exist");

		final String[] keys = skipModes.keySet().toArray(new String[] {});
		new AlertDialog.Builder(getContext())
				.setSingleChoiceItems(keys, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						skipReceiptMode = skipModes.get(keys[which]);
						dialog.dismiss();
					}
				})
				.create().show();
	}
}
