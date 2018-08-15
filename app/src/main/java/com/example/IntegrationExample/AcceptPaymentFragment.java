package com.example.IntegrationExample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class AcceptPaymentFragment extends Fragment {
    private String   mImagePath;
    private EditText edtLogin, edtPassword, edtExtID, edtAmount, edtDescription, edtReceiptEmail, edtReceiptPhone;
    private EditText edtHeader, edtFooter;
    private CheckBox cbAmount, cbOffline, cbProduct, cbAux, cbReaderType, cbPrintCopy;
    private RadioGroup rgInputType;
    private Button   btnSelectPhoto, btnCapturePhoto;
    private Button   btnAcceptPayment;
    private EditText txtResult;

    private String product, readerType;
    private ArrayAdapter<String> purchasesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readerType = null;
        product = "";
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
        cbProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    showSetProductDialog();
                else
                    product = "";
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
        cbProduct.setOnCheckedChangeListener(null);
        cbReaderType.setOnCheckedChangeListener(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        edtLogin = (EditText)view.findViewById(R.id.edtLogin);
        edtPassword = (EditText)view.findViewById(R.id.edtPassword);
        edtExtID = (EditText)view.findViewById(R.id.edtExtId);
        cbAmount = (CheckBox)view.findViewById(R.id.cbAmount);
        edtAmount = (EditText)view.findViewById(R.id.edtAmount);
        edtDescription = (EditText)view.findViewById(R.id.edtDescription);
        edtReceiptEmail = (EditText)view.findViewById(R.id.edtReceiptEmail);
        edtReceiptPhone = (EditText)view.findViewById(R.id.edtReceiptPhone);
        edtHeader = (EditText)view.findViewById(R.id.edtHeader);
        edtFooter = (EditText)view.findViewById(R.id.edtFooter);
        cbProduct = (CheckBox)view.findViewById(R.id.cbProduct);
        cbAux = (CheckBox)view.findViewById(R.id.cbAux);
        cbReaderType = (CheckBox)view.findViewById(R.id.cbReaderType);
        cbPrintCopy = (CheckBox)view.findViewById(R.id.cbPrintCopy);
        cbOffline = (CheckBox)view.findViewById(R.id.cbOffline);
        rgInputType = (RadioGroup)view.findViewById(R.id.rgInputType);
        btnCapturePhoto = (Button)view.findViewById(R.id.btnCapturePhoto);
        btnSelectPhoto = (Button)view.findViewById(R.id.btnSelectPhoto);
        btnAcceptPayment = (Button)view.findViewById(R.id.btnAcceptPayment);
        txtResult = (EditText)view.findViewById(R.id.txtResult);

        edtHeader.setText(getText(R.string.default_header));
        edtFooter.setText(getText(R.string.default_footer));

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
//                String s = "{\"Purchases\": [{\"Title\":\"Тариф за пересылку\", \"Price\":680.00,\"Quantity\": 1, \"TaxCode\":[\"VAT1800\"]},{\"Title\":\"Страхование\", \"Price\":0.16,\"Quantity\": 1, \"TaxCode\":[\"VAT1800\"]},{\"Title\":\"Объявленная ценность\", \"Price\":28.00,\"Quantity\": 1, \"TaxCode\":[\"VAT1800\"]},{\"Title\":\"Тариф за объявленную ценность\", \"Price\":0.28,\"Quantity\": 1, \"TaxCode\":[\"VAT1800\"]}]}";
//
//                startActivityForResult(new Intent("ru.ibox.pro.acceptpayment")
//                        .putExtra("Email", getString(R.string.login))
//                        .putExtra("Password", getString(R.string.password))
//                .putExtra("PrinterHeader", "Внутренняя почта\nZX123456789ZX\nОтправлние EMS\nс объявл. ценностью и налож. платежом\nФизическое лицо\nas\nКуда: 443051, САМАРСКАЯ ОБЛАСТЬ, САМАРА\nАвиа транспорт\nВес: 1.000кг.")
//                        .putExtra("Purchases", s), 500);

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
            txtResult.setText(getResult(data));
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
        intent.putExtra("Email", edtLogin.getText().toString());
        intent.putExtra("Password", edtPassword.getText().toString());
        intent.putExtra("ExtID", edtExtID.getText().toString());
        intent.putExtra("Offline", cbOffline.isChecked());
        if (cbAmount.isChecked())
            intent.putExtra("Amount", amount);
        intent.putExtra("ReceiptEmail", edtReceiptEmail.getText().toString());
        intent.putExtra("ReceiptPhone", edtReceiptPhone.getText().toString());
        intent.putExtra("PrinterHeader", edtHeader.getText().toString());
        intent.putExtra("PrinterFooter", edtFooter.getText().toString());
        intent.putExtra("Description", description);
        if (imagePath != null)
            intent.putExtra("Image", imagePath);
        if (rgInputType.getCheckedRadioButtonId() != -1) {
            String inputType = null;
            switch (rgInputType.indexOfChild(rgInputType.findViewById(rgInputType.getCheckedRadioButtonId()))) {
                case 0:
                    inputType = "CARD";
                    break;
                case 1:
                    inputType = "NFC";
                    break;
                case 2:
                    inputType = "CASH";
                    break;
                case 3:
                    inputType = "PREPAID";
                    break;
                case 4:
                    inputType = "CREDIT";
                    break;
                case 5:
                    inputType = "LINK";
                    break;
                case 6:
                    inputType = "OUTER_CARD";
                    break;
            }
            if (inputType != null)
                intent.putExtra("InputType", inputType);
        }

        //Платеж с использованием продуктов
        if (cbProduct.isChecked())
            intent.putExtra("Product", product);

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

        startActivityForResult(intent, 500);
    }

    private String getResult(Intent data) {
        String strResult = "";

        if (data.getExtras().containsKey("TransactionId"))
            strResult += "Transaction ID : " + data.getExtras().getString("TransactionId") + "\n";

        if (data.getExtras().containsKey("Invoice"))
            strResult += "Invoice : " + data.getExtras().getString("Invoice") + "\n";

        if (data.getExtras().containsKey("RRN"))
            strResult += "RRN : " + data.getExtras().getString("RRN") + "\n";

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

        if (data.getExtras().containsKey("IIN"))
            strResult += "IIN : " +  data.getExtras().getString("IIN")+ "\n";

        if (data.getExtras().containsKey("Created"))
            strResult += "Created : " +  data.getExtras().getLong("Created") + "\n";

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

        if (data.getExtras().containsKey("ExtID"))
            strResult += "Ext ID : " + data.getExtras().getString("ExtID") + "\n";

        if (data.getExtras().containsKey("ExternalPayment"))
            strResult += "ExternalPayment : " + data.getExtras().getString("ExternalPayment") + "\n";

        return strResult;
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

    private void showSetProductDialog() {
        final EditText edtProduct = (EditText) LayoutInflater.from(getContext()).inflate(R.layout.dialog_purchase, null);
        edtProduct.setText(MainActivity.TEST_PRODUCT);
        new AlertDialog.Builder(getContext())
                .setView(edtProduct)
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        product = edtProduct.getText().toString();
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
}
