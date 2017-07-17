package com.example.IntegrationExample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class ReverseFragment : Fragment() {

    companion object {
        private val TEST_REVERSE_BY_POSITIONS = true
    }

    lateinit var edtTrID: EditText
    lateinit var edtHeader: EditText
    lateinit var edtFooter: EditText
    lateinit var btnReturn: Button
    lateinit var btnCancel: Button
    lateinit var txtResult: EditText

    private val purchases = """{
                \"Purchases\": [{
                \"Title\": \"Позиция 2\",
                        \"Price\": 100.00,
                        \"Quantity\": 1,
                        \"TaxCode\": [VAT1800]
                        }]
            }"""

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_reverse, container, false)

        edtTrID = view.findViewById(R.id.edtTrId) as EditText
        edtHeader = view.findViewById(R.id.edtHeader) as EditText
        edtFooter = view.findViewById(R.id.edtFooter) as EditText
        btnReturn = view.findViewById(R.id.btnReturn) as Button
        btnCancel = view.findViewById(R.id.btnCancel) as Button
        txtResult = view.findViewById(R.id.txtResult) as EditText

        btnReturn.setOnClickListener { returnPayment() }

        btnCancel.setOnClickListener { cancelPayment() }

        return view
    }

    private fun returnPayment() {
        val intent = Intent("ru.ibox.pro.reversepayment").apply {
            putExtra("Email", getString(R.string.login))
            putExtra("Password", getString(R.string.password))
            putExtra("Action", "Return")
            putExtra("TrID", edtTrID.text.toString())
            putExtra("PrinterHeader", edtHeader.text.toString())
            putExtra("PrinterFooter", edtFooter.text.toString())

            if (TEST_REVERSE_BY_POSITIONS)
                putExtra("Purchases", purchases)
        }

        startActivityForResult(intent, 501)
    }

    private fun cancelPayment() {
        val intent = Intent("ru.ibox.pro.reversepayment").apply {
            putExtra("Email", getString(R.string.login))
            putExtra("Password", getString(R.string.password))
            putExtra("Action", "Cancel")
            putExtra("TrID", edtTrID.text.toString())
            putExtra("PrinterHeader", edtHeader.text.toString())
            putExtra("PrinterFooter", edtFooter.text.toString())

            if (TEST_REVERSE_BY_POSITIONS)
                putExtra("Purchases", purchases)
        }

        startActivityForResult(intent, 502)
    }

    private fun getResult(data: Intent): String {
        var result = ""

        if (data.extras.containsKey("TransactionId"))
            result += "Transaction ID : " + data.extras.getString("TransactionId") + "\n"

        if (data.extras.containsKey("Invoice"))
            result += "Invoice : " + data.extras.getString("Invoice") + "\n"

        if (data.extras.containsKey("RRN"))
            result += "RRN : " + data.extras.getString("RRN") + "\n"

        if (data.extras.containsKey("ReceiptPhone"))
            result += "ReceiptPhone : " + data.extras.getString("ReceiptPhone") + "\n"

        if (data.extras.containsKey("ReceiptEmail"))
            result += "ReceiptEmail : " + data.extras.getString("ReceiptEmail") + "\n"

        if (data.extras.containsKey("Amount"))
            result += "Amount : " + data.extras.getDouble("Amount") + "\n"

        if (data.extras.containsKey("PAN"))
            result += "PAN : " + data.extras.getString("PAN") + "\n"

        if (data.extras.containsKey("Created"))
            result += "Created : " + data.extras.getLong("Created") + "\n"

        if (data.extras.containsKey("FiscalPrinterSN"))
            result += "FiscalPrinterSN : " + data.extras.getString("FiscalPrinterSN") + "\n"

        if (data.extras.containsKey("FiscalShift"))
            result += "FiscalShift : " + data.extras.getString("FiscalShift") + "\n"

        if (data.extras.containsKey("FiscalCryptoVerifCode"))
            result += "FiscalCryptoVerifCode : " + data.extras.getString("FiscalCryptoVerifCode") + "\n"

        if (data.extras.containsKey("FiscalDocSN"))
            result += "FiscalDocSN : " + data.extras.getString("FiscalDocSN") + "\n"

        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 501 && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                txtResult.setText(getResult(data))
            } else {
                if (data.extras.containsKey("ErrorMessage"))
                    txtResult.setText(data.extras.getString("ErrorMessage"))
                else {
                    txtResult.setText("Payment return error")
                }
            }
        }

        if (requestCode == 502 && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                txtResult.setText(getResult(data))
            } else {
                if (data.extras.containsKey("ErrorMessage"))
                    txtResult.setText(data.extras.getString("ErrorMessage"))
                else
                    txtResult.setText("Payment cancel error")
            }
        }
    }
}
