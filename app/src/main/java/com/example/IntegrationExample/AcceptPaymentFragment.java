package com.example.IntegrationExample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class AcceptPaymentFragment extends Fragment {
    private String   mImagePath;
    private EditText edtLogin, edtPassword, edtExtID, edtExtTID, edtAmount, edtDescription, edtReceiptEmail, edtReceiptPhone;
    private EditText edtHeader, edtFooter;
    private CheckBox cbAmount, cbSkipReceipt, cbOffline, cbProduct, cbAux, cbAuxTags, cbReaderType, cbReaderID, cbPrintCopy, cbSkipFiscalRequest;
    private RadioGroup rgInputType;
    private Button   btnSelectPhoto, btnCapturePhoto;
    private Button   btnAcceptPayment;
    private EditText txtResult;

    private String product, readerType, readerID, auxTags, skipReceiptMode;
    private ArrayAdapter<String> purchasesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readerType = null;
        readerID = null;
        product = "";
        auxTags = null;
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
        cbAmount.setOnCheckedChangeListener(null);
        cbAux.setOnCheckedChangeListener(null);
        cbAuxTags.setOnCheckedChangeListener(null);
        cbProduct.setOnCheckedChangeListener(null);
        cbReaderType.setOnCheckedChangeListener(null);
        cbReaderID.setOnCheckedChangeListener(null);
        cbSkipReceipt.setOnCheckedChangeListener(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        edtLogin = (EditText)view.findViewById(R.id.edtLogin);
        edtPassword = (EditText)view.findViewById(R.id.edtPassword);
        edtExtID = (EditText)view.findViewById(R.id.edtExtId);
        edtExtTID = (EditText)view.findViewById(R.id.edtExtTid);
        cbAmount = (CheckBox)view.findViewById(R.id.cbAmount);
        edtAmount = (EditText)view.findViewById(R.id.edtAmount);
        edtDescription = (EditText)view.findViewById(R.id.edtDescription);
        edtReceiptEmail = (EditText)view.findViewById(R.id.edtReceiptEmail);
        edtReceiptPhone = (EditText)view.findViewById(R.id.edtReceiptPhone);
        cbSkipReceipt = (CheckBox)view.findViewById(R.id.cbSkipReceipt);
        edtHeader = (EditText)view.findViewById(R.id.edtHeader);
        edtFooter = (EditText)view.findViewById(R.id.edtFooter);
        cbProduct = (CheckBox)view.findViewById(R.id.cbProduct);
        cbAux = (CheckBox)view.findViewById(R.id.cbAux);
        cbAuxTags = (CheckBox)view.findViewById(R.id.cbAuxTags);
        cbReaderType = (CheckBox)view.findViewById(R.id.cbReaderType);
        cbReaderID = (CheckBox)view.findViewById(R.id.cbReaderId);
        cbPrintCopy = (CheckBox)view.findViewById(R.id.cbPrintCopy);
        cbSkipFiscalRequest = (CheckBox)view.findViewById(R.id.cbSkipFiscalRequest);
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
            StringBuilder strResult = new StringBuilder();

            if (data != null && data.getExtras() != null) {
                if (data.getExtras().containsKey("ErrorCode"))
                    strResult.append("Код ошибки: ").append(data.getExtras().getInt("ErrorCode", 0)).append("\n");

                if (data.getExtras().containsKey("ErrorMessage"))
                    strResult.append(data.getExtras().getString("ErrorMessage")).append("\n");
                else
                    strResult.append("Платеж не проведен!").append("\n");
            }
        	else
        	    strResult.append("Платеж не проведен!");
            txtResult.setText(strResult.toString());
        }
	}
	
    private void acceptPayment(double amount, String description, String imagePath) {
        Intent intent = new Intent("ru.ibox.pro.acceptpayment");

        //CHIP&SIGN, CHIP&PIN
        //intent.putExtra("ReaderType", "CHIP&PIN");
        intent.putExtra("Email", edtLogin.getText().toString());
        intent.putExtra("Password", edtPassword.getText().toString());
        intent.putExtra("ExtID", edtExtID.getText().toString());
        intent.putExtra("ExternalTerminalID", edtExtTID.getText().toString());
        intent.putExtra("Offline", cbOffline.isChecked());
        if (cbAmount.isChecked())
            intent.putExtra("Amount", amount);
        intent.putExtra("ReceiptEmail", edtReceiptEmail.getText().toString());
        intent.putExtra("ReceiptPhone", edtReceiptPhone.getText().toString());
        intent.putExtra("SkipReceiptScr", skipReceiptMode);
        intent.putExtra("PrinterHeader", edtHeader.getText().toString());
        intent.putExtra("PrinterFooter", edtFooter.getText().toString());
        intent.putExtra("Description", description);
        intent.putExtra("FiscalResultSkip", cbSkipFiscalRequest.isChecked());
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

        startActivityForResult(intent, 500);
    }

    public static String getResult(Intent data) {
        StringBuilder strResult = new StringBuilder();

        if (data.getExtras() != null) {
            strResult.append("Общие параметры совершенной операции:\n");
            if (data.getExtras().containsKey("TransactionId"))
                strResult.append("Уникальный идентификатор транзакции в процессинге ibox: ").append("\n").append(data.getExtras().getString("TransactionId")).append("\n");
            if (data.getExtras().containsKey("Created"))
                strResult.append("Дата и время создания транзакции в процессинге ibox (формат UNIX time): ").append("\n").append(String.valueOf(data.getExtras().getLong("Created"))).append("\n");
            if (data.getExtras().containsKey("CreatedDT"))
                strResult.append("Дата и время создания транзакции в процессинге ibox (формат yyyy-mm-ddThh:mm:ss): ").append("\n").append(data.getExtras().getString("CreatedDT")).append("\n");
            if (data.getExtras().containsKey("ClientID"))
                strResult.append("ID клиента в системе ibox: ").append("\n").append(String.valueOf(data.getExtras().getInt("ClientID"))).append("\n");
            if (data.getExtras().containsKey("BranchID"))
                strResult.append("ID филиала клиента в системе ibox: ").append("\n").append(String.valueOf(data.getExtras().getInt("BranchID"))).append("\n");
            if (data.getExtras().containsKey("PosID"))
                strResult.append("ID агента в системе ibox: ").append("\n").append(String.valueOf(data.getExtras().getInt("PosID"))).append("\n");
            if (data.getExtras().containsKey("Amount"))
                strResult.append("Сумма транзакции: ").append("\n").append(String.valueOf(data.getExtras().getDouble("Amount"))).append("\n");
            if (data.getExtras().containsKey("Invoice"))
                strResult.append("Номер чека в процессинге ibox: ").append("\n").append(data.getExtras().getString("Invoice")).append("\n");
            if (data.getExtras().containsKey("PaymentType"))
                strResult.append("Тип оплаты: ").append("\n").append(data.getExtras().getString("PaymentType")).append("\n");
            if (data.getExtras().containsKey("Description"))
                strResult.append("Назначение (описание) платежа системы продавца: ").append("\n").append(data.getExtras().getString("Description")).append("\n");
            if (data.getExtras().containsKey("ExtID"))
                strResult.append("Внешний идентификатор системы продавца: ").append("\n").append(data.getExtras().getString("ExtID")).append("\n");
            if (data.getExtras().containsKey("ReceiptPhone"))
                strResult.append("Телефон покупателя: ").append("\n").append(data.getExtras().getString("ReceiptPhone")).append("\n");
            if (data.getExtras().containsKey("ReceiptEmail"))
                strResult.append("Адрес электронной почты покупателя: ").append("\n").append(data.getExtras().getString("ReceiptEmail")).append("\n");
            if (data.getExtras().containsKey("ExternalPayment"))
                strResult.append("Данные для оплаты внешнего платежа: ").append("\n").append(data.getExtras().getString("ExternalPayment")).append("\n");

            strResult.append("\n\nПараметры совершенной операции по карте:\n");
            if (data.getExtras().containsKey("IIN"))
                strResult.append("Тип оплаты или платежной системы: ").append("\n").append(data.getExtras().getString("IIN")).append("\n");
            if (data.getExtras().containsKey("RRN"))
                strResult.append("Reference number: ").append("\n").append(data.getExtras().getString("RRN")).append("\n");
            if (data.getExtras().containsKey("ApprovalCode"))
                strResult.append("Код подтверждения транзакции: ").append("\n").append(data.getExtras().getString("ApprovalCode")).append("\n");
            if (data.getExtras().containsKey("TerminalID"))
                strResult.append("Терминал ID банка эквайера: ").append("\n").append(data.getExtras().getString("TerminalID")).append("\n");
            if (data.getExtras().containsKey("AcquirerTranId"))
                strResult.append("Уникальный идентификатор транзакции в процессинге банка: ").append("\n").append(data.getExtras().getString("AcquirerTranId")).append("\n");
            if (data.getExtras().containsKey("PAN"))
                strResult.append("Маскированный номер карты плательщика: ").append("\n").append(data.getExtras().getString("PAN")).append("\n");

            strResult.append("\n\nПараметры итогов фискализации операции:\n");
            if (data.getExtras().containsKey("FiscalPrinterSN"))
                strResult.append("Заводской № ККТ: ").append("\n").append(data.getExtras().getString("FiscalPrinterSN")).append("\n");
            if (data.getExtras().containsKey("FiscalShift"))
                strResult.append("№ кассовой смены: ").append("\n").append(data.getExtras().getString("FiscalShift")).append("\n");
            if (data.getExtras().containsKey("FiscalCryptoVerifCode"))
                strResult.append("КПК документа (устаревшее): ").append("\n").append(data.getExtras().getString("FiscalCryptoVerifCode")).append("\n");
            if (data.getExtras().containsKey("FiscalDocSN"))
                strResult.append("№ фискального чека в пределах кассовой смены: ").append("\n").append(data.getExtras().getString("FiscalDocSN")).append("\n");
            if (data.getExtras().containsKey("FiscalPrinterRegnum"))
                strResult.append("Регистрационный № ККТ: ").append("\n").append(data.getExtras().getString("FiscalPrinterRegnum")).append("\n");
            if (data.getExtras().containsKey("FiscalDocumentNumber"))
                strResult.append("№ фискального документа: ").append("\n").append(data.getExtras().getString("FiscalDocumentNumber")).append("\n");
            if (data.getExtras().containsKey("FiscalStorageNumber"))
                strResult.append("№ фискального накопителя: ").append("\n").append(data.getExtras().getString("FiscalStorageNumber")).append("\n");
            if (data.getExtras().containsKey("FiscalMark"))
                strResult.append("Фискальный признак документа: ").append("\n").append(data.getExtras().getString("FiscalMark")).append("\n");
            if (data.getExtras().containsKey("FiscalDatetime"))
                strResult.append("Дата и время фискализации: ").append("\n").append(data.getExtras().getString("FiscalDatetime")).append("\n");
        }

        return strResult.toString();
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
