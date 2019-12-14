package com.example.IntegrationExample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import java.io.File

class AcceptPaymentFragment : Fragment() {

    companion object {
        private val TEST_CUSTOM_FIELDS = false
        private val TEST_PURCHASES = true
    }

    var mImagePath: String = ""

    lateinit var edtAmount: EditText
    lateinit var edtDescription: EditText
    lateinit var edtHeader: EditText
    lateinit var edtFooter: EditText
    lateinit var rgInputType: RadioGroup
    lateinit var btnSelectPhoto: Button
    lateinit var btnCapturePhoto: Button
    lateinit var btnAcceptPayment: Button
    lateinit var txtResult: EditText

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_payment, container, false)

        edtDescription = view.findViewById(R.id.edtDescription) as EditText
        edtAmount = view.findViewById(R.id.edtAmount) as EditText
        edtHeader = view.findViewById(R.id.edtHeader) as EditText
        edtFooter = view.findViewById(R.id.edtFooter) as EditText
        rgInputType = view.findViewById(R.id.rgInputType) as RadioGroup
        btnCapturePhoto = view.findViewById(R.id.btnCapturePhoto) as Button
        btnSelectPhoto = view.findViewById(R.id.btnSelectPhoto) as Button
        btnAcceptPayment = view.findViewById(R.id.btnAcceptPayment) as Button
        txtResult = view.findViewById(R.id.txtResult) as EditText

        edtHeader.setText(getText(R.string.default_header))
        edtFooter.setText(getText(R.string.default_footer))

        btnCapturePhoto.setOnClickListener {
            val outputFileUri = Uri.fromFile(File(cameraImagePath))
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .apply { putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri) }

            startActivityForResult(intent, 250)
        }

        btnSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, 251)
        }

        btnAcceptPayment.setOnClickListener {
            val amount = edtAmount.text.toString().toDouble()
            val description = edtDescription.text.toString()

            acceptPayment(amount, description, mImagePath)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 250 && resultCode == Activity.RESULT_OK) {
            mImagePath = cameraImagePath
            return
        }

        if (requestCode == 251 && resultCode == Activity.RESULT_OK) {
            mImagePath = getRealPathFromUri(data?.data ?: Uri.EMPTY)
            return
        }

        if (requestCode == 500 && resultCode == Activity.RESULT_OK && data != null) {
            var strResult = "";

            if (data.extras.containsKey("TransactionId"))
                strResult += "Transaction ID : " + data.extras.getString("TransactionId") + "\n";

            if (data.extras.containsKey("Invoice"))
                strResult += "Invoice : " + data.extras.getString("Invoice") + "\n";

            if (data.extras.containsKey("RRN"))
                strResult += "RRN : " + data.extras.getString("RRN") + "\n";

            if (data.extras.containsKey("ReceiptPhone"))
                strResult += "ReceiptPhone : " +  data.extras.getString("ReceiptPhone")+ "\n";

            if (data.extras.containsKey("ReceiptEmail"))
                strResult += "ReceiptEmail : " +  data.extras.getString("ReceiptEmail")+ "\n";

            if (data.extras.containsKey("PaymentType"))
                strResult += "PaymentType : " +  data.extras.getString("PaymentType")+ "\n";

            if (data.extras.containsKey("Amount"))
                strResult += "Amount : " +  data.extras.getDouble("Amount")+ "\n";

            if (data.extras.containsKey("PAN"))
                strResult += "PAN : " +  data.extras.getString("PAN")+ "\n";

            if (data.extras.containsKey("Created"))
                strResult += "Created : " +  data.extras.getLong("Created") + "\n";

            if (data.extras.containsKey("FiscalPrinterSN"))
                strResult += "FiscalPrinterSN : " +  data.extras.getString("FiscalPrinterSN") + "\n";

            if (data.extras.containsKey("FiscalShift"))
                strResult += "FiscalShift : " +  data.extras.getString("FiscalShift") + "\n";

            if (data.extras.containsKey("FiscalCryptoVerifCode"))
                strResult += "FiscalCryptoVerifCode : " +  data.extras.getString("FiscalCryptoVerifCode") + "\n";

            if (data.extras.containsKey("FiscalDocSN"))
                strResult += "FiscalDocSN : " +  data.extras.getString("FiscalDocSN") + "\n";

            txtResult.setText(strResult);
        }

        if (requestCode == 500 && resultCode != Activity.RESULT_OK) {
            if (data != null && data.extras.containsKey("ErrorMessage"))
                txtResult.setText(data.extras.getString("ErrorMessage"))
            else
                txtResult.setText("Платеж не проведен!")
        }
    }

    private fun acceptPayment(amount: Double, description: String, imagePath: String?) {
        val intent = Intent("ru.ibox.pro.acceptpayment").apply {
            //CHIP&SIGN, CHIP&PIN
            //putExtra("ReaderType", "CHIP&PIN");

            putExtra("Email", getString(R.string.login))
            putExtra("Password", getString(R.string.password))
            putExtra("Amount", amount)
            //putExtra("ReceiptEmail", "test@test.com");
            //putExtra("ReceiptPhone", "+79161112233");
            putExtra("PrinterHeader", edtHeader.text.toString())
            putExtra("PrinterFooter", edtFooter.text.toString())
        }

        val inputType: String = when (rgInputType.indexOfChild(rgInputType
                .findViewById(rgInputType.checkedRadioButtonId))) {
            0 -> "CARD"
            1 -> "NFC"
            2 -> "CASH"
            3 -> "PREPAID"
            else -> "CARD"
        }
        intent.putExtra("InputType", inputType)

        // Простой платеж
        if (!TEST_CUSTOM_FIELDS && !TEST_PURCHASES) {
            intent.putExtra("Description", description)
            if (imagePath != null)
                intent.putExtra("Image", imagePath)
        }

        //Платеж с использованием продуктов
        if (TEST_CUSTOM_FIELDS) {
            val product = "ST00012|Product=DELIVERY|_CLIENT_NAME_=Иванов|_CONTRACT_NO_=123456"
            intent.putExtra("Product", product)
        }

        // Простой платеж с произвольным фискальным чеком
        if (TEST_PURCHASES) {
            try {
                intent.putExtra("Description", description)

                val purchases = "{" +
                        "    \"Purchases\": [{" +
                        "    \"Title\": \"Позиция 1\"," +
                        "            \"Price\": 150.25," +
                        "            \"Quantity\": 2," +
                        "           \"TaxCode\": [\"VAT1800\"]" +
                        "}, {" +
                        "    \"Title\": \"Позиция 2\"," +
                        "            \"Price\": 100.00," +
                        "            \"Quantity\": 1," +
                        "           \"TaxCode\": [VAT1800]" +
                        "}]" +
                        "}"


                intent.putExtra("Purchases", purchases)
                Log.i("TEST_PURCHASES", purchases)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        startActivityForResult(intent, 500)
    }

    private val cameraImagePath: String
        get() = android.os.Environment.getExternalStorageDirectory().absolutePath + "/camera.tmp"

    private fun getRealPathFromUri(contentUri: Uri): String {
        val projection = arrayOf(android.provider.MediaStore.MediaColumns.DATA)
        val cursor = activity.contentResolver.query(contentUri, projection, null, null, null)
        return if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(android.provider.MediaStore.MediaColumns.DATA)
            cursor.getString(index)
        } else ""
    }

}
